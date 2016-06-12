package graphicInterface.view;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import graphicInterface.IPChanger;
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
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class IPChangerController {
	private     IPChanger mainApp;
	
    @FXML
    private TextField textIP;

    @FXML
    private Button proposerButt;
    
    String NotreIP;
    // Reference to the main application.
    
    @FXML
    private void initialize() {
    	this.textIP.setText("localhost");
        proposerButt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream("src/Properties/SecondaryProperties.txt"), "utf-8"))) {
             writer.write("main=false\ngui=false\ncontainer-name = Parlement\nhost = "+getIp()+"\n#local-port=1099\n");
          } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

            }
        }); 
    	//System.out.println("Hahahaha");
    }
    
 

    

public String getIp(){
	
	return textIP.getText();
	
}

public void setMainApp(IPChanger mainApp) {
    this.mainApp = mainApp;
}


public void setIP(String Ip){
	
	this.textIP.setText(Ip);
	
}
}
