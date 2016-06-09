package agents;

import java.io.StringWriter;

import com.fasterxml.jackson.databind.ObjectMapper;

import Class_For_JSON.MajEnv;
import ParlementSim.ParlementManager;

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

/**
 * <b>EnvironnementalAgent est la classe représentant l'agent environnement dans
 * notre SMA Parlement.</b>
 * <p>
 * L'agent environnement possède les attributs suivants :
 * <ul>
 * <li>La valeur du contexte économique actuel du pays.</li>
 * <li>La valeur du contexte social (qualité de vie) actuel du pays.</li>
 * <li>L'AID de l'agent loi pour pouvoir rapidement communiquer avec lui</li>
 * <li>L'AID de l'agent sondage pour les mêmes raisons qu'au dessus</li>
 * <li>L'AID de l'agent simulation pour les mêmes raisons qu'au dessus</li>
 * <li>Le manager du parlement pour recevoir les AID ci-dessus</li>
 * </ul>
 * </p>
 * <p>
 * La première classe sert à l'instanciation de l'agent. Les comportements de
 * l'agent Environnement sont spécifiés dans les quatre classes suivantes.
 * </p>
 *
 *
 * @author Benoit
 * @version 2.1
 */

@SuppressWarnings("serial")
public class EnvironmentalAgent extends Agent {

	/**
	 * L'état du contexte économique du pays dans un flottant. Variable.
	 *
	 * @see #setup()
	 */
	float context_eco;

	/**
	 * L'état du contexte social du pays dans un flottant. Variable.
	 *
	 * @see #setup()
	 */
	float qualite_vie;

	/**
	 * L'AID de l'agent loi. Non modifiable
	 *
	 * @see #setup()
	 */
	AID ALoi;

	/**
	 * L'AID de l'agent sondage. Non modifiable
	 *
	 * @see #setup()
	 */
	AID ASondage;

	/**
	 * L'AID de l'agent simulation. Non modifiable
	 *
	 * @see #setup()
	 */
	AID ASimulation;

	/**
	 * Le manager du parlement. Non modifiable
	 *
	 * @see #setup()
	 */
	ParlementManager parl_mana = new ParlementManager();

	/**
	 * Méthode d'instanciation (appelée à la création) de notre agent
	 * Environnement
	 * <p>
	 * Lors du lancement de notre plateforme JADE, l'agent Environnement est
	 * créé grâce à cette méthode setup()
	 * </p>
	 */
	protected void setup() {

		// Enregistrement auprès du DF
		DFAgentDescription dafd = new DFAgentDescription();
		dafd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Monde");
		sd.setName("AEnvironnement");
		dafd.addServices(sd);
		try {
			DFService.register(this, dafd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		System.out.println("Agent Environment créé :" + this.getLocalName());

		// Initialisation des varibles internes (statique au depart...)
		context_eco = 50; // 0 a 100
		qualite_vie = 50; // 0 a 100

		addBehaviour(new OneShotBehaviour() {

			@Override
			public void action() {
				// On récupère les AID des agents nécessaires
				while (ALoi == null || ASondage == null || ASimulation == null) {
					ALoi = parl_mana.getReceiver(myAgent, "Parlement", "ALoi");
					ASondage = parl_mana.getReceiver(myAgent, "Parlement", "ASondage");
					ASimulation = parl_mana.getReceiver(myAgent, "Parlement", "ASimulation");
				}
				// Ajout des deux behaviours de réceptions de messages
				addBehaviour(new WaitLoiRequest());
				addBehaviour(new WaitSondageRequest());

			}
		});

	}

	/**
	 * <b>WaitLoiRequest est le premier Behaviour de l'agent Environnement.</b>
	 * <p>
	 * Il est de type Cyclic. Notre agent Environnement est en constante attente
	 * d'une requête REQUEST de l'agent loi qui lui demande de mettre à jour ses
	 * variables.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Récupérer le message demandant
	 * une mise à jour de ses variables.
	 * <p>
	 *
	 * Cette action faite suite au vote d'une loi.
	 *
	 * <p>
	 * Ce Behaviour va instancier un OneShotBehaviour qui sera en charge de la
	 * suite du processus.
	 * </p>
	 *
	 * @see MajBehaviour
	 *
	 * @author Benoit
	 * @version : 1.2
	 */
	class WaitLoiRequest extends CyclicBehaviour {

		@Override
		public void action() {
			// On attend la reception d'un message de type REQUEST venant du
			// mediateur
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchSender(ALoi));
			ACLMessage message = myAgent.receive(mt);
			if (message != null) {
				// A la reception, on lance un OneShotBehaviour qui s'occupe de
				// la mise a jour des variables d'env (si besoin)
				myAgent.addBehaviour(new MajBehaviour(message));
			} else {
				block();
			}
		}
	}

	/**
	 * <b>WaitSondageRequest est le second Behaviour de l'agent
	 * Environnement.</b>
	 * <p>
	 * Il est de type Cyclic. Notre agent Environnement est en constante attente
	 * d'une requête REQUEST de l'agent sondage qui lui demande la valeur de ses
	 * variables.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Récupèrer le message demandant de
	 * fournir les valeurs de ses variables.
	 * <p>
	 *
	 * Cette action faite suite à la demande de sondage de la part de
	 * l'utilisateur.
	 *
	 * <p>
	 * Ce Behaviour va instancier un OneShotBehaviour qui sera en charge de la
	 * suite du processus.
	 *
	 *
	 * @see RepSondageBehaviour
	 *
	 *
	 * @author Benoit
	 * @version : 1.2
	 */
	class WaitSondageRequest extends CyclicBehaviour {

		@Override
		public void action() {
			// On attend la reception d'un message de type REQUEST venant du
			// mediateur
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchSender(ASondage));
			ACLMessage message = myAgent.receive(mt);
			if (message != null) {
				// A la reception, on lance un OneShotBehaviour qui s'occupe de
				// renvoyer ses 2 variables internes
				myAgent.addBehaviour(new RepSondageBehaviour(message));
			} else {
				block();
			}
		}
	}

	/**
	 * <b>MajBehaviour est le troisième Behaviour de l'agent Environnement.</b>
	 * <p>
	 * Il est de type OneShot. Notre agent Environnement va seulement modifier
	 * ses variables d'environnement lorsque le message lui est parvenu.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Modifier ses variables
	 * d'environnements.
	 * <p>
	 *
	 * Cette action faite suite au vote d'une loi.
	 *
	 * @see WaitSondageRequest
	 *
	 * @see RepSondageBehaviour
	 *
	 * @author Benoit
	 * @version : 1.2
	 */
	class MajBehaviour extends OneShotBehaviour {
		private String mess;
		private ACLMessage message;

		// Constructor
		public MajBehaviour(ACLMessage message2) {
			this.message = message2;
			this.mess = message.getContent();
		}

		// Task to do
		public void action() {
			// On deserialise le message contenant les 2 valeurs de variables à
			// modifer.
			ObjectMapper mapper = new ObjectMapper();
			try {
				MajEnv ort = mapper.readValue(mess, MajEnv.class);
				// Mise a jour des deux variables avec les valeurs du message
				context_eco = context_eco + ort.getContext_eco();
				qualite_vie = qualite_vie + ort.getQualite_vie();
			} catch (Exception ex) {
				System.out.println("EXCEPTION" + ex.getMessage());
			}

			// Juste pour debug
			System.out.println();
			System.out.println("---------------------------DEBUG VARIABLE ENVIRONNEMENT ------------------------------");
			System.out.println("Eco :" + context_eco);
			System.out.println("Vie :" + qualite_vie);
			System.out.println("---------------------------FIN DEBUG ---------------------------------------");
			System.out.println();

			// Si les valeurs sont en dessous d'un certain seuil...
			if (context_eco < 10 || qualite_vie < 10 || (context_eco + qualite_vie) / 2 < 20) {

				// On affiche le resultat (perdu)
				System.out.println("Vous avez perdu...");
				System.out.println("Eco :" + context_eco);
				System.out.println("Vie :" + qualite_vie);

				// On envoie un message a 'ASimulation' pour le prevenir que la
				// partie est terminée.
				if (ASimulation != null) {
					ACLMessage message1 = new ACLMessage(ACLMessage.INFORM);
					message1.addReceiver(ASimulation);
					message1.setContent("Le joueur a perdu, merci d'arreter la partie");
					myAgent.send(message1);
				} else {
					System.out.println(getLocalName() + "--> No receiver");
				}
			}
		}
	}

	/**
	 * <b>MajBehaviour est le quatrième et dernier Behaviour de l'agent
	 * Environnement.</b>
	 * <p>
	 * Il est de type OneShot. Notre agent Environnement va seulement renvoyer
	 * les valeurs de ses variables d'environnement lorsqu'on lui demande et
	 * donc qu'il a reçu le message correspondant.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Renvoyer les valeurs de ses
	 * variables d'enregistrement.
	 * <p>
	 *
	 * Cette action faite à la demande de sondage de la part de l'utilisateur.
	 *
	 * @see WaitLoiRequest
	 *
	 * @author Benoit
	 * @version : 1.2
	 */
	class RepSondageBehaviour extends OneShotBehaviour {
		private ACLMessage message;

		// Constructor
		public RepSondageBehaviour(ACLMessage message2) {
			this.message = message2;
		}

		// Task to do
		public void action() {
			// On serialise le message contenant les 2 valeurs de variables à
			// modifer.
			ACLMessage message1 = message.createReply();
			ObjectMapper mapper1 = new ObjectMapper();
			StringWriter sw = new StringWriter();

			MajEnv or = new MajEnv(context_eco, qualite_vie);
			try {
				mapper1.writeValue(sw, or);
				String s1 = sw.toString();
				// On renvoie le message avec nos 2 valeurs à l'agent Sondage
				// (reply)
				message1.setPerformative(ACLMessage.INFORM);
				message1.setContent(s1);
				myAgent.send(message1);
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}
	}
}