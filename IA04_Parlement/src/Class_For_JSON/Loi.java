package Class_For_JSON;

import java.util.List;

import jade.core.AID;

public class Loi {
	int id;
	String description;
	float effet_qualite_vie;
	float effet_context_eco;
	List<String> L_PartiPolitique;
	//Information concernant le proposant (evite d'avoir a envoyer des messages...)
	String proposant;
	float influence, charisme, popularite, notoriete;

	public Loi(int id, String description, float effet_qualite_vie, float effet_context_eco,
			List<String> l_PartiPolitique, String proposant, float influence, float charisme, float popularite,
			float notoriete) {
		super();
		this.id = id;
		this.description = description;
		this.effet_qualite_vie = effet_qualite_vie;
		this.effet_context_eco = effet_context_eco;
		L_PartiPolitique = l_PartiPolitique;
		this.proposant = proposant;
		this.influence = influence;
		this.charisme = charisme;
		this.popularite = popularite;
		this.notoriete = notoriete;
	}
	
	public Loi(){};

	public void affiche(){
		System.out.println("id : "+ this.id);
		System.out.println("description : "+ this.description);
		System.out.println("effet_qualite_vie : "+ this.effet_qualite_vie);
		System.out.println("effet_context_eco : "+ this.effet_context_eco);
		System.out.println("L_PartiPolitique : "+ this.L_PartiPolitique.toString());
		System.out.println("proposant : "+ this.proposant);
		System.out.println("influence : "+ this.influence);
		System.out.println("charisme : "+ this.charisme);
		System.out.println("popularite : "+ this.popularite);
		System.out.println("notoriete : "+ this.notoriete);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public float getEffet_qualite_vie() {
		return effet_qualite_vie;
	}

	public void setEffet_qualite_vie(float effet_qualite_vie) {
		this.effet_qualite_vie = effet_qualite_vie;
	}

	public float getEffet_context_eco() {
		return effet_context_eco;
	}

	public void setEffet_context_eco(float effet_context_eco) {
		this.effet_context_eco = effet_context_eco;
	}

	public List<String> getL_PartiPolitique() {
		return L_PartiPolitique;
	}

	public void setL_PartiPolitique(List<String> l_PartiPolitique) {
		L_PartiPolitique = l_PartiPolitique;
	}

	public String getProposant() {
		return proposant;
	}

	public void setProposant(String proposant) {
		this.proposant = proposant;
	}

	public float getInfluence() {
		return influence;
	}

	public void setInfluence(float influence) {
		this.influence = influence;
	}

	public float getCharisme() {
		return charisme;
	}

	public void setCharisme(float charisme) {
		this.charisme = charisme;
	}

	public float getPopularite() {
		return popularite;
	}

	public void setPopularite(float popularite) {
		this.popularite = popularite;
	}

	public float getNotoriete() {
		return notoriete;
	}

	public void setNotoriete(float notoriete) {
		this.notoriete = notoriete;
	}
	
	
}
