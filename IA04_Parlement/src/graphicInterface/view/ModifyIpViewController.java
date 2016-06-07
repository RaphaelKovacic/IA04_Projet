package graphicInterface.view;

import graphicInterface.MainApp;
import graphicInterface.model.Depute;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ModifyIpViewController {

    @FXML
    private TextField Ip;

    Stage dialogStage;


    // Reference to the main application.
    private MainApp mainApp;
    
    
    public ModifyIpViewController() {
    }

    @FXML
    private void initialize() {


    }
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

    }
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    
}
