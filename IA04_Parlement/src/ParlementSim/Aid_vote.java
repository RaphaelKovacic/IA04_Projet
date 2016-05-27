package ParlementSim;

import jade.core.AID;

/**
 * <b>Aid_vote est la classe représentant le vote d'un député.</b>
 * <p>
 * Un Aid_vote possède les attributs suivants :
 * <ul>
 * <li>Un AID qui est l'identifiant JADE du votant.</li>
 * <li>Une chaine de caractère correspondant à son vote.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Aid_vote est une classe qui est utilisée par l'agent loi (ALoi)
 * afin de garder l'historique des votes des membres du parlant à
 * chaque tour de vote (ou de demande d'avis)
 * </p>
 *
 * @author Benoit
 * @version 1.0
 */

public class Aid_vote {

	/**
	 * L'AID du votant.
	 *
	 * @see Aid_vote#getVotant()
	 * @see Aid_vote#setVotant(AID)
	 */
	AID votant;

	/**
	 * La valeur du vote du votant.
	 *
	 * @see Aid_vote#getVote()
	 * @see Aid_vote#setVote(String)
	 */
	String vote;


	/**
	 * Constructeur de Aid_vote.
	 * <p>
	 * À la contruction d'un Aid_vote, les attributs de l'objet sont initialisés.
	 * </p>
	 *
	 * @param votant
	 *            L'AID du votant.
	 * @param vote
	 *            La valeur du vote du votant.
	 * @see Aid_vote#votant
	 * @see Aid_vote#vote
	 *
	 *
	 */
	public Aid_vote(AID votant, String vote) {
		this.votant = votant;
		this.vote = vote;
	}

	/**
	 * Retourne l'AID du votant.
	 *
	 * @return L'identifiant JADE de l'agent votant dans le SMA.
	 */
	public AID getVotant() {
		return votant;
	}

	/**
	 * Met à jour l'AID du votant.
	 *
	 * @param votant
	 *            Le nouvel AID du votant.
	 */
	public void setVotant(AID votant) {
		this.votant = votant;
	}

	/**
	 * Retourne la valeur de vote du votant.
	 *
	 * @return La chaine de caractère qui correspond au vote du votant
	 */
	public String getVote() {
		return vote;
	}

	/**
	 * Met à jour le vote du votant.
	 *
	 * @param vote
	 *            Le nouveau vote du votant.
	 */
	public void setVote(String vote) {
		this.vote = vote;
	}
	
}
