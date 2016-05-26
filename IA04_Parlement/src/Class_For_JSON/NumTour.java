package Class_For_JSON;

import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * <b>NumTour est la classe représentant le numéro du tour de la partie en cours.</b>
 * <p>
 * Un NumTour possède l'attribut suivant : 
 * <ul>
 * <li>Un entier correspondant au numéro du tour en cours</li>
 * </ul>
 * </p>
 * 
 * <p> NumTour est une classe servant à serializer en JSON le numéro du tour actuel.
 * Elle est utilisée dans l'échange de messages entre agents.
 * </p>
 * @author  Benoit
 * @version 1.0
 */


public class NumTour {
	
	/**
     * Le numéro du tour en cours.
     * @see NumTour#getNum()
     * @see NumTour#setNumm(int)
     */
	int num;


	 /**
     * Constructeur de NumTour .
     * <p>
     * À la contruction d'un NumTour on fixe le numéro du tour.
     * </p>
     * 
     * @param num
     *            Le numero du tour.
     * 
     * @see NumTour#num
     */
	public NumTour(int num) {
		this.num = num;
	}
	
	/**
     * Constructeur de NumTour vide.
     * <p>
     * Constructeur d'un NumTour vierge de tout numéro de tour.
     * </p>
     */
	public NumTour() {
	}
	
	/**
     * Retourne le numéro du tour du NumTour
     * 
     * @return Le numéro du tour
     */
	public int getNum() {
		return num;
	}
	
	/**
     * Met à jour le numéro de tour du NumTour
     * 
     * @param _num
     *           Le nouveau numéro de tour
     */
	@JsonSetter
	public void setNum(int _num) {
		this.num = _num;
	}

	
}
