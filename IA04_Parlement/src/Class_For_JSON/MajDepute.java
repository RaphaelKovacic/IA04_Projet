package Class_For_JSON;

import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * <b>MajDepute est la classe représentant les paramètres variables d'un
 * député.</b>
 * <p>
 * Un MajDepute possède les attributs suivants :
 * <ul>
 * <li>Un float correspondant à l'effet sur l'influence du député</li>
 * <li>Un float correspondant à l'effet sur la popularité du député</li>
 * <li>Un float correspondant à l'effet sur la notoriété du député</li>
 * <li>Un float correspondant à l'effet sur la crédibilité du député</li>
 * </ul>
 * </p>
 * 
 * <p>
 * MajDepute est une classe servant à serializer en JSON les modifications à
 * apporter sur les caractéristiques variable d'un député. Elle est utilisée
 * dans l'échange de messages entre agents.
 * </p>
 * 
 * @author Benoit
 * @version 1.1
 */
public class MajDepute {

	/**
	 * La valeur correspondant à l'effet sur l'influence du député.
	 * 
	 * @see MajDepute#getEffet_Influence()
	 * @see MajDepute#setEffet_Influence(float)
	 */
	float effet_Influence;

	/**
	 * La valeur correspondant à l'effet sur la popularité du député.
	 * 
	 * @see MajDepute#getEffet_Popularite()
	 * @see MajDepute#setEffet_Popularite(float)
	 */
	float effet_Popularite;

	/**
	 * La valeur correspondant à l'effet sur la notoriété du député
	 * 
	 * @see MajDepute#getEffet_Notoriete()
	 * @see MajDepute#setEffet_Notoriete(float)
	 */
	float effet_Notoriete;

	/**
	 * La valeur correspondant à l'effet sur la crédibilité du député
	 * 
	 * @see MajDepute#getEffet_Credibilite()
	 * @see MajDepute#setEffet_Credibilite(float)
	 */
	float effet_Credibilite;

	/**
	 * Constructeur de MajDepute .
	 * <p>
	 * À la contruction d'un MajDepute on fixe tous les attributs de la classe :
	 * l'effet sur chaque paramètre d'un député.
	 * </p>
	 * 
	 * @param effet_Influence
	 *            La valeur de l'effet sur l'influence.
	 * 
	 * @param effet_Popularite
	 *            La valeur de l'effet sur la popularité.
	 * 
	 * @param effet_Notoriete
	 *            La valeur de l'effet sur la notoriété.
	 * 
	 * @param effet_Credibilite
	 *            La valeur de l'effet sur la crédibilité.
	 * 
	 * @see MajDepute#effet_Influence
	 * @see MajDepute#effet_Popularite
	 * @see MajDepute#effet_Notoriete
	 * @see MajDepute#effet_Credibilite
	 */
	public MajDepute(float effet_Influence, float effet_Popularite, float effet_Notoriete, float effet_Credibilite) {
		super();
		this.effet_Influence = effet_Influence;
		this.effet_Popularite = effet_Popularite;
		this.effet_Notoriete = effet_Notoriete;
		this.effet_Credibilite = effet_Credibilite;
	}

	/**
	 * Constructeur de MajDepute vide.
	 * <p>
	 * Constructeur d'un MajDepute vierge de toute influence sur tous les
	 * paramètres.
	 * </p>
	 */
	public MajDepute() {
	};

	/**
	 * Retourne la valeur de l'effet sur l'influence.
	 * 
	 * @return La valeur de l'effet sur l'influence.
	 */
	public float getEffet_Influence() {
		return effet_Influence;
	}

	/**
	 * Met à jour l'effet sur l'influence.
	 * 
	 * @param _effet_Influence
	 *            La nouvelle valeur de l'effet sur l'influence.
	 */
	@JsonSetter
	public void setEffet_Influence(float _effet_Influence) {
		this.effet_Influence = _effet_Influence;
	}

	/**
	 * Retourne la valeur de l'effet sur la popularité.
	 * 
	 * @return La valeur de l'effet sur la popularité.
	 */
	public float getEffet_Popularite() {
		return effet_Popularite;
	}

	/**
	 * Met à jour l'effet sur la popularité.
	 * 
	 * @param _effet_Popularité
	 *            La nouvelle valeur de l'effet sur la popularité.
	 */
	@JsonSetter
	public void setEffet_Popularite(float effet_Popularite) {
		this.effet_Popularite = effet_Popularite;
	}

	/**
	 * Retourne la valeur de l'effet sur la notoriété.
	 * 
	 * @return La valeur de l'effet sur la notoriété.
	 */
	public float getEffet_Notoriete() {
		return effet_Notoriete;
	}

	/**
	 * Met à jour l'effet sur la notoriété.
	 * 
	 * @param _effet_Notoriete.
	 *            La nouvelle valeur de l'effet sur la notoriété.
	 */
	@JsonSetter
	public void setEffet_Notoriete(float effet_Notoriete) {
		this.effet_Notoriete = effet_Notoriete;
	}

	/**
	 * Retourne la valeur de l'effet sur la crédibilité.
	 * 
	 * @return La valeur de l'effet sur la crédibilité.
	 */
	public float getEffet_Credibilite() {
		return effet_Credibilite;
	}

	/**
	 * Met à jour l'effet sur la crédibilité.
	 * 
	 * @param _effet_Credibilite
	 *            La nouvelle valeur de l'effet sur la crédibilité.
	 */
	@JsonSetter
	public void setEffet_Credibilite(float effet_Credibilite) {
		this.effet_Credibilite = effet_Credibilite;
	}

}