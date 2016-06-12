package ParlementSim;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.ArrayList;
import java.util.List;


/**
 * <b>ParlementManager est la classe permettant de requêter le DF.</b>
 *
 * <p>
 * ParlementManager est utilisé par toutes les classe lorsqu'elles
 * veulent être mise en relation avec d'autres agents. Pour ce faire
 * elles ont besoin de connaitre l'AID  de l'agent à qui parler.
 * Or la plupart du temps elles ne connaissent que le nom (en dur)
 * de l'agent avec qui elles veulent communiquer.
 * Ainsi cette classe permet le matching entre les deux.
 * </p>
 *
 * @author Benoit
 * @version 1.0
 */

public class ParlementManager{

	/**
	 * Retourne l'AID de l'agent à partir de son nom et son type.
	 *
	 * @param
	 * 			a
	 * 		L'agent
	 * @param
	 * 			S1
	 * 		Le nom du Type d'agent que vous cherchez.
	 * @param
	 * 			S2
	 * 		Le nom de l'agent que vous cherchez.
	 *
	 * @return Un AID
	 */
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

	/**
	 * Retourne tous les AID de l'agent à partir de son nom et son type.
	 *
	 * Utilisé pour récupérer les AID de tous les députés de notre SMA.
	 *
	 * @param
	 * 			a
	 * 		L'agent
	 * @param
	 * 			S1
	 * 		Le nom du Type d'agent que vous cherchez.
	 * @param
	 * 			S2
	 * 		Le nom de l'agent que vous cherchez.
	 *
	 * @return Une liste d'AID
	 */
	public List<AID> getAllAidOf(Agent a,String S1, String S2) {
		List<AID> L_AID = new ArrayList<>();
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
