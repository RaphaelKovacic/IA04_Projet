package agents;

import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import Class_For_JSON.Loi;
import Class_For_JSON.MajDepute;
import Class_For_JSON.MajEnv;
import ParlementSim.Aid_vote;
import ParlementSim.ParlementManager;
import agents.MediateurAgent.ActionFromUtilisateur;
import agents.MediateurAgent.PrecisionFromUtilisateur;
import agents.MediateurAgent.TourFromSimulation;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import launcher.AgentLauncher;

public class LoiAgent extends Agent{

	List<Aid_vote> L_AID_Vote = new ArrayList<Aid_vote>();
	Loi loi_en_cours = new Loi();
	AID proposant;
	int nb_votant;
	int nb_vote_pour;
	int nb_vote_contre;

	AID AMediateur;
	AID AUtilisateur;
	AID AEnvironnement;
	List<AID> List_Depute = new ArrayList<AID>();

	ParlementManager parl_mana = new ParlementManager();

	protected void setup() 
	{ 
		// Enregistrement aupr�s du DF
		DFAgentDescription dafd = new DFAgentDescription();
		dafd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Parlement");
		sd.setName("ALoi");
		dafd.addServices(sd);
		try {
			DFService.register(this, dafd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

		nb_votant = 0;
		nb_vote_pour = 0;
		nb_vote_contre = 0;
		proposant = null;


		System.out.println("Agent Loi cr�� : "+this.getLocalName());
		addBehaviour(new OneShotBehaviour(){

			@Override
			public void action() {
				// On r�cup�re les AID des agents n�cessaires
				while (AMediateur == null || AUtilisateur == null || AEnvironnement == null || List_Depute.size() != AgentLauncher.NB_DEPUTE){
					AMediateur = parl_mana.getReceiver(myAgent, "Parlement", "AMediateur");
					AUtilisateur = parl_mana.getReceiver(myAgent, "Parlement", "AUtilisateur");
					AEnvironnement = parl_mana.getReceiver(myAgent, "Monde", "AEnvironnement");
					List_Depute = parl_mana.getAllAidOf(myAgent, "Parlement", "ADepute");
				}

				nb_votant = List_Depute.size(); // tous les d�put�s + l'utilisateur - proposant = tous les d�put�s

				addBehaviour(new RequestOfMediator()); // rec�ption d'un message demandant de faire proposer une loi ou de faire voter une loi
				addBehaviour(new ProposalLawOfDepute()); // r�ception d'une proposition de loi d'un d�put�
				addBehaviour(new AcceptLawOfDepute()); // r�ception d'un vote favorable d'un d�put� ou utilisateur.
				addBehaviour(new RefuseLawOfDepute());// r�ception d'un vote d�favorable d'un d�put� ou utilisateur.

			}});


	}

	class RequestOfMediator extends CyclicBehaviour{

		@Override
		public void action() {

			// On attend la reception d'un message de type REQUEST venant de l'agent Mediateur
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchSender(AMediateur)
					);
			ACLMessage message = myAgent.receive(mt);
			if (message != null){

				// On reinitialise les votes.
				nb_vote_pour = 0;
				nb_vote_contre = 0;
				L_AID_Vote.clear();
				proposant = null;
				//remettre a 0 loi en cours

				// Si le mediateur demande a l'agent Loi de faire proposer une loi par un d�put�
				if (message.getContent().contains("Proposer une loi")){

					//l'agent loi demande � un agent d�put� de proposer une loi
					myAgent.addBehaviour(new DemandeLoi(message));

					//Sinon
				}else{

					// Le proposant est forc�ment l'utilisateur
					proposant = AUtilisateur;

					//R�cup�ration de loi (On deserialise le message)
					ObjectMapper mapper = new ObjectMapper();
					try {
						loi_en_cours = mapper.readValue(message.getContent(), Loi.class);
					}
					catch(Exception ex) {
						System.out.println("EXCEPTION" + ex.getMessage());
					}

					//l'agent loi demande � tous les d�put�s de voter pour la loi propos�e par l'utilisateur.
					myAgent.addBehaviour(new VoteLoi(message));
				}

			}else{
				block();
			}
		}
	}

	class DemandeLoi extends OneShotBehaviour{
		private ACLMessage message;

		// Constructor
		public DemandeLoi(ACLMessage message2) {
			this.message = message2;
		}

		// Task to do
		public void action() {

			//Envoie d'un message a l'agent D�put� (al�atoire pour le moment) pour qu'il propose une loi
			ACLMessage forward = message.createReply();
			forward.removeReceiver(message.getSender());
			proposant = List_Depute.get((int)(Math.random() * (List_Depute.size()))); // Retourne un d�put� au hasard parmi tous
			forward.addReceiver(proposant);
			forward.setPerformative(ACLMessage.REQUEST);
			forward.setContent(message.getContent());
			myAgent.send(forward);

		}
	}

	class VoteLoi extends OneShotBehaviour{
		private ACLMessage message;

		// Constructor
		public VoteLoi(ACLMessage message2) {
			this.message = message2;
		}

		// Task to do
		public void action() {

			//Envoie d'un message a tous les d�put�s (et utilisateur si besoin) pour qu'ils votent pour la loi contenu dans le message
			ACLMessage forward = message.createReply();
			forward.removeReceiver(message.getSender());

			// On ajoute tous les d�put�s en tant que destinataire
			for (int i = 0 ; i < List_Depute.size() ; i++)
				forward.addReceiver(List_Depute.get(i));

			// Si la proposition de loi vient d'un d�put� il faut rajouter l'utilisateur pour qu'il puisse voter...
			if ( List_Depute.contains(message.getSender()))
				forward.addReceiver(AUtilisateur);

			// Pas besoin de faire voter celui qui propose la loi...
			forward.removeReceiver(proposant);

			forward.setPerformative(ACLMessage.PROPOSE);
			forward.setContent(message.getContent());
			myAgent.send(forward);

		}
	}

	class ProposalLawOfDepute extends CyclicBehaviour{

		@Override
		public void action() {

			// On attend la reception d'un message de type PROPOSE venant de l'agent proposant la loi
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
					MessageTemplate.MatchSender(proposant) // (peut etre pas obligatoire)
					);
			ACLMessage message = myAgent.receive(mt);
			if (message != null){

				//R�cup�ration de loi (On deserialise le message)
				ObjectMapper mapper = new ObjectMapper();
				try {
					loi_en_cours = mapper.readValue(message.getContent(), Loi.class);
				}
				catch(Exception ex) {
					System.out.println("EXCEPTION" + ex.getMessage());
				}

				// On demande alors � tous (sauf la personne ayant propos� la loi) de voter la loi
				myAgent.addBehaviour(new VoteLoi(message));
			}else{
				block();
			}
		}
	}

	class AcceptLawOfDepute extends CyclicBehaviour{

		@Override
		public void action() {

			// On attend la reception d'un message de type Accept_Proposal venant des votants
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
			ACLMessage message = myAgent.receive(mt);
			if (message != null){
				// On met � jour les votes.
				nb_vote_pour++;

				if (List_Depute.contains(message.getSender())){
					Aid_vote e = new Aid_vote(message.getSender(), "pour");
					L_AID_Vote.add(e);
				}else{
					Aid_vote e = new Aid_vote(AUtilisateur, "pour");
					L_AID_Vote.add(e);
				}

				// Si le nombre total des votants est atteint (vote termin�)
				if(L_AID_Vote.size() == nb_votant)

					// Traitement des cons�quences 
					myAgent.addBehaviour(new ConsequenceLoi());
			}else{
				block();
			}
		}
	}

	class RefuseLawOfDepute extends CyclicBehaviour{

		@Override
		public void action() {

			// On attend la reception d'un message de type Accept_Proposal venant des votants
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL);
			ACLMessage message = myAgent.receive(mt);
			if (message != null){
				// On met � jour les votes.
				nb_vote_contre++;

				if (List_Depute.contains(message.getSender())){
					Aid_vote e = new Aid_vote(message.getSender(), "contre");
					L_AID_Vote.add(e);
				}else{
					Aid_vote e = new Aid_vote(AUtilisateur, "contre");
					L_AID_Vote.add(e);
				}

				// Si le nombre total des votants est atteint (vote termin�)
				if(L_AID_Vote.size() == nb_votant)

					// Traitement des cons�quences 
					myAgent.addBehaviour(new ConsequenceLoi());

			}else{
				block();
			}
		}
	}

	class ConsequenceLoi extends OneShotBehaviour{

		// Task to do
		public void action() {

			// Le vote est termin�...
			System.out.println("La Loi a �t� vot� avec : "+nb_vote_pour+" vote Pour et "+nb_vote_contre+" vote Contre.");

			//Pour chaque personne ayant vot�
			for (int i = 0 ; i < L_AID_Vote.size() ; i++){

				ObjectMapper mapper1 = new ObjectMapper();
				StringWriter sw = new StringWriter();
				MajDepute or = new MajDepute(0,0,0,0);

				// Si la loi passe
				if (nb_vote_pour > nb_vote_contre){

					// Les personnes ayant vot�s "pour" voit leur influence augmenter... (envoie de message de type INFORM)
					// Les personnes ayant vot�s "contre" voit leur influence diminuer... (envoie de message de type INFORM)
					if (L_AID_Vote.get(i).getVote().contains("pour")){
						or.setEffet_Influence(5);

						// qualite de vie
						if(loi_en_cours.getEffet_qualite_vie() > 0)
							or.setEffet_Popularite(5);
						else
							or.setEffet_Popularite(-5);

						// eco
						if(loi_en_cours.getEffet_context_eco() > 0)
							or.setEffet_Notoriete(5);
						else
							or.setEffet_Notoriete(-5);
					}


					else if(L_AID_Vote.get(i).getVote().contains("contre")){
						or.setEffet_Influence(-5);

						// qualite de vie
						if(loi_en_cours.getEffet_qualite_vie() > 0)
							or.setEffet_Popularite(-5);
						else
							or.setEffet_Popularite(5);

						// eco
						if(loi_en_cours.getEffet_context_eco() > 0)
							or.setEffet_Notoriete(-5);
						else
							or.setEffet_Notoriete(5);	
					}


					else{ // rien vote (impossible pour le moment)
						or.setEffet_Influence(0);
						or.setEffet_Notoriete(0);
						or.setEffet_Popularite(0);
					}


					try {
						mapper1.writeValue(sw, or);
						String s1 = sw.toString();
						ACLMessage message1 = new ACLMessage(ACLMessage.INFORM);
						message1.addReceiver(L_AID_Vote.get(i).getVotant());
						message1.setContent(s1);
						myAgent.send(message1);
					}
					catch(Exception ex) {
						System.out.println(ex.getMessage());
					}

					//Sinon
				}else{
					// Les personnes ayant vot�s "pour" voit leur influence diminuer... (envoie de message de type INFORM)
					// Les personnes ayant vot�s "contre" voit leur influence augmenter... (envoie de message de type INFORM)

					ACLMessage message1 = new ACLMessage(ACLMessage.INFORM);
					message1.addReceiver(L_AID_Vote.get(i).getVotant());

					if (L_AID_Vote.get(i).getVote().contains("pour")){
						or.setEffet_Influence(-5);

						// qualite de vie
						if(loi_en_cours.getEffet_qualite_vie() > 0)
							or.setEffet_Popularite(5);
						else
							or.setEffet_Popularite(-5);

						// eco
						if(loi_en_cours.getEffet_context_eco() > 0)
							or.setEffet_Notoriete(5);
						else
							or.setEffet_Notoriete(-5);
					}


					else if(L_AID_Vote.get(i).getVote().contains("contre")){
						or.setEffet_Influence(5);

						// qualite de vie
						if(loi_en_cours.getEffet_qualite_vie() > 0)
							or.setEffet_Popularite(-5);
						else
							or.setEffet_Popularite(5);

						// eco
						if(loi_en_cours.getEffet_context_eco() > 0)
							or.setEffet_Notoriete(-5);
						else
							or.setEffet_Notoriete(5);	
					}


					else{ // rien vote (impossible pour le moment)
						or.setEffet_Influence(0);
						or.setEffet_Notoriete(0);
						or.setEffet_Popularite(0);
					}

					try {
						mapper1.writeValue(sw, or);
						String s1 = sw.toString();
						message1.setContent(s1);
						myAgent.send(message1);
					}
					catch(Exception ex) {
						System.out.println(ex.getMessage());
					}
				}				
			}

			// Si la loi passe
			if (nb_vote_pour > nb_vote_contre){
				//Mise a jour des variables d'environnements.
				// On serialise le message contenant les 2 "valeurs" de variables � modifer.
				ObjectMapper mapper1 = new ObjectMapper();
				StringWriter sw = new StringWriter();

				MajEnv env = new MajEnv(loi_en_cours.getEffet_context_eco(),loi_en_cours.getEffet_qualite_vie());
				try {
					mapper1.writeValue(sw, env);
					String s1 = sw.toString();
					ACLMessage message2 = new ACLMessage(ACLMessage.REQUEST);
					message2.addReceiver(AEnvironnement);
					message2.setContent(s1);
					myAgent.send(message2);
				}
				catch(Exception ex) {
					System.out.println(ex.getMessage());
				}
			}

			//Mise � jour des caracteritiques du proposant
			ObjectMapper mapper1 = new ObjectMapper();
			StringWriter sw = new StringWriter();
			MajDepute or = new MajDepute(0,0,0,0);
			
			// qualite de vie
			if(loi_en_cours.getEffet_qualite_vie() > 0)
				or.setEffet_Popularite(-10);
			else
				or.setEffet_Popularite(10);

			// eco
			if(loi_en_cours.getEffet_context_eco() > 0)
				or.setEffet_Notoriete(-10);
			else
				or.setEffet_Notoriete(10);
			
			// Si la loi passe
			if (nb_vote_pour > nb_vote_contre){	
				or.setEffet_Influence(10);
				//Sinon
			}else		
				or.setEffet_Influence(-10);
			

			try {
				mapper1.writeValue(sw, or);
				String s1 = sw.toString();
				ACLMessage message1 = new ACLMessage(ACLMessage.INFORM);
				message1.addReceiver(proposant);
				message1.setContent(s1);
				myAgent.send(message1);
			}
			catch(Exception ex) {
				System.out.println(ex.getMessage());
			}

			// Dans tous les cas on envoie un message � l'agent Mediateur pour le pr�venir que le vote est termin� (fin du tour).
			ACLMessage message2 = new ACLMessage(ACLMessage.REQUEST);
			message2.addReceiver(AMediateur);
			message2.setContent("La loi a fini d'�tre vot�e.");
			myAgent.send(message2);
		}
	}
}
