package launcher;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
public class AgentLauncher {
	public static int NB_DEPUTE = 5;

	public static void main(String[] args) {

		Runtime rt = Runtime.instance();
		ProfileImpl p = null;
		try {
			p = new ProfileImpl("./Properties/SecondaryProperties.txt");
			ContainerController cc = rt.createAgentContainer(p);

			//Agent d'environnement
			AgentController ac1 = cc.createNewAgent("AEnvironnement", "agents.EnvironmentalAgent", null);
			ac1.start();
			
			//Agent Loi
			(cc.createNewAgent("ALoi","agents.LoiAgent", null)).start();
			
			//Agent KB
			(cc.createNewAgent("AKB","agents.KB", null)).start();
			
			//Agent Sondage
			(cc.createNewAgent("ASondage","agents.SondageAgent", null)).start();
			
			//Agent Mediateur
			(cc.createNewAgent("AMediateur","agents.MediateurAgent", null)).start();
			
			//Agent Utilisateur
			(cc.createNewAgent("AUtilisateur","agents.UtilisateurAgent", null)).start();

			//Agents Députés
			int i = 0;
			while(i<NB_DEPUTE){
				(cc.createNewAgent("ADepute"+i,"agents.DeputeAgent", null)).start();
				i+=1;
			}
			
			//Agent Simulation
			AgentController ac = cc.createNewAgent("ASimulation","agents.SimulationAgent", null);
			ac.start();
			
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}

