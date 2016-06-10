package launcher;

import graphicInterface.MainApp;
import jade.core.ProfileImpl;

import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

/**
 * <b>AgentLauncher est la classe représentant servant à initialiser notre
 * conteneur spécifique Parlement .</b>
 * <p>
 * La classe AgentLauncher possède l'attribut suivant :
 * <ul>
 * <li>Le nombre de député à intialiser dans notre parlement</li>
 * </ul>
 * </p>
 *
 *
 * @author Benoit
 * @version 1.0
 */

public class AgentLauncher {

	/**
	 * Le nombre d'agent Depute à instancier dans notre conteneur Parlement.
	 *
	 * @see #main(String[])
	 */
	public static final int NB_DEPUTE = 30;

	/**
	 * Méthode de création de notre conteneur secondaire Parlement.
	 * <p>
	 * On crée notre conteneur secondaire à l'aide du fichier de propriétés
	 * SecondaryProperties.txr du dossier Proprietes On crée tous les agents
	 * nécessaires en définissant leur nom qui nous servira pour communiquer,
	 * plus tard, avec eux. Chaque nom est préfixé par un "A" signifiant
	 * "Agent".
	 * </p>
	 *
	 * @param args
	 * 			arguments de base de la fonction main
	 *
	 */
	public static void main(String[] args) {

		Runtime rt = Runtime.instance();
		ProfileImpl p = null;
		try {
			p = new ProfileImpl("./Properties/SecondaryProperties.txt");
			ContainerController cc = rt.createAgentContainer(p);

			// Agent d'environnement
			AgentController ac1 = cc.createNewAgent("AEnvironnement", "agents.EnvironmentalAgent", null);
			ac1.start();

			// Agent Loi
			(cc.createNewAgent("ALoi", "agents.LoiAgent", null)).start();

			// Agent KB
			(cc.createNewAgent("AKB", "agents.KB", null)).start();

			// Agent Sondage
			(cc.createNewAgent("ASondage", "agents.SondageAgent", null)).start();

			// Agent Mediateur
			(cc.createNewAgent("AMediateur", "agents.MediateurAgent", null)).start();

			// Agent Utilisateur
			(cc.createNewAgent("AUtilisateur", "agents.UtilisateurAgent", null)).start();

			// Agents Députés
			int i = 0;
			while (i < NB_DEPUTE) {
				(cc.createNewAgent("ADepute" + i, "agents.DeputeAgent", null)).start();
				i += 1;
			}

			// Agent Rumeur
			(cc.createNewAgent("ARumeur", "agents.RumeurAgent", null)).start();

			// Agent Simulation
			AgentController ac = cc.createNewAgent("ASimulation", "agents.SimulationAgent", null);
			ac.start();
			MainApp.main(args);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
