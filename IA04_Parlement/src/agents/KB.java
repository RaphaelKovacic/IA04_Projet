package agents;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;


import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public class KB extends Agent{
	String query;
	String querydist;
	Model model;
	
	protected void setup() 
	{ 
		// Inscription auprès du DF
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
		addBehaviour(new WaitBehaviour());

	}

	class WaitBehaviour extends CyclicBehaviour{

		@Override
		public void action() {
			// On attend la réception de message de type REQUEST
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage message = myAgent.receive(mt);
			if (message != null){
				// A la reception, on lance un OneShotBehaviour qui s'occupe de traiter le message et de répondre
				myAgent.addBehaviour(new TraiteMessBehaviour(message));
			}else{
				block();
			}
		}
	}

	class TraiteMessBehaviour extends OneShotBehaviour{
		private String req_s;
		private ACLMessage message;

		// Constructor
		public TraiteMessBehaviour(ACLMessage message2) {
			this.message = message2;
			this.req_s = message.getContent();
		}

		// Task to do
		public void action() {
			//Execute la query
			Query query = QueryFactory.create(req_s);
			QueryExecution queryExecution = QueryExecutionFactory.create(query, model);
			ResultSet r = queryExecution.execSelect();
			
			// A MODIFIER
			// -> il faudrait transformer la réponse reçu en serialisant la liste de lois au format JSON...
			
			//répond avec le résultat de la query
			ACLMessage reply = message.createReply();
			reply.setPerformative(ACLMessage.INFORM);
			reply.setContent(ResultSetFormatter.asText(r));
			myAgent.send(reply);
			queryExecution.close();
		}
	}

}



