package agents;

import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import com.fasterxml.jackson.databind.ObjectMapper;

import Class_For_JSON.MajEnv;
import ParlementSim.ParlementManager;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * <b>SondageAgent est la classe représentant l'agent sondage dans notre SMA
 * Parlement.</b>
 * <p>
 * L'agent sondage possède les attributs suivants :
 * <ul>
 * <li>L'AID de l'agent environnement pour pouvoir rapidement communiquer avec
 * lui</li>
 * <li>L'AID de l'agent médiateur pour les mêmes raisons qu'au dessus</li>
 * <li>Le manager du parlement pour recevoir les AID ci-dessus</li>
 * </ul>
 * </p>
 * <p>
 * La première classe sert à l'instanciation de l'agent. Les comportements de
 * l'agent Sondage sont spécifiés dans les quatre classes suivantes.
 * </p>
 *
 *
 * @author Benoit  Etienne
 * @version 3.1
 */
@SuppressWarnings("serial")
public class SondageAgent extends Agent {

	/**
	 * L'AID de l'agent environnement. Non modifiable
	 *
	 * @see #setup()
	 */
	AID AEnvironnement;

	/**
	 * L'AID de l'agent médiateur. Non modifiable
	 *
	 * @see #setup()
	 */
	AID AMediateur;

	/**
	 * Le manager du parlement. Non modifiable
	 *
	 * @see #setup()
	 */
	ParlementManager parl_mana = new ParlementManager();

	/**
	 * Méthode d'instanciation (appelée à la création) de notre agent Sondage
	 * <p>
	 * Lors du lancement de notre plateforme JADE, l'agent Sondage est créé
	 * grâce à cette méthode setup().
	 * </p>
	 */
	protected void setup() {
		// Enregistrement auprès du DF
		DFAgentDescription dafd = new DFAgentDescription();
		dafd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Parlement");
		sd.setName("ASondage");
		dafd.addServices(sd);
		try {
			DFService.register(this, dafd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		System.out.println("Agent Sondage créé : " + this.getLocalName());

		addBehaviour(new OneShotBehaviour() {

			@Override
			public void action() {
				// On récupère les AID des agents nécessaires
				while (AMediateur == null || AEnvironnement == null) {
					AMediateur = parl_mana.getReceiver(myAgent, "Parlement", "AMediateur");
					AEnvironnement = parl_mana.getReceiver(myAgent, "Monde", "AEnvironnement");
				}
				addBehaviour(new RequestOfMediator()); // recéption d'un message demandant de faire un
				// sondage sur l'environnement

				addBehaviour(new ReponseOfEnvironnement()); // réception de la réponse contenant
				// les variables de l'environnement.

			}
		});

	}

	/**
	 * <b>RequestOfMediator est le premier Behaviour de l'agent Sondage.</b>
	 * <p>
	 * Il est de type Cyclic. Notre agent Sondage est en constante attente d'une
	 * requête PROPOSE de l'agent Médiateur qui lui demande les valeurs
	 * actuelles des variables d'environnement.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Récupèrer le message demandant
	 * les valeurs des variables d'environnement actuelles.
	 * <p>
	 *
	 * Cette action fait suite à une demande de sondage de la part de
	 * l'utilisateur.
	 *
	 * <p>
	 * Ce Behaviour va instancier un OneShotBehaviour qui sera en charge de la
	 * suite du processus.
	 * </p>
	 *
	 * @author Benoit
	 * @version : 1.2
	 */
	class RequestOfMediator extends CyclicBehaviour {

		@Override
		public void action() {

			// On attend la reception d'un message de type REQUEST venant de
			// l'agent Mediateur
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchSender(AMediateur));
			ACLMessage message = myAgent.receive(mt);
			if (message != null) {
				// A la reception, l'agent demande a l'agent Environnement
				// quelles sont ses variables.
				myAgent.addBehaviour(new AskForVariableEnv(message));
			} else {
				block();
			}
		}
	}

	/**
	 * <b>AskVariableEnv est le second Behaviour de l'agent Sondage.</b>
	 * <p>
	 * Il est de type OneShot. Notre agent Sondage ne fait ce comportement
	 * qu'une seule fois. Lorsque le médiateur lui a déjà demandé de lui fournir
	 * les variables d'nevironnement du pays.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Envoyer un message à l'agent
	 * environnement pour connaitre l'état de ses variables.
	 * <p>
	 *
	 * Cette action fait suite à une demande des valeurs des variables par
	 * l'agent médiateur à notre agent Sondage. Voir Behaviour ci-dessus.
	 *
	 * @author Benoit
	 * @version 1.1
	 */
	class AskForVariableEnv extends OneShotBehaviour {
		private String mess;
		private ACLMessage message;

		// Constructor
		public AskForVariableEnv(ACLMessage message2) {
			this.message = message2;
			this.mess = message.getContent();
		}

		// Task to do
		public void action() {

			// Envoie d'un message a l'agent Environnement pour connaitre ses
			// variables
			ACLMessage forward = message.createReply();
			forward.removeReceiver(message.getSender());
			forward.addReceiver(AEnvironnement);
			forward.setPerformative(ACLMessage.REQUEST);
			forward.setContent("Quelles-sont tes variables d'environnement?");
			myAgent.send(forward);

		}
	}

	/**
	 * <b>ReponseOfEnvironnement est le troisième Behaviour de l'agent
	 * Sondage.</b>
	 * <p>
	 * Il est de type Cyclic. Notre agent Sondage est en constante attente d'une
	 * requête INFORM de l'agent Environnement qui répond au message envoyé par
	 * notre agent Sondage lui demandant les valeurs de ses variables
	 * d'environnements.
	 * </p>
	 *
	 * Cette action faite suite à une demande de sondage de la part de
	 * l'utilisateur.
	 *
	 * <p>
	 * Ce Behaviour va instancier un OneShotBehaviour qui sera en charge de la
	 * suite du processus.
	 * </p>
	 *
	 * @author Benoit
	 * @version : 1.1
	 */
	class ReponseOfEnvironnement extends CyclicBehaviour {

		@Override
		public void action() {

			// On attend la reception d'un message de type REQUEST venant de
			// l'agent Mediateur
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchSender(AEnvironnement));
			ACLMessage message = myAgent.receive(mt);
			if (message != null) {
				// A la reception, l'agent demande a l'agent Environnement
				// quelles sont ses variables.
				myAgent.addBehaviour(new GiveVarToMediat(message));
			} else {
				block();
			}
		}
	}

	/**
	 * <b>GiveVarToMediat est le quatrième et dernier Behaviour de l'agent
	 * Sondage.</b>
	 * <p>
	 * Il est de type OneShot. Notre agent Sondage va afficher les variables
	 * d'environnements envoyées par l'agent environnement seulement lorsqu'il a
	 * reçu ces mêmes variables.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Affiche les variables
	 * d'environnement actuelles.
	 * <p>
	 *
	 * Cette action faite suite et finie l'action de demande de sondage de la
	 * part de l'utilisateur.
	 *
	 * </p>
	 *
	 * @author Benoit
	 * @version : 1.1
	 */
	class GiveVarToMediat extends OneShotBehaviour {
		private String mess;
		private ACLMessage message;

		// Constructor
		public GiveVarToMediat(ACLMessage message2) {
			this.message = message2;
			this.mess = message.getContent();
		}

		public void action() {
			/*
			 * //Envoie d'un message a l'agent Environnement pour connaitre ses
			 * variables ACLMessage forward = message.createReply();
			 * forward.removeReceiver(message.getSender());
			 * forward.addReceiver(AMediateur);
			 * forward.setPerformative(ACLMessage.INFORM);
			 * forward.setContent(message.getContent()); myAgent.send(forward);
			 */

			// Affichage des variables dans la console (a modifier -> tour
			// d'après...).
			// On deserialise le message contenant les 2 valeurs de variables à
			// modifer.
			ObjectMapper mapper = new ObjectMapper();
			try {
				MajEnv ort = mapper.readValue(mess, MajEnv.class);
				// Mise a jour des deux variables avec les valeurs du message
				System.out.println("");
				System.out.println("-----------------------RÉSULTAT SONDAGE ------------------------------");
				System.out.println(ort.getContext_eco());
				System.out.println(ort.getQualite_vie());
				System.out.println("-----------------------RÉSULTAT SONDAGE ------------------------------");
			} catch (Exception ex) {
				System.out.println("EXCEPTION" + ex.getMessage());
			}
		}
	}
}
