package Class_For_JSON;

import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * <b>MajEnv est la classe représentant les paramètres décrivant le pays.</b>
 * <p>
 * Un MajEnv possède les attributs suivants :
 * <ul>
 * <li>Un float correspondant à la santé économique du pays</li>
 * <li>Un float correspondant à la qualité de vie des habitants du pays</li>
 * </ul>
 * </p>
 * 
 * <p>
 * MajEnv est une classe servant à serializer en JSON l'état actuel d'un pays.
 * Elle est utilisée dans l'échange de messages entre agents.
 * </p>
 * 
 * @author Benoit
 * @version 1.0
 */
public class MajEnv {

	/**
	 * La valeur correspondant au contexte économique du pays.
	 * 
	 * @see MajEnv#getContext_eco()
	 * @see MajEnv#setContext_eco(int)
	 */
	float context_eco;

	/**
	 * La valeur correspondant à la qualité de vie des habitants du pays.
	 * 
	 * @see MajEnv#getQualite_vie()
	 * @see MajEnv#setQualite_vie(int)
	 */
	float qualite_vie;

	/**
	 * Constructeur de MajEnv .
	 * <p>
	 * À la contruction d'un MajEnv on fixe les deux attributs de la classe :
	 * valeur du contexte économique et de la qualité de vie du pays.
	 * </p>
	 * 
	 * @param context_eco
	 *            La valeur du context économique.
	 * 
	 * @param qualite_vie
	 *            La valeur de la qualité de vie
	 * 
	 * @see MajEnv#context_eco
	 * @see MajEnv#qualite_vie
	 */
	public MajEnv(float context_eco, float qualite_vie) {
		this.context_eco = context_eco;
		this.qualite_vie = qualite_vie;
	}

	/**
	 * Constructeur de MajEnv vide.
	 * <p>
	 * Constructeur d'un MajEnv vierge de tout numéro de tour.
	 * </p>
	 */
	public MajEnv() {
	}

	/**
	 * Retourne la valeur traduisant l'état de l'économie du pays.
	 * 
	 * @return La valeur du contexte économique
	 */
	public float getContext_eco() {
		return context_eco;
	}

	/**
	 * Met à jour le contexte économique
	 * 
	 * @param _context_eco
	 *            Le nouveau contexte économique
	 */
	@JsonSetter
	public void setContext_eco(int _context_eco) {
		this.context_eco = _context_eco;
	}

	/**
	 * Retourne la valeur traduisant la qualité de vie dans le pays.
	 * 
	 * @return La valeur de la qualité de vie du pays.
	 */
	public float getQualite_vie() {
		return qualite_vie;
	}

	/**
	 * Met à jour la qualité du pays.
	 * 
	 * @param _qualite_vie
	 *            La nouvelle qualité de vie dans le pays.
	 */
	@JsonSetter
	public void setQualite_vie(int _qualite_vie) {
		this.qualite_vie = _qualite_vie;
	}

}