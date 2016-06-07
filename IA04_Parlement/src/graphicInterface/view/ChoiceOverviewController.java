package graphicInterface.view;

import graphicInterface.MainApp;
import graphicInterface.model.Depute;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ChoiceOverviewController {

    @FXML
    private Button proposeLoi;
    @FXML
    private Button accepteLoi;
    @FXML
    private Button refuseLoi;
    @FXML
    private Button rumeur;
    @FXML
    private Button sondage;
    @FXML
    private Button butt;
    @FXML
    private TextArea caseTexte;

    private String loiEnCours = "" ;
    // Reference to the main application.
    private MainApp mainApp;
	private Stage dialogStage;
    
    private int choix=0;
    public ChoiceOverviewController() {
    }

    @FXML
    private void initialize() {
    	

        proposeLoi.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("proposeLoiButton!");
                choix = 0;
                dialogStage.close();
            }
        });
        
        
        proposeLoi.hoverProperty().addListener((observable)-> {
            final String idButton = proposeLoi.getText();

            if (proposeLoi.isHover()) {
            	caseTexte.setText( " Loi à proposer : " + idButton
            		);
            } else {
            	caseTexte.setText(" ------------ ");
            }
        });

        
        accepteLoi.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("accepteLoiButton!");
                choix = 1;
                dialogStage.close();
            }
        });
        
        accepteLoi.hoverProperty().addListener((observable)-> {
            final String idButton = accepteLoi.getText();

            if (accepteLoi.isHover()) {
            	caseTexte.setText( " Loi à voter : " + loiEnCours
            		);
            } else {
            	caseTexte.setText(" ------------ ");
            }
        });
        
        refuseLoi.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("refuseLoiButton!");
                choix = 2;
                dialogStage.close();
            }
        });
        
        refuseLoi.hoverProperty().addListener((observable)-> {
            final String idButton = refuseLoi.getText();

            if (refuseLoi.isHover()) {
            	caseTexte.setText( " Loi à voter : " + loiEnCours
            		);
            } else {
            	caseTexte.setText(" ------------ ");
            }
        });
        
        rumeur.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("rumeurButton!");
                choix = 3;
                dialogStage.close();
            }
        });
        
        rumeur.hoverProperty().addListener((observable)-> {
            final String idButton = rumeur.getText();

            if (rumeur.isHover()) {
            	caseTexte.setText( " rumeur à propager : " + idButton
            		);
            } else {
            	caseTexte.setText(" ------------ ");
            }
        });
        sondage.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("sondageButton!");
                choix = 4;
                dialogStage.close();
            }
            
            
            
            
        }); 
        sondage.hoverProperty().addListener((observable)-> {
            final String idButton = sondage.getText();

            if (sondage.isHover()) {
            	caseTexte.setText( " sondage à réaliser : " + idButton
            		);
            } else {
            	caseTexte.setText(" ------------ ");
            }
        });
         

    	
    }
    
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        proposeLoi.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("proposeLoiButton!");
                choix = 0;
                dialogStage.close();
            }
        });
        accepteLoi.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("accepteLoiButton!");
                choix = 1;
                dialogStage.close();
            }
        });
        
        refuseLoi.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("refuseLoiButton!");
                choix = 2;
                dialogStage.close();
            }
        });
        rumeur.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("rumeurButton!");
                choix = 3;
                dialogStage.close();
            }
        });
        sondage.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("sondageButton!");
                choix = 4;
                dialogStage.close();
            }
        }); 
    }
    public void setChoice(int value) {
    	switch(value){
    	case 0 : {
    		
    		enableButtons();
    		break;
    	}
    	case 1 : {
    		enableButtons1();
    		break;
    	}
    	}
    	
    }
    public void enableButtons(){
    	
    	
    	proposeLoi.setDisable(false);
    	rumeur.setDisable(false);
    	sondage.setDisable(false);
    	butt.setDisable(false);
    	caseTexte.setText("C'est à vous de jouer");
    }
    
    public void enableButtons1(){
    	
    	refuseLoi.setDisable(false);
    	accepteLoi.setDisable(false);
    	caseTexte.setText("Ce à vous de voter");
    	
    	
    }
    public void disableButtons(){
    	
    	
    	proposeLoi.setDisable(true);
    	rumeur.setDisable(true);
    	sondage.setDisable(true);
    	butt.setDisable(true);
    	accepteLoi.setDisable(true);
    	refuseLoi.setDisable(true);

    }
    public int getChoix(){
    	
    	return choix;
    }
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    public void setLoiEnCours(String loi_){
    	this.loiEnCours = loi_;
    }
}
