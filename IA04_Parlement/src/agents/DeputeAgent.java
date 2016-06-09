package agents;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import Class_For_JSON.DeputeAttRumeur;
import Class_For_JSON.Loi;
import Class_For_JSON.MajDepute;

import ParlementSim.ParlementManager;
import graphicInterface.MainApp;
import ParlementSim.Aid_vote;

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

/**
 * <b>DeputeAgent est la classe représentant l'agent député dans
 * notre SMA Parlement.</b>
 * <p>
 * L'agent député possède les attributs suivants :
 * <ul>
 * <li>Un attribut Influence qui représente l'influence du député</li>
 * <li>Un attribut Popularite qui représente la popularité du député</li>
 * <li>Un attribut Notoriete qui représente la notoriété du député</li>
 * <li>Un attribut Credibilite qui représente la crédibilité du député </li>
 * <li>Un attribut Parti_Politique qui stocke le parti politique du
 * député </li>
 * <li>Un attribut Charisme qui représente la le charisme du député </li>
 * <li>Un attribut Hésitation qui représente l'hésitation du député </li>
 *
 * <li>Un attribut A qui représente l'importance de l'influence du député dans le calcul du score d'une loi préalable à la décision du député</li>
 * <li>Un attribut BP qui représente l'importance du contexte social de la loi dans le calcul du score d'une loi préalable à la décision du député</li>
 * <li>Un attribut BE qui représente l'importance du contexte économique de la loi dans le calcul du score d'une loi préalable à la décision du député</li>
 * <li>Un attribut G qui représente l'importance du charisme du député proposant  dans le calcul du score d'une loi préalable à la décision du député</li>
 * <li>Un attribut D qui représente le bonus si la loi provient d'un député du même parti dans le calcul du score d'une loi préalable à la décision du député</li>
 *
 * <li>Un attribut APeuple qui représente l'importance de la popularité dans le calcul du score d'une loi</li>
 * <li>Un attribut BPeuple qui représente l'importance du contexte social du pays dans le calcul du score d'une loi</li>
 * <li>Un attribut AEntreprise qui représente l'importance de la notoriété dans le calcul du score d'une loi</li>
 * <li>Un attribut BEntreprise qui représente l'importance du contexte éco du pays dans le calcul du score d'une loi</li>
 *
 * <li>L'AID de l'agent loi pour pouvoir rapidement communiquer avec lui</li>
 * <li>L'AID de l'agent KB pour les mêmes raisons qu'au dessus</li>
 * <li>L'AID de l'agent rumeur pour les mêmes raisons qu'au dessus</li>
 *
 * <li>Un attribut L_Parti qui est une liste de tous les partis politiques
 * possibles du jeu.</li>
 * <li>Le manager du parlement pour recevoir les AID ci-dessus</li>
 *
 * </ul>
 * </p>
 * <p>
 * La première classe sert à l'instanciation de l'agent. Les comportements de
 * l'agent député sont spécifiés dans les classes suivantes
 * </p>
 *
 *
 * @author Benoit  Etienne
 * @version 4.3
 */

@SuppressWarnings("serial")
public class DeputeAgent extends Agent {
	// Caractéristiques dynamiques

	/**
	 * L'influence du député. Variable.
	 *
	 * @see #setup()
	 */
	float Influence;

	/**
	 * La popularité du député. Variable.
	 *
	 * @see #setup()
	 */
	float Popularite;

	/**
	 * La notoriété du député. Variable.
	 *
	 * @see #setup()
	 */
	float Notoriete;

	/**
	 * La crédibilité du député. Variable.
	 *
	 * @see #setup()
	 */
	float Credibilite;

	/**
	 * Le parti politique du député. Statique.
	 *
	 * @see #setup()
	 */
	String Parti_Politique;

	/**
	 * L'influence de l'utilisateur. Statique.
	 *
	 * @see #setup()
	 */
	float Charisme;

	/**
	 * La propension de l'utilisateur à hésiter. Statique.
	 *
	 * @see #setup()
	 */
	float Hesitation;

	/**
	 * Importance du député dans le calcul du score d'une loi. Statique.
	 *
	 * @see #setup()
	 */
	float A;

	/**
	 * Importance de l'apport social d'une loi dans le calcul du score de cette loi. Statique.
	 *
	 * @see #setup()
	 */
	float BP;

	/**
	 * Importance de l'apport économique d'une loi dans le calcul du score de cette loi. Statique.
	 *
	 * @see #setup()
	 */
	float BE;

	/**
	 * Importance du charisme du député dans le calcul du score d'une loi. Statique.
	 *
	 * @see #setup()
	 *
	 */
	float G;

	/**
	 * Importance du parti politique (bonus si même parti que le député) dans le calcul du score d'une loi. Statique.
	 *
	 * @see #setup()
	 *
	 */
	float D;

	/**
	 * Importance de la popularité du député dans le calcul du score d'une loi. Statique.
	 *
	 * @see #setup()
	 *
	 */
	float APeuple;

	/**
	 * Importancedu contexte social actuel dans le pays dans le calcul du score d'une loi. Statique.
	 *
	 * @see #setup()
	 *
	 */
	float BPeuple;

	/**
	 * Importance de la notoriété du député dans le calcul du score d'une loi. Statique.
	 *
	 * @see #setup()
	 *
	 */
	float AEntreprise;

	/**
	 * Importance du contexte économique actuel du pays dans le calcul du score d'une loi. Statique.
	 *
	 * @see #setup()
	 *
	 */
	float BEntreprise;

	/**
	 * L'AID de l'agent loi. Non modifiable
	 *
	 * @see #setup()
	 */
	AID ALoi;

	/**
	 * L'AID de l'agent Rumeur. Non modifiable
	 *
	 * @see #setup()
	 */
	AID ARumeur;

	/**
	 * L'AID de l'agent KB. Non modifiable
	 *
	 * @see #setup()
	 */
	AID AKB;

	/**
	 * La liste de tous les partis possibles de l'utilisateur. Constante.
	 *
	 * @see #setup()
	 */
	List<String> L_Parti;

	/**
	 * Le manager du parlement. Non modifiable
	 *
	 * @see #setup()
	 */
	ParlementManager parl_mana = new ParlementManager();


	/**
	 * Méthode d'instanciation (appelée à la création) de notre agent
	 * Deputé.
	 * <p>
	 * Lors du lancement de notre plateforme JADE, chaque agent député est créé
	 * grâce à cette méthode setup()
	 * </p>
	 */
	protected void setup() {
		// Enregistrement auprès du DF
		DFAgentDescription dafd = new DFAgentDescription();
		dafd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Parlement");
		sd.setName("ADepute");
		dafd.addServices(sd);
		try {
			DFService.register(this, dafd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		// Initialisation de la liste des partis (issue de l'univers Divergente)
		L_Parti = new ArrayList<String>();
		L_Parti.add("Altruistes");
		L_Parti.add("Erudits");
		L_Parti.add("Audacieux");
		L_Parti.add("Sinceres");
		L_Parti.add("Fraternels");

		// Initialisation des caractéristiques dynamiques
		Credibilite = (float) (Math.random() * 100); // Entre 0 et 100 au départ
		Influence = (float) (Math.random() * 70); // Entre 0 et 70 au départ
		Parti_Politique = L_Parti.get((int) (Math.random() * L_Parti.size())); // aléatoire
		Popularite = (float) (Math.random() * 100); // Entre 0 et 100 au départ
		Notoriete = (float) (Math.random() * 100); // Entre 0 et 100 au départ

		// Initialisation des caractéristiques statiques
		Charisme = (float) (Math.random() * 100); // Entre 0 et 100 au départ

		A = 2; // coeff influence
		BP = 1; // coeff EffetQualitedeVie
		BE = 1; // coeff EffetEconomie
		G = 1; // coeff Charisme
		D = 50; // +50 si la loi fait parti du parti..

		APeuple = BPeuple = AEntreprise = BEntreprise = (float) Math.random(); // entre 0 et 1

		// Soutien du peuple et des entreprises pas pris en compte (pas encore
		// implémenté...)
		float higher = (A * 100 + BP * 25 + BE * 25 + G * 100 + D * 1
				+ (APeuple * 100 + BPeuple * 25 + AEntreprise * 100 + BEntreprise * 25)); // maximum du score
		// pour "voter une
		// loi"
		float lower = (A * 0 + BP * (-25) + BE * (-25) + G * 0 + D * 0
				+ (APeuple * 0 + BPeuple * (-25) + AEntreprise * 0 + BEntreprise * (-25)));// minimum du score pour
		// "voter une loi"
		Hesitation = (float) (Math.random() * (higher - lower)) + lower; // un peu de random ne
		// fait pas de mal :D

		System.out.println("Agent Député créé : " + this.getLocalName());

		addBehaviour(new OneShotBehaviour() {
			@Override
			public void action() {
				// On récupère les AID des agents nécessaires
				while (ALoi == null || AKB == null || ARumeur == null) {
					AKB = parl_mana.getReceiver(myAgent, "KB", "AKB");
					ALoi = parl_mana.getReceiver(myAgent, "Parlement", "ALoi");
					ARumeur = parl_mana.getReceiver(myAgent, "Parlement", "ARumeur");
				}
				addBehaviour(new RequestToProposeLaw()); // recéption d'un message demandant
				// de proposer une loi (provient de
				// ALoi)

				addBehaviour(new RequestToVote()); // réception d'un message demandant de voter pour
				// une loi (provient de ALoi)

				addBehaviour(new RequestToSondage()); // réception d'un message demandant de donner
				// son avis pour une loi (provient de ALoi)

				addBehaviour(new RequestToModifCara()); // réception d'un message demandant de
				// modifier ses cara (provient de ALoi)

				addBehaviour(new ProposeLaw()); // envoie de la loi récupéré par KB à la loi

				addBehaviour(new AnswerRequestCharacteristicsFromRumourAgent()); // envoie les caractéristiques (influence, ...) à la demande de l'agent Rumeur
			}
		});
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MainApp.addDepute("Depute", "Classique", this.Parti_Politique, this.Popularite, this.Credibilite, this.Notoriete, this.Charisme, this.getAID().getLocalName());// ajout dans le modele grpahique

	}


	/**
	 * <b>RequestToProposeLaw est le premier Behaviour de l'agent
	 * Député.</b>
	 * <p>
	 * Il est de type Cyclic. Notre agent député est en constante attente
	 * d'une requête REQUEST de l'agent Loi lui demandant de proposer une 
	 * loi lors de ce tour.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Réalise la demande de l'agent
	 * loi qui est de proposer une loi lors du tour de jeu en cours.
	 * <p>
	 *
	 * @see GetLawToProposeFromKB
	 * @author Benoit
	 * @version 1.1
	 */
	class RequestToProposeLaw extends CyclicBehaviour {

		@Override
		public void action() {

			// On attend la reception d'un message de type REQUEST venant de
			// l'agent Loi
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchSender(ALoi));
			ACLMessage message = myAgent.receive(mt);
			if (message != null) {
				// A la reception, l'agent propose a Aloi une loi qu'il aimerait
				// faire passer.
				myAgent.addBehaviour(new GetLawToProposeFromKB());
			} else {
				block();
			}
		}
	}

	/**
	 * <b>GetLawToProposeFromKB est le second Behaviour de l'agent
	 * Député.</b>
	 * <p>
	 * Il est de type OneShot. Notre agent député ne va requêter l'agent KB
	 * pour connaitre la loi qu'il va proposer seulement lorsqu'il
	 * a été lui même été averti par message par l'agent loi qu'il doit proposer une
	 * loi lors de ce tour.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Réalise la demande à l'agent KB
	 * pour connaitre la loi qu'il va proposer.
	 * <p>
	 *
	 * @see RequestToProposeLaw
	 * @author Etienne
	 * @version 1.3
	 */
	class GetLawToProposeFromKB extends OneShotBehaviour {

		public void action() {

			// On envoie un message à l'agent KB pour récupérer une loi non
			// encore votée correspondant à notre parti
			ACLMessage message1 = new ACLMessage(ACLMessage.REQUEST);
			message1.addReceiver(AKB);
			message1.setContent(Parti_Politique);
			myAgent.send(message1);

		}
	}

	/**
	 * <b>ProposeLaw est le troisième Behaviour de l'agent
	 * Député.</b>
	 * <p>
	 * Il est de type Cyclic. Notre agent est en constante "écoute" de l'agent KB
	 * pour récupérer la loi qu'il va proposer lorsque KB envoie effectivement 
	 * le message contenant cette loi.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Récupère la loi formalisée en JSON
	 * envoyée par KB.
	 * Loi qu'il doit proposer.
	 * <p>
	 *
	 * @author Etienne
	 * @version 2.2
	 */
	class ProposeLaw extends CyclicBehaviour {

		public void action() {

			// On attend la reception d'un message de type REQUEST venant de
			// l'agent Loi
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchSender(AKB));
			ACLMessage message = myAgent.receive(mt);
			if (message != null) {

				// On deserialize la loi envoyée par KB.
				ObjectMapper mapper = new ObjectMapper();
				try {
					Loi loi_de_kb = mapper.readValue(message.getContent(), Loi.class);
					// Ajout des informations du député
					loi_de_kb.setProposant(getAID().toString());
					loi_de_kb.setInfluence(Influence);
					loi_de_kb.setPopularite(Popularite);
					loi_de_kb.setNotoriete(Notoriete);
					loi_de_kb.setCharisme(Charisme);
					System.out.println();
					System.out.println(
							"-----------------------DEBUG PARTI DU DEPUTE PROPOSANT (AGENTDEPUTE) ------------------------------");
					System.out.println("PARTI DU DÉPUTÉ PROPOSANT : " + Parti_Politique);
					System.out.println(
							"-----------------------FIN PARTI DU DEPUTE PROPOSANT (AGENTDEPUTE) ------------------------------");
					System.out.println();
					ObjectMapper mapper1 = new ObjectMapper();
					StringWriter sw1 = new StringWriter();

					try {

						mapper1.writeValue(sw1, loi_de_kb);
						String s = sw1.toString();
						// Répondre à l'agent loi avec notre loi formatée en JSON
						// avec les infos du député en plus.
						ACLMessage message2 = new ACLMessage(ACLMessage.PROPOSE);
						message2.addReceiver(ALoi);
						message2.setContent(String.valueOf(s));
						myAgent.send(message2);

					} catch (Exception ex) {
						System.out.println("EXCEPTION" + ex.getMessage());
					}

				} catch (Exception ex) {
				}

			} else
				block();

		}
	}

	/**
	 * <b>RequestToVote est le quatrième Behaviour de l'agent
	 * Député.</b>
	 * <p>
	 * Il est de type Cyclic. Notre agent est en constante "écoute" de l'agent loi
	 * pour récupérer le message lui demander de voter une loi.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Récupère le message de type PROPOSE
	 * de la part de l'agent Loi avec le conversation ID correspondant à un 
	 * vote de loi et lui demandant de voter une proposition de loi.
	 * <p>
	 *
	 * @see VoteLoi
	 * @author Benoit
	 * @version 1.3
	 */
	class RequestToVote extends CyclicBehaviour {

		@Override
		public void action() {

			// On attend la reception d'un message de type PROPOSE venant de
			// l'agent Loi
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
					MessageTemplate.and(MessageTemplate.MatchSender(ALoi),
							MessageTemplate.MatchConversationId("Proposition de loi")));
			ACLMessage message = myAgent.receive(mt);
			if (message != null) {
				// A la reception, l'agent propose a Aloi une loi qu'il aimerait
				// faire passer.
				myAgent.addBehaviour(new VoteLoi(message));
			} else {
				block();
			}
		}
	}

	/**
	 * <b>RequestToSondage est le cinquième Behaviour de l'agent
	 * Député.</b>
	 * <p>
	 * Il est de type Cyclic. Notre agent est en constante "écoute" de l'agent loi
	 * pour intercepter le message lui demandant son avis sur une loi proposée
	 * par l'utilisateur.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Récupère le message de type PROPOSE
	 * de la part de l'agent Loi avec le conversation ID correspondant à une demande
	 * d'avis sur une loi et lui demandant de rendre son avis dessus.
	 * <p>
	 *
	 * @see SondageLoi
	 * @author Etienne
	 * @version 1.3
	 */
	class RequestToSondage extends CyclicBehaviour {

		@Override
		public void action() {

			// On attend la reception d'un message de type PROPOSE venant de
			// l'agent Loi demander l'avis du député pour une loi
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
					MessageTemplate.and(MessageTemplate.MatchSender(ALoi),
							MessageTemplate.MatchConversationId("Demande de sondage")));
			ACLMessage message = myAgent.receive(mt);
			if (message != null) {

				myAgent.addBehaviour(new SondageLoi(message));
			} else {
				block();
			}
		}
	}


	/**
	 * <b>RequestToModifCara est le sixième Behaviour de l'agent
	 * Député.</b>
	 * <p>
	 * Il est de type Cyclic. Notre agent est en constante "écoute" de l'agent loi et de l'agent rumeur
	 * pour intercepter le message lui demandant de mettre à jour ses caractéristiques
	 * lorsque le contexte le demande (suite au vote d'une loi ou au résultat
	 * d'une proposition de loi de sa part, ou bien suite à une rumeur).
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Récupère le message de type INFORM
	 * de la part de l'agent Loi ou de l'agent Rumeur lui demandant de mettre à jour ses caractéristiques
	 * internes.
	 * <p>
	 *
	 * @author Benoit  Cristian
	 * @version 1.4
	 */
	class RequestToModifCara extends CyclicBehaviour {

		@Override
		public void action() {

			// On attend la reception d'un message de type INFORM venant de
			// l'agent Loi
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.or(MessageTemplate.MatchSender(ALoi),MessageTemplate.MatchSender(ARumeur)));
			ACLMessage message = myAgent.receive(mt);
			if (message != null) {
				// A la reception, on met à jour les caract de l'agent
				// On deserialise le message contenant les valeurs de variables
				// à modifer.
				ObjectMapper mapper = new ObjectMapper();
				try {
					MajDepute ort = mapper.readValue(message.getContent(), MajDepute.class);
					// Mise a jour des variables avec les valeurs du message
					Influence += ort.getEffet_Influence();
					Popularite += ort.getEffet_Popularite();
					Notoriete += ort.getEffet_Notoriete();
					Credibilite += ort.getEffet_Credibilite();

					if (Influence < 0)
						Influence = 0;
					if (Popularite < 0)
						Popularite = 0;
					if (Notoriete < 0)
						Notoriete = 0;
					if (Credibilite < 0)
						Credibilite = 0;

				} catch (Exception ex) {
					System.out.println("EXCEPTION" + ex.getMessage());
				}

				/*
				 * // Juste pour debug System.out.println("");
				 * System.out.println(myAgent.getLocalName());
				 * System.out.println("Influence :"+Influence);
				 */
			} else {
				block();
			}
		}
	}


	/**
	 * <b>VoteLoi est le septième Behaviour de l'agent
	 * Député.</b>
	 * <p>
	 * Il est de type OneShot. Notre agent ne doit rendre son vote sur une loi
	 * seulement lorsqu'il a préalablement intercepté le message de vote d'une loi
	 * venant de l'agent loi.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant :Vote la loi récupérée dans le message
	 * demandant de voter une loi par envoyée par l'agent loi.
	 * <p>
	 *
	 * @see RequestToVote
	 * @author Benoit
	 * @version 1.3
	 */
	class VoteLoi extends OneShotBehaviour {
		private String mess;
		private ACLMessage message;

		// Constructor
		public VoteLoi(ACLMessage message2) {
			this.message = message2;
			this.mess = message.getContent();
		}

		// Task to do
		public void action() {

			// L'agent répond en précisant son vote
			ACLMessage reply = message.createReply();

			// Récupération de loi (On deserialise le message)
			Loi l = new Loi();
			ObjectMapper mapper = new ObjectMapper();
			try {
				l = mapper.readValue(message.getContent(), Loi.class);
			} catch (Exception ex) {
				System.out.println("EXCEPTION" + ex.getMessage());
			}
			// Calcul du score de la loi...
			float scoreLoi = A * l.getInfluence() + BP * l.getEffet_qualite_vie() + BE * l.getEffet_context_eco();
			scoreLoi += G * l.getCharisme() + APeuple * l.getPopularite() + BPeuple * l.getEffet_qualite_vie();
			scoreLoi += AEntreprise * l.getNotoriete() + BEntreprise * l.getEffet_context_eco();
			if (l.getL_PartiPolitique().contains(Parti_Politique))
				scoreLoi += D;

			// Vote oui ou non suivant le score de la loi et son hésitation.
			if (scoreLoi >= Hesitation) {
				reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);

				//Serialise réponse POUR au vote sous la forme d'un AID-Vote

				Aid_vote sondage_pour = new Aid_vote(myAgent.getAID().getLocalName(), "pour", Parti_Politique);

				// Gestion JSON : serialization
				ObjectMapper mapper1 = new ObjectMapper();
				StringWriter sw1 = new StringWriter();

				try {
					mapper1.writeValue(sw1, sondage_pour);
				} catch (IOException e) {
					e.printStackTrace();
				}
				String s1 = sw1.toString();
				reply.setContent(s1);

			} else {
				reply.setPerformative(ACLMessage.REJECT_PROPOSAL);

				//Serialise réponse CONTRE au vote sous la forme d'un AID-Vote
				Aid_vote sondage_contre = new Aid_vote(myAgent.getAID().getLocalName(), "contre", Parti_Politique);

				// Gestion JSON : serialization
				ObjectMapper mapper2 = new ObjectMapper();
				StringWriter sw2 = new StringWriter();

				try {
					mapper2.writeValue(sw2, sondage_contre);
				} catch (IOException e) {
					e.printStackTrace();
				}
				String s2 = sw2.toString();
				reply.setContent(s2);
			}
			// Envoie du vote
			myAgent.send(reply);
		}
	}

	/**
	 * <b>SondageLoi est le septième Behaviour de l'agent
	 * Député.</b>
	 * <p>
	 * Il est de type OneShot. Notre agent ne doit rendre son avis sur une loi
	 * seulement lorsqu'il a préalablement intercepté le message d'avis d'une loi
	 * venant de l'agent loi.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Donne son avis sur
	 * la loi récupérée dans le message demandant 
	 * de donner son avis sur choisie par l'utilisateur
	 *  une loi par envoyée par l'agent loi.
	 * <p>
	 *
	 * @see RequestToSondage
	 * @author Etienne
	 * @version 2.1
	 */
	class SondageLoi extends OneShotBehaviour {
		private String mess;
		private ACLMessage message;

		// Constructor
		public SondageLoi(ACLMessage message2) {
			this.message = message2;
			this.mess = message.getContent();
		}

		// Task to do
		public void action() {

			// L'agent répond en précisant son avis sur la loi
			ACLMessage reply = message.createReply();

			// Récupération de loi (On deserialise le message)
			Loi l = new Loi();
			ObjectMapper mapper = new ObjectMapper();
			try {
				l = mapper.readValue(message.getContent(), Loi.class);
			} catch (Exception ex) {
				System.out.println("EXCEPTION" + ex.getMessage());
			}
			// Calcul du score de la loi...
			float scoreLoi = A * l.getInfluence() + BP * l.getEffet_qualite_vie() + BE * l.getEffet_context_eco();
			scoreLoi += G * l.getCharisme() + APeuple * l.getPopularite() + BPeuple * l.getEffet_qualite_vie();
			scoreLoi += AEntreprise * l.getNotoriete() + BEntreprise * l.getEffet_context_eco();
			if (l.getL_PartiPolitique().contains(Parti_Politique))
				scoreLoi += D;

			// Alteration de l'avis donné par la crédibilité du député
			if (Credibilite > 80)
				scoreLoi -= (scoreLoi * 2) / 100;
			else if (Credibilite > 70)
				scoreLoi -= (scoreLoi * 7) / 100;
			else if (Credibilite > 60)
				scoreLoi -= (scoreLoi * 12) / 100;
			else if (Credibilite > 60)
				scoreLoi -= (scoreLoi * 20) / 100;

			// Vote oui ou non suivant le score de la loi et son hésitation.
			if (scoreLoi >= Hesitation) {

				System.out.print("Depute AID "+myAgent.getAID());

				reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);

				//Serialise réponse POUR au sondage sous la forme d'un AID-Vote
				Aid_vote sondage_pour = new Aid_vote(getAID().getLocalName(), "pour", Parti_Politique);

				// Gestion JSON : serialization
				ObjectMapper mapper1 = new ObjectMapper();
				StringWriter sw1 = new StringWriter();

				try {
					mapper1.writeValue(sw1, sondage_pour);
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.print("Debug sondage loi : "+sw1.toString());
				String s1 = sw1.toString();
				reply.setContent(s1);

			} else {

				System.out.print("Depute AID "+myAgent.getAID());

				reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
				//Serialise réponse CONTRE au sondage sous la forme d'un AID-Vote
				Aid_vote sondage_contre = new Aid_vote(getAID().getLocalName(), "contre", Parti_Politique);

				// Gestion JSON : serialization
				ObjectMapper mapper2 = new ObjectMapper();
				StringWriter sw2 = new StringWriter();


				try {
					mapper2.writeValue(sw2, sondage_contre);
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.print("Debug sondage loi : "+sw2.toString());
				String s2 = sw2.toString();
				reply.setContent(s2);

			}
			// Envoie de l'avis :)
			myAgent.send(reply);
		}
	}

	/**
	 * <b>AnswerRequestCharacteristicsFromRumourAgent est le huitième Behaviour de l'agent
	 * Député.</b>
	 * <p>
	 * Il est de type Cyclic. Notre agent député est en constante attente
	 * d'une requête REQUEST de l'agent Rumeur lui demandant ses caractéristiques.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Renvoie l'influence, la popularité et la crédibilité
	 * du député à l'agent Rumeur pour que celui-ci fasse le calcul.
	 * <p>
	 *
	 * @author Cristian
	 * @version 1.0
	 */
	class AnswerRequestCharacteristicsFromRumourAgent extends CyclicBehaviour {

		@Override
		public void action() {

			// On attend la reception d'un message de type REQUEST venant de
			// l'agent Rumeur
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchSender(ARumeur));
			ACLMessage message = myAgent.receive(mt);
			if (message != null) {
				// A la reception, l'agent député renvoie son influence, sa popularité et sa crédibilité
				// a l'agent Rumeur.
				ACLMessage answer = message.createReply();
				answer.setPerformative(ACLMessage.INFORM);

				ObjectMapper mapper1 = new ObjectMapper();
				StringWriter sw = new StringWriter();
				DeputeAttRumeur depAt = new DeputeAttRumeur(Influence, Popularite, Credibilite);

				try {
					mapper1.writeValue(sw, depAt);
					String s1 = sw.toString();
					answer.setContent(s1);
					myAgent.send(answer);
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}

			} else {
				block();
			}
		}
	}
}

