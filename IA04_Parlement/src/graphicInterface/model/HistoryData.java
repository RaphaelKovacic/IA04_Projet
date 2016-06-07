package graphicInterface.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class HistoryData  {

    private final StringProperty intitule;
    private final StringProperty code;

	
    public HistoryData(String a, int b){
        this.intitule = new SimpleStringProperty(a);
        this.code = new SimpleStringProperty(Integer.toString(b));
    }
    
    public String getCode() {
		return code.get();
	}
    
    public String getIntitule() {
		return intitule.get();
	}
    
    public StringProperty intituleProperty() {
        return this.intitule;
    }
    public StringProperty codeProperty() {
        return this.code;
    }
    
}
