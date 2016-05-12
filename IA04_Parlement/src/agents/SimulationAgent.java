package agents;

import java.io.StringWriter;
import com.fasterxml.jackson.databind.ObjectMapper;

import Class_For_JSON.NumTour;
import ParlementSim.ParlementManager;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.Agent;

public class SimulationAgent extends Agent{
	int tour;
	int FinJeu_tour;
	public boolean partie_finie;

	AID AEnvironnement = null;
	AID AMediateur = null;

	ParlementManager parl_mana = new ParlementManager();


	protected void setup() 
	{
		// Enregistrement auprès du DF
		DFAgentDescription dafd = new DFAgentDescription();
		dafd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Parlement");
		sd.setName("ASimulation");
		dafd.addServices(sd);
		try {
			DFService.register(this, dafd);
		}
		catch (FIPAException fe) {
			System.out.println(fe);
			fe.printStackTrace();
		}

		// Initialisation des variables
		tour = 0;
		FinJeu_tour = 365;
		partie_finie = false;
		AMediateur = AEnvironnement = null;

		addBehaviour(new OneShotBehaviour(){

			@Override
			public void action() {
				// On récupère les AID des agents nécessaires
				while (AMediateur == null || AEnvironnement == null){
					AMediateur = parl_mana.getReceiver(myAgent, "Parlement", "AMediateur");
					AEnvironnement = parl_mana.getReceiver(myAgent, "Monde", "AEnvironnement");				
				}
				addBehaviour(new WaitMessJoueur()); // Lancer le jeu (REQUEST)
				addBehaviour(new WaitMessMediateur()); // reception des fins de tour venant de l'agent Mediateur (INFORM)
				addBehaviour(new WaitMessEnvironnement());// reception possible d'un message d'environnement (fin de jeu partie perdue) (INFORM)
			}});



		System.out.println("Agent Simulation créé: "+this.getLocalName());
		
		System.out.println("Pour lancer le jeu, envoyez une REQUEST à l'agent "+this.getLocalName());
	} // fin Setup

	class NouvTourMediateur extends OneShotBehaviour{

		@Override
		public void action() {
			// Si la partie est terminée on détruit l'agent de Simulation
			if (tour == FinJeu_tour || partie_finie == true ){
				System.out.println("Mort de l'agent Simulation (partie terminée)");
				doDelete();
			}

			tour++;

			// On envoie un message serialise (num tour) a 'AMediateur' pour le prevenir qu'un nouveau tour commence.

			if (AMediateur != null) {
				ObjectMapper mapper1 = new ObjectMapper();
				StringWriter sw = new StringWriter();
				NumTour or = new NumTour(tour);
				try {
					mapper1.writeValue(sw, or);
					String s1 = sw.toString();
					ACLMessage message1 = new ACLMessage(ACLMessage.REQUEST);
					message1.addReceiver(AMediateur);
					message1.setContent(String.valueOf(s1));
					myAgent.send(message1);
					//System.out.println(myAgent.getLocalName()+" -> "+AMediateur.getLocalName() +" : " +message1.getContent() );
				}
				catch(Exception ex) {
					System.out.println(ex);
				}
			}else{
				System.out.println(
						getLocalName() + "--> No receiver");
			}
		}
	}

	class WaitMessMediateur extends CyclicBehaviour{

		@Override
		public void action() {
			// On attend la reception d'un message de type INFORM venant du mediateur
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchSender(AMediateur));
			ACLMessage message = myAgent.receive(mt);
			if (message != null){
				// A la reception, on lance un OneShotBehaviour qui s'occupe de lancer un nouveau tour de jeu.
				myAgent.addBehaviour(new NouvTourMediateur());
			}else{
				block();
			}
		}
	}

	class WaitMessEnvironnement extends CyclicBehaviour{

		@Override
		public void action() {
			// On attend la reception d'un message de type INFORM venant de l'Environnment
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchSender(AEnvironnement));
			ACLMessage message = myAgent.receive(mt);

			// Si on recoit un message de ce type alors on met fin au jeu... (partie perdue)
			if (message != null){
				System.out.println("Mort de l'agent Simulation (partie terminée)");
				doDelete();
			}else{
				block();
			}
		}
	}

	class WaitMessJoueur extends CyclicBehaviour{

		@Override
		public void action() {
			// On attend la reception d'un message de type Request venant du joueur
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage message = myAgent.receive(mt);

			if (message != null){
				myAgent.addBehaviour(new NouvTourMediateur());
			}else{
				block();
			}
		}
	}

}