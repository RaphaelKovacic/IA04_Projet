package agents;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

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


public class DeputeAgent extends Agent{
	//Caractéristiques dynamiques
	float Influence;
	float Popularite;
	float Notoriete;
	float Credibilite;
	String Parti_Politique;
	
	//Caractéristiques statiques
	float Charisme;
	float Hesitation;
	
	//Coefficient (calcul du score pour faire passer une loi)
	float A,BP,BE,G,D;
	float APeuple, BPeuple, AEntreprise ,BEntreprise ;
	
	// Agent avec qui il peut communiquer
	AID ALoi;
	
	//Liste de tous les partis existants.
	List<String> L_Parti;
	
	ParlementManager parl_mana = new ParlementManager();

	protected void setup() 
	{ 
		// Enregistrement auprès du DF
		DFAgentDescription dafd = new DFAgentDescription();
		dafd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Parlement");
		sd.setName("ADepute");
		dafd.addServices(sd);
		try {
			DFService.register(this, dafd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
		//Initialisation de la liste des partis #Divergente
		L_Parti = new ArrayList<String>();
		L_Parti.add("Altruistes");L_Parti.add("Erudits");L_Parti.add("Audacieux");
		L_Parti.add("Sinceres");L_Parti.add("Fraternels");
		
		// Initialisation des caractéristiques dynamiques
		Credibilite = (float)(Math.random() * 100); // Entre 0 et 100 au départ
		Influence = (float)(Math.random() * 70); // Entre 0 et 70 au départ
		Parti_Politique = L_Parti.get((int)(Math.random() * L_Parti.size())); // aléatoire
		Popularite = (float)(Math.random() * 100); // Entre 0 et 100 au départ
		Notoriete =  (float)(Math.random() * 100); // Entre 0 et 100 au départ
		
		// Initialisation des caractéristiques statiques
		Charisme = (float)(Math.random() * 100); // Entre 0 et 100 au départ
		
		A = 2; 	// coeff influence
		BP = 1;	// coeff EffetQualitedeVie
		BE = 1;	// coeff EffetEconomie
		G = 1;	// coeff Charisme
		D = 50; // +50 si la loi fait parti du parti..
		
		APeuple = BPeuple = AEntreprise = BEntreprise = (float)Math.random(); //entre 0 et 1

		// Soutien du peuple et des entreprises pas pris en compte (pas encore implémenté...)
		float higher = (A*100+BP*25+BE*25+G*100+D*1+(APeuple*100+BPeuple*25+AEntreprise*100+BEntreprise*25)); // maximum du score pour "voter une loi"
		float lower = (A*0+BP*(-25)+BE*(-25)+G*0+D*0+(APeuple*0+BPeuple*(-25)+AEntreprise*0+BEntreprise*(-25)));// minimum du score pour "voter une loi"
		Hesitation = (float)(Math.random() * (higher-lower)) + lower; // un peu de random ne fait pas de mal :D

		System.out.println("Agent Député créé : "+this.getLocalName());
		
		addBehaviour(new OneShotBehaviour(){
			@Override
			public void action() {
				// On récupère les AID des agents nécessaires
				while (ALoi == null){
					ALoi = parl_mana.getReceiver(myAgent, "Parlement", "ALoi");				
				}
				addBehaviour(new RequestToProposeLaw()); // recéption d'un message demandant de proposer une loi (provient de ALoi)
				addBehaviour(new RequestToVote()); // réception d'un message demandant de voter pour une loi (provient de ALoi)
				addBehaviour(new RequestToModifCara()); // réception d'un message demandant de modifier ses cara (provient de ALoi)
			}});

	}

	class RequestToProposeLaw extends CyclicBehaviour{

		@Override
		public void action() {

			// On attend la reception d'un message de type REQUEST venant de l'agent Loi
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchSender(ALoi)
					);
			ACLMessage message = myAgent.receive(mt);
			if (message != null){
				// A la reception, l'agent propose a Aloi une loi qu'il aimerait faire passer.
				myAgent.addBehaviour(new ProposeLaw(message));
			}else{
				block();
			}
		}
	}

	class ProposeLaw extends OneShotBehaviour{
		private String mess;
		private ACLMessage message;

		// Constructor
		public ProposeLaw(ACLMessage message2) {
			this.message = message2;
			this.mess = message.getContent();
		}

		// Task to do
		public void action() {

			// On cherche quelle est la loi que l'on va proposer (plus tard dans l'onthologie... A MODIFIER)


			// On renvoie un message de type PROPOSE serialise contenant cette loi
			ObjectMapper mapper1 = new ObjectMapper();
			StringWriter sw = new StringWriter();

			// Ceci est A MODIFIER : loi écrite en dur ici pour commencer...
			List<String> l_PartiPolitique = new ArrayList<String>();
			l_PartiPolitique.add(Parti_Politique);
			Loi or = new Loi(1, "description", -5 , 5, l_PartiPolitique,myAgent.getLocalName(),Influence,Charisme,Popularite,Notoriete);
			try {
				mapper1.writeValue(sw, or);
				String s1 = sw.toString();

				ACLMessage reply = message.createReply();
				reply.setPerformative(ACLMessage.PROPOSE);
				reply.setContent(String.valueOf(s1));
				myAgent.send(reply);
			}
			catch(Exception ex) {}
		}
	}

	class RequestToVote extends CyclicBehaviour{

		@Override
		public void action() {

			// On attend la reception d'un message de type PROPOSE venant de l'agent Loi
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
					MessageTemplate.MatchSender(ALoi));
			ACLMessage message = myAgent.receive(mt);
			if (message != null){
				// A la reception, l'agent propose a Aloi une loi qu'il aimerait faire passer.
				myAgent.addBehaviour(new VoteLoi(message));
			}else{
				block();
			}
		}
	}
	
	class RequestToModifCara extends CyclicBehaviour{

		@Override
		public void action() {

			// On attend la reception d'un message de type INFORM venant de l'agent Loi
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchSender(ALoi));
			ACLMessage message = myAgent.receive(mt);
			if (message != null){
				// A la reception, on met à jour les caract de l'agent
				// On deserialise le message contenant les valeurs de variables à modifer.
				ObjectMapper mapper = new ObjectMapper();
				try {
					MajDepute ort = mapper.readValue(message.getContent(),MajDepute.class);
					// Mise a jour des  variables avec les valeurs du message
					Influence += ort.getEffet_Influence();
					Popularite += ort.getEffet_Popularite();
					Notoriete+= ort.getEffet_Notoriete();
					Credibilite += ort.getEffet_Credibilite();
					
					if (Influence< 0)
						Influence = 0;
					if (Popularite< 0)
						Popularite = 0;
					if (Notoriete< 0)
						Notoriete = 0;
					if (Credibilite< 0)
						Credibilite = 0;
				}
				catch(Exception ex) {
					System.out.println("EXCEPTION" + ex.getMessage());
				}

				/*// Juste pour debug
				System.out.println("");
				System.out.println(myAgent.getLocalName());
				System.out.println("Influence :"+Influence);*/
			}else{
				block();
			}
		}
	}

	class VoteLoi extends OneShotBehaviour{
		private String mess;
		private ACLMessage message;

		// Constructor
		public VoteLoi(ACLMessage message2) {
			this.message = message2;
			this.mess = message.getContent();
		}

		// Task to do
		public void action() {

			// 	L'agent répond en précisant son vote
			ACLMessage reply = message.createReply();
			
			//Récupération de loi (On deserialise le message)
			Loi l = new Loi();
			ObjectMapper mapper = new ObjectMapper();
			try {
				l = mapper.readValue(message.getContent(), Loi.class);
			}
			catch(Exception ex) {
				System.out.println("EXCEPTION" + ex.getMessage());
			}
			//Calcul du score de la loi...
			float scoreLoi = A*l.getInfluence()+BP*l.getEffet_qualite_vie()+BE*l.getEffet_context_eco();
			scoreLoi += G*l.getCharisme()+APeuple*l.getPopularite()+BPeuple*l.getEffet_qualite_vie();
			scoreLoi += AEntreprise*l.getNotoriete()+BEntreprise*l.getEffet_context_eco();
			if (l.getL_PartiPolitique().contains(Parti_Politique))
				scoreLoi += D;
			
			// Vote oui ou non suivant le score de la loi et son hésitation.
			if (scoreLoi >= Hesitation){
				reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
				reply.setContent("Je vote pour");				
			}else{
				reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
				reply.setContent("Je vote contre");	
			}
			// Envoie du vote
			myAgent.send(reply);
		}
	}
}