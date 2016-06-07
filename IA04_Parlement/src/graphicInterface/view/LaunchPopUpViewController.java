package graphicInterface.view;

import graphicInterface.MainApp;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class LaunchPopUpViewController {
    @FXML
    private Button Launch;
    
	private Stage dialogStage;

	private MainApp mainApp;
    
    private void initialize() {

        Launch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("accepteLoiButton!");
                dialogStage.close();
            }
        });

    	
    }
    
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

}
