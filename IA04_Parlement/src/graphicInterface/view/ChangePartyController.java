package graphicInterface.view;

import graphicInterface.MainApp;
import graphicInterface.model.Depute;
import graphicInterface.model.Loi;
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

public class ChangePartyController {
    @FXML
    private ChoiceBox<String> choiceList;

    @FXML
    private Button proposerButt;
    Stage dialogStage;
    String choix;

    // Reference to the main application.
    private MainApp mainApp;
    
    
    public ChangePartyController() {
 
    }

    @FXML
    private void initialize() {

        proposerButt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	choix = choiceList.getSelectionModel().getSelectedItem();
                dialogStage.close();
            }
        }); 

    }
    
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        // Add observable list data to the table
        choiceList.setItems(mainApp.getPartiData());

    }
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    public String getParti(){
    	return this.choix;
    }


	public void setPartis(ObservableList<String> listePartis) {
        choiceList.setItems(listePartis);
	}
    
}
