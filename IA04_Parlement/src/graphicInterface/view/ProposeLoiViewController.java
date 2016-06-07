package graphicInterface.view;

import graphicInterface.MainApp;
import graphicInterface.model.Depute;
import graphicInterface.model.Loi;
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
    private ChoiceBox<Loi> choiceList;

    @FXML
    private Button proposerButt;
    @FXML
    private TextArea textZone;
    Stage dialogStage;
    Loi choix;

    // Reference to the main application.
    private MainApp mainApp;
    
    
    public ProposeLoiViewController() {
        choiceList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> setTextArea(((Loi) newValue).getDescription()));

    }

    @FXML
    private void initialize() {

        proposerButt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	choix = (Loi) choiceList.getSelectionModel().getSelectedItem();
            	System.out.println("clickOnProposerButt!");
                dialogStage.close();
            }
        }); 
    	
    }
    
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        // Add observable list data to the table
        choiceList.setItems(mainApp.getLoiData());
        
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
    
}
