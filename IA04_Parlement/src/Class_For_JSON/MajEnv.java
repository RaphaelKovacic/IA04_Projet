package Class_For_JSON;


public class MajEnv {
	float context_eco;
	float qualite_vie;
	
	//Constructeur
	public MajEnv(float context_eco, float qualite_vie) {
		this.context_eco = context_eco;
		this.qualite_vie = qualite_vie;
	}
	
	public MajEnv(){}
	
	
	// Getters and Setters
	public float getContext_eco() {
		return context_eco;
	}

	public void setContext_eco(int context_eco) {
		this.context_eco = context_eco;
	}
	public float getQualite_vie() {
		return qualite_vie;
	}
	public void setQualite_vie(int qualite_vie) {
		this.qualite_vie = qualite_vie;
	}
	
	
}