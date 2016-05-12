package agents;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import Class_For_JSON.Loi;
import Class_For_JSON.MajDepute;
import Class_For_JSON.NumTour;
import ParlementSim.ParlementManager;
import agents.SondageAgent.ReponseOfEnvironnement;
import agents.SondageAgent.RequestOfMediator;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public class UtilisateurAgent extends Agent{
	//Caractéristiques dynamiques
	float Influence;
	float Popularite;
	float Notoriete;
	float Credibilite;
	String Parti_Politique;
	
	//Caractéristiques statiques
	float Charisme;
	
	//Liste de tous les partis existants.
	List<String> L_Parti;
	
	AID ALoi;
	AID AMediateur;
	
	ParlementManager parl_mana = new ParlementManager();

	protected void setup() 
	{ 
		// Enregistrement auprès du DF
		DFAgentDescription dafd = new DFAgentDescription();
		dafd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Parlement");
		sd.setName("AUtilisateur");
		dafd.addServices(sd);
		try {
			DFService.register(this, dafd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
		//Initialisation de la liste des partis #Divergente
		L_Parti = new ArrayList<String>();
		L_Parti.add("Altruistes");L_Parti.add("Erudits");L_Parti.add("Audacieux");
		L_Parti.add("Sinceres");L_Parti.add("Fraternels");
		
		// Initialisation des caractéristiques dynamiques
		Credibilite = (float)(Math.random() * 100); // Entre 0 et 100 au départ
		Influence = (float)(Math.random() * 70); // Entre 0 et 70 au départ
		Parti_Politique = L_Parti.get((int)(Math.random() * L_Parti.size())); // aléatoire
		Popularite = (float)(Math.random() * 100); // Entre 0 et 100 au départ
		Notoriete =  (float)(Math.random() * 100); // Entre 0 et 100 au départ
		
		// Initialisation des caractéristiques statiques
		Charisme = (float)(Math.random() * 100); // Entre 0 et 100 au départ

		System.out.println("Agent Utilisateur créé : "+this.getLocalName());

		addBehaviour(new OneShotBehaviour(){

			@Override
			public void action() {
				// On récupère les AID des agents nécessaires
				while (AMediateur == null || ALoi == null){
					AMediateur = parl_mana.getReceiver(myAgent, "Parlement", "AMediateur");
					ALoi = parl_mana.getReceiver(myAgent, "Parlement", "ALoi");				
				}
				addBehaviour(new LActionsFromMediateur()); // recéption d'un message proposant de choisir une action parmis plusieurs actions.
				addBehaviour(new PrecisonActionFromMediateur()); // recéption d'un message demandant des précisions sur l'action choisit
				addBehaviour(new RequestToVote()); // réception d'un message proventant de l'ALoi demandant de voter pour une loi
				addBehaviour(new RequestToModifCara()); // réception d'un message proventant de l'ALoi demandant de modifier ses caract
			}});
		

	}

	class LActionsFromMediateur extends CyclicBehaviour{

		@Override
		public void action() {

			// On attend la reception d'un message de type PROPOSE venant de l'agent Mediateur contenant la Liste des Actions possibles à ce tour
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
					MessageTemplate.MatchSender(AMediateur)
					);
			ACLMessage message = myAgent.receive(mt);
			if (message != null){
				// On désérialise la liste d'actions.
				List<String> L_Actions = new ArrayList<String>();
				ObjectMapper mapper = new ObjectMapper();
				try {
					L_Actions = mapper.readValue(message.getContent(), L_Actions.getClass());
					
					//Ecrit sur la console
					System.out.println();
					System.out.println("Voici la liste des actions possibles ce tour-ci");
					System.out.println(L_Actions.toString());
					System.out.println("Renvoyer un message (ACCEPT_PROPOSAL) à l'agent Mediateur avec pour contenu une de ces actions");
				}
				catch(Exception ex) {
					System.out.println("EXCEPTION" + ex.getMessage());
				}
				
			}else{
				block();
			}
		}
	}
	
	class PrecisonActionFromMediateur extends CyclicBehaviour{

		@Override
		public void action() {

			// On attend la reception d'un message de type Request venant de l'agent Mediateur (Demande d'informations suivant les actions choisit)
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchSender(AMediateur)
					);
			ACLMessage message = myAgent.receive(mt);
			if (message != null){
				switch(message.getContent()){
				
					case "Loi?": 
						//Ecrit sur la console
						System.out.println();
						System.out.println("Précisez votre loi à l'agent Médiateur (INFORM).");
						System.out.println("La forme du message doit être comme suit : ");
						System.out.println("{\"id\":1,\"description\":\"description\",\"effet_qualite_vie\":-5.0,\"effet_context_eco\":5.0,\"proposant\":\"ADepute1\",\"influence\":4.639233,\"charisme\":13.48552,\"popularite\":26.205364,\"notoriete\":47.22229,\"l_PartiPolitique\":[\"Erudits\"]}");
						break;
				}
				
			}else{
				block();
			}
		}
	}

	class RequestToVote extends CyclicBehaviour{

		@Override
		public void action() {

			// On attend la reception d'un message de type REQUEST venant de l'agent Loi
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
					MessageTemplate.MatchSender(ALoi));
			ACLMessage message = myAgent.receive(mt);
			if (message != null){			
				// L'utilisateur doit voter

				//Récupération de loi (On deserialise le message)
				Loi loi_en_cours = new Loi();
				ObjectMapper mapper = new ObjectMapper();
				try {
					loi_en_cours = mapper.readValue(message.getContent(), Loi.class);
				}
				catch(Exception ex) {
					System.out.println("EXCEPTION" + ex.getMessage());
				}
				// //Ecrit sur la console
				System.out.println();
				System.out.println("Vous devez voter pour la loi ci-desous");
				loi_en_cours.affiche();
				System.out.println("Merci de répondre à l'Agent Loi (ACCEPT_PROPOSAL ou REJECT_PROPOSAL)");
				
			}else{
				block();
			}
		}
	}
	
	class RequestToModifCara extends CyclicBehaviour{

		@Override
		public void action() {

			// On attend la reception d'un message de type INFORM venant de l'agent Loi
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchSender(ALoi));
			ACLMessage message = myAgent.receive(mt);
			if (message != null){
				// A la reception, on met à jour les caract de l'agent
				// On deserialise le message contenant les valeurs de variables à modifer.
				ObjectMapper mapper = new ObjectMapper();
				try {
					MajDepute ort = mapper.readValue(message.getContent(),MajDepute.class);
					// Mise a jour des  variables avec les valeurs du message
					Influence += ort.getEffet_Influence();
					Popularite += ort.getEffet_Popularite();
					Notoriete+= ort.getEffet_Notoriete();
					Credibilite += ort.getEffet_Credibilite();
					
					if (Influence< 0)
						Influence = 0;
					if (Popularite< 0)
						Popularite = 0;
					if (Notoriete< 0)
						Notoriete = 0;
					if (Credibilite< 0)
						Credibilite = 0;
					
				}
				catch(Exception ex) {
					System.out.println("EXCEPTION" + ex.getMessage());
				}

				// Juste pour debug
				System.out.println("");
				System.out.println(".....Debug....");
				System.out.println(myAgent.getLocalName());
				System.out.println("Influence :"+Influence);
				System.out.println("Popularite :"+Popularite);
				System.out.println("Notoriete :"+Notoriete);
				System.out.println("Credibilite :"+Credibilite);
			}else{
				block();
			}
		}
	}
}