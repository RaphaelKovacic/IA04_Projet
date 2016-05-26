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


@SuppressWarnings("serial")
public class DeputeAgent extends Agent{
	//Caract�ristiques dynamiques
	
	/**
     * L'influence du député. Variable.
     * 
     * @see setup()
     */
	float Influence;
	
	/**
     * La popularité du député. Variable.
     * 
     * @see setup()
     */
	float Popularite;
	
	/**
     * La notoriété du député. Variable.
     * 
     * @see setup()
     */
	float Notoriete;
	
	/**
     * La crédibilité du député. Variable.
     * 
     * @see setup()
     */
	float Credibilite;
	
	/**
     * Le parti politique du député. Statique.
     * 
     * @see setup()
     */
	String Parti_Politique;
	
	//Caract�ristiques statiques
	
	/**
     * L'influence de l'utilisateur. Variable.
     * 
     * @see setup()
     */
	float Charisme;
	float Hesitation;
	
	//Coefficient (calcul du score pour faire passer une loi)
	float A,BP,BE,G,D;
	float APeuple, BPeuple, AEntreprise ,BEntreprise ;
	
	// Agent avec qui il peut communiquer
	AID ALoi;
	AID AKB;
	
	//Liste de tous les partis existants.
	List<String> L_Parti;
	
	ParlementManager parl_mana = new ParlementManager();

	protected void setup() 
	{ 
		// Enregistrement aupr�s du DF
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
		
		// Initialisation des caract�ristiques dynamiques
		Credibilite = (float)(Math.random() * 100); // Entre 0 et 100 au d�part
		Influence = (float)(Math.random() * 70); // Entre 0 et 70 au d�part
		Parti_Politique = L_Parti.get((int)(Math.random() * L_Parti.size())); // al�atoire
		Popularite = (float)(Math.random() * 100); // Entre 0 et 100 au d�part
		Notoriete =  (float)(Math.random() * 100); // Entre 0 et 100 au d�part
		
		// Initialisation des caract�ristiques statiques
		Charisme = (float)(Math.random() * 100); // Entre 0 et 100 au d�part
		
		A = 2; 	// coeff influence
		BP = 1;	// coeff EffetQualitedeVie
		BE = 1;	// coeff EffetEconomie
		G = 1;	// coeff Charisme
		D = 50; // +50 si la loi fait parti du parti..
		
		APeuple = BPeuple = AEntreprise = BEntreprise = (float)Math.random(); //entre 0 et 1

		// Soutien du peuple et des entreprises pas pris en compte (pas encore impl�ment�...)
		float higher = (A*100+BP*25+BE*25+G*100+D*1+(APeuple*100+BPeuple*25+AEntreprise*100+BEntreprise*25)); // maximum du score pour "voter une loi"
		float lower = (A*0+BP*(-25)+BE*(-25)+G*0+D*0+(APeuple*0+BPeuple*(-25)+AEntreprise*0+BEntreprise*(-25)));// minimum du score pour "voter une loi"
		Hesitation = (float)(Math.random() * (higher-lower)) + lower; // un peu de random ne fait pas de mal :D

		System.out.println("Agent D�put� cr�� : "+this.getLocalName());
		
		addBehaviour(new OneShotBehaviour(){
			@Override
			public void action() {
				// On r�cup�re les AID des agents n�cessaires
				while (ALoi == null || AKB == null){
					AKB = parl_mana.getReceiver(myAgent, "KB", "AKB");
					ALoi = parl_mana.getReceiver(myAgent, "Parlement", "ALoi");				
				}
				addBehaviour(new RequestToProposeLaw()); // rec�ption d'un message demandant de proposer une loi (provient de ALoi)
				addBehaviour(new RequestToVote()); // r�ception d'un message demandant de voter pour une loi (provient de ALoi)
				addBehaviour(new RequestToSondage()); // r�ception d'un message demandant de donner son avis pour une loi (provient de ALoi)
				addBehaviour(new RequestToModifCara()); // r�ception d'un message demandant de modifier ses cara (provient de ALoi)
				addBehaviour(new ProposeLaw()); //envoie de la loi récupéré par KB à la loi
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
				myAgent.addBehaviour(new GetLawToProposeFromKB());
			}else{
				block();
			}
		}
	}

	class GetLawToProposeFromKB extends OneShotBehaviour{

		// Task to do
		public void action() {

			// On envoie un message à l'agent KB pour récupérer une loi non encore votée correspondant à notre parti
			ACLMessage message1 = new ACLMessage(ACLMessage.REQUEST);
			message1.addReceiver(AKB);
			message1.setContent(Parti_Politique);
			myAgent.send(message1);
			
			// On crée le behaviour pour récupérer la loi proposée par KB et répondre à loi
			//myAgent.addBehaviour(new ProposeLaw(message));
		}
	}
	
	class ProposeLaw extends CyclicBehaviour{
		
		// Task to do
		public void action() {
			
			
			// On attend la reception d'un message de type REQUEST venant de l'agent Loi
			MessageTemplate mt = MessageTemplate.and(
							MessageTemplate.MatchPerformative(ACLMessage.INFORM),
							MessageTemplate.MatchSender(AKB)
							);
			ACLMessage message = myAgent.receive(mt);
			if (message != null){
					
				//On deserialize la loi envoyée par KB.
				ObjectMapper mapper = new ObjectMapper();
				try {
					Loi loi_de_kb = mapper.readValue(message.getContent(),Loi.class);
					//Ajout des informations du député
					loi_de_kb.setProposant(getAID().toString());
					loi_de_kb.setInfluence(Influence);
					loi_de_kb.setPopularite(Popularite);
					loi_de_kb.setNotoriete(Notoriete);
					loi_de_kb.setCharisme(Charisme);
					
					System.out.println("PARTI DU DÉPUTÉ PROPOSANT : "+Parti_Politique);

					ObjectMapper mapper1 = new ObjectMapper();
					StringWriter sw1 = new StringWriter();

					try {
						
						mapper1.writeValue(sw1, loi_de_kb);
						String s = sw1.toString();
						System.out.println("Loi choisie pour député dans député: "+s);
						
						//Répons à l'agent loi avec notre loi formatée en JSON avec les infos du député en plus.
						ACLMessage message2 = new ACLMessage(ACLMessage.PROPOSE);
						message2.addReceiver(ALoi);
						message2.setContent(String.valueOf(s));
						myAgent.send(message2);
						
				}
				catch(Exception ex) {
					System.out.println("EXCEPTION" + ex.getMessage());
				}
				
				}catch(Exception ex){}
						
			}
			else
				block();
			
		}
	}

	class RequestToVote extends CyclicBehaviour{

		@Override
		public void action() {

			// On attend la reception d'un message de type PROPOSE venant de l'agent Loi
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
					MessageTemplate.and(MessageTemplate.MatchSender(ALoi),
					MessageTemplate.MatchConversationId("Proposition de loi")));
			ACLMessage message = myAgent.receive(mt);
			if (message != null){
				// A la reception, l'agent propose a Aloi une loi qu'il aimerait faire passer.
				myAgent.addBehaviour(new VoteLoi(message));
			}else{
				block();
			}
		}
	}
	
	class RequestToSondage extends CyclicBehaviour{

		@Override
		public void action() {

			// On attend la reception d'un message de type PROPOSE venant de l'agent Loi demander l'avis du député pour une loi
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
					MessageTemplate.and(MessageTemplate.MatchSender(ALoi),
					MessageTemplate.MatchConversationId("Demande de sondage")));
			ACLMessage message = myAgent.receive(mt);
			if (message != null){
				
				myAgent.addBehaviour(new SondageLoi(message));
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
				// A la reception, on met � jour les caract de l'agent
				// On deserialise le message contenant les valeurs de variables � modifer.
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

			// 	L'agent r�pond en pr�cisant son vote
			ACLMessage reply = message.createReply();
			
			//R�cup�ration de loi (On deserialise le message)
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
			
			// Vote oui ou non suivant le score de la loi et son h�sitation.
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
	
	class SondageLoi extends OneShotBehaviour{
		private String mess;
		private ACLMessage message;

		// Constructor
		public SondageLoi(ACLMessage message2) {
			this.message = message2;
			this.mess = message.getContent();
		}

		// Task to do
		public void action() {

			// 	L'agent répond en précisant son avis sur la loi
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
			
			//Alteration de l'avis donné par la crédibilité du député
			if(Credibilite > 80)
				scoreLoi -= (scoreLoi *2)/100;
			else if(Credibilite >70)
				scoreLoi -= (scoreLoi *7)/100;
			else if(Credibilite >60)
				scoreLoi -= (scoreLoi *12)/100;
			else if(Credibilite >60)
				scoreLoi -= (scoreLoi *20)/100;
			
			
			// Vote oui ou non suivant le score de la loi et son hésitation.
			if (scoreLoi >= Hesitation){
				reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
				reply.setContent("Je vote pour");				
			}else{
				reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
				reply.setContent("Je vote contre");	
			}
			// Envoie de l'avis :)
			myAgent.send(reply);
		}
	}
}