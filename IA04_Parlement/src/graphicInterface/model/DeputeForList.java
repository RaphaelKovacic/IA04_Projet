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

public class DeputeForList {

	private final int id;
	

    private final StringProperty Description;

    public DeputeForList(int x, String a){
    	this.id = x;
    	this.Description = new SimpleStringProperty(a);
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
