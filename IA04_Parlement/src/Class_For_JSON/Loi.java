package Class_For_JSON;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * <b>Loi est la classe représentant une loi dans notre architecture JADE.</b>
 * <p>
 * Une loi possède les attributs suivants :
 * <ul>
 * <li>Un identifiant unique attribué définitivement.</li>
 * <li>Le nom unique attribué définitivement.</li>
 * <li>Une description sur le contenu de la loi, non modifiable.</li>
 * <li>L'effet de la loi sur la qualité de vie du pays.</li>
 * <li>L'effet de la loi sur l'économie du pays.</li>
 * <li>La liste des partis politiques pouvant proposer cette loi.</li>
 * <li>Le nom du proposant de la loi.</li>
 * <li>La valeur de l'influence du proposant de la loi.</li>
 * <li>La valeur du charisme du proposant de la loi.</li>
 * <li>La valeur de la popularité du proposant de la loi.</li>
 * <li>La valeur de la notoriété du proposant de la loi.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Loi est une classe servant notamment à serializer en JSON une loi. Elle est
 * utilisée dans l'échange de messages entre agents.
 * </p>
 * <p>
 * La classe Loi implémente l'interface Comparable pour permettre la comparaison
 * automatiquede lois grâce à la methode sort()
 * </p>
 * 
 * @author Benoit & Etienne
 * @version 2.0.1
 */

public class Loi implements Comparable<Loi> {

	/**
	 * L'ID de la loi. Cet ID n'est pas modifiable.
	 * 
	 * @see Loi#getId()
	 * @see Loi#setId(int)
	 */
	@JsonProperty("id")
	int id;

	/**
	 * Le nom de la loi. Ce nom n'est pas modifiable.
	 *
	 * @see Loi#getNom()
	 * @see Loi#setNom(String)
	 */
	@JsonProperty("nom")
	String nom;

	/**
	 * La description de la loi. Cette description n'est pas modifiable.
	 * 
	 * @see Loi#getDescription()
	 * @see Loi#setDescription(String)
	 */
	@JsonProperty("description")
	String description;

	/**
	 * La valeur l'influence sur le paramètre qualité de vie qui décrit le bien
	 * être de la population du pays. Cette valeur n'est pas modifiable.
	 * 
	 * @see Loi#getEffet_qualite_vie()
	 * @see Loi#setEffet_qualite_vie(float)
	 */
	@JsonProperty("effet_qualite_vie")
	float effet_qualite_vie;

	/**
	 * La valeur l'influence sur le paramètre santé économique qui décrit la
	 * santé économique du pays. Cette valeur n'est pas modifiable.
	 * 
	 * @see Loi#getEffet_context_eco()
	 * @see Loi#setEffet_context_eco(float)
	 */
	@JsonProperty("effet_context_eco")
	float effet_context_eco;

	/**
	 * La liste des partis politiques susceptibles de proposer cette loi. Cette
	 * liste n'est pas modifiable.
	 * 
	 * @see Loi#getL_PartiPolitique()
	 * @see Loi#setL_PartiPolitique(List)
	 */
	@JsonProperty("l_PartiPolitique")
	List<String> l_PartiPolitique;

	/**
	 * Le nom du député proposant la loi. Cette chaune de caractère n'est pas
	 * modifiable.
	 * 
	 * @see Loi#getProposant()
	 * @see Loi#setProposant(String)
	 */
	@JsonProperty("proposant")
	String proposant;

	/**
	 * La valeur du paramètre "influence" du proposant de la loi. Cette valeur
	 * n'est pas modifiable.
	 * 
	 * @see Loi#getInfluence()
	 * @see Loi#setInfluence(float)
	 */
	@JsonProperty("influence")
	float influence;

	/**
	 * La valeur du paramètre "charisme" du proposant de la loi. Cette valeur
	 * n'est pas modifiable.
	 * 
	 * @see Loi#getCharisme()
	 * @see Loi#setCharisme(float)
	 */
	@JsonProperty("charisme")
	float charisme;

	/**
	 * La valeur du paramètre "popularité" du proposant de la loi. Cette valeur
	 * n'est pas modifiable. Cette valeur correspond à la côté de popularité
	 * dont bénéficie le député vis à vis du peuple.
	 * 
	 * @see Loi#getPopularite()
	 * @see Loi#setPopularite(float)
	 */
	@JsonProperty("popularite")
	float popularite;

	/**
	 * La valeur du paramètre "notoriété" du proposant de la loi. Cette valeur
	 * n'est pas modifiable. Cette valeur correspond à la côté de popularité
	 * dont bénéficie le député vis à vis des entreprises.
	 * 
	 * @see Loi#getNotoriete()
	 * @see Loi#setNotoriete(float)
	 */
	@JsonProperty("notoriete")
	float notoriete;

	/**
	 * Constructeur de loi .
	 * <p>
	 * À la contruction d'une loi, les attributs de l'objet sont initialisés.
	 * </p>
	 * 
	 * @param id
	 *            L'identifiant unique de la loi.
	 * @param nom
	 * 			  Le nom de la loi.
	 * @param description
	 *            La description de la loi.
	 * @param effet_qualite_vie
	 *            L'effet sur la qualité de vie du pays.
	 * @param effet_context_eco
	 *            L'effet sur la santé économique du pays.
	 * @param l_PartiPolitique
	 *            La liste des partis politiques pouvant proposer la loi.
	 * @param proposant
	 *            Le nom du proposant de la loi.
	 * @param influence
	 *            La valeur de l'influence du proposant
	 * @param charisme
	 *            La valeur du charisme du proposant
	 * @param popularite
	 *            La valeur de la popularite du proposant
	 * @param notoriete
	 *            La valeur de la notoriété du proposant
	 * 
	 * @see Loi#id
	 * @see Loi#nom
	 * @see Loi#description
	 * @see Loi#effet_qualite_vie
	 * @see Loi#l_PartiPolitique
	 * @see Loi#proposant
	 * @see Loi#influence
	 * @see Loi#charisme
	 * @see Loi#popularite
	 * @see Loi#notoriete
	 * 
	 * 
	 */
	public Loi(int id, String nom, String description, float effet_qualite_vie, float effet_context_eco,
			List<String> l_PartiPolitique, String proposant, float influence, float charisme, float popularite,
			float notoriete) {
		super();
		this.id = id;
		this.nom = nom;
		this.description = description;
		this.effet_qualite_vie = effet_qualite_vie;
		this.effet_context_eco = effet_context_eco;
		this.l_PartiPolitique = l_PartiPolitique;
		this.proposant = proposant;
		this.influence = influence;
		this.charisme = charisme;
		this.popularite = popularite;
		this.notoriete = notoriete;
	}

	/**
	 * Constructeur de loi vide .
	 * <p>
	 * Constructeur d'une loi vierge de toute initialisation d'attributs.
	 * </p>
	 *
	 */

	public Loi() {
	}

	/**
	 * Méthode d'affichage des caractéristiques d'une loi
	 * <p>
	 * Affiche toutes les caractéristiques.
	 * </p>
	 *
	 */

	public void affiche() {
		System.out.println("id : " + this.id);
		System.out.println("nom : " + this.nom);
		System.out.println("description : " + this.description);
		System.out.println("effet_qualite_vie : " + this.effet_qualite_vie);
		System.out.println("effet_context_eco : " + this.effet_context_eco);
		if (this.l_PartiPolitique != null)
			System.out.println("l_PartiPolitique : " + this.l_PartiPolitique.toString());
		System.out.println("proposant : " + this.proposant);
		System.out.println("influence : " + this.influence);
		System.out.println("charisme : " + this.charisme);
		System.out.println("popularite : " + this.popularite);
		System.out.println("notoriete : " + this.notoriete);
	}

	/**
	 * Affichage des informations d'une loi pour l'utilisateur.
	 * <p>
	 * Affiche seulement ce que l'utilisateur doit voir de la loi (éviter la
	 * triche).
	 * </p>
	 *
	 */
	public void affiche_a_utilisateur() {
		System.out.println("------------------------------------------------------");
		System.out.println("id : " + this.id);
		System.out.println("nom : " + this.nom);
		System.out.println("description : " + this.description);
		System.out.println("------------------------------------------------------");
	}

	/**
	 * Retourne l'ID de la loi.
	 * 
	 * @return L'identifiant de la loi.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Met à jour l'id de la loi
	 * 
	 * @param _id
	 *            Le nouvel id du membre
	 */
	@JsonSetter
	public void setId(int _id) {
		this.id = _id;
	}



	/**
	 * Retourne le nom de la loi.
	 *
	 * @return La chaine de caractère du nom la loi.
	 */
	public String getNom() {
		return nom;
	}

	/**
	 * Met à jour le nom de la loi.
	 *
	 * @param _nom
	 *            Le nouveau nom de la loi.
	 *
	 */
	@JsonSetter
	public void setNom(String _nom) {
		this.nom = _nom;
	}


	/**
	 * Retourne la description de la loi.
	 * 
	 * @return La chaine de caractère décrivant la loi.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Met à jour la description de la loi.
	 * 
	 * @param _description
	 *            La nouvelle description de la loi.
	 * 
	 */
	@JsonSetter
	public void setDescription(String _description) {
		this.description = _description;
	}

	/**
	 * Retourne l'effet sur la qualité de vie dans le pays pour la loi.
	 * 
	 * @return La valeur de l'effet sur la qualité de vie.
	 */
	public float getEffet_qualite_vie() {
		return effet_qualite_vie;
	}

	/**
	 * Met à jour l'effet de la loi sur la qualité de vie du pays.
	 * 
	 * @param _effet_qualite_vie
	 *            Le nouvel effet sur la qualité de vie
	 * 
	 */
	@JsonSetter
	public void setEffet_qualite_vie(float _effet_qualite_vie) {
		this.effet_qualite_vie = _effet_qualite_vie;
	}

	/**
	 * Retourne l'effet sur la santé économique dans le pays pour la loi.
	 * 
	 * @return La valeur de l'effet sur la santé économique du pays.
	 */
	public float getEffet_context_eco() {
		return effet_context_eco;
	}

	/**
	 * Met à jour l'effet de la loi sur la santé économique du pays.
	 * 
	 * @param effet_context_eco
	 *            Le nouvel effet sur la santé économique.
	 * 
	 */
	@JsonSetter
	public void setEffet_context_eco(float effet_context_eco) {
		this.effet_context_eco = effet_context_eco;
	}

	/**
	 * Retourne la liste des partis politiques susceptible de proposant la loi.
	 * 
	 * @return La liste de chaines de caractères correspondant aux partis
	 *         politiques susceptibles de proposer la loi.
	 */
	public List<String> getL_PartiPolitique() {
		return l_PartiPolitique;
	}

	/**
	 * Met à jour la liste des partis politiques pouvant proposer la loi.
	 * 
	 * @param _l_PartiPolitique
	 *            La nouvelle liste des partis politiques pouvant proposer la
	 *            loi.
	 * 
	 */
	@JsonSetter
	public void setL_PartiPolitique(List<String> _l_PartiPolitique) {
		l_PartiPolitique = _l_PartiPolitique;
	}

	/**
	 * Retourne le nom du proposant de la loi.
	 * 
	 * @return La chaine de caractère correspondant au proposant de la loi.
	 */
	public String getProposant() {
		return proposant;
	}

	/**
	 * Met à jour le nom du proposant de la loi.
	 * 
	 * @param _proposant
	 *            Le nouveau nom du proposant de la loi.
	 * 
	 */
	@JsonSetter
	public void setProposant(String _proposant) {
		this.proposant = _proposant;
	}

	/**
	 * Retourne la valeur de l'attribut influence du proposant de la loi.
	 * 
	 * @return La valeur de l'influence du proposant.
	 */
	public float getInfluence() {
		return influence;
	}

	/**
	 * Met à jour la valeur de l'influence du proposant de la loi
	 * 
	 * @param _influence
	 *            La nouvelle valeur de l'influence du proposant
	 * 
	 */
	@JsonSetter
	public void setInfluence(float _influence) {
		this.influence = _influence;
	}

	/**
	 * Retourne la valeur de l'attribut charisme du proposant de la loi.
	 * 
	 * @return La valeur du charisme du proposant.
	 */
	public float getCharisme() {
		return charisme;
	}

	/**
	 * Met à jour la valeur du charisme du proposant de la loi
	 * 
	 * @param _charisme
	 *            La nouvelle valeur du charisme du proposant
	 * 
	 */
	@JsonSetter
	public void setCharisme(float _charisme) {
		this.charisme = _charisme;
	}

	/**
	 * Retourne la valeur de l'attribut popularité du proposant de la loi.
	 * 
	 * @return La valeur de la popularité du proposant.
	 */
	public float getPopularite() {
		return popularite;
	}

	/**
	 * Met à jour la valeur de la popularité du proposant de la loi
	 * 
	 * @param _popularite
	 *            La nouvelle valeur de la popularité du proposant.
	 * 
	 */
	@JsonSetter
	public void setPopularite(float _popularite) {
		this.popularite = _popularite;
	}

	/**
	 * Retourne la valeur de l'attribut notoriété du proposant de la loi.
	 * 
	 * @return La valeur de la notoriété du proposant.
	 */
	public float getNotoriete() {
		return notoriete;
	}

	/**
	 * Met à jour la valeur de la notoriété du proposant de la loi
	 * 
	 * @param _notoriete
	 *            La nouvelle valeur de la notoriété du proposant
	 * 
	 */
	@JsonSetter
	public void setNotoriete(float _notoriete) {
		this.notoriete = _notoriete;
	}

	/**
	 * Retourne une booléen issu de la comparaison entre deux lois. Méthode
	 * obligatoire pour implémenter complétement l'interface Comparable.
	 * Utiliser pour trier une liste de loi par id croissant.
	 * 
	 * @return La valeur -1, 1 ou 0 selon la comparaison entre deux lois.
	 */
	public int compareTo(Loi _to_compare) {
		if (this.id < _to_compare.getId())
			return -1;
		else if (this.id > _to_compare.getId())
			return 1;
		else
			return 0;
	}

}
