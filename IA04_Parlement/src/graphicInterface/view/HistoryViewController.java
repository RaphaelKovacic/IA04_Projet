package graphicInterface.view;

import graphicInterface.MainApp;
import graphicInterface.model.Depute;
import graphicInterface.model.HistoryData;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

public class HistoryViewController {

    private MainApp mainApp;

    @FXML
    private TableView<HistoryData> HistoryTable;
    @FXML
    private TableColumn<HistoryData, String> codeColumn;
    @FXML
    private TableColumn<HistoryData, String> intuleColumn;
    
    
    @FXML
    private void initialize() {

    	codeColumn.setCellValueFactory(cellData -> cellData.getValue().codeProperty());
    	intuleColumn.setCellValueFactory(cellData -> cellData.getValue().intituleProperty());
    }
    
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        // Add observable list data to the table
        HistoryTable.setItems(mainApp.getHistoryData());
    }
    
}
