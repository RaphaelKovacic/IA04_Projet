package graphicInterface.view;

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

public class ProposeLoiViewController {
    @FXML
    private ChoiceBox<String> choiceList = new ChoiceBox<String>();;

    @FXML
    private Button proposerButt;
    @FXML
    private TextArea textZone;
    Stage dialogStage;
    Loi choix;

    // Reference to the main application.
    private MainApp mainApp;
    private ObservableList<Loi> listeLoi = FXCollections.observableArrayList();
    private ObservableList<String> listeLoiName = FXCollections.observableArrayList();

    public ProposeLoiViewController() {
 
    }

    @FXML
    private void initialize() {

        proposerButt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	choix = FindLoi( choiceList.getSelectionModel().getSelectedItem());
            	System.out.println("clickOnProposerButt!");
                dialogStage.close();
            }
        }); 

    }
    
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        // Add observable list data to the table
       // choiceList.setItems(mainApp.getLoiData());

    }
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    public Loi getLoi(){
    	return this.choix;
    }
    public void setTextArea(String text){
    	
    	this.textZone.setText(text);
    	
    }

	public void setLois(ObservableList<Loi> listeLoi) {
		this.listeLoi = listeLoi;
		
		
		int i = 0;
		while (i < this.listeLoi .size()){
			System.out.println(this.listeLoi .get(i).getId() + this.listeLoi .get(i).getDescription());
			listeLoiName.add(this.listeLoi.get(i).getName());
			i+=1;
		}
			
		
				choiceList.setItems(listeLoiName);
				
				
				
		 ///       choiceList.getSelectionModel().selectedItemProperty().addListener(
         //       (observable, oldValue, newValue) -> setTextArea(((Loi) newValue).getDescription()));


			       
				choiceList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>()
				{
				    public void changed(ObservableValue<? extends String> source, String oldValue, String newValue)
				    {
				     	  Loi loi = FindLoi(newValue);
		    	    	  if(loi != null){
		    	    		  
		    	    		  setTextArea(loi.getDescription());
		    	    		  
		    	    	  }else{
		    	    			System.out.println("BABAYAGA");

		    	    	  }				    }
				});
				
				
	}
	public Loi FindLoi(String i){
		System.out.println("/////"+i);

		int x = 0;
		while( x < this.listeLoi.size()){
		System.out.println("/////"+this.listeLoi.get(x).getId());
		if(this.listeLoi.get(x).getName() == i){
			return this.listeLoi.get(x);
		}
		x+=1;
	}
	return null;
	}

}
