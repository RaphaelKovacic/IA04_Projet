package agents;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import agents.SimulationAgent.WaitMessMediateur;

import com.fasterxml.jackson.databind.ObjectMapper;

import Class_For_JSON.DeputeAttRumeur;
import Class_For_JSON.Loi;
import Class_For_JSON.MajDepute;
import Class_For_JSON.MajEnv;
import ParlementSim.ParlementManager;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import launcher.AgentLauncher;

/**
 * <b>RumeurAgent est la classe représentant l'agent rumeur dans notre SMA
 * Parlement.</b>
 * <p>
 * L'agent rumeur possède les attributs suivants
 * <ul>
 * <li>L'AID de l'agent utilisateur pour pouvoir rapidement communiquer avec
 * lui</li>
 * <li>L'AID de l'agent mediateur pour pouvoir rapidement communiquer avec
 * lui</li>
 * <li>La liste des AID de députés du SMA</li>
 * <li>La liste des DeputeAttRumeur (caractéristiques) des députés du SMA</li>
 * <li>Le DeputeAttRumeur de l'agent utilisateur</li>
 * <li>Une valeur binaire marquant l'état occupé de l'agent (en train de traiter une demande/disponible)</li>
 * </ul>
 * </p>
 *
 *
 * @author Cristian
 * @version 1.0
 */

public class RumeurAgent extends Agent {
	/**
	 * L'AID de l'agent Utilisateur. Non modifiable
	 *
	 * @see #setup()
	 */
	AID AUtilisateur;

	/**
	 * L'AID de l'agent Mediateur. Non modifiable
	 *
	 * @see #setup()
	 */
	AID AMediateur;

	/**
	 * La liste des AID des agents députés du SMA. Non modifiable
	 *
	 * @see #setup()
	 */
	List<AID> List_Depute = new ArrayList<AID>();

	/**
	 * La liste des DeputeAttRumeur des agents députés du SMA. Variable, réinitialisée à chaque fois que l'agent reçoit une
	 * requête pour répandre des rumeurs.
	 *
	 * @see #setup()
	 * @see TreatRumourRequest
	 */
	List<DeputeAttRumeur> List_DeputeAttRumeur = new ArrayList<DeputeAttRumeur>();

	/**
	 * La valeur des caracteristiques de l'agent Utilisateur. Variable
	 *
	 * @see #setup()
	 * @see TreatRumourRequest
	 */
	DeputeAttRumeur UtilisateurAttRumeur = null;

	/**
	 * Drapeau indiquant que l'agent est en train de traiter une action. Variable.
	 * Mis à <b>false</b> à l'initiation de l'agent.
	 * Mis à <b>true</b> au début du traitement d'une action.
	 * Remis à <b>false</b> à la fin du comportement séquentiel de l'agent.
	 *
	 * @see #setup()
	 * @see TreatRumourRequest
	 * @see ExecuteActionOnDeputyIDReception
	 */
	boolean processingDemand;

	/**
	 * Méthode d'instanciation (appelée à la création) de notre agent
	 * rumeur.
	 * <p>
	 * Lors du lancement de notre plateforme JADE, l'agent rumeur est crée
	 * grâce à cette méthode setup()
	 * </p>
	 */
	protected void setup() {
		// Enregistrement auprès du DF
		DFAgentDescription dafd = new DFAgentDescription();
		dafd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Parlement");
		sd.setName("ARumeur");
		dafd.addServices(sd);
		try {
			DFService.register(this, dafd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		addBehaviour(new OneShotBehaviour() {
			@Override
			public void action() {

				processingDemand = false;

				ParlementManager parl_mana = new ParlementManager();

				// On récupère les AID des agents nécessaires
				while (AUtilisateur == null || AMediateur == null || List_Depute.size() != AgentLauncher.NB_DEPUTE) {
					AUtilisateur = parl_mana.getReceiver(myAgent, "Parlement", "AUtilisateur");
					AMediateur = parl_mana.getReceiver(myAgent, "Parlement", "AMediateur");
					List_Depute = parl_mana.getAllAidOf(myAgent, "Parlement", "ADepute");
				}

				addBehaviour(new TreatRumourRequest());
			}
		});


	}

	/**
	 * <b>TreatRumourRequest est le premier Behaviour de l'agent
	 * Rumeur</b>
	 * <p>
	 * Il s'agit d'un CyclicBehaviour. Notre agent Rumeur est en constante attente
	 * d'une requête REQUEST de l'agent Mediateur lui demandant de commencer la procédure pour répandre une rumeur.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : demande aux députés et à l'utilisateur leurs caractéristiques et ajoute un behaviour séquentiel, contenant:
	 * <ul>
	 * <li>un ParallelBehaviour pour recevoir les caractéristiques de chacun;</li>
	 * <li>un OneShotBehaviour pour proposer les caractéristiques des députés à l'utilisateur;</li>
	 * <li>un Behaviour qui, à la réception d'un ACCEPT_PROPOSAL avec l'id du député pour lequel on répand les rumeurs,
	 * calcule les conséquences de l'action sur l'utilisateur et le député et les informe du résultat.</li>
	 * </ul>
	 * Aussi, donne la valeur <b>true</b> au drapeau processingDemand.
	 * <p>
	 *
	 * @author Cristian
	 * @version 1.0
	 */
	class TreatRumourRequest extends CyclicBehaviour {

		@Override
		public void action() {

			if (!processingDemand) {
				// On attend la reception d'un message de type REQUEST venant de
				// l'agent Mediateur
				MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
						MessageTemplate.MatchSender(AMediateur));
				ACLMessage message = myAgent.receive(mt);
				if (message != null) {
					// A la reception, l'agent rumeur demande les caractéristiques aux agents Députés.
					// Il déclenche un behaviour séquentiel pour traiter la demande du médiateur

					processingDemand = true;

					// Vider la liste des caractéristiques s'il y en a après des rumeurs précédentes.
					List_DeputeAttRumeur.clear();
					UtilisateurAttRumeur = null;

					// Ajout du behaviour sequentiel qui s'occupera du deroulement de l'action.
					SequentialBehaviour rumourWave = new SequentialBehaviour();
					// Ajout du behaviour parallele qui recevra les réponses avec les caractéristiques des députés.
					ParallelBehaviour receiveCharacteristicsBehaviour = new ParallelBehaviour(myAgent, ParallelBehaviour.WHEN_ALL);
					for (int i = 0; i < List_Depute.size(); i++)
						receiveCharacteristicsBehaviour.addSubBehaviour(new ReceiveCharacteristicsFromDeputyOrUser(myAgent,List_Depute.get(i)));
					receiveCharacteristicsBehaviour.addSubBehaviour(new ReceiveCharacteristicsFromDeputyOrUser(myAgent,AUtilisateur));
					rumourWave.addSubBehaviour(receiveCharacteristicsBehaviour);
					rumourWave.addSubBehaviour(new ProposeDeputyCharacteristicsToUser());
					rumourWave.addSubBehaviour(new ExecuteActionOnDeputyIDReception());

					myAgent.addBehaviour(rumourWave);


					ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);

					for (int i = 0; i < List_Depute.size(); i++)
						msg.addReceiver(List_Depute.get(i));
					msg.addReceiver(AUtilisateur);
					myAgent.send(msg);




				} else {
					block();
				}
			} else {
				block();
			}
		}
	}

	/**
	 * <b>ReceiveCharacteristicsFromDeputyOrUser est le second Behaviour de l'agent Rumeur</b>
	 * <p>
	 * Il s'agit d'un Behaviour générique. Plusieurs comportements de ce type feront partie du comportement
	 * parallèle initié à la réception d'une requête (message REQUEST) de la part de l'agent Mediateur.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Attendre la réception des caractéristiques (influence, popularité et crédibilité)
	 * du député (ou Utilisateur) dont l'AID est gardé localement (message INFORM). A la réception du message, ajouter les caractéristiques reçues
	 * à la liste des attributs des députés gardée localement dans l'agent Rumeur, ou bien les affecter à la variable gardant les attributs
	 * de l'agent Utilisateur si son AID est utilisé par le behaviour. Le behaviour est terminé une fois que le message est reçu
	 * et les caractéristiques sont ajoutées à la liste ou à la variable mentionnée précedamment.
	 * <p>
	 *
	 * @see TreatRumourRequest
	 *
	 * @author Cristian
	 * @version : 1.0
	 */
	class ReceiveCharacteristicsFromDeputyOrUser extends Behaviour {

		/**
		 * L'AID de l'agent Député ou Utilisateur. Non modifiable
		 *
		 */
		AID resultSender;

		/**
		 * Valeur booléenne marquant la réception du message dont l'expéditeur correspond à l'AID.
		 *
		 * @see #done()
		 */
		boolean received;

		public ReceiveCharacteristicsFromDeputyOrUser(Agent a, AID sender) {
			super(a);
			resultSender = sender;
			received = false;
		}

		@Override
		public void action() {
			// TODO Auto-generated method stub

			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchSender(resultSender));
			ACLMessage message = myAgent.receive(mt);
			if (message != null) {

				// On desérialise le message
				ObjectMapper mapper = new ObjectMapper();
				DeputeAttRumeur caracteristiques;

				try {
					caracteristiques = mapper.readValue(message.getContent(), DeputeAttRumeur.class);
					if (resultSender.equals(AUtilisateur)) { // Si le sender est l'utilisateur...
						//... on fixe ses attributs
						UtilisateurAttRumeur = caracteristiques;
					} else {
						// si c'est un député, on ajoute aux caracteristiques l'index du député tel que dans List_Depute de l'agent Rumeur
						caracteristiques.set_Id(List_Depute.indexOf(resultSender));
						// on ajoute la caractéristique du député dans la liste des caractéristiques des députés
						List_DeputeAttRumeur.add(caracteristiques);
					}

					received = true;
				} catch (Exception ex) {
					System.out.println("EXCEPTION" + ex.getMessage());
				}

			}
			else {
				block();
			}
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return received;
		}
	}

	/**
	 * <b>ProposeDeputyCharacteristicsToUser est le troisième Behaviour de l'agent Rumeur</b>
	 * <p>
	 * Il s'agit d'un OneShotBehaviour. C'est le comportement qui suit de manière séquentielle la fin du comportement
	 * parallèle initié à la réception d'une requête (message REQUEST) de la part de l'agent Mediateur.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Envoyer la liste des caractéristiques des députés remplie lors du déroulement
	 * du comportement parallèle à l'agent Utilisateur (message PROPOSE).
	 * <p>
	 *
	 * @see TreatRumourRequest
	 *
	 * @author Cristian
	 * @version : 1.0
	 */
	class ProposeDeputyCharacteristicsToUser extends OneShotBehaviour {

		@Override
		public void action() {
			// TODO Auto-generated method stub

			ACLMessage message = new ACLMessage(ACLMessage.PROPOSE);
			message.addReceiver(AUtilisateur);

			// On sérialise le message
			ObjectMapper mapper1 = new ObjectMapper();
			StringWriter sw = new StringWriter();
			try {
				mapper1.writeValue(sw, List_DeputeAttRumeur);
				String s1 = sw.toString();
				message.setContent(s1);
				myAgent.send(message);
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}
	}

	/**
	 * <b>ExecuteActionOnDeputyIDReception est le quatrième Behaviour de l'agent Rumeur</b>
	 * <p>
	 * Il s'agit d'un Behaviour générique. C'est le comportement qui suit de manière séquentielle la fin du comportement
	 * envoyant les caractéristiques des députés à l'utilisateur. Il marque la fin de l'action "Répandre une rumeur".
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Attendre le choix du député pour lequel une rumeur est répandue de la part de l'agent Utilisateur
	 * (message ACCEPT_PROPOSAL). A sa réception, comparer les caractéristiques du député choisi avec celles de l'agent Utilisateur
	 * pour conclure sur les conséquences de l'action (réussite/échec). La fin du behaviour est marquée après l'envoi de
	 * messages INFORM à l'agent Député et à l'agent Utilisateur dont le contenu sert à mettre à jour leur influence et leur popularité.
	 * A la fin du comportement, le drapeau processingDemand reprend la valeur <b>false</b>.
	 * <p>
	 *
	 * @see TreatRumourRequest
	 *
	 * @author Cristian
	 * @version : 1.0
	 */
	class ExecuteActionOnDeputyIDReception extends Behaviour {

		/**
		 * Variable booléenne marquant la fin du comportement.
		 *
		 * @see #done()
		 *
		 */
		boolean done;

		public ExecuteActionOnDeputyIDReception() {
			super();
			done = false;
		}

		@Override
		public void action() {
			// TODO Auto-generated method stub

			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
			ACLMessage message = myAgent.receive(mt);
			if (message != null) {

				DeputeAttRumeur caracteristiques = null;

				// trouver les caractéristiques correspondantes (il est possible d'optimiser cela si nécessaire)
				for (int i = 0; i < List_DeputeAttRumeur.size(); i++)
					if (List_DeputeAttRumeur.get(i).get_Id() == Integer.parseInt(message.getContent())) {
						caracteristiques = List_DeputeAttRumeur.get(i);
					}

				// CALCUL CONSEQUENCES
				// Valeurs màj pour le gagnant et le perdant
				MajDepute majGagnant = new MajDepute(10,10,0,0);
				MajDepute majPerdant = new MajDepute(-10,-10,0,0);
				// On sérialise le message
				ObjectMapper mapper1 = new ObjectMapper();
				StringWriter sw = new StringWriter();
				StringWriter sw2 = new StringWriter();
				String sWinner = null;
				String sLoser = null;
				try {
					mapper1.writeValue(sw, majGagnant);
					sWinner = sw.toString();
					mapper1.writeValue(sw2, majPerdant);
					sLoser = sw2.toString();
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
				// On prépare les messages de mise à jour des caractéristiques à envoyer au gagnant et au perdant
				ACLMessage winnerMsg = new ACLMessage(ACLMessage.INFORM);
				winnerMsg.setContent(sWinner);
				ACLMessage loserMsg = new ACLMessage(ACLMessage.INFORM);
				loserMsg.setContent(sLoser);

				// le rapport de puissance permet d'ajuster la valeur aléatoire qui permet de nommer le gagnant
				float rapportPuissance = (float)(UtilisateurAttRumeur.get_Influence() * UtilisateurAttRumeur.get_Popularite() * UtilisateurAttRumeur.get_Credibilite()) /
						(caracteristiques.get_Influence() * caracteristiques.get_Popularite() * caracteristiques.get_Credibilite());


				boolean utilisateurGagne; // vrai si c'est l'utilisateur qui emporte l'action
				utilisateurGagne = (Math.random()*rapportPuissance) >= 0.5;

				// on fixe les destinataires des changements de caractéristiques positifs et négatifs
				if (utilisateurGagne) {
					System.out.println("Les gens ont cru aux rumeurs!");
					winnerMsg.addReceiver(AUtilisateur);
					loserMsg.addReceiver(List_Depute.get(Integer.parseInt(message.getContent())));
				} else {
					System.out.println("Les gens n'ont pas cru aux rumeurs!");
					winnerMsg.addReceiver(List_Depute.get(Integer.parseInt(message.getContent())));
					loserMsg.addReceiver(AUtilisateur);
				}

				// on envoie les messages de màj au gagnant et au perdant
				myAgent.send(winnerMsg);
				myAgent.send(loserMsg);

				// done est mis à true pour marquer la fin du behaviour
				done = true;

			}
			else {
				block();
			}
		}

		/**
		 * Méthode contenant les actions à faire après la fin du comportement.
		 * <p>
		 * Remet à <b>false</b> le drapeau processingDemand permettant de commencer une autre action de type "Repandre des rumeurs".
		 * </p>
		 */
		@Override
		public int onEnd() {
			// TODO Auto-generated method stub

			// Remise à false du drapeau processingDemand, permettant de commencer le traitement d'une nouvelle requête de répandre des rumeurs.
			processingDemand = false;

			ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
			msg.addReceiver(AMediateur);
			msg.setContent("Aucune");
			myAgent.send(msg);
			
			
			return 0;
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return done;
		}
	}
}
