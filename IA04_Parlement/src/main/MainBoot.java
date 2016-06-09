package main;

import graphicInterface.IPChanger;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;

/**
 * <b>MainBoot est la classe représentant servant à initialiser notre plateforme
 * JADE.</b>
 * <p>
 * La classe MainBoot possède ne possède aucun attribut. La classe MainBoot
 * possède une seule méthode.
 * </p>
 *
 *
 * @author Benoit
 * @version 1.0
 */

public class MainBoot {

	/**
	 * Méthode d'instanciation de notre conteneur principal avec les agents DF,
	 * AMS et RMA La méthode utilise le fichier de configuration
	 * MainProperties.txt du dossier Properties.
	 * @param
	 * 		args
	 *
	 * 	Arguements de bases de la fonction main
	 */
	public static void main(String[] args) {

		String MAIN_PROPERTIES_FILE = "./Properties/MainProperties.txt";
		Runtime rt = Runtime.instance();
		Profile p = null;
		try {
			p = new ProfileImpl(MAIN_PROPERTIES_FILE);
			AgentContainer mc = rt.createMainContainer(p);
			IPChanger.main(args);

		} catch (Exception ex) {

			System.out.println("ExceptionMainController");
		}
	}
}
