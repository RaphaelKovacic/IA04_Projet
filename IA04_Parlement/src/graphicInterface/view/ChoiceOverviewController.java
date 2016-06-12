package graphicInterface.view;

import java.util.List;

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
    private Button aucuneButton;
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
    private Button changerParti;

    @FXML
    private TextArea caseTexte;

    private String loiEnCours = "" ;
    // Reference to the main application.
    private MainApp mainApp;
	private Stage dialogStage;
    
    private int choix=-1;
    public ChoiceOverviewController() {
    }

    @FXML
    private void initialize() {
    	

        proposeLoi.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
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
         
        butt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("AvisButton!");
                choix = 5;
                dialogStage.close();
            }
            
            
            
            
        }); 
        butt.hoverProperty().addListener((observable)-> {
            final String idButton = butt.getText();

            if (butt.isHover()) {
            	caseTexte.setText( " Avis à récuperer : " + idButton
            		);
            } else {
            	caseTexte.setText(" ------------ ");
            }
        });
        changerParti.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                choix = 6;
                dialogStage.close();
            }
            
            
            
            
        }); 
        changerParti.hoverProperty().addListener((observable)-> {
            final String idButton = changerParti.getText();

            if (changerParti.isHover()) {
            	caseTexte.setText( " Changer de Parti : " + idButton
            		);
            } else {
            	caseTexte.setText(" ------------ ");
            }
        });
        aucuneButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                choix = 7;
                dialogStage.close();
            }
            
            
            
            
        }); 
        aucuneButton.hoverProperty().addListener((observable)-> {
            final String idButton = aucuneButton.getText();

            if (aucuneButton.isHover()) {
            	caseTexte.setText( " Aucune Action : " + idButton
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
                choix = 0;
                dialogStage.close();
            }
        });
        accepteLoi.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                choix = 1;
                dialogStage.close();
            }
        });
        
        refuseLoi.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                choix = 2;
                dialogStage.close();
            }
        });
        rumeur.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                choix = 3;
                dialogStage.close();
            }
        });
        sondage.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                choix = 4;
                dialogStage.close();
            }
        });
        butt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                choix = 5;
                dialogStage.close();
            }
        }); 
        changerParti.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                choix = 6;
                dialogStage.close();
            }
        }); 
        aucuneButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                choix = 7;
                dialogStage.close();
            }
        }); 
    }
    public void setChoice(int value, List<String> l_Actions) {
    	switch(value){
    	case 0 : {
    		enableButtons(l_Actions);
    		break;
    	}
    	case 1 : {
    		enableButtons1();
    		break;
    	}
    	}
    	
    }
    public void enableButtons(List<String> l_Actions){
    	
    	//"Aucune"
    	//"Proposer une loi"
    	//"Faire un sondage"
    	//"Changer de parti"
    	//"Avis du parlement"
    	//"Repandre une rumeur"
       	if(l_Actions.indexOf("Aucune") != -1){
        	aucuneButton.setDisable(false);
        	}
    	if(l_Actions.indexOf("Proposer une loi") != -1){
    	proposeLoi.setDisable(false);
    	}
    	if(l_Actions.indexOf("Repandre une rumeur") != -1){

    	rumeur.setDisable(false);
    	}
    	if(l_Actions.indexOf("Faire un sondage") != -1){

    	sondage.setDisable(false);
    	}
    	if(l_Actions.indexOf("Avis du parlement") != -1){

    	butt.setDisable(false);
    	}
    	if(l_Actions.indexOf("Changer de parti") != -1){

    	changerParti.setDisable(false);
    	}
    	caseTexte.setText("C'est à vous de jouer");
    }
    
    public void enableButtons1(){
    	
    	aucuneButton.setDisable(true);
    	refuseLoi.setDisable(false);
    	accepteLoi.setDisable(false);
    	caseTexte.setText("à vous de voter");
    	
    	
    }
    public void disableButtons(){
    	
    	aucuneButton.setDisable(true);
    	proposeLoi.setDisable(true);
    	rumeur.setDisable(true);
    	sondage.setDisable(true);
    	butt.setDisable(true);
    	accepteLoi.setDisable(true);
    	refuseLoi.setDisable(true);
    	changerParti.setDisable(true);

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
