package agents;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.List;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import Class_For_JSON.*;
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
 * <b>KB est la classe représentant l'agent JADE Knowledge Base.</b>
 * <p>
 * L'agent KB possède les attributs suivants :
 * <ul>
 * <li>Un attribut model, classe de JENA stockant le modèle RDF</li>
 * <li>L'AID de l'agent loi pour pouvoir rapidement communiquer avec lui</li>
 * <li>L'AID de l'agent médiateur pour les mêmes raisons qu'au dessus</li>
 * <li>Le manager du parlement pour recevoir les AID ci-dessus</li>
 * </ul>
 * </p>
 * <p>
 * La première classe sert à l'instanciation de l'agent Les comportements de
 * l'agent KB sont spécifiés dans les deux classes suivantes.
 * Les fonctions utilisées sont répertoriées à la fin de ce fichier.
 * </p>
 * 
 * 
 * @author Benoit & Etienne
 * @version 2.2
 */

@SuppressWarnings("serial")
public class KB extends Agent {

	/**
	 * Le model à charger depuis notre fichier dans le formalisme turle.
	 * Non modifiable
	 * 
	 * @see #setup()
	 */
	Model model;

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
	 * Le manager du parlement. Non modifiable
	 * 
	 * @see #setup()
	 */
	ParlementManager parl_mana = new ParlementManager();

	/**
	 * Méthode d'instanciation (appelée à la création) de notre agent KB
	 * <p>
	 * Lors du lancement de notre plateforme JADE, l'agent KB est créé grâce à
	 * cette méthode setup()
	 * </p>
	 */
	protected void setup() {
		// Inscription auprès du DF
		DFAgentDescription dafd = new DFAgentDescription();
		dafd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("KB");
		sd.setName("AKB");
		dafd.addServices(sd);
		try {
			DFService.register(this, dafd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		model = ModelFactory.createDefaultModel();
		try {
			model.read(new FileInputStream("Lois.n3"), null, "TURTLE");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		addBehaviour(new OneShotBehaviour() {

			@Override
			public void action() {

				// On récupère les AID des agents nécessaires
				while (AMediateur == null || ALoi == null) {
					AMediateur = parl_mana.getReceiver(myAgent, "Parlement", "AMediateur");
					ALoi = parl_mana.getReceiver(myAgent, "Parlement", "ALoi");
				}
				addBehaviour(new RequestLaw()); // réception d'un message de type REQUEST avec un nom de
												// parti, on doit répondre avec la liste des lois (ou bien
												// une seule) non encore votées correspondantes

				addBehaviour(new VotedAckLaw()); // réception d'un message de type INFORM avec un id de
												 // loi que l'on doit passer à is_voted = true

			}
		});

		System.out.println("Agent KB créé : " + this.getLocalName());
	}

	/**
	 * <b>RequestLaw est le premier Behaviour de l'agent KB.</b>
	 * <p>
	 * Il est de type Cyclic. Notre agent KB est en constante attente d'une
	 * requête d'un agent extérieur pouvant le soliciter.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Retourne une loi (au député le
	 * requêtant) ou une liste de loi (à l'agent médiateur forwardant le message
	 * de l'utilisateur)
	 * <p>
	 * 
	 * @author Etienne
	 * @version 2.1
	 */
	class RequestLaw extends CyclicBehaviour {

		/**
		 * Le parti politique contenu dans le message reçu. Non modifiable.
		 * 
		 * @see #action()
		 */
		String parti_politique;

		/**
		 * Méthode contenant les actions effectuées par le Behaviour.
		 * <p>
		 * L'ensemble des tâches réalisées par le Behaviour RequestLaw est
		 * contenu ici.
		 * </p>
		 */

		@Override
		public void action() {

			// On attend la reception d'un message de type REQUEST
			ACLMessage message;

			// Soit il vient du médiateur et on doit renvoyer toutes les lois
			// correspondants à ce parti (qui a forwarder le choix de
			// l'utilisateur)
			MessageTemplate mt_mediateur = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchSender(AMediateur));

			// Soit il vient d'un député à qui on doit renvoyer seulement une
			// loi de la liste en JSON
			MessageTemplate mt_depute = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);

			// --------------------------------------------
			// POUR UTILISATEUR
			// --------------------------------------------
			if ((message = receive(mt_mediateur)) != null) {

				// Récupère le nom du parti
				parti_politique = message.getContent();

				List<Loi> liste_d_objet_loi_du_parti = new ArrayList<Loi>();
				liste_d_objet_loi_du_parti = PossibleLaws(parti_politique);

				// Gestion JSON : serialization
				ObjectMapper mapper1 = new ObjectMapper();
				StringWriter sw1 = new StringWriter();

				try {

					mapper1.writeValue(sw1, liste_d_objet_loi_du_parti);
					String s1 = sw1.toString();

					// Réponse à celui qui nous a envoyé la demande
					ACLMessage reply = message.createReply();
					reply.setPerformative(ACLMessage.INFORM);
					reply.setContent(s1);
					myAgent.send(reply);
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
			}

			// --------------------------------------------
			// POUR DÉPUTÉ
			// --------------------------------------------
			else if ((message = receive(mt_depute)) != null) {

				// Récupère le nom du parti
				parti_politique = message.getContent();

				List<Loi> liste_d_objet_loi_du_parti = new ArrayList<Loi>();
				liste_d_objet_loi_du_parti = PossibleLaws(parti_politique);

				Loi au_hasard = liste_d_objet_loi_du_parti
						.get((int) (Math.random() * liste_d_objet_loi_du_parti.size()));

				ObjectMapper mapper = new ObjectMapper();
				StringWriter sw = new StringWriter();

				try {

					mapper.writeValue(sw, au_hasard);
					String s = sw.toString();

					// Réponse au député ayant fait la demande
					ACLMessage reply = message.createReply();
					reply.setPerformative(ACLMessage.INFORM);
					reply.setContent(s);
					myAgent.send(reply);
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
			}

			else
				block();
		}
	}

	/**
	 * <b>VotedAckLaw est le second Behaviour de l'agent KB.</b>
	 * <p>
	 * Ce second behaviour est de type Cyclic. Notre agent KB est en constante
	 * attente d'un acquittement concernant une loi qui viendrait d'être votée.
	 * </p>
	 * <p>
	 * Il implémente le comportement suivant : Modifie le model JENA pour
	 * "désactiver" les lois votées afin que celles-ci ne puissent l'être une et
	 * une seule fois.
	 * <p>
	 * 
	 * @author Etienne
	 * @version 1.2
	 */

	class VotedAckLaw extends CyclicBehaviour {

		String id_voted;

		@Override
		public void action() {

			// On attend la reception d'un message de type INFORM de la part de
			// l'agent loi

			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchSender(ALoi));

			ACLMessage message = myAgent.receive(mt);
			if (message != null) {

				// Récupère le nom du parti
				id_voted = message.getContent();

				String lawPrefix = model.getNsPrefixURI("law");

				// Fixe la propriété et on récupère la ressource qui correspond
				// à l'id passée en paramètre et récupère de l'agent loi.
				Property voted_property = model.getProperty(lawPrefix + "is_voted");
				Resource law_voted_node = getLoiFromId(id_voted);

				// On itère sur le statement correspondant au noeud RDF de la
				// loi qui l'on veut passer à "votée"
				StmtIterator it = model
						.listStatements(new SimpleSelector(law_voted_node, voted_property, (Resource) null));

				// Liste contenant notre statement à modifier
				ArrayList<Statement> statementsToModify = new ArrayList<Statement>();

				// Obligation de JENA pour changer la valeur d'un noeud RDF il
				// faut le faire en deux fois : d'abord itérer et récupèrer le
				// statement à modifier
				if (it.hasNext()) {

					Statement stmt = it.nextStatement();

					// Ajout à la liste des statement à modifier
					statementsToModify.add(stmt);

				}

				// Parcourir la liste de sstatement à modifier en passant leur
				// object du triplet law:is_voted à "true"
				for (int z = 0; z < statementsToModify.size(); z++) {
					Statement current_statement_rm = statementsToModify.get(z);
					current_statement_rm.changeObject("true");

				}

			} else
				block();

		}
	}

	/**
	 * Renvoie une liste de loi disponibles (non encore votées) pour un parti
	 * spécifique.
	 * 
	 * @param _s
	 *            Chaîne de caractère correspondant au parti politique dont on
	 *            cherche les lois disponibles au vote
	 * 
	 * Retourne une liste d'objet de type Loi correspondant aux lois
	 *            disponibles au vote pour le parti.
	 * 
	 * @return Une liste d'objets Loi.
	 * 
	 * @see Class_For_JSON.Loi
	 */

	public List<Loi> PossibleLaws(String _s) {

		ArrayList<Statement> statement_lois_du_parti = new ArrayList<Statement>();

		// Récupère loi du parti
		statement_lois_du_parti = getLoiFromParti(_s);

		List<Loi> liste_d_objet_loi_du_parti = new ArrayList<Loi>();
		System.out.println();
		System.out.println("-----------------------DEBUG KB LOIS POSSIBLES ------------------------------");
		for (int w = 0; w < statement_lois_du_parti.size(); w++) {
			// Gestion selon le formalisme JENA pour chaque statement de la
			// Arraylist on doit récupèrer le Sujet pour ainsi ensuite récupérer
			// les valeurs de tous les objets en relation avec celui-ci
			Statement current_statement = statement_lois_du_parti.get(w);
			Resource subject_law_current = current_statement.getSubject();

			System.out.println("Loi pour les " + _s + " intitulé : " + subject_law_current.toString());

			// Récupère l'id, le nom, la desc, l'effet éco, l'effect lifestyle
			// de chaque loi
			int id_current = (int) Long.valueOf(getIdFromSubject(subject_law_current)).longValue();
			String name_current = getNameFromSubject(subject_law_current);

			String desc_current = getDescFromSubject(subject_law_current);
			float life_impact_current = getLifeFromSubject(subject_law_current);
			float eco_impact_current = getEcoFromSubject(subject_law_current);

			List<String> L_PartiPolitique = new ArrayList<String>();
			L_PartiPolitique.add(_s);

			// Instancie un objet de type loi
			Loi loi_temp = new Loi(id_current, name_current, desc_current, life_impact_current, eco_impact_current, L_PartiPolitique,
					null, 0, 0, 0, 0);

			// Ajoute à la liste
			liste_d_objet_loi_du_parti.add(loi_temp);
		}
		System.out.println("-----------------------FIN DEBUG KB LOIS POSSIBLES ------------------------------");
		System.out.println();

		return liste_d_objet_loi_du_parti;
	}

	/**
	 * Renvoie le nom JENA de la loi correspondant à l'id passé en paramètre. On
	 * renvoie le sujet du triplet (sujet_voulu, law:id, _id)
	 * 
	 * @param _id
	 *            La chaine de caractère correspondant à un id de loi.
	 * 
	 * Retourne le nom JENA dans une ressource correspondant à l'id
	 *            de la loi voulue.
	 * 
	 * @return Une ressource JENA correspondant au sujet de l'id de la loi
	 *         passée en paramètre.
	 * 
	 */

	public Resource getLoiFromId(String _id) {

		String lawPrefix = model.getNsPrefixURI("law");

		Property id_property = model.getProperty(lawPrefix + "id");
		ExtendedIterator<Statement> it = model.listStatements(new SimpleSelector(null, id_property, _id));

		if (it.hasNext())
			return it.next().getSubject();

		return null;
	}

	/**
	 * Renvoie une liste de Statement JENA filtrée sur le nom du parti politique
	 * grâce au triple (sujet, law:politic_party, _parti)
	 * 
	 * @param _parti
	 *            Chaîne de caractère correspondant au parti politique dont on
	 *            cherche les lois disponibles au vote
	 * 
	 * Retourne une liste de statement correspondant aux triples
	 *            (loi, law:politic_party, _parti)
	 * 
	 * @return Une liste de statement.
	 * 
	 * @see #getIsVotedFromSubject(Resource)
	 * @see #checkIfNotVoted(Statement)
	 * 
	 */

	public ArrayList<Statement> getLoiFromParti(String _parti) {

		ArrayList<Statement> statementsLawFromParti = new ArrayList<Statement>();

		String lawPrefix = model.getNsPrefixURI("law");
		Property politic_party_property = model.getProperty(lawPrefix + "politic_party");

		StmtIterator it = model.listStatements(new SimpleSelector(null, politic_party_property, _parti));

		if (it.hasNext())
			while (it.hasNext()) {

				Statement current_statement = it.nextStatement();

				if (checkIfNotVoted(current_statement))
					statementsLawFromParti.add(current_statement);

			}
		else
			return null;

		return statementsLawFromParti;
	}

	/**
	 * Renvoie un booléen issu du test sur le statut d'une loi.
	 * 
	 * @param _st
	 *            Un statement JENA ou triple RDF
	 * 
	 * Retourne un booléen pour savoir si la loi n'a pas été votée
	 *            (true) ou votée (false)
	 * 
	 * @return Un booléen.
	 * 
	 * @see #getLoiFromParti(String)
	 * @see #getIsVotedFromSubject(Resource)
	 */

	public boolean checkIfNotVoted(Statement _st) {

		Resource rsc = _st.getSubject();

		return getIsVotedFromSubject(rsc).equalsIgnoreCase("false");

	}

	/**
	 * Renvoie la chaine de caractère correspondant à la valeur de
	 * la propriété law:id pour
	 * un sujet "loi" passé en paramètre. Assimilable à un "getter" JENA sur une
	 * propriété donnée avec un sujet passé en argument.
	 * 
	 * @param _rsc
	 *            Une ressource "objet" JENA
	 * 
	 * Retourne une chaine de caractère correspondant à la valeur de
	 *            la propriété law:id
	 * 
	 * @return Une chaine de caractère
	 * 
	 * @see Class_For_JSON.Loi
	 */

	public String getIdFromSubject(Resource _rsc) {

		String lawPrefix = model.getNsPrefixURI("law");

		Property id_property = model.getProperty(lawPrefix + "id");
		StmtIterator it = model.listStatements(new SimpleSelector(_rsc, id_property, (Resource) null));

		if (it.hasNext())
			return it.nextStatement().getObject().toString();

		return null;
	}

	/**
	 * Renvoie la chaine de caractère correspondant à la valeur de la propriété law:name pour
	 * un sujet "loi" passé en paramètre. Assimilable à un "getter" JENA sur une
	 * propriété donnée avec un sujet passé en argument.
	 * 
	 * @param _rsc
	 *            Une ressource "objet" JENA
	 * 
	 * Retourne une chaine de caractère correspondant à la valeur de
	 *            la propriété law:name
	 * 
	 * @return Une chaine de caractère
	 * 
	 * @see Class_For_JSON.Loi
	 */

	public String getNameFromSubject(Resource _rsc) {

		String lawPrefix = model.getNsPrefixURI("law");

		Property name_property = model.getProperty(lawPrefix + "name");
		StmtIterator it = model.listStatements(new SimpleSelector(_rsc, name_property, (Resource) null));

		if (it.hasNext())
			return it.nextStatement().getObject().toString();

		return null;
	}

	/**
	 * Renvoie la chaine de caractère correspondant à la valeur de la propriété law:id pour
	 * un sujet "loi" passé en paramètre. Assimilable à un "getter" JENA sur une
	 * propriété donnée avec un sujet passé en argument.
	 * 
	 * @param _rsc
	 *            Une ressource "objet" JENA
	 * 
	 * Retourne une chaine de caractère correspondant à la valeur de
	 *            la propriété law:id
	 * 
	 * @return Une chaine de caractère
	 * 
	 * @see Class_For_JSON.Loi
	 */

	public String getDescFromSubject(Resource _rsc) {

		String lawPrefix = model.getNsPrefixURI("law");

		Property desc_property = model.getProperty(lawPrefix + "desc");
		StmtIterator it = model.listStatements(new SimpleSelector(_rsc, desc_property, (Resource) null));

		if (it.hasNext())
			return it.nextStatement().getObject().toString();

		return null;
	}

	/**
	 * Renvoie le float correspondant à la valeur de la propriété law:eco_effect pour un
	 * sujet "loi" passé en paramètre. Assimilable à un "getter" JENA sur une
	 * propriété donnée avec un sujet passé en argument.
	 * 
	 * @param _rsc
	 *            Une ressource "objet" JENA
	 * 
	 * Retourne un float correspondant à la valeur de la propriété
	 *            law:eco_effect qui correspondant à l'effet économique d'une
	 *            loi
	 * 
	 * @return Un float
	 * 
	 * @see Class_For_JSON.Loi
	 */

	public float getEcoFromSubject(Resource _rsc) {

		String lawPrefix = model.getNsPrefixURI("law");

		Property eco_property = model.getProperty(lawPrefix + "eco_effect");
		StmtIterator it = model.listStatements(new SimpleSelector(_rsc, eco_property, (Resource) null));

		if (it.hasNext()) {

			String[] split_eco_string = it.nextStatement().getObject().toString().split("http");
			String split_eco_part = split_eco_string[0].substring(0, split_eco_string[0].length() - 2);
			return Float.valueOf(split_eco_part).floatValue();
		}

		return 0;
	}

	/**
	 * Renvoie le float correspondant à la valeur de la propriété law:life_effect pour un
	 * sujet "loi" passé en paramètre. Assimilable à un "getter" JENA sur une
	 * propriété donnée avec un sujet passé en argument.
	 * 
	 * @param _rsc
	 *            Une ressource "objet" JENA
	 * 
	 * Retourne un float correspondant à la valeur de la propriété
	 *            law:life_effect qui correspondant à l'effet sur la qualité de
	 *            vie d'une loi
	 * 
	 * @return Un float
	 * 
	 * @see Class_For_JSON.Loi
	 */
	public float getLifeFromSubject(Resource _rsc) {

		String lawPrefix = model.getNsPrefixURI("law");

		Property life_property = model.getProperty(lawPrefix + "life_effect");
		StmtIterator it = model.listStatements(new SimpleSelector(_rsc, life_property, (Resource) null));

		if (it.hasNext()) {

			String[] split_life_string = it.nextStatement().getObject().toString().split("http");
			String split_life_part = split_life_string[0].substring(0, split_life_string[0].length() - 2);
			return Float.valueOf(split_life_part).floatValue();
		}

		return 0;
	}

	/**
	 * Récupère le sujet du triplet RDF (loi_a_examiner, law:is_voted,
	 * sujet_voulu) sous la forme d'une chaine de caractère.
	 * 
	 * @param _rsc
	 *            Une ressource JENA qui est un sujet "loi"
	 * 
	 * Retourne la chaine de caractère correspondant à la valeur de
	 *            la propriété is_voted pour le sujet loi passé en paramètre.
	 * 
	 * @return Une chaine de caractère
	 * 
	 * @see #getLoiFromParti(String)
	 * @see #checkIfNotVoted(Statement)
	 * @see Class_For_JSON.Loi
	 */

	public String getIsVotedFromSubject(Resource _rsc) {

		String lawPrefix = model.getNsPrefixURI("law");

		Property id_property = model.getProperty(lawPrefix + "is_voted");
		StmtIterator it = model.listStatements(new SimpleSelector(_rsc, id_property, (Resource) null));
		if (it.hasNext())
			return it.nextStatement().getObject().toString();
		return null;
	}

}
