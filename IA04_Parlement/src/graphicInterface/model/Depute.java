package graphicInterface.model;


import java.time.LocalDate;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Depute {

    private final StringProperty nom;
    private final StringProperty prenom;
    private final StringProperty parti;
    private final FloatProperty popularite;
    private final FloatProperty notoriete;
    private final FloatProperty charisme;
    private final FloatProperty credibilite;
    private String idDepute;
    public Depute(String a, String b) {
        this(a, b, "pirate", 50f, 50f, 50f, 50f, "plouc");
    }

    public Depute(String firstName, String lastName, String parti, float popularite, float  credibilite, float notoriete, float charisme, String idDepute) {
        this.nom = new SimpleStringProperty(firstName);
        this.prenom = new SimpleStringProperty(lastName);
        this.parti = new SimpleStringProperty(parti);
        this.popularite = new SimpleFloatProperty(popularite);
        this.credibilite = new SimpleFloatProperty(credibilite);
        this.notoriete = new SimpleFloatProperty(notoriete);
        this.charisme = new SimpleFloatProperty(charisme);
        this.idDepute = new String(idDepute);
    }


	public float getCharisme() {
		return charisme.get();
	}
    public float getCredibilite() {
		return credibilite.get();
	}
    public String getNom() {
		return nom.get();
	}
    public String getPrenom() {
		return prenom.get();
	}
    public float getNotoriete() {
		return notoriete.get();
	}
    public String getParti() {
		return parti.get();
	}
    public float getPopularite() {
		return popularite.get();
	}
    public String getIdDepute(){
    	return this.idDepute;
    }
    public StringProperty prenomProperty() {
        return this.prenom;
    }
    public StringProperty nomProperty() {
        return this.nom;
    }
    public StringProperty partiProperty() {
        return parti;
    }
    public FloatProperty notorieteProperty() {
        return notoriete;
    }
    public FloatProperty credibiliteProperty() {
        return credibilite;
    }
    public FloatProperty populariteProperty() {
        return popularite;
    }
    public FloatProperty charismeProperty() {
        return charisme;
    }
    public void setNom(String nom_){
    	this.nom.set(nom_);
    }
    public void setPrenom(String prenom_){
    	this.prenom.set(prenom_);
    }
    public void setParti(String parti_){
    	this.parti.set(parti_);
    }
    public void setNotoriete(float not_){
    	this.notoriete.set(not_);
    }
    public void setCredibiltie(float cre_){
    	this.credibilite.set(cre_);
    }
    public void setPopularite(float popu_){
    	this.popularite.set(popu_);
    }
    
    public void setCharisme(float char_){
    	this.charisme.set(char_);
    }
    public void setID(String idDepute_){
    	
    	this.idDepute = idDepute_;
    	
    }
    
    
    

}
