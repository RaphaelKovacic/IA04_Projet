package ParlementSim;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.ArrayList;
import java.util.List;

public class ParlementManager{
//	AID AEnvironnement;
//	AID AMediateur;
//	AID ASimulation;
//	AID ALoi;
//	AID ASondage;
//	AID AUtilisateur;
//	List<AID> L_ADepute;
//
//	public ParlementManager() {
//		AEnvironnement = getReceiver;
//		AMediateur = aMediateur;
//		ASimulation = aSimulation;
//		ALoi = aLoi;
//		ASondage = aSondage;
//		AUtilisateur = aUtilisateur;
//		L_ADepute = l_ADepute;
//	}

	public AID getReceiver(Agent a,String S1, String S2) {
		AID rec = null;
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(S1);
		sd.setName(S2);
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(a, template);
			if (result.length > 0){
				int i = (int)(Math.random() * (result.length));
				rec = result[i].getName();				
			}
		} catch(FIPAException fe) {

			System.out.println(fe.getMessage());
		}
		return rec;
	}
	
	public List<AID> getAllAidOf(Agent a,String S1, String S2) {
		List<AID> L_AID = new ArrayList<AID>();
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(S1);
		sd.setName(S2);
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(a, template);
			if (result.length > 0){
				for (int i=0 ; i<result.length ; i++ )
					L_AID.add(result[i].getName());
			}
		} catch(FIPAException fe) {

			System.out.println(fe.getMessage());
		}
		return L_AID;
	}
}
