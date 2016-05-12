package main;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;

public class MainBoot {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String MAIN_PROPERTIES_FILE = "./Properties/MainProperties.txt";
		Runtime rt = Runtime.instance();
		Profile p = null;
		try{
		p = new ProfileImpl(MAIN_PROPERTIES_FILE);
		AgentContainer mc = rt.createMainContainer(p);
		}
		catch(Exception ex) {
			
			System.out.println("ExceptionMainController");
		}
	}
}
