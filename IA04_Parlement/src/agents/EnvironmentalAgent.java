package agents;


import java.io.StringWriter;

import com.fasterxml.jackson.databind.ObjectMapper;

import Class_For_JSON.MajEnv;
import ParlementSim.ParlementManager;
import agents.SimulationAgent.WaitMessEnvironnement;
import agents.SimulationAgent.WaitMessJoueur;
import agents.SimulationAgent.WaitMessMediateur;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.AID;
import jade.core.Agent;

public class EnvironmentalAgent extends Agent{
	// Variables de l'environnement
	float context_eco;
	float qualite_vie;
	
	AID ALoi;
	AID ASondage;
	AID ASimulation;
	
	ParlementManager parl_mana = new ParlementManager();

	protected void setup() 
	{ 

		// Enregistrement auprès du DF
		DFAgentDescription dafd = new DFAgentDescription();
		dafd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Monde");
		sd.setName("AEnvironnement");
		dafd.addServices(sd);
		try {
			DFService.register(this, dafd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

		System.out.println("Agent Environment créé :"+this.getLocalName());
		
		// Initialisation des varibles internes (statique au depart...)
		context_eco = 50; // 0 a 100
		qualite_vie = 50; // 0 a 100

		addBehaviour(new OneShotBehaviour(){

			@Override
			public void action() {
				// On récupère les AID des agents nécessaires
				while (ALoi == null || ASondage == null || ASimulation == null){
					ALoi = parl_mana.getReceiver(myAgent, "Parlement", "ALoi");
					ASondage = parl_mana.getReceiver(myAgent, "Parlement", "ASondage");
					ASimulation = parl_mana.getReceiver(myAgent, "Parlement", "ASimulation");
				}
				// Ajout des deux behaviours de réceptions de messages
				addBehaviour(new WaitLoiRequest());
				addBehaviour(new WaitSondageRequest());

			}});

	}

	class WaitLoiRequest extends CyclicBehaviour{

		@Override
		public void action() {
			// On attend la reception d'un message de type REQUEST venant du mediateur
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchSender(ALoi));
			ACLMessage message = myAgent.receive(mt);
			if (message != null){
				// A la reception, on lance un OneShotBehaviour qui s'occupe de la mise a jour des variables d'env (si besoin)
				myAgent.addBehaviour(new MajBehaviour(message));
			}else{
				block();
			}
		}
	}

	class WaitSondageRequest extends CyclicBehaviour{

		@Override
		public void action() {
			// On attend la reception d'un message de type REQUEST venant du mediateur
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchSender(ASondage));
			ACLMessage message = myAgent.receive(mt);
			if (message != null){
				// A la reception, on lance un OneShotBehaviour qui s'occupe de renvoyer ses 2 variables internes
				myAgent.addBehaviour(new RepSondageBehaviour(message));
			}else{
				block();
			}
		}
	}

	class MajBehaviour extends OneShotBehaviour{
		private String mess;
		private ACLMessage message;

		// Constructor
		public MajBehaviour(ACLMessage message2) {
			this.message = message2;
			this.mess = message.getContent();
		}

		// Task to do
		public void action() {
			// On deserialise le message contenant les 2 valeurs de variables à modifer.
			ObjectMapper mapper = new ObjectMapper();
			try {
				MajEnv ort = mapper.readValue(mess,MajEnv.class);
				// Mise a jour des deux variables avec les valeurs du message
				context_eco = context_eco + ort.getContext_eco();
				qualite_vie = qualite_vie + ort.getQualite_vie();
			}
			catch(Exception ex) {
				System.out.println("EXCEPTION" + ex.getMessage());
			}

			// Juste pour debug
			System.out.println("");
			System.out.println(".....Debug....");
			System.out.println("Eco :"+context_eco);
			System.out.println("Vie :"+qualite_vie);

			// Si les valeurs sont en dessous d'un certain seuil... 
			if (context_eco < 10 || qualite_vie < 10 || (context_eco + qualite_vie)/2 < 20 ){

				// On affiche le resultat (perdu)
				System.out.println("Vous avez perdu...");
				System.out.println("Eco :"+context_eco);
				System.out.println("Vie :"+qualite_vie);


				// On envoie un message a 'ASimulation' pour le prevenir que la partie est terminée.
				if (ASimulation != null) {
					ACLMessage message1 = new ACLMessage(ACLMessage.INFORM);
					message1.addReceiver(ASimulation);
					message1.setContent("Le joueur a perdu, merci d'arreter la partie");
					myAgent.send(message1);
				}else{
					System.out.println(
							getLocalName() + "--> No receiver");
				}
			}
		}
	}

	class RepSondageBehaviour extends OneShotBehaviour{
		private ACLMessage message;

		// Constructor
		public RepSondageBehaviour(ACLMessage message2) {
			this.message = message2;
		}

		// Task to do
		public void action() {
			// On serialise le message contenant les 2 valeurs de variables à modifer.
			ACLMessage message1 =  message.createReply();
			ObjectMapper mapper1 = new ObjectMapper();
			StringWriter sw = new StringWriter();

			MajEnv or = new MajEnv(context_eco,qualite_vie);
			try {
				mapper1.writeValue(sw, or);
				String s1 = sw.toString();
				// On renvoie le message avec nos 2 valeurs à l'agent Sondage (reply)
				message1.setPerformative(ACLMessage.INFORM);
				message1.setContent(s1);
				myAgent.send(message1);
			}
			catch(Exception ex) {
				System.out.println(ex.getMessage());
			}
		}
	}
}