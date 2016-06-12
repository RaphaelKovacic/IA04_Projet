package graphicInterface.view;

import java.util.ArrayList;

import graphicInterface.MainApp;
import graphicInterface.model.Depute;
import graphicInterface.model.DeputeForList;
import graphicInterface.model.Loi;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ListDeputesViewController {
    @FXML
    private ChoiceBox<Integer> choiceList;

    @FXML
    private Button proposerButt;
    @FXML
    private TextArea textZone;
    Stage dialogStage;
    Integer choix;
    private ObservableList<DeputeForList> myList  = FXCollections.observableArrayList();
    private ObservableList<Integer> arrayL = FXCollections.observableArrayList();

    // Reference to the main application.
    private MainApp mainApp;
    
    
    public ListDeputesViewController() {
 
    }

    @FXML
    private void initialize() {

        proposerButt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	choix = (Integer) choiceList.getSelectionModel().getSelectedItem();
            	System.out.println("clickOnProposerButt!");
                dialogStage.close();
            }
        }); 

    }
    
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        // Add observable list data to the table

    }
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    public int getDeputeID(){
    	return this.choix;
    }
    public void setTextArea(String text){
    	
    	this.textZone.setText(text);
    	
    }

	public void setLois(ObservableList<DeputeForList> observableList) {
		myList = observableList;
		System.out.println("entreee"+observableList.size());
		
		int i = 0;
		while (i < myList.size()){
			System.out.println(myList.get(i).getId() + myList.get(i).getDescription());
			arrayL.add(myList.get(i).getId());
			i+=1;
		}
			
		
       choiceList.setItems(arrayL);
   //    choiceList.getSelectionModel().selectedItemProperty().addListener(
   //            (observable, oldValue, newValue) -> setTextArea((FindDepute(newValue)).getDescription()));

       
       choiceList.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
    	      @Override
    	      public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
    	    	  DeputeForList deput = FindDepute(number2.intValue());
    	    	  if(deput != null){
    	    		  
    	    		  setTextArea(deput.getDescription());
    	    		  
    	    	  }else{
    	    			System.out.println("BABAYAGA");

    	    	  }
    	      
    	      }
    	    });
       
	}
    
	public DeputeForList FindDepute(Integer i){
		System.out.println("/////"+i);

		int x = 0;
		while( x < myList.size()){
		System.out.println("/////"+myList.get(x).getId());
		if(myList.get(x).getId() == i){
			return myList.get(x);
		}
		x+=1;
	}
	return null;
	}
}
