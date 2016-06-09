package Class_For_JSON;

import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * <b>DeputeAttRumeur est la classe représentant les paramètres d'un député (y compris l'utilisateur) pris en compte pour répandre une rumeur.</b>
 * <p>
 * Un DeputeAttRumeur possède les attributs suivants :
 * <ul>
 * <li>Un int correspondant à l'id du député local à l'agent Rumeur</li>
 * <li>Un float correspondant à l'influence du député</li>
 * <li>Un float correspondant à la popularité du député</li>
 * <li>Un float correspondant à la crédibilité du député</li>
 * </ul>
 * </p>
 *
 * <p>
 * DeputeAttRumeur est une classe servant à serializer en JSON les paramètres susmentionnés. Elle est utilisée
 * dans l'échange de messages avec l'agent Rumeur.
 * </p>
 *
 * @author Cristian
 * @version 1.0
 */
public class DeputeAttRumeur {

	/**
	 * La valeur correspondant à l'id du député, local à l'agent Rumeur.
	 *
	 */
	int id;

	/**
	 * La valeur correspondant à l'influence du député.
	 *
	 */
	float influence;

	/**
	 * La valeur correspondant à la popularité du député.
	 *
	 */
	float popularite;

	/**
	 * La valeur correspondant à la crédibilité du député
	 *
	 */
	float credibilite;

	/**
	 * Constructeur de DeputeAttRumeur .
	 * <p>
	 * À la contruction d'un DeputeAttRumeur on fixe tous les attributs de la classe :
	 * l'influence, la popularité et la crédibilité du député (ou de l'utilisateur).
	 * </p>
	 *
	 * @param influence
	 *            La valeur de l'influence.
	 *
	 * @param popularite
	 *            La valeur de la popularité.
	 *
	 * @param credibilite
	 *            La valeur de la crédibilité.
	 *
	 */
	public DeputeAttRumeur(float influence, float popularite, float credibilite) {
		super();
		this.influence = influence;
		this.popularite = popularite;
		this.credibilite = credibilite;
		this.id = -1;
	}

	/**
	 * Constructeur de DeputeAttRumeur vide.
	 * <p>
	 * Constructeur d'un DeputeAttRumeur vierge.
	 * </p>
	 */
	public DeputeAttRumeur() {
	}

	/**
	 * Retourne la valeur de l'influence.
	 *
	 * @return La valeur de l'influence.
	 */
	public float get_Influence() {
		return influence;
	}

	/**
	 * Met à jour l'influence.
	 *
	 * @param _influence
	 *            La nouvelle valeur de l'influence.
	 */
	@JsonSetter
	public void set_Influence(float _influence) {
		this.influence = _influence;
	}

	/**
	 * Retourne la valeur de la popularité.
	 *
	 * @return La valeur de la popularité.
	 */
	public float get_Popularite() {
		return popularite;
	}

	/**
	 * Met à jour la popularité.
	 *
	 * @param popularite
	 *            La nouvelle valeur de la popularité.
	 */
	@JsonSetter
	public void set_Popularite(float popularite) {
		this.popularite = popularite;
	}

	/**
	 * Retourne la valeur de la crédibilité.
	 *
	 * @return La valeur de la crédibilité.
	 */
	public float get_Credibilite() {
		return credibilite;
	}

	/**
	 * Met à jour la crédibilité.
	 *
	 * @param credibilite
	 *            La nouvelle valeur de la crédibilité.
	 */
	@JsonSetter
	public void set_Credibilite(float credibilite) {
		this.credibilite = credibilite;
	}

	/**
	 * Retourne la valeur de l'id local à l'agent Rumeur.
	 *
	 * @return La valeur de l'id local à l'agent Rumeur.
	 */
	public int get_Id() {
		return id;
	}

	/**
	 * Met à jour l'id local à l'agent Rumeur.
	 *
	 * @param id
	 *            La nouvelle valeur de l'id local à l'agent Rumeur.
	 */
	@JsonSetter
	public void set_Id(int id) {
		this.id = id;
	}

	/**
	 * Affichage des informations d'un député pour l'utilisateur.
	 *
	 */
	public void affiche_a_utilisateur() {
		System.out.println("------------------------------------------------------");
		System.out.println("id : " + this.id);
		System.out.println("influence : " + this.influence);
		System.out.println("popularité : " + this.popularite);
		System.out.println("crédibilité : " + this.credibilite);
		System.out.println("------------------------------------------------------");
	}
}