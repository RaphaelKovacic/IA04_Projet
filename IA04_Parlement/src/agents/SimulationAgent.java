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

/**
 * <b>SimulationAgent est la classe représentant l'agent simulation dans notre
 * SMA Parlement.</b>
 * <p>
 * L'agent simulation possède les attributs suivants
 * <ul>
 * <li>Le numéro du tour en cours de jeu.</li>
 * <li>Le nombre de tour max d'une partie de notre jeu.</li>
 * <li>Un booléen représentant l'état de la partie : finie ou en cours.</li>
 * <li>L'AID de l'agent environnement pour les mêmes raisons qu'au dessus</li>
 * <li>L'AID de l'agent médiateur pour les mêmes raisons qu'au dessus</li>
 * <li>Le manager du parlement pour recevoir les AID ci-dessus</li>
 * </ul>
 * </p>
 * <p>
 * La première classe sert à l'instanciation de l'agent de simulation. Les
 * comportements de l'agent simulation sont spécifiés dans les quatre classes
 * suivantes
 * </p>
 * 
 * 
 * @author Benoit
 * @version 2.1
 */
@SuppressWarnings("serial")
public class SimulationAgent extends Agent {

	/**
	 * Le numéro du tour actuel. Variable.
	 * 
	 * @see setup()
	 */
	int tour;

	/**
	 * Le nombre tour maximum dans une partie. Constant.
	 * 
	 * @see setup()
	 */
	int FinJeu_tour;

	/**
	 * L'état actuel de la partie. Variable.
	 * 
	 * @see setup()
	 */
	public boolean partie_finie;

	/**
	 * L'AID de l'agent environnement. Non modifiable
	 * 
	 * @see setup()
	 */
	AID AEnvironnement = null;

	/**
	 * L'AID de l'agent médiateur. Non modifiable
	 * 
	 * @see setup()
	 */
	AID AMediateur = null;

	/**
	 * Le manager du parlement. Non modifiable
	 * 
	 * @see setup()
	 */
	ParlementManager parl_mana = new ParlementManager();

	/**
	 * Méthode d'instanciation (appelée à la création) de notre agent Simulation
	 * <p>
	 * Lors du lancement de notre plateforme JADE, l'agent Simulation est créé
	 * grâce à cette méthode setup()
	 * </p>
	 */
	protected void setup() {
		// Enregistrement auprès du DF
		DFAgentDescription dafd = new DFAgentDescription();
		dafd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Parlement");
		sd.setName("ASimulation");
		dafd.addServices(sd);
		try {
			DFService.register(this, dafd);
		} catch (FIPAException fe) {
			System.out.println(fe);
			fe.printStackTrace();
		}

		// Initialisation des variables
		tour = 0;
		FinJeu_tour = 365;
		partie_finie = false;
		AMediateur = AEnvironnement = null;

		addBehaviour(new OneShotBehaviour() {

			@Override
			public void action() {
				// On récupère les AID des agents nécessaires
				while (AMediateur == null || AEnvironnement == null) {
					AMediateur = parl_mana.getReceiver(myAgent, "Parlement", "AMediateur");
					AEnvironnement = parl_mana.getReceiver(myAgent, "Monde", "AEnvironnement");
				}
				addBehaviour(new WaitMessJoueur()); // Lancer le jeu (REQUEST)
				
				addBehaviour(new WaitMessMediateur()); // reception des fins de tour venant de
													   // l'agent Mediateur (INFORM)
				
				addBehaviour(new WaitMessEnvironnement()); // reception possible d'un message
														  // d'environnement (fin de jeu
														  // partie perdue) (INFORM)
			}
		});

		System.out.println("Agent Simulation créé: " + this.getLocalName());

		System.out.println("Pour lancer le jeu, envoyez une REQUEST à l'agent " + this.getLocalName());
	} // fin Setup

	/**
	 * <b>NouvTourMediateur est le premier Behaviour de l'agent Simulation</b>
	 * <p>
	 * Il est de type OneShot. Notre agent Simulation va utiliser ce Behaviour
	 * pour créer un nouveau tour. Il est instancier lorsque l'agent simulation
	 * reçoit un message de fin de tour par l'agent mediateur ou une requete du
	 * joueur.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Crée un nouveau tour de jeu.
	 * <p>
	 * 
	 * Cette action faite suite à la fin d'un tour.
	 * 
	 * @see SimulationAgent#WaitMessMediateur
	 * 
	 * @author Benoit
	 * @version : 1.2
	 */
	class NouvTourMediateur extends OneShotBehaviour {

		@Override
		public void action() {
			// Si la partie est terminée on détruit l'agent de Simulation
			if (tour == FinJeu_tour || partie_finie == true) {
				System.out.println("Mort de l'agent Simulation (partie terminée)");
				doDelete();
			}

			tour++;

			// On envoie un message serialise (num tour) a 'AMediateur' pour le
			// prevenir qu'un nouveau tour commence.

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
					// System.out.println(myAgent.getLocalName()+" ->
					// "+AMediateur.getLocalName() +" : " +message1.getContent()
					// );
				} catch (Exception ex) {
					System.out.println(ex);
				}
			} else {
				System.out.println(getLocalName() + "--> No receiver");
			}
		}
	}

	/**
	 * <b>WaitMessMediateur est le second Behaviour de l'agent Simulation</b>
	 * <p>
	 * Il est de type Cyclic. Notre agent Simulation est constamment dans
	 * l'attente de recevoir un message de la part du médiateur. Il va ensuite
	 * instancier le behaviour correspondant si un nouveau tour doit être crée.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Réceptionne une communication par
	 * message de la part du médiateur.
	 * <p>
	 * 
	 * 
	 * @see SimulationAgent#NouvTourMediateur
	 * 
	 * @author Benoit
	 * @version : 1.2
	 */
	class WaitMessMediateur extends CyclicBehaviour {

		@Override
		public void action() {
			// On attend la reception d'un message de type INFORM venant du
			// mediateur
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchSender(AMediateur));
			ACLMessage message = myAgent.receive(mt);
			if (message != null) {
				// A la reception, on lance un OneShotBehaviour qui s'occupe de
				// lancer un nouveau tour de jeu.
				myAgent.addBehaviour(new NouvTourMediateur());
			} else {
				block();
			}
		}
	}

	/**
	 * <b>WaitMessMediateur est le troisième Behaviour de l'agent Simulation</b>
	 * <p>
	 * Il est de type Cyclic. Notre agent Simulation est constamment dans
	 * l'attente de recevoir un message INFORM de la part de l'environnement.
	 * Lors de la réception de ce message c'est que la parti est terminée.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Réceptionne un message de fin de
	 * la partie de la part de l'environnement.
	 * <p>
	 * 
	 * Il s'occupe de se tuer lui même.
	 * 
	 * @author Benoit
	 * @version : 1.2
	 */
	class WaitMessEnvironnement extends CyclicBehaviour {

		@Override
		public void action() {
			// On attend la reception d'un message de type INFORM venant de
			// l'Environnment
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchSender(AEnvironnement));
			ACLMessage message = myAgent.receive(mt);

			// Si on recoit un message de ce type alors on met fin au jeu...
			// (partie perdue)
			if (message != null) {
				System.out.println("Mort de l'agent Simulation (partie terminée)");
				doDelete();
			} else {
				block();
			}
		}
	}

	/**
	 * <b>WaitMessMediateur est le quatirème et dernier Behaviour de l'agent
	 * Simulation</b>
	 * <p>
	 * Il est de type Cyclic. Notre agent Simulation est constamment dans
	 * l'attente de recevoir un message de type REQUEST de la part du joueur. Il
	 * va ensuite instancier le behaviour correspondant si un nouveau tour doit
	 * être crée.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Réceptionne une communication par
	 * message de la part du joueur.
	 * <p>
	 * 
	 * 
	 * @see SimulationAgent#NouvTourMediateur
	 * 
	 * @author Benoit
	 * @version : 1.2
	 */
	class WaitMessJoueur extends CyclicBehaviour {

		@Override
		public void action() {
			// On attend la reception d'un message de type Request venant du
			// joueur
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage message = myAgent.receive(mt);

			if (message != null) {
				myAgent.addBehaviour(new NouvTourMediateur());
			} else {
				block();
			}
		}
	}

}