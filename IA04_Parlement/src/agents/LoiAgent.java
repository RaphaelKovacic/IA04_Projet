package agents;

import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import Class_For_JSON.Loi;
import Class_For_JSON.MajDepute;
import Class_For_JSON.MajEnv;
import ParlementSim.Aid_vote;
import ParlementSim.ParlementManager;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import launcher.AgentLauncher;

/**
 * <b>LoiAgent est la classe représentant l'agent loi dans
 * notre SMA Parlement.</b>
 * <p>
 * L'agent loi possède les attributs suivants :
 * <ul>
 * <li>Un attribut L_AID_Vote qui représente la liste des AID des députés ayant voté ou donné leur avis</li>
 * <li>Un attribut loi_en_cours qui représente la loi en cours de vote ou de sondage</li>
 * <li>Un attribut proposant qui représente l'AID de l'agent proposant la loi à voter</li>
 * <li>Un attribut nb_votant qui représente le nombre de votant lors du tour de vote ou de sondage</li>
 * <li>Un attribut nb_vote_pour qui représente le nombre de députés POUR lors du tour de vote ou de sondage</li>
 * <li>Un attribut nb_vote_contre qui représente le nombre de député CONTRE lors du tour de vote ou de sondage</li>

 * <li>L'AID de l'agent mediateur pour pouvoir rapidement communiquer avec lui</li>
 * <li>L'AID de l'agent utilisateur pour pouvoir rapidement communiquer avec lui</li>
 * <li>L'AID de l'agent environnement pour pouvoir rapidement communiquer avec lui</li>
 * <li>L'AID de l'agent KB pour les mêmes raisons qu'au dessus</li>
 * <li>Une liste avec tous les AID des députés dans notre SMA</li>
 *
 * <li>Le manager du parlement pour recevoir les AID ci-dessus</li>
 *
 * </ul>
 * </p>
 * <p>
 * La première classe sert à l'instanciation de l'agent. Les comportements de
 * l'agent loi sont spécifiés dans les classes suivantes.
 * </p>
 *
 *
 * @author Benoit  Etienne
 * @version 2.3
 */
@SuppressWarnings("serial")
public class LoiAgent extends Agent {

	/**
	 * La liste des AID des agents ayant voté. Variable.
	 *
	 * @see #setup()
	 */
	List<Aid_vote> L_AID_Vote = new ArrayList<Aid_vote>();

	/**
	 * La loi en cours d'étude (vote ou sondage) à ce tour. Variable.
	 *
	 * @see #setup()
	 */
	Loi loi_en_cours = new Loi();

	/**
	 * L'AID du proposant à ce tour. Variable.
	 *
	 * @see #setup()
	 */
	AID proposant;

	/**
	 * Le nombre total de votant attendu lors du tour. Variable.
	 *
	 * @see #setup()
	 */
	int nb_votant;

	/**
	 * Le nombre de députés POUR lors de ce tour de vote ou sondage d'une loi. Variable.
	 *
	 * @see #setup()
	 */
	int nb_vote_pour;

	/**
	 * Le nombre de députés CONTRE lors de ce tour de vote ou sondage d'une loi. Variable.
	 *
	 * @see #setup()
	 */
	int nb_vote_contre;

	/**
	 * L'AID de l'agent médiateur. Non modifiable
	 *
	 * @see #setup()
	 */
	AID AMediateur;

	/**
	 * L'AID de l'agent utilisateur. Non modifiable
	 *
	 * @see #setup()
	 */
	AID AUtilisateur;

	/**
	 * L'AID de l'agent environnement. Non modifiable
	 *
	 * @see #setup()
	 */
	AID AEnvironnement;

	/**
	 * L'AID de l'agent KB. Non modifiable
	 *
	 * @see #setup()
	 */
	AID AKB;

	/**
	 * La liste des AID des agents députés du SMA. Non modifiable
	 *
	 * @see #setup()
	 */
	List<AID> List_Depute = new ArrayList<AID>();


	/**
	 * Le manager du parlement. Non modifiable
	 *
	 * @see #setup()
	 */
	ParlementManager parl_mana = new ParlementManager();


	/**
	 * Méthode d'instanciation (appelée à la création) de notre agent
	 * loi.
	 * <p>
	 * Lors du lancement de notre plateforme JADE, l'agent loi est créé
	 * grâce à cette méthode setup().
	 * </p>
	 */
	protected void setup() {
		// Enregistrement auprès du DF
		DFAgentDescription dafd = new DFAgentDescription();
		dafd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Parlement");
		sd.setName("ALoi");
		dafd.addServices(sd);
		try {
			DFService.register(this, dafd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		nb_votant = 0;
		nb_vote_pour = 0;
		nb_vote_contre = 0;
		proposant = null;

		System.out.println("Agent Loi créé : " + this.getLocalName());
		addBehaviour(new OneShotBehaviour() {

			@Override
			public void action() {
				// On récupère les AID des agents nécessaires
				while (AMediateur == null || AUtilisateur == null || AEnvironnement == null
						|| List_Depute.size() != AgentLauncher.NB_DEPUTE || AKB == null) {
					AKB = parl_mana.getReceiver(myAgent, "KB", "AKB");
					AMediateur = parl_mana.getReceiver(myAgent, "Parlement", "AMediateur");
					AUtilisateur = parl_mana.getReceiver(myAgent, "Parlement", "AUtilisateur");
					AEnvironnement = parl_mana.getReceiver(myAgent, "Monde", "AEnvironnement");
					List_Depute = parl_mana.getAllAidOf(myAgent, "Parlement", "ADepute");
				}

				nb_votant = List_Depute.size(); // Tous les députés + l'utilisateur - proposant = tous les députés

				addBehaviour(new RequestOfMediator()); // recéption d'un message demandant de faire
				// proposer une loi ou de faire voter une
				// loi

				addBehaviour(new ProposalLawOfDepute()); // réception d'une proposition de
				// loi d'un député

				addBehaviour(new AcceptLawOfDepute()); // réception d'un vote favorable d'un député
				// ou utilisateur.

				addBehaviour(new RefuseLawOfDepute());// réception d'un vote défavorable d'un
				// député ou utilisateur.

			}
		});

	}

	/**
	 * <b>RequestOfMediator est le premier Behaviour de l'agent
	 * loi.</b>
	 * <p>
	 * Il est de type Cyclic. Notre agent loi est en constante attente
	 * d'une requête REQUEST de l'agent mediateur l'informant que l'action
	 * en cours lors de ce tour est soit une proposition ou un avis demandé
	 * par l'utilisateur soit tout autre tour et dans ce cas il doit
	 * demander aux députés de lui soumettre une loi au vote.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Récupère le message de la part
	 * de l'agent mediateur. Analyse ce message et fait suivre le bon processus
	 * en fonction du message.
	 * <p>
	 *
	 * @see VoteLoi
	 * @see SondageLoi
	 * @see DemandeLoi
	 *
	 * @author Benoit  Etienne
	 * @version 3.6
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

				// On reinitialise les votes.
				nb_vote_pour = 0;
				nb_vote_contre = 0;
				L_AID_Vote.clear();
				proposant = null;
				// remettre a 0 loi en cours

				// Si le mediateur demande a l'agent Loi de faire proposer une
				// loi par un député
				if (message.getContent().contains("Proposer une loi")) {

					// l'agent loi demande à un agent député de proposer une loi
					myAgent.addBehaviour(new DemandeLoi(message));

					// Sinon
				} else {

					// TODO DELETE ? USELESS ?
					// Le proposant est forcément l'utilisateur
					proposant = AUtilisateur;

					// Récupération de loi (On deserialise le message)
					ObjectMapper mapper = new ObjectMapper();
					try {
						loi_en_cours = mapper.readValue(message.getContent(), Loi.class);
					} catch (Exception ex) {
						System.out.println("EXCEPTION" + ex.getMessage());
					}

					if (message.getConversationId().equalsIgnoreCase("Proposition de loi"))
						// l'agent loi demande à tous les députés de voter pour
						// la loi proposée par l'utilisateur.
						myAgent.addBehaviour(new VoteLoi(message));

					else if (message.getConversationId().equalsIgnoreCase("Demande de sondage"))
						// l'agent loi demande à tous les députés de se faire
						// sonder par rapport à une loi proposée par
						// l'utilisateur.
						myAgent.addBehaviour(new SondageLoi(message));

				}

			} else {
				block();
			}
		}
	}


	/**
	 * <b>DemandeLoi est le second Behaviour de l'agent
	 * loi.</b>
	 * <p>
	 * Il est de type OneShot. Notre agent ne va engager le processus
	 * de demander une loi à soumettre au vote que lorsque le bon
	 * message est reçu de la part du médiateur (voir Behaviour parent).
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Selectionne un député au hasard et lui demande 
	 * de proposer une loi.
	 * <p>
	 *
	 * @see RequestOfMediator
	 *
	 * @author Benoit
	 * @version 2.1
	 */
	class DemandeLoi extends OneShotBehaviour {
		private ACLMessage message;

		// Constructor
		public DemandeLoi(ACLMessage message2) {
			this.message = message2;
		}

		// Task to do
		public void action() {

			// Envoie d'un message a l'agent Député (aléatoire pour le moment)
			// pour qu'il propose une loi
			ACLMessage forward = message.createReply();
			forward.removeReceiver(message.getSender());
			proposant = List_Depute.get((int) (Math.random() * (List_Depute.size()))); // Retourne un député au hasard parmi tous
			forward.addReceiver(proposant);
			forward.setPerformative(ACLMessage.REQUEST);
			forward.setContent(message.getContent());
			myAgent.send(forward);

		}
	}

	/**
	 * <b>VoteLoi est le troisième Behaviour de l'agent
	 * loi.</b>
	 * <p>
	 * Il est de type OneShot. Notre agent ne va engager le processus
	 * de faire voter une loi que lorsque le bon message est reçu de la part
	 * du mediateur.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Envoie un message à tous les députés
	 * leur demandant de voter la loi contenue dans le message.
	 * <p>
	 *
	 * @see RequestOfMediator
	 *
	 * @author Benoit  Etienne
	 * @version 2.3
	 */
	class VoteLoi extends OneShotBehaviour {
		private ACLMessage message;

		// Constructor
		public VoteLoi(ACLMessage message2) {
			this.message = message2;
		}

		// Task to do
		public void action() {

			// Envoi d'un message a tous les députés (et utilisateur si besoin)
			// pour qu'ils votent pour la loi contenu dans le message
			ACLMessage forward = message.createReply();
			forward.removeReceiver(message.getSender());

			// On ajoute tous les députés en tant que destinataire
			for (int i = 0; i < List_Depute.size(); i++)
				forward.addReceiver(List_Depute.get(i));

			// Si la proposition de loi vient d'un député il faut rajouter
			// l'utilisateur pour qu'il puisse voter...
			if (List_Depute.contains(message.getSender()))
				forward.addReceiver(AUtilisateur);

			// Pas besoin de faire voter celui qui propose la loi...
			forward.removeReceiver(proposant);

			forward.setPerformative(ACLMessage.PROPOSE);
			forward.setContent(message.getContent());
			forward.setConversationId("Proposition de loi");
			myAgent.send(forward);

		}
	}

	/**
	 * <b>SondageLoi est le troisième Behaviour de l'agent
	 * loi.</b>
	 * <p>
	 * Il est de type OneShot. Notre agent ne va engager le processus
	 * de demander l'avis aux députés sur une loi que lorsque le bon message est reçu de la part
	 * du mediateur.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Envoie un message à tous les députés
	 * leur demandant leur avis sur la loi contenue dans le message.
	 * <p>
	 *
	 * @see RequestOfMediator
	 *
	 * @author Etienne
	 * @version 1.2
	 */
	class SondageLoi extends OneShotBehaviour {
		private ACLMessage message;

		// Constructor
		public SondageLoi(ACLMessage message2) {
			this.message = message2;
		}

		// Task to do
		public void action() {

			// Envoie d'un message a tous les députés (et utilisateur si besoin)
			// pour qu'ils se fassent sonder pour la loi contenu dans le message
			ACLMessage forward = message.createReply();
			forward.removeReceiver(message.getSender());

			// On ajoute tous les députés en tant que destinataire
			for (int i = 0; i < List_Depute.size(); i++)
				forward.addReceiver(List_Depute.get(i));

			// Si la proposition de loi vient d'un député il faut rajouter
			// l'utilisateur pour qu'il puisse voter...
			if (List_Depute.contains(message.getSender()))
				forward.addReceiver(AUtilisateur);

			// Pas besoin de faire voter celui qui propose la loi...
			forward.removeReceiver(proposant);

			forward.setPerformative(ACLMessage.PROPOSE);
			forward.setContent(message.getContent());
			forward.setConversationId("Demande de sondage");
			myAgent.send(forward);

		}
	}

	/**
	 * <b>ProposalLawOfDepute est le cinquième Behaviour de l'agent
	 * loi.</b>
	 * <p>
	 * Il est de type Cyclic. Notre agent est en constante attente d'une proposition
	 * de loi faite par un député qu'il faut soumettre au vote de l'assemblée dans son ensemble.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Récupère la loi à faire voter envoyée par un député.
	 * Envoie un message à tous les autres députés ainsi qu'à l'utilisateur leur demandant de voter pour cette loi.
	 * <p>
	 *
	 * @author Benoit  Etienne
	 * @version 2.1
	 */
	class ProposalLawOfDepute extends CyclicBehaviour {

		@Override
		public void action() {

			// On attend la reception d'un message de type PROPOSE venant de
			// l'agent proposant la loi
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
					MessageTemplate.MatchSender(proposant) // (peut etre pas
					// obligatoire)
			);
			ACLMessage message = myAgent.receive(mt);
			if (message != null) {

				// Récupération de loi (On deserialise le message)
				ObjectMapper mapper = new ObjectMapper();
				try {
					loi_en_cours = mapper.readValue(message.getContent(), Loi.class);
				} catch (Exception ex) {
					System.out.println("EXCEPTION" + ex.getMessage());
				}

				// On demande alors à tous (sauf la personne ayant proposé la
				// loi) de voter la loi
				myAgent.addBehaviour(new VoteLoi(message));
			} else {
				block();
			}
		}
	}

	/**
	 * <b>AcceptLawOfDepute est le sixième Behaviour de l'agent
	 * loi.</b>
	 * <p>
	 * Il est de type Cyclic. Notre agent est en constante attente d'une réponse positive (POUR)
	 * concernant la loi en cours.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Récupère la réponse positive, met à jour sa liste locale 
	 * qui tient à jour le vote de chaque député.
	 * Teste si on a reçu tous les votes pour la loi en cours d'étude.
	 * Si tel est le cas il faut instancier le behaviour de fin de vote ou de fin de sondage.
	 * <p>
	 *
	 * @see ConsequenceVote
	 * @see ConsequenceSondage
	 *
	 * @author Benoit  Etienne
	 * @version 2.1
	 */
	class AcceptLawOfDepute extends CyclicBehaviour {

		@Override
		public void action() {

			// On attend la reception d'un message de type Accept_Proposal
			// venant des votants
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
			ACLMessage message = myAgent.receive(mt);
			if (message != null) {
				// On met à jour les votes.
				nb_vote_pour++;

				if (List_Depute.contains(message.getSender())) {

					//Deserialise le Aid_vote envoyé par le député contenant son vote/avis au sondage
					Aid_vote current_vote = new Aid_vote();
					ObjectMapper mapper = new ObjectMapper();
					try {
						current_vote = mapper.readValue(message.getContent(), Aid_vote.class);
					} catch (Exception ex) {
						System.out.println("EXCEPTION" + ex.getMessage());
					}

					L_AID_Vote.add(current_vote);
				} else {
					//TODO Récupérer parti de l'utilisateur pour la liste sur l'historique du vote de chaque député au tour courant
					Aid_vote e = new Aid_vote(AUtilisateur.getLocalName(), "pour", null);
					L_AID_Vote.add(e);
				}

				// Si le nombre total des votants est atteint (vote terminé)
				if (L_AID_Vote.size() == nb_votant) {

					// Traitement des conséquences du vote
					if (message.getConversationId() == null
							|| message.getConversationId().equalsIgnoreCase("Proposition de loi"))
						myAgent.addBehaviour(new ConsequenceVote());
						// Traitement des conséquences de la demande de sondage
					else if (message.getConversationId().equalsIgnoreCase("Demande de sondage"))
						myAgent.addBehaviour(new ConsequenceSondage());
					else
						myAgent.addBehaviour(new ConsequenceVote());

				}

			} else {
				block();
			}
		}
	}


	/**
	 * <b>RefuseLawOfDepute est le septième Behaviour de l'agent
	 * loi.</b>
	 * <p>
	 * Il est de type Cyclic. Notre agent est en constante attente d'une réponse négative (CONTRE)
	 * concernant la loi en cours.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Récupère la réponse positive, met à jour sa liste locale 
	 * qui tient à jour le vote de chaque député.
	 * Teste si on a reçu tous les votes pour la loi en cours d'étude.
	 * Si tel est le cas il faut instancier le behaviour de fin de vote ou de fin de sondage.
	 * <p>
	 *
	 * @see ConsequenceVote
	 * @see ConsequenceSondage
	 *
	 * @author Benoit  Etienne
	 * @version 2.1
	 */
	class RefuseLawOfDepute extends CyclicBehaviour {

		@Override
		public void action() {

			// On attend la reception d'un message de type Accept_Proposal
			// venant des votants
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL);
			ACLMessage message = myAgent.receive(mt);
			if (message != null) {
				// On met à jour les votes.
				nb_vote_contre++;

				if (List_Depute.contains(message.getSender())) {

					//Deserialise le Aid_vote envoyé par le député contenant son vote/avis au sondage
					Aid_vote current_vote = new Aid_vote();
					ObjectMapper mapper = new ObjectMapper();
					try {
						current_vote = mapper.readValue(message.getContent(), Aid_vote.class);
					} catch (Exception ex) {
						System.out.println("EXCEPTION" + ex.getMessage());
					}

					L_AID_Vote.add(current_vote);

				} else {

					Aid_vote e = new Aid_vote(AUtilisateur.getLocalName(), "contre", null);
					L_AID_Vote.add(e);
				}

				// Si le nombre total des votants est atteint (vote terminé)
				if (L_AID_Vote.size() == nb_votant) {

					// Traitement des conséquences du vote
					if (message.getConversationId() == null
							|| message.getConversationId().equalsIgnoreCase("Proposition de loi"))
						myAgent.addBehaviour(new ConsequenceVote());
						// Traitement des conséquences de la demande de sondage
					else if (message.getConversationId().equalsIgnoreCase("Demande de sondage"))
						myAgent.addBehaviour(new ConsequenceSondage());
					else
						myAgent.addBehaviour(new ConsequenceVote());
				}
			} else {
				block();
			}
		}
	}

	/**
	 * <b>ConsequenceVote est le huitième Behaviour de l'agent
	 * loi.</b>
	 * <p>
	 * Il est de type OneShot. Notre agent va s'occuper de faire le récapitulatif du vote de la loi
	 * en cours.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Il effectue le vote en cours. Décide si la loi est passée ou non.
	 * Il va aussi envoyer un acquittement à l'agent KB si la loi est votée.
	 * Il va envoyer un message à tous les députés (utilisateur compris) pour leur demander de mettre
	 * à jour leur caractéristiques selon le résultat du vote.
	 * Enfin il envoie un message au médiateur pour lui signifier la fin du vote.
	 * <p>
	 *
	 * @see AcceptLawOfDepute
	 * @see RefuseLawOfDepute
	 *
	 * @author Benoit  Etienne
	 * @version 2.1
	 */
	class ConsequenceVote extends OneShotBehaviour {

		// Task to do
		public void action() {

			// Affichage récap vote
			System.out.println();
			System.out.println("---------------------------QUI A VOTÉ QUOI ?---------------------------");
			for (int z = 0; z < L_AID_Vote.size(); z++){
				Aid_vote current_AID_Vote = L_AID_Vote.get(z);
				current_AID_Vote.affiche();
			}
			System.out.println("-----------------------------------------------------------------------");

			System.out.println();
			// Le vote est terminé...
			System.out.println("---------------------------RÉSULTAT VOTE--------------------------------");
			System.out.println(
					"La Loi a été votée avec : " + nb_vote_pour + " vote Pour et " + nb_vote_contre + " vote Contre.");
			System.out.println("---------------------------FIN RÉSULTAT VOTE---------------------------");

			// Pour chaque député ayant voté
			for (int i = 0; i < L_AID_Vote.size(); i++) {

				ObjectMapper mapper1 = new ObjectMapper();
				StringWriter sw = new StringWriter();
				MajDepute or = new MajDepute(0, 0, 0, 0);

				// Si la loi passe
				if (nb_vote_pour > nb_vote_contre) {

					// Les personnes ayant votés "pour" voit leur influence
					// augmenter... (envoie de message de type INFORM)
					// Les personnes ayant votés "contre" voit leur influence
					// diminuer... (envoie de message de type INFORM)
					if (L_AID_Vote.get(i).getVote().contains("pour")) {
						or.setEffet_Influence(5);

						// qualite de vie
						if (loi_en_cours.getEffet_qualite_vie() > 0)
							or.setEffet_Popularite(5);
						else
							or.setEffet_Popularite(-5);

						// eco
						if (loi_en_cours.getEffet_context_eco() > 0)
							or.setEffet_Notoriete(5);
						else
							or.setEffet_Notoriete(-5);
					}

					else if (L_AID_Vote.get(i).getVote().contains("contre")) {
						or.setEffet_Influence(-5);

						// qualite de vie
						if (loi_en_cours.getEffet_qualite_vie() > 0)
							or.setEffet_Popularite(-5);
						else
							or.setEffet_Popularite(5);

						// eco
						if (loi_en_cours.getEffet_context_eco() > 0)
							or.setEffet_Notoriete(-5);
						else
							or.setEffet_Notoriete(5);
					}

					else { // rien vote (impossible pour le moment)
						or.setEffet_Influence(0);
						or.setEffet_Notoriete(0);
						or.setEffet_Popularite(0);
					}

					try {
						mapper1.writeValue(sw, or);
						String s1 = sw.toString();
						ACLMessage message1 = new ACLMessage(ACLMessage.INFORM);
						message1.addReceiver(getAID(L_AID_Vote.get(i).getVotant()));
						message1.setContent(s1);
						myAgent.send(message1);
					} catch (Exception ex) {
						System.out.println(ex.getMessage());
					}

					// Sinon
				} else {
					// Les personnes ayant votés "pour" voit leur influence
					// diminuer... (envoie de message de type INFORM)
					// Les personnes ayant votés "contre" voit leur influence
					// augmenter... (envoie de message de type INFORM)

					ACLMessage message1 = new ACLMessage(ACLMessage.INFORM);
					message1.addReceiver(getAID(L_AID_Vote.get(i).getVotant()));

					if (L_AID_Vote.get(i).getVote().contains("pour")) {
						or.setEffet_Influence(-5);

						// qualite de vie
						if (loi_en_cours.getEffet_qualite_vie() > 0)
							or.setEffet_Popularite(5);
						else
							or.setEffet_Popularite(-5);

						// eco
						if (loi_en_cours.getEffet_context_eco() > 0)
							or.setEffet_Notoriete(5);
						else
							or.setEffet_Notoriete(-5);
					}

					else if (L_AID_Vote.get(i).getVote().contains("contre")) {
						or.setEffet_Influence(5);

						// qualite de vie
						if (loi_en_cours.getEffet_qualite_vie() > 0)
							or.setEffet_Popularite(-5);
						else
							or.setEffet_Popularite(5);

						// eco
						if (loi_en_cours.getEffet_context_eco() > 0)
							or.setEffet_Notoriete(-5);
						else
							or.setEffet_Notoriete(5);
					}

					else { // rien vote (impossible pour le moment)
						or.setEffet_Influence(0);
						or.setEffet_Notoriete(0);
						or.setEffet_Popularite(0);
					}

					try {
						mapper1.writeValue(sw, or);
						String s1 = sw.toString();
						message1.setContent(s1);
						myAgent.send(message1);
					} catch (Exception ex) {
						System.out.println(ex.getMessage());
					}
				}
			}

			// Si la loi passe
			if (nb_vote_pour > nb_vote_contre) {

				// Envoi l'ACK à l'agent KB pour lui dire que la loi a été votée
				// et qu'elle n'est plus disponible
				ACLMessage message5 = new ACLMessage(ACLMessage.INFORM);
				message5.addReceiver(AKB);
				String loi_en_cours_id_string = String.valueOf(loi_en_cours.getId());
				message5.setContent(loi_en_cours_id_string);
				myAgent.send(message5);

				// Mise a jour des variables d'environnements.
				// On serialise le message contenant les 2 "valeurs" de
				// variables à modifer.
				ObjectMapper mapper1 = new ObjectMapper();
				StringWriter sw = new StringWriter();

				MajEnv env = new MajEnv(loi_en_cours.getEffet_context_eco(), loi_en_cours.getEffet_qualite_vie());
				try {
					mapper1.writeValue(sw, env);
					String s1 = sw.toString();
					ACLMessage message2 = new ACLMessage(ACLMessage.REQUEST);
					message2.addReceiver(AEnvironnement);
					message2.setContent(s1);
					myAgent.send(message2);
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
			}

			// Mise à jour des caracteritiques du proposant
			ObjectMapper mapper1 = new ObjectMapper();
			StringWriter sw = new StringWriter();
			MajDepute or = new MajDepute(0, 0, 0, 0);

			// qualite de vie
			if (loi_en_cours.getEffet_qualite_vie() > 0)
				or.setEffet_Popularite(-10);
			else
				or.setEffet_Popularite(10);

			// eco
			if (loi_en_cours.getEffet_context_eco() > 0)
				or.setEffet_Notoriete(-10);
			else
				or.setEffet_Notoriete(10);

			// Si la loi passe
			if (nb_vote_pour > nb_vote_contre) {
				or.setEffet_Influence(10);
				// Sinon
			} else
				or.setEffet_Influence(-10);

			try {
				mapper1.writeValue(sw, or);
				String s1 = sw.toString();
				ACLMessage message1 = new ACLMessage(ACLMessage.INFORM);
				message1.addReceiver(proposant);
				message1.setContent(s1);
				myAgent.send(message1);
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}

			// Dans tous les cas on envoie un message à l'agent Mediateur pour
			// le prévenir que le vote est terminé (fin du tour).
			ACLMessage message2 = new ACLMessage(ACLMessage.REQUEST);
			message2.addReceiver(AMediateur);
			message2.setContent("La loi a fini d'être votée.");
			myAgent.send(message2);
		}
	}

	/**
	 * <b>ConsequenceSondage est le neuvième et dernier Behaviour de l'agent
	 * loi.</b>
	 * <p>
	 * Il est de type OneShot. Notre agent va s'occuper de faire le récapitulatif de la demande de sondage
	 * dans le parlement pour la loi soumise au sondage.
	 *
	 * en cours.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Afficher le résultat de l'estimation.
	 * Envoie un message au médiateur pour lui signifier la fin du sondage dans le parlement
	 * <p>
	 *
	 * @see AcceptLawOfDepute
	 * @see RefuseLawOfDepute
	 *
	 * @author Etienne
	 * @version 1.2
	 */
	class ConsequenceSondage extends OneShotBehaviour {

		public void action() {

			// Affichage récap sondage
			System.out.println("---------------------------QUI PENSE QUOI ?----------------------------");
			for (int z = 0; z < L_AID_Vote.size(); z++){
				Aid_vote current_AID_Vote = L_AID_Vote.get(z);
				current_AID_Vote.affiche();
			}
			System.out.println("-----------------------------------------------------------------------");


			// Le sondage est terminé...
			System.out.println("---------------------------ESTIMATION----------------------------------");

			if (nb_vote_pour > nb_vote_contre)
				System.out.println("La loi semblerait pouvoir passer ... avec " + nb_vote_pour + " vote 'Pour' et "
						+ nb_vote_contre + " vote 'Contre'.");
			else if (nb_vote_pour < nb_vote_contre)
				System.out.println("La loi serait refoulée ... avec " + nb_vote_pour + " vote 'Pour' et "
						+ nb_vote_contre + " vote 'Contre'.");
			else
				System.out.println("La loi en balance total ... avec " + nb_vote_pour + " vote 'Pour' et "
						+ nb_vote_contre + " vote 'Contre'.");

			System.out.println("---------------------------FIN ESTIMATION-------------------------------");

			// Dans tous les cas on envoie un message à l'agent Mediateur pour
			// le prévenir que le vote est terminé (fin du tour).
			ACLMessage message2 = new ACLMessage(ACLMessage.REQUEST);
			message2.addReceiver(AMediateur);
			message2.setContent("La loi a fini d'être sondée.");
			message2.setConversationId("Demande de sondage");
			myAgent.send(message2);
		}
	}
}
