package agents;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import Class_For_JSON.*;
import ParlementSim.ParlementManager;
import agents.MediateurAgent.ActionFromUtilisateur;
import agents.MediateurAgent.PrecisionFromUtilisateur;
import agents.MediateurAgent.TourFromSimulation;
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
import jade.util.leap.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class KB extends Agent{
	
	//TODO Ajouter un paramètre de type ArrayList qui contiendra la liste des ID des lois déjà proposées afin de ne pas proposer deux fois une loi dans la même partie.
	//TODO A discuter.. Pas forcément le cas.
	String query;
	String querydist;
	Model model;
	
	AID ALoi;
	AID AMediateur;
	List<AID>List_Depute = new LinkedList();
	
	ParlementManager parl_mana = new ParlementManager();
	
	
	protected void setup() 
	{ 
		// Inscription aupr�s du DF
		DFAgentDescription dafd = new DFAgentDescription();
		dafd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("KB");
		sd.setName("AKB");
		dafd.addServices(sd);
		try {
			DFService.register(this, dafd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
		model = ModelFactory.createDefaultModel();
		try {
			model.read(new FileInputStream("Lois.n3"),null, "TURTLE");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		addBehaviour(new OneShotBehaviour(){

			
			@Override
			public void action() {
			
			// On r�cup�re les AID des agents n�cessaires
			while (AMediateur == null || ALoi == null){
				AMediateur = parl_mana.getReceiver(myAgent, "Parlement", "AMediateur");
				ALoi = parl_mana.getReceiver(myAgent, "Parlement", "ALoi");	
				List_Depute = parl_mana.getAllAidOf(myAgent, "Parlement", "ADepute");
			}
			addBehaviour(new RequestLawsFromUser()); // réception d'un message de type REQUEST avec un nom de parti, on doit répondre avec la liste des lois non encore votées correspondantes ou bien une seule

			
			addBehaviour(new VotedAckLaw()); // réception d'un message de type INFORM avec un id de loi que l'on doit passée à is_voted = true

			}});



		System.out.println("Agent KB créé : "+this.getLocalName());
	}
	
	//TODO split this behaviour in two different ones : from utilisateur and from deputé (first case : return this entire list of possible law, second case : juste a random one)
	class RequestLawsFromUser extends CyclicBehaviour{

		String parti_politique;
		
		@Override
		public void action() {

			// On attend la reception d'un message de type REQUETE 
			ACLMessage message;
			
			// Soit il vient du médiateur (qui a forwarder le choix de l'utilisateur
			MessageTemplate mt_mediateur = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchSender(AMediateur)
					);
			
			//Soit il vient d'un député à qui on doit renvoyer seulement une loi de la list en JSON
			MessageTemplate mt_depute = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			
			
			
			if ((message = receive(mt_mediateur))!= null){
				
				//Récupère le nom du parti
				parti_politique = message.getContent();
				
				ArrayList lois_du_parti = new ArrayList();
				
				List liste_d_objet_loi_du_parti = PossibleLaws(parti_politique);

				//Gestion JSON : serialization
				for( int y = 0; y < liste_d_objet_loi_du_parti.size(); y++) 
			       {
					Loi current_loi = (Loi) liste_d_objet_loi_du_parti.get(y);
					//TODO Delete
					current_loi.affiche();
					
					//Serialize en tableau JSON
					/*JSONObject current_json_object = new JSONObject();
					current_json_object.put("id", loi.getId());
				    //TODO Rajouter nom d'une loi ?
				    current_json_object.put("intitulé", name);
				      
				    current_json_object.put("description", loi.getDescription());
						
				    current_json_object.put("effet qualité vie", loi.getEffet_qualite_vie());
				    current_json_object.put("effet économique", loi.getEffet_context_eco());
						
				    json_array.add(current_json_object);*/
			       }
				
				ObjectMapper mapper1 = new ObjectMapper();
				StringWriter sw1 = new StringWriter();

				try {
					
					mapper1.writeValue(sw1, liste_d_objet_loi_du_parti);
					String s1 = sw1.toString();
					System.out.println("Tableau JSON de loi pour utilisateur : "+s1);
					
					// Réponse à celui qui nous a envoyé la demande
					ACLMessage reply =  message.createReply();
					reply.setPerformative(ACLMessage.INFORM);
					reply.setContent(s1);			
					myAgent.send(reply);
				}
				catch(Exception ex) {
					System.out.println(ex.getMessage());
				}
			}	
			
			
			
			else if((message = receive(mt_depute))!= null){
					
				//Récupère le nom du parti
				parti_politique = message.getContent();
				
				ArrayList lois_du_parti = new ArrayList();
				List liste_d_objet_loi_du_parti = PossibleLaws(parti_politique);

				Loi au_hasard =  (Loi) liste_d_objet_loi_du_parti.get((int)(Math.random() * liste_d_objet_loi_du_parti.size()));;
				
				ObjectMapper mapper = new ObjectMapper();
				StringWriter sw = new StringWriter();

				try {
					
					mapper.writeValue(sw, au_hasard);
					String s = sw.toString();
					//System.out.println("Loi choisie pour député : "+s);
					
					// Réponse au député ayant fait la demande
					ACLMessage reply =  message.createReply();
					reply.setPerformative(ACLMessage.INFORM);
					reply.setContent(s);			
					myAgent.send(reply);
				}
				catch(Exception ex) {
					System.out.println(ex.getMessage());
				}
				}
				
			else{
				block();
			}
		}
	}

	
	
	
	class VotedAckLaw extends CyclicBehaviour{

		String id_voted;
		
		@Override
		public void action() {

			// On attend la reception d'un message de type REQUETE 
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage message = myAgent.receive(mt);
			if (message != null){
				
				//Récupère le nom du parti
				id_voted = message.getContent().toString();
				
				String lawPrefix=model.getNsPrefixURI("law");
				
				//Fixe la propriété et on récupère la ressource qui correspond à l'id passée en paramètre et récupère de l'agent loi.
				Property voted_property =model.getProperty(lawPrefix+"is_voted");
				Resource law_voted_node = getLoiFromId(id_voted);
			
				//On itère sur le statement correspondant au noeud RDF de la loi qui l'on veut passer à "votée"
				StmtIterator it = model.listStatements(new SimpleSelector(law_voted_node,voted_property, (Resource)null));
				//System.out.println("Récupéreration du noeud correspondant à la loi : "+it.hasNext());
				
				//Liste contenant notre statement à modifier
				ArrayList statementsToModify = new ArrayList();
				//Obligation de JENA pour changer la valeur d'un noeud RDF il faut le faire en deux fois : d'abord itérer et récupèrer le statement à modifier
				if(it.hasNext()) {
					
					Statement stmt  = it.nextStatement(); 
					//Ajout à la liste des statement à modifier 
					statementsToModify.add(stmt); 
					//System.out.println("Valeur de object law-is_voted avant modif : "+stmt.getObject().toString());
					

				}
					
				//Parcourir la liste de sstatement à modifier en passant leur object du triplet law:is_voted à "true"
				for( int z = 0; z < statementsToModify.size(); z++) 
			       {
						Statement stmt_rm = (Statement) statementsToModify.get(z);
						stmt_rm.changeObject("true");
			            
			       }
				
				//TODO Delete Test de notre modification

				
			}else{
				block();
			}
		}
	}

	
	
	public List PossibleLaws(String _s){
		
		ArrayList lois_du_parti = new ArrayList();
		
		//Récupère loi du parti
		lois_du_parti = getLoiFromParti(_s);
		
		List liste_d_objet_loi_du_parti = new LinkedList();
		//ArrayList liste_d_objet_loi_d_un_parti = new ArrayList();
		
		for( int w = 0; w < lois_du_parti.size(); w++) 
	       {
				//Gestion selon le formalisme JENA pour chaque statement de la Arraylist on doit récupèrer le Sujet pour ainsi ensuite récupérer les valeurs de tous les objets en relation avec celui-ci
				Statement stmt_current = (Statement) lois_du_parti.get(w);
				Resource subject_law_current = stmt_current.getSubject();
				System.out.println("Loi pour les "+_s+" numéro"+w+"intitulé : "+subject_law_current.toString());
				
				//Récupère l'id, le nom, la desc, l'effet éco, l'effect lifestyle de chaque loi
				int id_current = (int) Long.valueOf(getIdFromSubject(subject_law_current)).longValue();				
				String name_current = getNameFromSubject(subject_law_current);
				String desc_current = getDescFromSubject(subject_law_current);
				float life_impact_current = getLifeFromSubject(subject_law_current);
				float eco_impact_current = getEcoFromSubject(subject_law_current);
				
				List<String> L_PartiPolitique = new LinkedList();
				L_PartiPolitique.add(_s);
				
				//Instancie un objet de type loi 
				Loi loi_temp = new Loi(id_current, desc_current, life_impact_current, eco_impact_current, L_PartiPolitique, null, 0, 0, 0, 0);	
				
				//Ajoute à la liste
				liste_d_objet_loi_du_parti.add(loi_temp);
	       }
		
		return liste_d_objet_loi_du_parti;
	}
	
	
	
	
	//Récupérer le subject d'une loi en fonction de l'id passé en paramètre
	public Resource getLoiFromId(String _id){
		
		String lawPrefix=model.getNsPrefixURI("law");
		String parlementPrefix=model.getNsPrefixURI("parlement");
		
		Property id_property =model.getProperty(lawPrefix+"id");
		ExtendedIterator<Statement> it = model.listStatements(new SimpleSelector((Resource)null,id_property,_id));
		//System.out.println("getLoiFrom id a des éléments : "+it.hasNext());
		if(it.hasNext()) 
			return it.next().getSubject();
		
		return 
				null;
	}
		
		
	//Récupère une liste de statement de loi d'un parti ssi elles n'ont pas été votée 
	public ArrayList getLoiFromParti(String _parti){
			
			ArrayList statementsToFromParti = new ArrayList();
			
			String lawPrefix=model.getNsPrefixURI("law");
			String parlementPrefix=model.getNsPrefixURI("parlement");
			
			Property politic_party_property =model.getProperty(lawPrefix+"politic_party");
			
			StmtIterator it = model.listStatements(new SimpleSelector((Resource)null,politic_party_property,_parti));
			//System.out.println("getLoiFromParti a des éléments : "+it.hasNext());
			if(it.hasNext())
				while(it.hasNext()) {
					
					Statement current_statement = it.nextStatement();
					//System.out.println("Loi de nom : "+current_statement.getSubject().toString());
					if(checkIfNotVoted(current_statement)){
						//System.out.println("Ajout !");
						statementsToFromParti.add(current_statement);
					}
				}
			else 
					return null;
			
			return statementsToFromParti;
		}

	//Prend un statement en paramètre, vérifie si cette loi n'a pas été votée
	public boolean checkIfNotVoted(Statement _st){
	
		Resource rsc = _st.getSubject();
		
		
		if(getIsVotedFromSubject(rsc).equalsIgnoreCase("false"))
		{
			//System.out.println("True : non votée ");
			return true;
		}
		
		else{
			//System.out.println("False : déjà votée ");
			return false;
			}
		
	}
	
	//récupère la chaine de caractère "id" d'un subject "loi" passé en paramètre
	public String getIdFromSubject(Resource _rsc){
			
			String lawPrefix=model.getNsPrefixURI("law");
			
			Property id_property =model.getProperty(lawPrefix+"id");
			StmtIterator it = model.listStatements(new SimpleSelector(_rsc,id_property,(Resource)null));
			//System.out.println("getIdFromSubject id a des éléments : "+it.hasNext());
			if(it.hasNext()) 
				return it.nextStatement().getObject().toString();
			
			return 
					null;
		}

	//récupère la chaine de caractère "name" d'un subject "loi" passé en paramètre
	public String getNameFromSubject(Resource _rsc){
		
		String lawPrefix=model.getNsPrefixURI("law");
		
		Property name_property =model.getProperty(lawPrefix+"name");
		StmtIterator it = model.listStatements(new SimpleSelector(_rsc,name_property,(Resource)null));
		//System.out.println("getNameFromSubject id a des éléments : "+it.hasNext());
		if(it.hasNext()) 
			return it.nextStatement().getObject().toString();
		
		return 
				null;
	}
	//récupère la chaine de caractère "desc" d'un subject "loi" passé en paramètre
	public String getDescFromSubject(Resource _rsc){
		
		String lawPrefix=model.getNsPrefixURI("law");
		
		Property desc_property =model.getProperty(lawPrefix+"desc");
		StmtIterator it = model.listStatements(new SimpleSelector(_rsc,desc_property,(Resource)null));
		//System.out.println("getDescFromSubject id a des éléments : "+it.hasNext());
		if(it.hasNext()) 
			return it.nextStatement().getObject().toString();
		
		return 
				null;
	}

	//récupère le float "impact_eco" d'un subject "loi" passé en paramètre
	public float getEcoFromSubject(Resource _rsc){
		
		String lawPrefix=model.getNsPrefixURI("law");
		
		Property eco_property =model.getProperty(lawPrefix+"eco_effect");
		StmtIterator it = model.listStatements(new SimpleSelector(_rsc,eco_property,(Resource)null));
		//System.out.println("getEcoFromSubject id a des éléments : "+it.hasNext());
		if(it.hasNext()) {
			
			String[] split_eco_string =  it.nextStatement().getObject().toString().split("http");
		    String split_eco_part = split_eco_string[0].substring(0,split_eco_string[0].length()-2);
			return Float.valueOf(split_eco_part).floatValue();
			}
		
		return 
				0;
	}

	//récupère le float "impact_lifestyle" d'un subject "loi" passé en paramètre
	public float getLifeFromSubject(Resource _rsc){
		
		String lawPrefix=model.getNsPrefixURI("law");
		
		Property life_property =model.getProperty(lawPrefix+"life_effect");
		StmtIterator it = model.listStatements(new SimpleSelector(_rsc,life_property,(Resource)null));
		//System.out.println("getLifeFromSubject id a des éléments : "+it.hasNext());
		if(it.hasNext()) {
			
				String[] split_life_string =  it.nextStatement().getObject().toString().split("http");
				String split_life_part = split_life_string[0].substring(0,split_life_string[0].length()-2);
				return Float.valueOf(split_life_part).floatValue();
				}
		
		return 
				0;
	}
	
	//récupère la chaine de caractère "is_voted" d'un subject "loi" passé en paramètre
		public String getIsVotedFromSubject(Resource _rsc){
				
				String lawPrefix=model.getNsPrefixURI("law");
				String parlementPrefix=model.getNsPrefixURI("parlement");
				
				Property id_property =model.getProperty(lawPrefix+"is_voted");
				StmtIterator it = model.listStatements(new SimpleSelector(_rsc,id_property,(Resource)null));
				//System.out.println("getIsVotedFromSubject id a des éléments : "+it.hasNext());
				if(it.hasNext()) 
					return it.nextStatement().getObject().toString();
				
				return 
						null;
		}
	
}




