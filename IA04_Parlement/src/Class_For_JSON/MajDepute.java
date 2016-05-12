package Class_For_JSON;


public class MajDepute {
	float effet_Influence;
	float effet_Popularite;
	float effet_Notoriete;
	float effet_Credibilite;
	
	public MajDepute(float effet_Influence, float effet_Popularite, float effet_Notoriete, float effet_Credibilite) {
		super();
		this.effet_Influence = effet_Influence;
		this.effet_Popularite = effet_Popularite;
		this.effet_Notoriete = effet_Notoriete;
		this.effet_Credibilite = effet_Credibilite;
	}
	
	public MajDepute(){};
	
	
	public float getEffet_Influence() {
		return effet_Influence;
	}
	public void setEffet_Influence(float effet_Influence) {
		this.effet_Influence = effet_Influence;
	}
	public float getEffet_Popularite() {
		return effet_Popularite;
	}
	public void setEffet_Popularite(float effet_Popularite) {
		this.effet_Popularite = effet_Popularite;
	}
	public float getEffet_Notoriete() {
		return effet_Notoriete;
	}
	public void setEffet_Notoriete(float effet_Notoriete) {
		this.effet_Notoriete = effet_Notoriete;
	}
	public float getEffet_Credibilite() {
		return effet_Credibilite;
	}
	public void setEffet_Credibilite(float effet_Credibilite) {
		this.effet_Credibilite = effet_Credibilite;
	}
	
	
}