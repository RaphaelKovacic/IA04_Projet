package agents;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import agents.DeputeAgent.AnswerRequestCharacteristicsFromRumourAgent;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import Class_For_JSON.DeputeAttRumeur;
import Class_For_JSON.Loi;
import Class_For_JSON.MajDepute;

import ParlementSim.ParlementManager;

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
 * <b>UtilisateurAgent est la classe représentant l'agent utilisateur dans notre
 * SMA Parlement.</b>
 * <p>
 * L'agent Utilisateur possède les attributs suivants
 * <ul>
 * <li>Un attribut Influence qui représente l'influence de l'utilisateur</li>
 * <li>Un attribut Popularite qui représente la popularité de l'utilisateur</li>
 * <li>Un attribut Notoriete qui représente la notoriété de l'utilisateur</li>
 * <li>Un attribut Credibilite qui représente la crédibilité de l'utilisateur
 * </li>
 * <li>Un attribut Parti_Politique qui stocke le parti politique de
 * l'utilisateur</li>
 * 
 * <li>Un attribut L_Parti qui est une liste de tous les partis politiques
 * possibles du jeu.</li>
 * 
 * <li>L'AID de l'agent loi pour pouvoir rapidement communiquer avec lui</li>
 * <li>L'AID de l'agent médiateur pour les mêmes raisons qu'au dessus</li>
 * <li>L'AID de l'agent simulation pour les mêmes raisons qu'au dessus</li>
 * <li>L'AID de l'agent rumeur pour les mêmes raisons qu'au dessus</li>
 * <li>Le manager du parlement pour recevoir les AID ci-dessus</li>
 * <li>Le grade actuel du joueur qui correspond à son niveau</li>
 * </ul>
 * </p>
 * <p>
 * La première classe sert à l'instanciation de l'agent Les comportements de
 * l'agent KB sont spécifiés dans les huit classes suivantes La fonction
 * utilisée se situe à la fin de ce fichier
 * </p>
 * 
 * 
 * @author Benoit & Etienne
 * @version 3.1
 */

@SuppressWarnings("serial")
public class UtilisateurAgent extends Agent {

	/**
	 * L'influence de l'utilisateur. Variable.
	 * 
	 * @see #setup()
	 */
	float Influence;

	/**
	 * La popularité de l'utilisateur. Variable. Sa cote d'amour auprès du
	 * peuple
	 * 
	 * @see #setup()
	 */
	float Popularite;

	/**
	 * La notoriété de l'utilisateur. Variable. Sa cote d'amour auprès des
	 * entreprises
	 * 
	 * @see #setup()
	 */
	float Notoriete;

	/**
	 * La crédibilité de l'utilisateur. Variable.
	 * 
	 * @see #setup()
	 */
	float Credibilite;

	/**
	 * Le parti politique de l'utilisateur. Variable.
	 * 
	 * @see #setup()
	 */
	String Parti_Politique;

	/**
	 * Le charisme de l'utilisateur. Statique.
	 * 
	 * @see #setup()
	 */
	float Charisme;

	/**
	 * La liste de tous les partis possibles de l'utilisateur. Constante.
	 * 
	 * @see #setup()
	 */
	List<String> L_Parti;

	/**
	 * L'AID de l'agent loi. Non modifiable
	 * 
	 * @see #setup()
	 */
	AID ALoi;

	/**
	 * L'AID de l'agent médiateur. Non modifiable
	 * 
	 * @see #setup()
	 */
	AID AMediateur;

    /**
     * L'AID de l'agent simulation. Non modifiable
     *
     * @see #setup()
     */
    AID ASimulation;
    
    /**
     * L'AID de l'agent simulation. Non modifiable
     *
     * @see #setup()
     */
    AID ARumeur;

	/**
	 * Le manager du parlement. Non modifiable
	 * 
	 * @see #setup()
	 */
	ParlementManager parl_mana = new ParlementManager();

	/**
	 * Le niveau actuel de l'utilisateur. Variable. 0 : simple député
	 * fraichement élu 1 : quand attributs statiques sont >50 2 : quand
	 * attributs statiques sont >65 3 : quand attributs statiques sont >80 4 :
	 * quand attributs statiques sont >90
	 * 
	 * @see #setup()
	 */
	int grade_utilisateur = 0;

	/**
	 * Méthode d'instanciation (appelée à la création) de notre agent
	 * Utilisateur
	 * <p>
	 * Lors du lancement de notre plateforme JADE, l'agent Utilisateur est créé
	 * grâce à cette méthode setup()
	 * </p>
	 */
	protected void setup() {
		// Enregistrement auprès du DF
		DFAgentDescription dafd = new DFAgentDescription();
		dafd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Parlement");
		sd.setName("AUtilisateur");
		dafd.addServices(sd);
		try {
			DFService.register(this, dafd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		// Initialisation de la liste des partis #Divergente
		L_Parti = new ArrayList<String>();
		L_Parti.add("Altruistes");
		L_Parti.add("Erudits");
		L_Parti.add("Audacieux");
		L_Parti.add("Sinceres");
		L_Parti.add("Fraternels");

		// Initialisation des caractèristiques dynamiques
		Credibilite = (float) (Math.random() * 100); // Entre 0 et 100 au départ
		Influence = (float) (Math.random() * 70); // Entre 0 et 70 au départ
		Parti_Politique = L_Parti.get((int) (Math.random() * L_Parti.size())); // aléatoire
		Popularite = (float) (Math.random() * 100); // Entre 0 et 100 au départ
		Notoriete = (float) (Math.random() * 100); // Entre 0 et 100 au départ

		// Initialisation des caractèristiques statiques
		Charisme = (float) (Math.random() * 100); // Entre 0 et 100 au départ

		System.out.println("Agent Utilisateur crée : " + this.getLocalName());

		addBehaviour(new OneShotBehaviour() {

			@Override
			public void action() {
				// On récupère les AID des agents nécessaires
				while (AMediateur == null || ALoi == null || ARumeur == null) {
					AMediateur = parl_mana.getReceiver(myAgent, "Parlement", "AMediateur");
					ALoi = parl_mana.getReceiver(myAgent, "Parlement", "ALoi");
                    ASimulation = parl_mana.getReceiver(myAgent, "Parlement", "ASimulation");
                    ARumeur = parl_mana.getReceiver(myAgent, "Parlement", "ARumeur");
				}
				addBehaviour(new LActionsFromMediateur()); // recéption d'un message proposant de choisir une
															// action parmis plusieurs actions.
				
				addBehaviour(new PrecisonActionFromMediateur()); // recéption d'un message demandant des
																// précisions sur l'action choisit
				
				addBehaviour(new RequestToVote()); // réception d'un message proventant de l'ALoi
													// demandant de voter pour une loi
				
				addBehaviour(new RequestToModifCara()); // réception d'un message proventant de
														// l'ALoi demandant de modifier ses caract
				
				addBehaviour(new ChangeParty()); // Changer de parti
				
				addBehaviour(new GiveYourInfo()); // Envoyer son parti à l'agent médiateur
				
				addBehaviour(new ChooseAlaw()); // Recevoir la liste des lois envoyée par mediateur afin de
												// les afficher et d'en choisir une
				
				addBehaviour(new ActualLevelOfUser()); // Pour checker et afficher le niveau actuel du joueur
				
				addBehaviour(new AnswerRequestCharacteristicsFromRumourAgent()); // envoie les caractéristiques (influence, ...) à la demande de l'agent Rumeur
			
				addBehaviour(new ReceiveDeputiesChoiceForRumours()); // reçoit les caractéristiques des députés pour répandre des rumeurs
			}
		});

	}

	/**
	 * <b>LActionsFromMediateur est le premier Behaviour de l'agent
	 * Utilisateur</b>
	 * <p>
	 * Il est de type Cyclic. Notre agent Utilisateur est en constante attente
	 * d'une requête PROPOSE de l'agent Médiateur avec les actions possibles de
	 * ce tour.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Affiche les actions possibles
	 * lors du tour de jeu en cours et la procédure pour les réaliser
	 * <p>
	 * 
	 * @author Benoit
	 * @version 2.1
	 */
	class LActionsFromMediateur extends CyclicBehaviour {

		@SuppressWarnings("unchecked")
		@Override
		public void action() {

			// On attend la reception d'un message de type PROPOSE venant de
			// l'agent Mediateur contenant la Liste des Actions possibles à ce
			// tour
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
					MessageTemplate.MatchSender(AMediateur));
			ACLMessage message = myAgent.receive(mt);
			if (message != null) {
				// On deserialise la liste d'actions.
				List<String> L_Actions = new ArrayList<String>();
				ObjectMapper mapper = new ObjectMapper();
				try {
					L_Actions = mapper.readValue(message.getContent(), L_Actions.getClass());

					// Ecrit sur la console
					System.out.println();
					System.out.println("-------------------- ACTIONS POSSIBLES ---------------");
					System.out.println("Voici la liste des actions possibles ce tour-ci");
					System.out.println(L_Actions.toString());
					System.out.println(
							"Renvoyer un message (ACCEPT_PROPOSAL) à l'agent Mediateur avec pour contenu une de ces actions");
					System.out.println("------------------FIN ACTIONS POSSIBLES ---------------");
					System.out.println();
				} catch (Exception ex) {
					System.out.println("EXCEPTION" + ex.getMessage());
				}
			} else
				block();

		}
	}

	/**
	 * <b>PrecisonActionFromMediateur est le second Behaviour de l'agent
	 * Utilisateur</b>
	 * <p>
	 * Il est de type Cyclic. Notre agent Utilisateur est en constante attente
	 * d'une requête REQUEST de l'agent Médiateur avec un complément
	 * d'insctruction à gérer par l'utilisateur concernant l'action d'un tour.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Afficher la procédure de
	 * changement de parti après que l'utilisateur ait choisi l'action de
	 * changer de parti.
	 * <p>
	 * 
	 * @author Etienne & Benoit
	 * @version 2.1
	 */
	class PrecisonActionFromMediateur extends CyclicBehaviour {

		@Override
		public void action() {

			// On attend la reception d'un message de type Request venant de
			// l'agent Mediateur (Demande d'informations suivant les actions
			// choisit)
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchSender(AMediateur));
			ACLMessage message = myAgent.receive(mt);
			if (message != null) {
				switch (message.getContent()) {

				case "Change de parti":
					// Construction de la liste des partis que l'utilisateur
					// peut choisir lors de son changement de parti
					List<String> liste_partis_possibles = new ArrayList<String>();
					liste_partis_possibles = L_Parti;

					liste_partis_possibles.remove(Parti_Politique);

					// Ecrit sur la console
					System.out.println("-------------------------CHANGEMENT DE PARTI----------------------");
					System.out.println("Vous êtes actuellement membre des " + Parti_Politique);
					System.out.println();
					System.out.println(
							"Préciser le nouveau parti que vous voulez intégrer en envoyant un message comme suit :");
					System.out.println("Voici la liste des partis possibles : " + liste_partis_possibles.toString());
					System.out.println("Vous allez perdre influence, popularité, notorieté et credibilité;");
					System.out.println(
							"Merci de répondre à l'Agent Utilisateur (CONFIRM) avec le nom du parti voulu pour le changement");
					System.out.println("-------------------------FIN CHANGEMENT DE PARTI----------------------");

					// Recrée la liste pour anticiper et gérer un éventuel
					// prochain changement de parti
					L_Parti.add(Parti_Politique);
					break;
				}

			} else
				block();
		}
	}

	/**
	 * <b>ChangeParty est le troisième Behaviour de l'agent Utilisateur</b>
	 * <p>
	 * Il est de type Cyclic. Notre agent Utilisateur est en constante attente
	 * d'une requête CONFIRM de du joueur avec le parti qu'il a choisi lors de
	 * l'action de changement de parti.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Récupère et modifie le parti de
	 * l'agent Utilisateur suite à l'action de changement de parti puis l'envoi
	 * du parti choisi par le joueur. C'est en quelque sorte la fin de l'action
	 * "changer de parti"
	 * <p>
	 * 
	 * @author Etienne
	 * @version 1.2
	 */
	class ChangeParty extends CyclicBehaviour {

		@Override
		public void action() {

			String parti_a_rejoindre;
			// On attend la reception d'un message de type Request venant de
			// l'agent Mediateur
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);

			ACLMessage message = myAgent.receive(mt);
			if (message != null) {
				parti_a_rejoindre = message.getContent();
				if (L_Parti.contains(parti_a_rejoindre)) {

					// TODO Ajuster l'impact du changement de parti :
					// actuellement -20% à toutes les caractéristiques.
					Influence = Influence - ((Influence * 20) / 100);
					Popularite = Popularite - ((Popularite * 20) / 100);
					Notoriete = Notoriete - ((Notoriete * 20) / 100);
					Credibilite = Credibilite - ((Credibilite * 20) / 100);
					VerifierCarac();

					// Rendre effectif le changement de parti
					Parti_Politique = parti_a_rejoindre;
					System.out.println("------------------------- NOUVEAU PARTI----------------------");
					System.out.println("Vous venez de rejoindre les " + Parti_Politique + " ! Bienvenue !");
					System.out.println("Voter pour la loi proposée ci-dessus pour finir le tour.");
					System.out.println("-------------------------FIN NOUVEAU PARTI----------------------");
				} else
					System.out.println(
							"Nom de parti non valide, veuillez recommencer votre changement de parti en renvoyant un message de type INFORM à l'agent Utilisateur");

			} else
				block();

		}
	}

	/**
	 * <b>RequestToVote est le quatirème Behaviour de l'agent Utilisateur</b>
	 * <p>
	 * Il est de type Cyclic. Notre agent Utilisateur est en constante attente
	 * d'une requête PROPOSE venant de l'agent médiateur et demandant au joueur
	 * de voter pour une loi.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Récupère la loi envoyée par
	 * l'agent médiateur et pour lequel l'utilisateur doit donner son vote.
	 * 
	 * Cette action de vote est obligatoire à chaque tour sauf si l'utilisateur
	 * choisi l'action "proposer une loi".
	 * 
	 * C'est cette action qui cloture le tour d'un utilisateur lorsqu'il n'a pas
	 * choisi de proposer une loi de lui même.
	 * <p>
	 * 
	 * @author Benoit
	 * @version 1.2
	 */
	class RequestToVote extends CyclicBehaviour {

		@Override
		public void action() {

			// On attend la reception d'un message de type REQUEST venant de
			// l'agent Loi
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
					MessageTemplate.MatchSender(ALoi));
			ACLMessage message = myAgent.receive(mt);
			if (message != null) {
				// L'utilisateur doit voter

				// Récupération de loi (On deserialise le message)
				Loi loi_en_cours = new Loi();
				ObjectMapper mapper = new ObjectMapper();
				try {
					loi_en_cours = mapper.readValue(message.getContent(), Loi.class);
				} catch (Exception ex) {
					System.out.println("EXCEPTION" + ex.getMessage());
				}
				// //Ecrit sur la console
				System.out.println();
				System.out.println("Vous devez voter pour la loi ci-desous");
				loi_en_cours.affiche();
				System.out.println("Merci de répondre à l'Agent Loi (ACCEPT_PROPOSAL ou REJECT_PROPOSAL)");

			} else {
				block();
			}
		}
	}

	/**
	 * <b>GiveYourInfo est le cinquième Behaviour de l'agent Utilisateur</b>
	 * <p>
	 * Il est de type Cyclic. Notre agent Utilisateur est en constante attente
	 * d'une requête QUERY_REF venant de l'agent médiateur et demandant à cette
	 * agent de fournir ses informations.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Fourni les informations sur lui
	 * même (info statiques) lorsqu'il le médiateur le demande.
	 * 
	 * Cette action est très importante lorsque l'utilisateur choisi de demander
	 * l'avis du parlement ou de proposer une loi puisque pour être votée ou
	 * sondée, une loi doit contenir les informations du proposant. Dans le cas
	 * de actions nommées ci avant ce sera les informations de l'utilisateur.
	 * <p>
	 * 
	 * @author Etienne
	 * @version 1.2
	 */
	class GiveYourInfo extends CyclicBehaviour {

		@Override
		public void action() {

			// On attend la reception d'un message de type QUERY_REF venant de
			// l'agent médiateur pour lui donner nos informations
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF),
					MessageTemplate.MatchSender(AMediateur));

			ACLMessage message = myAgent.receive(mt);
			if (message != null) {

				switch (message.getContent()) {

				case "Vos informations sur vous je vous prie":

					// On créé une loi temporaire dans laquelle on va stocker
					// nos informations propres qui sont importantes
					List<String> l_PartiPolitique = new ArrayList<String>();
					l_PartiPolitique.add(Parti_Politique);
					Loi utilisateur_carac = new Loi(0, null, null, 0, 0, l_PartiPolitique, "Utilisateur", Influence, Charisme,
							Popularite, Notoriete);

					// On sérialize cette loi que l'on envoie dans le message
					// INFORM REF en réponse, à Médiateur
					ObjectMapper mapper = new ObjectMapper();
					StringWriter sw = new StringWriter();

					try {
						mapper.writeValue(sw, utilisateur_carac);
						String s = sw.toString();
						ACLMessage reply = message.createReply();
						reply.setPerformative(ACLMessage.INFORM_REF);
						reply.setContent(s);
						myAgent.send(reply);

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			} else
				block();

		}
	}

	/**
	 * <b>ChooseAlaw est le sixième Behaviour de l'agent Utilisateur</b>
	 * <p>
	 * Il est de type Cyclic. Notre agent Utilisateur est en constante attente
	 * d'une requête QUERY_IF venant de l'agent médiateur et demandant à l'agent
	 * utilisateur d'afficher les loi disponibles pour la proposition de loi ou
	 * la demande d'avis du parlement.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Affiche à l'utilisateur les lois
	 * disponibles au vote ou à la demande d'avis et fournit la procédure à
	 * celui ci pour effectuer l'une des deux actions.
	 * 
	 * Cette action d'affichage termine la première partie des actions :
	 * Proposer une loi ou Avis du parlement.
	 * <p>
	 * 
	 * @author Etienne
	 * @version 1.6
	 */
	class ChooseAlaw extends CyclicBehaviour {

		@Override
		public void action() {

			// On attend la reception d'un message de type QUERY_I venant de
			// l'agent médiateur pour afficher les lois possibles
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF),
					MessageTemplate.MatchSender(AMediateur));

			ACLMessage message = myAgent.receive(mt);
			if (message != null) {

				// On deserialize la liste de loi contenue dans le message
				String content = message.getContent();

				// Stocke les lois envoyées par Mediateur dans liste de lois
				// locale
				List<Loi> Loi_a_choisir = new ArrayList<Loi>();

				try {
					Loi_a_choisir = new ObjectMapper().readValue(content, new TypeReference<List<Loi>>() {
					});
					// Tri des lois à afficher sur l'id de celle ci.Affiche le
					// plus petit ID en premier
					Collections.sort(Loi_a_choisir);
					// On les affiche
					System.out.println("-------------------------------------------------------");
					System.out.println("--------------------LOIS-------------------------------");
					for (int y = 0; y < Loi_a_choisir.size(); y++) {
						Loi_a_choisir.get(y).affiche_a_utilisateur();

					}
					System.out.println("------------------FIN LOIS-----------------------------");

					// On donne le protocole de réponse à l'utilisateur
					System.out.println("------------------RÉPONSE-----------------------------");

					// Affichage selon vote ou demande de sondage :
					if (message.getConversationId().equalsIgnoreCase("Proposition de loi"))
						System.out.println(
								"Envoyer un message de type INFORM_IF à l'agent médiateur avec seulement l'ID de la loi choisie et 'Proposition de loi' en conversation-id.");
					else
						System.out.println(
								"Envoyer un message de type INFORM_IF à l'agent médiateur avec seulement l'ID de la loi choisie et 'Demande de sondage' en conversation-id.");
					System.out.println("------------------FIN RÉPONSE-------------------------");
					System.out.println("-------------------------------------------------------");

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else
				block();

		}
	}

	/**
	 * <b>RequestToModifCara est le septième Behaviour de l'agent
	 * Utilisateur</b>
	 * <p>
	 * Il est de type Cyclic. Notre agent Utilisateur est en constante attente
	 * d'une requête INFORM venant de l'agent loi ou rumeur et demandant à l'agent
	 * utilisateur de modifier ses caractéristiques interne suite au vote d'une
	 * loi (soit qu'il a proposé ou bien qu'il a voté) ou à une rumeur.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Met à jour les informations
	 * internes de l'agent utilisateur après un vote ou une rumeur.
	 * 
	 * C'est la fin de l'action : Proposer une loi ou lorsque l'on vote une loi, ou bien lorsqu'on répand une rumeur.
	 * <p>
	 * 
	 * @author Benoit & Cristian
	 * @version 1.7
	 */
	class RequestToModifCara extends CyclicBehaviour {

		@Override
		public void action() {

			// On attend la reception d'un message de type INFORM venant de
			// l'agent Loi ou de l'agent Rumeur
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

					// TODO Utiliser fonction VerifCarac()
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

				// Juste pour debug
				System.out.println("");
				System.out.println(".....Debug....");
				System.out.println(myAgent.getLocalName());
				System.out.println("Influence :" + Influence);
				System.out.println("Popularite :" + Popularite);
				System.out.println("Notoriete :" + Notoriete);
				System.out.println("Credibilite :" + Credibilite);
                System.out.println("Moyenne :" + (Influence + Popularite + Notoriete + Credibilite)/4);
                System.out.println("Grade :" + grade_utilisateur);
			} else
				block();

		}
	}

	/**
	 * <b>ActualLevelOfUser est le huitième et dernier Behaviour de l'agent
	 * Utilisateur</b>
	 * <p>
	 * Il est de type Cyclic. Notre agent Utilisateur est en constante
	 * observation de l'agent utilisateur afin de modifier son niveau losque ses
	 * caractéristiques varient et entrainent un changement de niveau.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Met à jour le niveau de
	 * l'utilisateur et envoie un message de fin de partie à l'agent
     * simulation si c'est nécessaire.
	 * 
	 * <p>
	 * 
	 * @author Etienne
	 * @version 1.6
	 */
	class ActualLevelOfUser extends CyclicBehaviour {

		@Override
		public void action() {
            
			float moyenne = (Influence + Popularite + Notoriete + Credibilite ) /4;

			//Fin de la partie
			if (moyenne <= 20){
				System.out.println("---------------GAME OVER-----------------");
				System.out.println("Partie terminée. Vos caractéristiques sont trop basses ..");
				System.out.println("------------------------------------------");
				grade_utilisateur = -1;
			}

            //Achievements
			else if (moyenne >= 50 && moyenne < 60 && grade_utilisateur <= 1) {
				System.out.println("---------------ACHIEVEMENT-----------------");
				System.out.println("FÉLICITATION on vous écoute enfin un minimum dans cette assemblée ! ");
				System.out.println("------------------------------------------");
				grade_utilisateur = 2;
			}

			else if (moyenne >= 60 && moyenne < 70 && grade_utilisateur <= 2) {
				System.out.println("---------------ACHIEVEMENT-----------------");
				System.out.println("FÉLICITATION vous devenez rapporteur au budget de l'assemblée ! ");
				System.out.println("------------------------------------------");
				grade_utilisateur = 3;
			}

			else if (moyenne >= 70 && moyenne < 80 && grade_utilisateur <= 3) {
				System.out.println("---------------ACHIEVEMENT-----------------");
				System.out.println("FÉLICITATION vous devenez président des " + Parti_Politique + " au parlement ..");
				System.out.println("------------------------------------------");
				grade_utilisateur = 4;
			}

			else if (moyenne >= 80 && grade_utilisateur <= 4) {
				System.out.println("---------------GAGNÉ-----------------");
				System.out.println("FÉLICITATION vous devenez président de l'assemblée");
				System.out.println("------------------------------------------");
				grade_utilisateur = 5;
			}

            //Rétrogradations
            else if (moyenne < 50 && moyenne > 20 && grade_utilisateur >= 2) {
                System.out.println("-----------RÉTROGRADATION-----------");
                System.out.println("ATTENTION vous perdez de l'importance. Vous n'êtes plus qu'un simple député.");
                System.out.println("------------------------------------------");
                grade_utilisateur = 1;
            }
            else if (moyenne >= 50 && moyenne < 60 && grade_utilisateur >= 3) {
                System.out.println("-----------RÉTROGRADATION-----------");
                System.out.println("ATTENTION vous perdez de l'importance. Vous n'êtes plus qu'un simple député écouté.");
                System.out.println("------------------------------------------");
                grade_utilisateur = 2;
            }

            else if (moyenne >= 60 && moyenne < 70 && grade_utilisateur >= 4) {
                System.out.println("-----------RÉTROGRADATION-----------");
                System.out.println("ATTENTION vous perdez de l'importance. Vous n'êtes plus qu'un simple député rapporteur.");
                System.out.println("------------------------------------------");
                grade_utilisateur = 3;
            }


            // Envoi d'un message à l'agent de simulation pour finir le jeu
            if(grade_utilisateur == -1)
            {
                ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                message.addReceiver(ASimulation);
                message.setContent("perdu");
                myAgent.send(message);
            }

            // Envoi d'un message à l'agent de simulation pour finir le jeu
            else if (grade_utilisateur == 5){
                ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                message.addReceiver(ASimulation);
                message.setContent("gagne");
                myAgent.send(message);
            }

		}
	}

	/**
	 * Vérifie les informations de l'utilisateur afin qu'elles ne descendent
	 * jamais dans les négatifs.
	 * 
	 * @see Class_For_JSON.Loi
	 */
	public void VerifierCarac() {

		if (Influence < 0)
			Influence = 0;
		if (Popularite < 0)
			Popularite = 0;
		if (Notoriete < 0)
			Notoriete = 0;
		if (Credibilite < 0)
			Credibilite = 0;
	}
	
	/**
	 * <b>AnswerRequestCharacteristicsFromRumourAgent est le huitième Behaviour de l'agent
	 * Utilisateur</b>
	 * <p>
	 * Il est de type Cyclic. Notre agent utilisateur est en constante attente
	 * d'une requête REQUEST de l'agent Rumeur lui demandant ses caractéristiques.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Renvoie l'influence, la popularité et la crédibilité de l'utilisateur à l'agent Rumeur
	 * pour que celui-ci fasse le calcul.
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
	
	/**
	 * <b>ReceiveDeputiesChoiceForRumours est le neuvième Behaviour de l'agent
	 * Utilisateur</b>
	 * <p>
	 * Il est de type Cyclic. Notre agent utilisateur est en constante attente
	 * d'une requête PROPOSE de l'agent Rumeur lui proposant des caractéristiques de députés pour répandre des rumeurs.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Reçoit la liste des caractéristiques de députés pour répandre des rumeurs.
	 * <p>
	 *
	 * @author Cristian
	 * @version 1.0
	 */
	class ReceiveDeputiesChoiceForRumours extends CyclicBehaviour {

		@Override
		public void action() {

			// On attend la reception d'un message de type QUERY_I venant de
			// l'agent médiateur pour afficher les lois possibles
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
					MessageTemplate.MatchSender(ARumeur));

			ACLMessage message = myAgent.receive(mt);
			if (message != null) {

				// On deserialize la liste de loi contenue dans le message
				String content = message.getContent();

				// Stocke les caractéristiques envoyées par Mediateur dans liste de caractéristiques
				// locale
				List<DeputeAttRumeur> List_DeputeAttRumeur = new ArrayList<DeputeAttRumeur>();

				try {
					List_DeputeAttRumeur = new ObjectMapper().readValue(content, new TypeReference<List<DeputeAttRumeur>>() {
					});
					// On les affiche
					System.out.println("-------------------------------------------------------");
					System.out.println("--------------------DEPUTES-------------------------------");
					for (int y = 0; y < List_DeputeAttRumeur.size(); y++) {
						List_DeputeAttRumeur.get(y).affiche_a_utilisateur();

					}
					System.out.println("------------------FIN DEPUTES-----------------------------");

					// On donne le protocole de réponse à l'utilisateur
					System.out.println("------------------RÉPONSE-----------------------------");
					System.out.println(
								"Envoyer un message de type ACCEPT_PROPOSAL à l'agent rumeur avec seulement l'ID du député choisi");
					System.out.println("------------------FIN RÉPONSE-------------------------");
					System.out.println("-------------------------------------------------------");

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else
				block();

		}
	}
	
}