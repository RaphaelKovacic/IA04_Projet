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
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class AchievementViewController {

    @FXML
    private ImageView Achiev1;
    @FXML
    private ImageView Achiev2;
    @FXML
    private ImageView Achiev3;
    @FXML
    private TextFlow Achiev1T;
    @FXML
    private TextFlow Achiev2T;
    @FXML
    private TextFlow Achiev3T;
    Stage dialogStage;
    private ColorAdjust colorAdjust;
    private ColorAdjust colorAdjust1;
    private int tab[] = {0,0,0};
    // Reference to the main application.
    private MainApp mainApp;
    
    
    public AchievementViewController() {
    }

    @FXML
    private void initialize() {

        Text t1 = new Text("Faire passer votre première loi au sein du parlement.");
        Achiev1T.getChildren().add(t1);
        Text t2 = new Text("Détroner un des leaders.");
        Achiev2T.getChildren().add(t2);
        Text t3 = new Text("Devenir une des personnes le plus influents au sein du parlement.");
        Achiev3T.getChildren().add(t3);
        
        colorAdjust = new ColorAdjust();
        colorAdjust.setContrast(0.0);
        colorAdjust.setHue(0.0);
        colorAdjust.setBrightness(0.0);
        colorAdjust.setSaturation(-1.0);
        
        colorAdjust1 = new ColorAdjust();
        colorAdjust1.setContrast(0.0);
        colorAdjust1.setHue(0.0);
        colorAdjust1.setBrightness(0.0);
        colorAdjust1.setSaturation(0.0);

        if(tab[0] == 0){
        Achiev1.setEffect(colorAdjust);
        }else{
            Achiev1.setEffect(colorAdjust1);

        	
        }
        if(tab[1] == 0){
        Achiev2.setEffect(colorAdjust);
        }else{
            Achiev2.setEffect(colorAdjust1);

        	
        }
        if(tab[2] == 0){

        Achiev3.setEffect(colorAdjust);
        }else{
        	
            Achiev3.setEffect(colorAdjust1);

        }

        Achiev1.hoverProperty().addListener((observable)-> {
        		if(tab[0]==0){
            if (Achiev1.isHover()) {
                Achiev1.setEffect(colorAdjust1);
            		
            } else {
                Achiev1.setEffect(colorAdjust);
            }}
        });
        Achiev2.hoverProperty().addListener((observable)-> {
    		if(tab[1]==0){

            if (Achiev2.isHover()) {
            	Achiev2.setEffect(colorAdjust1);
            		
            } else {
            	Achiev2.setEffect(colorAdjust);
            }}
        });
        Achiev3.hoverProperty().addListener((observable)-> {
    		if(tab[3]==0){

            if (Achiev3.isHover()) {
            	Achiev3.setEffect(colorAdjust1);
            		
            } else {
            	Achiev3.setEffect(colorAdjust);
            }}
        });
    }
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

    }
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void unlock_achiev1(){
        tab[0]  = 1 ;

    	this.Achiev1.setEffect(colorAdjust1); 
    	
    }
    public void unlock_achiev2(){
        tab[1]  = 1 ;

    	this.Achiev2.setEffect(colorAdjust1); 
    	
    }  
    public void unlock_achiev3(){
        tab[2]  = 1 ;

    	this.Achiev3.setEffect(colorAdjust1); 
    }  
    public void setAchiev(int tab[]){
    	this.tab[0] = tab[0];
    	this.tab[1] = tab[1];
    	this.tab[2] = tab[2];

    }
}
