package agents;

import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import java.util.List;



import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


import Class_For_JSON.Loi;
import Class_For_JSON.NumTour;

import ParlementSim.ParlementManager;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
public class MediateurAgent extends Agent{
	List<String> L_Actions = new ArrayList<String>();
	String action_choisit;
	int num_tour_actuel;
	int nb_tour_proposeloi = 1;
	int nb_tour_sondage = 1;
	int nb_tour_changerparti = 1;
	int nb_tour_sondage_loi = 1;
	boolean vote_en_cours;
	
	List<Loi> Loi_possibles_user = new ArrayList<Loi>();
	Loi utilisateur_information = new Loi();

	AID ALoi;
	AID AUtilisateur;
	AID ASondage;
	AID ASimulation;
	AID AKB;

	ParlementManager parl_mana = new ParlementManager();
	
	Loi loi_choisie = new Loi();

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
				while (ALoi == null || AUtilisateur == null || ASondage == null || ASimulation == null || AKB == null){
					
					ALoi = parl_mana.getReceiver(myAgent, "Parlement", "ALoi");
					AUtilisateur = parl_mana.getReceiver(myAgent, "Parlement", "AUtilisateur");
					ASondage = parl_mana.getReceiver(myAgent, "Parlement", "ASondage");
					ASimulation = parl_mana.getReceiver(myAgent, "Parlement", "ASimulation");
					AKB = parl_mana.getReceiver(myAgent, "KB", "AKB");
				}

				addBehaviour(new TourFromSimulation()); // recéption d'un message marquant le début d'un nouveau tour de jeu de l'agent simulation (REQUEST)
				// --> Envoie d'un message a AUtilisateur avec la liste des actions possibles

				addBehaviour(new ActionFromUtilisateur()); // réception de l'action choisit par l'utilisateur (ACCEPT_PROPOSAL)
				// --> Suivant l'action possibilité de lui renvoyer un message pour lui demander (loi,personne visée, etc...)

				addBehaviour(new LawProposalFromUser()); // réception de l'information nécessaire pour faire l'action. (INFORM)
				// --> Suivant l'action traitement différents.

				addBehaviour(new FinVoteFromLoi()); // réception de la fin d'un vote de loi (REQUEST)
				// --> Suivant l'action traitement différents.
				
				addBehaviour(new ReceiveCaracFromUtilisateur()); // Récéption d'un message de type CONFIRM de la part de l'agent utilisateur avec comme contenu du message ses informations sérialisées dans une loi incomplète.

				addBehaviour(new ReceiveUserParty()); // Réception du INFORM_REF avec le nom du parti de l'user avant de requêter KB
				
				addBehaviour(new ReceiveLawsFromKB()); // Recevoir loi envoyées par KB pour l'user
			
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
				}

				if (num_tour_actuel % nb_tour_sondage == 0){
					L_Actions.add("Faire un sondage");
				}
				
				if (num_tour_actuel % nb_tour_changerparti == 0){
					L_Actions.add("Changer de parti");
				}
				
				if (num_tour_actuel % nb_tour_sondage_loi == 0){
					L_Actions.add("Avis du parlement");
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
				
				if(!action_choisit.equals("Proposer une loi") && !action_choisit.equals("Avis du parlement")){	// Si ce n'est pas à l'utilisateur de proposer une loi, alors il faut prévenir l'agent Loi qu'il demande à un député d'en proposer une

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
				
				switch(action_choisit){				
					
				case "Proposer une loi":
					// Il faut tout d'abord les informations de l"utilisateur et notamment son parti pour ensuite requête KB.
					
					ACLMessage message1 = new ACLMessage(ACLMessage.QUERY_REF);
					message1.addReceiver(AUtilisateur);
					message1.setContent("Vos informations sur vous je vous prie");
					message1.setConversationId("Proposition de loi");
					myAgent.send(message1);
					break;
					
				case "Avis du parlement":
					// Il faut tout d'abord les informations de l"utilisateur et notamment son parti pour ensuite requête KB.
					
					ACLMessage message4 = new ACLMessage(ACLMessage.QUERY_REF);
					message4.addReceiver(AUtilisateur);
					message4.setContent("Vos informations sur vous je vous prie");
					message4.setConversationId("Demande de sondage");
					myAgent.send(message4);
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
					
				case "Changer de parti":
					// Il faut demander à l'utilisateur de changer de parti et d'adapter ainsi ses variables d'environnement
					ACLMessage message3 = new ACLMessage(ACLMessage.REQUEST);
					message3.addReceiver(AUtilisateur);
					message3.setContent("Change de parti");
					myAgent.send(message3);
					break;

				case "Aucune":
					if(vote_en_cours == false){
						// Terminer le tour en informant l'agent de simulation
						if (ASondage != null) {
							ACLMessage message2 = new ACLMessage(ACLMessage.INFORM);
							message2.addReceiver(ASimulation);
							message2.setContent("Fin du tour");
							myAgent.send(message2);
						}
					}
					else
						System.out.println("N'oubliez pas de voter pour la loi proposée pour finir le tour. Envoyer un message à l'agent LOI avec ACCEPT OU REJECT PROPOSAL.");
						
					break;
				}
			}else{
				block();
			}
		}
	}

	class LawProposalFromUser extends CyclicBehaviour{

		@Override
		public void action() {

			// On attend la reception d'un message de type INFORM_IF venant du joueur
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM_IF);
			ACLMessage message = myAgent.receive(mt);
			
			if (message != null && message.getConversationId() != null && ( message.getConversationId().equalsIgnoreCase("Proposition de loi" ) || message.getConversationId().equalsIgnoreCase("Demande de sondage") )){
					
				//Récupère l'id contenu dans le message de l'utilisateur
				String id_choisi_string = message.getContent();
				int id = Integer.parseInt(id_choisi_string);
				
				//On récpuère la loi correspondante grâce à notre tableau de loi interne
				Loi loi_choisie = new Loi(); 
				
				for( int y = 0; y < Loi_possibles_user.size(); y++) 
			       {
					Loi current_law = Loi_possibles_user.get(y);
					if(current_law.getId() == id)
						loi_choisie = current_law;
						
			       }
			
				//On affiche la loi choisit par l'utilisateur
				System.out.println();
				System.out.println("Vous avez proposé cette loi : ");
				loi_choisie.affiche_a_utilisateur();
					
				//On seriaalize la loi choisie puis on l'envoie à l'agent loi		
				ObjectMapper mapper = new ObjectMapper();
				StringWriter sw = new StringWriter();
					
				try {
					mapper.writeValue(sw, loi_choisie);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String s = sw.toString();
				
				if (ALoi != null) {
					ACLMessage message2 = new ACLMessage(ACLMessage.REQUEST);
					message2.addReceiver(ALoi);
					message2.setConversationId(message.getConversationId());
					message2.setContent(s);
					myAgent.send(message2);
					vote_en_cours = true;

				}
		}
		else
			block();
			
		
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
				
				//Si on vient de faire un sondage, on a libéré l'agent loi et les députés de ce sondage, on peut faire voter la loi pour finir le tour
				if(message.getConversationId() != null && message.getConversationId().equalsIgnoreCase("Demande de sondage")){
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
				
				//Sinon, on vient de voter une loi, tour fini
				else
				{
					ACLMessage message2 = new ACLMessage(ACLMessage.INFORM);
					message2.addReceiver(ASimulation);
					message2.setContent("Fin du tour");
					myAgent.send(message2);
					vote_en_cours = false;
				
				}
			}
			
			else
				block();
			
		}
	}
	
	class ReceiveCaracFromUtilisateur extends CyclicBehaviour{

		@Override
		public void action() {

			// On attend la reception d'un message de type CONFIRM venant de l'agent Utilisateur
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
					MessageTemplate.MatchSender(AUtilisateur)
					);
			ACLMessage message = myAgent.receive(mt);
			if (message != null){
				
				// On deserialise le message contenant la loi incomplète (pour l'instant seulement les info de l'user)
				ObjectMapper mapper = new ObjectMapper();
				try {
					loi_choisie = mapper.readValue(message.getContent(),Loi.class);
					
					System.out.println("Debug : affichage de la loi incomplète seulement info de l'utilisateur reçue et stockées par médiateur : ");
					loi_choisie.affiche();
				}
				catch(Exception ex) {
					System.out.println("EXCEPTION" + ex.getMessage());
				}
			}else{
				block();
			}
		}
	
	}
	
	class ReceiveUserParty extends CyclicBehaviour{

		@Override
		public void action() {

			// On attend la reception d'un message de type INFORM_REF venant de l'agent utilisateur
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM_REF),
					MessageTemplate.MatchSender(AUtilisateur));
			
			ACLMessage message = myAgent.receive(mt);
			if (message != null){
				
				//On deserialise la loi reçue qui contient les informations de l'utilisateur
				ObjectMapper mapper = new ObjectMapper();
				
				try {
					//On la stocke localement (elle nous servira plus tard)
					utilisateur_information = mapper.readValue(message.getContent(),Loi.class);
					
					//TODO Delete
					System.out.println("Loi utilisateur (ses infos): ");
					utilisateur_information.affiche();
					
					//Petit test pour voir si on est toujours dans l'action de proposition d'une loi
					if(action_choisit.equalsIgnoreCase("Proposer une loi") || action_choisit.equalsIgnoreCase("Avis du parlement")){
						
						//On envoie le message à KB avec le parti de l'utilisateur
						ACLMessage message2 = new ACLMessage(ACLMessage.REQUEST);
						message2.addReceiver(AKB);
						message2.setConversationId(message.getConversationId());
						message2.setContent(utilisateur_information.getL_PartiPolitique().get(0));
						myAgent.send(message2);
					}
					
					else {
						System.out.println("Il y a corruption, on est pas dans un tour de prposition de loi par l'user");
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
				
			}
			
			else{
				block();
			}
		}
	}
	
	class ReceiveLawsFromKB extends CyclicBehaviour{

		@Override
		public void action() {

			// On attend la reception d'un message de type INFORM venant de l'agent KB avec les lois possibles pour l'user
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchSender(AKB));
			
			ACLMessage message = myAgent.receive(mt);
			if (message != null){
				
				
				//Petit test pour voir si on est toujours dans l'action de proposition d'une loi
				if(action_choisit.equalsIgnoreCase("Proposer une loi") || action_choisit.equalsIgnoreCase("Avis du parlement")){
					
					//On déserialize la list de lois renvoyée et on la stocke dans KB
					
					System.out.println("---------------------------------------------------");
					System.out.println("--------------------DEBUG--------------------------");
					System.out.println("----CONTROLE DE LA LISTE DE LOI ENVOYÉE PAR KB-----");
					System.out.println("---------------------------------------------------");
					
					try {
					
						//Récupère le contenu du message envoyé par KB (liste de loi JSON)
						String content = message.getContent();
						
						//Stocke les lois envoyées par KB dans variable locale
						Loi_possibles_user = new ObjectMapper().readValue(content, new TypeReference<List<Loi>>() { });
						
						
						for( int y = 0; y < Loi_possibles_user.size(); y++) 
					       {
							// On incorpore les informations de l'utilisateur dans chaque loi grâce à la variable locale prévue à cette effet
							Loi_possibles_user.get(y).setL_PartiPolitique(utilisateur_information.getL_PartiPolitique());
							Loi_possibles_user.get(y).setProposant(utilisateur_information.getProposant());
							Loi_possibles_user.get(y).setInfluence(utilisateur_information.getInfluence());
							Loi_possibles_user.get(y).setCharisme(utilisateur_information.getInfluence());
							Loi_possibles_user.get(y).setPopularite(utilisateur_information.getPopularite());
							Loi_possibles_user.get(y).setNotoriete(utilisateur_information.getNotoriete());
							
							//TODO Delete : Affichage pour vérification
							Loi_possibles_user.get(y).affiche();
					       }
						
						//Les serializer et les envoyer avec le bon performative à Utilisateur qui va les afficher :D
						
						//Serialization de la liste de lois (voir KB)
						ObjectMapper mapper1 = new ObjectMapper();
						StringWriter sw1 = new StringWriter();
	
						mapper1.writeValue(sw1, Loi_possibles_user);
						String s1 = sw1.toString();
						
						//Envoyer à utilisateur dans un message de type
						ACLMessage message2 = new ACLMessage(ACLMessage.QUERY_IF);
						message2.addReceiver(AUtilisateur);
						message2.setConversationId(message.getConversationId());
						message2.setContent(s1);
						myAgent.send(message2);
					}
					catch(Exception ex) {
						System.out.println("EXCEPTION" + ex.getMessage());
					}
				}
				
				else {
					System.out.println("Il y a corruption, on est pas dans un tour de proposition de loi par l'user");
				}
			}
			
			else{
				block();
			}
		}
	}
	
	
	

}
