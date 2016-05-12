package agents;

import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import com.fasterxml.jackson.databind.ObjectMapper;

import Class_For_JSON.MajEnv;
import ParlementSim.ParlementManager;
import agents.LoiAgent.AcceptLawOfDepute;
import agents.LoiAgent.ProposalLawOfDepute;
import agents.LoiAgent.RefuseLawOfDepute;
import agents.LoiAgent.RequestOfMediator;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import launcher.AgentLauncher;

public class SondageAgent extends Agent{

	AID AEnvironnement;
	AID AMediateur;

	ParlementManager parl_mana = new ParlementManager();

	protected void setup() 
	{ 
		// Enregistrement auprès du DF
		DFAgentDescription dafd = new DFAgentDescription();
		dafd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Parlement");
		sd.setName("ASondage");
		dafd.addServices(sd);
		try {
			DFService.register(this, dafd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
		System.out.println("Agent Sondage créé : "+this.getLocalName());

		addBehaviour(new OneShotBehaviour(){

			@Override
			public void action() {
				// On récupère les AID des agents nécessaires
				while (AMediateur == null || AEnvironnement == null){
					AMediateur = parl_mana.getReceiver(myAgent, "Parlement", "AMediateur");
					AEnvironnement = parl_mana.getReceiver(myAgent, "Monde", "AEnvironnement");				
				}
				addBehaviour(new RequestOfMediator()); // recéption d'un message demandant de faire un sondage sur l'environnement
				addBehaviour(new ReponseOfEnvironnement()); // réception de la réponse contenant les variables de l'environnement.

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
				// A la reception, l'agent demande a l'agent Environnement quelles sont ses variables.
				myAgent.addBehaviour(new AskForVariableEnv(message));
			}else{
				block();
			}
		}
	}

	class AskForVariableEnv extends OneShotBehaviour{
		private String mess;
		private ACLMessage message;

		// Constructor
		public AskForVariableEnv(ACLMessage message2) {
			this.message = message2;
			this.mess = message.getContent();
		}

		// Task to do
		public void action() {

			//Envoie d'un message a l'agent Environnement pour connaitre ses variables
			ACLMessage forward = message.createReply();
			forward.removeReceiver(message.getSender());
			forward.addReceiver(AEnvironnement);
			forward.setPerformative(ACLMessage.REQUEST);
			forward.setContent("Quelles-sont tes variables d'environnement?");
			myAgent.send(forward);

		}
	}

	class ReponseOfEnvironnement extends CyclicBehaviour{

		@Override
		public void action() {

			// On attend la reception d'un message de type REQUEST venant de l'agent Mediateur
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchSender(AEnvironnement)
					);
			ACLMessage message = myAgent.receive(mt);
			if (message != null){
				// A la reception, l'agent demande a l'agent Environnement quelles sont ses variables.
				myAgent.addBehaviour(new GiveVarToMediat(message));
			}else{
				block();
			}
		}
	}

	class GiveVarToMediat extends OneShotBehaviour{
		private String mess;
		private ACLMessage message;

		// Constructor
		public GiveVarToMediat(ACLMessage message2) {
			this.message = message2;
			this.mess = message.getContent();
		}

		// Task to do
		public void action() {
/*
			//Envoie d'un message a l'agent Environnement pour connaitre ses variables
			ACLMessage forward = message.createReply();
			forward.removeReceiver(message.getSender());
			forward.addReceiver(AMediateur);
			forward.setPerformative(ACLMessage.INFORM);
			forward.setContent(message.getContent());
			myAgent.send(forward);
			*/
			
			//Affichage des variables dans la console (a modifier -> tour d'après...).
			// On deserialise le message contenant les 2 valeurs de variables à modifer.
			ObjectMapper mapper = new ObjectMapper();
			try {
				MajEnv ort = mapper.readValue(mess,MajEnv.class);
				// Mise a jour des deux variables avec les valeurs du message
				System.out.println("");
				System.out.println("Résultat du sondage : ");
				System.out.println(ort.getContext_eco());
				System.out.println(ort.getQualite_vie());
			}
			catch(Exception ex) {
				System.out.println("EXCEPTION" + ex.getMessage());
			}
		}
	}
}
