package graphicInterface.view;

import graphicInterface.MainApp;
import graphicInterface.model.Depute;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class PersonOverviewController {
    @FXML
    private TableView<Depute> deputeTable;
    @FXML
    private TableColumn<Depute, String> nomColumn;
    @FXML
    private TableColumn<Depute, String> prenomColumn;

    @FXML
    private Label nomLabel;
    @FXML
    private Label prenomLabel;
    @FXML
    private Label partiLabel;
    @FXML
    private Label charismeLabel;
    @FXML
    private Label populariteLabel;
    @FXML
    private Label notorieteLabel;
    @FXML
    private Label influenceLabel;

    // Reference to the main application.
    private MainApp mainApp;
    
    
    public PersonOverviewController() {
    }

    @FXML
    private void initialize() {
        // Initialize the person table with the two columns.
        nomColumn.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
        prenomColumn.setCellValueFactory(cellData -> cellData.getValue().prenomProperty());
        
        showPersonDetails(null);

        deputeTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showPersonDetails(newValue));
    }
    
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        // Add observable list data to the table
        deputeTable.setItems(mainApp.getDeputeData());
    }
    private void showPersonDetails(Depute depute) {
        if (depute != null) {
            // Fill the labels with info from the person object.
            nomLabel.setText(depute.getNom());
            prenomLabel.setText(depute.getPrenom());
            partiLabel.setText(depute.getParti());
            charismeLabel.setText(Float.toString(depute.getCharisme()));
            populariteLabel.setText(Float.toString(depute.getPopularite()));
            notorieteLabel.setText(Float.toString(depute.getNotoriete()));
            influenceLabel.setText(Float.toString(depute.getCredibilite()));

        } else {
            nomLabel.setText("");
            prenomLabel.setText("");
            partiLabel.setText("");
            charismeLabel.setText("");
            populariteLabel.setText("");
            notorieteLabel.setText("");
            influenceLabel.setText("");
        }
    }

    
}
