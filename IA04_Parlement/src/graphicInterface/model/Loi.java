package graphicInterface.model;


import java.time.LocalDate;

import jade.util.leap.Set;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Loi {

	private final int id;
	
    private final StringProperty Name;

    private final StringProperty Description;

    public Loi(int x, String a,String b){
    	this.id = x;
    	this.Name = new SimpleStringProperty(a);
    	this.Description = new SimpleStringProperty(b);
    }
    
    public StringProperty nameProperty() {
		return Name;
	}
    
    public void SetName(String Name){
    	this.Name.set(Name);
}
    
    public String getName(){
    	return this.Name.get();
    }
    public String getDescription(){
    	
    	return this.Description.get();

    	
    }
   public void setDescription(String Descri){
	   
	   this.Description.set(Descri);
   }
public int getId() {
	return id;
}
}
