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
import Class_For_JSON.NumTour;
import ParlementSim.ParlementManager;
import agents.EnvironmentalAgent.WaitLoiRequest;
import agents.EnvironmentalAgent.WaitSondageRequest;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class MediateurAgent extends Agent{
	List<String> L_Actions = new ArrayList<String>();
	String action_choisit;
	int num_tour_actuel;
	int nb_tour_proposeloi = 3;
	int nb_tour_sondage = 2;
	boolean vote_en_cours;

	AID ALoi;
	AID AUtilisateur;
	AID ASondage;
	AID ASimulation;

	ParlementManager parl_mana = new ParlementManager();

	protected void setup() 
	{ 
		// Enregistrement auprès du DF
		DFAgentDescription dafd = new DFAgentDescription();
		dafd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Parlement");
		sd.setName("AMediateur");
		dafd.addServices(sd);
		try {
			DFService.register(this, dafd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

		num_tour_actuel = 0;
		vote_en_cours = false;

		addBehaviour(new OneShotBehaviour(){

			@Override
			public void action() {
				// On récupère les AID des agents nécessaires
				while (ALoi == null || AUtilisateur == null || ASondage == null || ASimulation == null){
					ALoi = parl_mana.getReceiver(myAgent, "Parlement", "ALoi");
					AUtilisateur = parl_mana.getReceiver(myAgent, "Parlement", "AUtilisateur");
					ASondage = parl_mana.getReceiver(myAgent, "Parlement", "ASondage");
					ASimulation = parl_mana.getReceiver(myAgent, "Parlement", "ASimulation");
				}

				addBehaviour(new TourFromSimulation()); // recéption d'un message marquant le début d'un nouveau tour de jeu de l'agent simulation (REQUEST)
				// --> Envoie d'un message a AUtilisateur avec la liste des actions possibles

				addBehaviour(new ActionFromUtilisateur()); // réception de l'action choisit par l'utilisateur (ACCEPT_PROPOSAL)
				// --> Suivant l'action possibilité de lui renvoyer un message pour lui demander (loi,personne visé, etc...)

				addBehaviour(new PrecisionFromUtilisateur()); // réception de l'information nécessaire pour faire l'action. (INFORM)
				// --> Suivant l'action traitement différents.

				addBehaviour(new FinVoteFromLoi()); // réception de la fin d'un vote de loi (REQUEST)
				// --> Suivant l'action traitement différents.

			}});



		System.out.println("Agent Mediateur créé : "+this.getLocalName());
	}

	class TourFromSimulation extends CyclicBehaviour{

		@Override
		public void action() {

			// On attend la reception d'un message de type INFORM venant de l'agent Simulation
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchSender(ASimulation)
					);
			ACLMessage message = myAgent.receive(mt);
			if (message != null){

				//On récupère la variable tour envoyé par l'agent Simulation et on la stocke
				NumTour NumTour = new NumTour();
				ObjectMapper mapper = new ObjectMapper();
				try {
					NumTour = mapper.readValue(message.getContent(), NumTour.class);
					num_tour_actuel = NumTour.getNum();
					System.out.println();
					System.out.println("Tour: "+num_tour_actuel);
				}
				catch(Exception ex) {
					System.out.println("EXCEPTION" + ex.getMessage());
				}

				// On construit la liste des actions a proposer au joueur suivant le numéro du tour
				L_Actions.clear();
				L_Actions.add("Aucune");
				action_choisit = null;
				vote_en_cours = false;

				if (num_tour_actuel % nb_tour_proposeloi == 0){
					L_Actions.add("Proposer une loi");
				}else{	// Si ce n'est pas à l'utilisateur de proposer une loi, alors il faut prévenir l'agent Loi qu'il demande à un député d'en proposer une

					//Envoie d'un message à l'Agent loi pour lancer une proposition de loi par un député
					if (ALoi != null) {
						ACLMessage message2 = new ACLMessage(ACLMessage.REQUEST);
						message2.addReceiver(ALoi);
						message2.setContent("Proposer une loi");
						myAgent.send(message2);
						vote_en_cours = true;
						//System.out.println(myAgent.getLocalName()+" -> "+ALoi.getLocalName() +" : " +message2.getContent() );
					}
				}

				if (num_tour_actuel % nb_tour_sondage == 0){
					L_Actions.add("Faire un sondage");
				}

				// Envoie d'un message à l'agent Utilisateur pour qu'il choisisse quelle actions faire.
				if (L_Actions.size() > 1)
					myAgent.addBehaviour(new ProposeActionsToUser());
			}else{
				block();
			}
		}
	}

	class ProposeActionsToUser extends OneShotBehaviour{

		// Task to do
		public void action() {

			// On serialise le message contenant la liste des actions possibles et on l'envoie à l'agent utilisateur
			if (AUtilisateur != null) {
				ObjectMapper mapper1 = new ObjectMapper();
				StringWriter sw = new StringWriter();
				try {
					mapper1.writeValue(sw, L_Actions);
					String s1 = sw.toString();
					ACLMessage message1 = new ACLMessage(ACLMessage.PROPOSE);
					message1.addReceiver(AUtilisateur);
					message1.setContent(String.valueOf(s1));
					myAgent.send(message1);
				}
				catch(Exception ex) {}
			}else{
				System.out.println(
						getLocalName() + "--> No receiver");
			}

		}
	}

	class ActionFromUtilisateur extends CyclicBehaviour{

		@Override
		public void action() {

			// On attend la reception d'un message de type ACCEPT_PROPOSAL venant du joueur
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
			ACLMessage message = myAgent.receive(mt);
			if (message != null){
				action_choisit = message.getContent();
				switch(action_choisit){				

				case "Proposer une loi":
					// Il faut demander à l'utilisateur de préciser quelle loi veut-il proposer.
					ACLMessage message1 = new ACLMessage(ACLMessage.REQUEST);
					message1.addReceiver(AUtilisateur);
					message1.setContent("Loi?");
					myAgent.send(message1);
					break;

				case "Faire un sondage":
					// Envoyer une demande à l'agent Sondage.
					if (ASondage != null) {
						ACLMessage message2 = new ACLMessage(ACLMessage.REQUEST);
						message2.addReceiver(ASondage);
						message2.setContent("Peux-tu me dire quelles-sont les variables d'environnement?");
						myAgent.send(message2);
					}
					break;	

				case "Aucune":
					if(vote_en_cours == false)
						// Envoyer une demande à l'agent Sondage.
						if (ASondage != null) {
							ACLMessage message2 = new ACLMessage(ACLMessage.INFORM);
							message2.addReceiver(ASimulation);
							message2.setContent("Fin du tour");
							myAgent.send(message2);
						}
					break;
				}
			}else{
				block();
			}
		}
	}

	class PrecisionFromUtilisateur extends CyclicBehaviour{

		@Override
		public void action() {

			// On attend la reception d'un message de type INFORM venant du joueur
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage message = myAgent.receive(mt);
			
			if (message != null){
				switch(action_choisit){				

				case "Proposer une loi":
					//Affichage de la loi choisit par l'utilisateur.
					Loi loi = new Loi();
					ObjectMapper mapper = new ObjectMapper();
					try {
						loi = mapper.readValue(message.getContent(), Loi.class);
					}
					catch(Exception ex) {
						System.out.println("EXCEPTION" + ex.getMessage());
					}

					System.out.println();
					System.out.println("Vous avez proposé cette loi : ");
					loi.affiche();
					
					//Envoie de la loi choisit par l'utilisateur à l'Agent Loi.
					if (ALoi != null) {
						ACLMessage message2 = new ACLMessage(ACLMessage.REQUEST);
						message2.addReceiver(ALoi);
						message2.setContent(message.getContent());
						myAgent.send(message2);
						vote_en_cours = true;
					}
					break;

				}
			}else{
				block();
			}
		}
	}
	class FinVoteFromLoi extends CyclicBehaviour{

		@Override
		public void action() {

			// On attend la reception d'un message de type REQUEST venant de l'agent LOI
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchSender(ALoi)
					);
			ACLMessage message = myAgent.receive(mt);
			if (message != null){
				ACLMessage message2 = new ACLMessage(ACLMessage.INFORM);
				message2.addReceiver(ASimulation);
				message2.setContent("Fin du tour");
				myAgent.send(message2);
				vote_en_cours = false;
			}else{
				block();
			}
		}
	}	

}
