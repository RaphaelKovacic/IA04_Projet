package ParlementSim;

import jade.core.AID;

/**
 * <b>Aid_vote est la classe représentant le vote d'un député.</b>
 * <p>
 * Un Aid_vote possède les attributs suivants :
 * <ul>
 * <li>Un string qui est le local name du votant.</li>
 * <li>Une chaine de caractère correspondant à son vote.</li>
 * <li>Une chaine de caractère correspondant au parti politique du votant.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Aid_vote est une classe qui est utilisée par l'agent loi (ALoi)
 * afin de garder l'historique des votes des membres du parlant à
 * chaque tour de vote (ou de demande d'avis)
 * </p>
 *
 * @author Benoit  Etienne
 * @version 2.1
 */

public class Aid_vote {

	/**
	 * Le local nome du votant.
	 *
	 * @see Aid_vote#getVotant()
	 * @see Aid_vote#setVotant(String)
	 */
	String votant;

	/**
	 * La valeur du vote du votant.
	 *
	 * @see Aid_vote#getVote()
	 * @see Aid_vote#setVote(String)
	 */
	String vote;

	/**
	 * Le parti du votant.
	 *
	 * @see Aid_vote#getParti()
	 * @see Aid_vote#setParti(String)
	 */
	String parti;


	/**
	 * Constructeur de Aid_vote vide .
	 * <p>
	 * Constructeur d'une loi vierge de toute initialisation d'attributs.
	 * </p>
	 *
	 */

	public Aid_vote() {
	}
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
	 * @param parti
	 * 			  La valeur du parti du votant
	 *
	 * @see Aid_vote#votant
	 * @see Aid_vote#vote
	 * @see Aid_vote#parti
	 *
	 *
	 */
	public Aid_vote(String votant, String vote, String parti) {
		this.votant = votant;
		this.vote = vote;
		this.parti = parti;
	}

	/**
	 * Retourne l'AID du votant.
	 *
	 * @return L'identifiant JADE de l'agent votant dans le SMA.
	 */
	public String getVotant() {
		return votant;
	}

	/**
	 * Met à jour l'AID du votant.
	 *
	 * @param votant
	 *            Le nouvel AID du votant.
	 */
	public void setVotant(String votant) {
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


	/**
	 * Retourne la valeur du parti du votant.
	 *
	 * @return La chaine de caractère qui correspond au parti du votant
	 */
	public String getParti() {
		return parti;
	}

	/**
	 * Met à jour le parti du votant.
	 *
	 * @param parti
	 *            Le nouveau parti du votant.
	 */
	public void setParti(String parti) {
		this.parti = parti;
	}

	/**
	 * Méthode d'affichage des caractéristiques d'un Aid_vote
	 * <p>
	 * Affiche toutes les caractéristiques.
	 * </p>
	 *
	 */

	public void affiche() {
		System.out.println();
		System.out.println("AID : " + this.votant);
		System.out.println("Vote : " + this.vote);
		System.out.println("Parti : " + this.parti);
		System.out.println();
	}


}
