package graphicInterface;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

import graphicInterface.model.Depute;
import graphicInterface.model.HistoryData;
import graphicInterface.model.Loi;
import graphicInterface.view.AchievementViewController;
import graphicInterface.view.ChoiceOverviewController;
import graphicInterface.view.HistoryViewController;
import graphicInterface.view.IPChangerController;
import graphicInterface.view.LaunchGameController;
import graphicInterface.view.LaunchPopUpViewController;
import graphicInterface.view.PersonOverviewController;
import graphicInterface.view.ProposeLoiViewController;
import graphicInterface.view.RootLayoutController;
import graphicInterface.view.StatisticsViewController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class IPChanger extends Application {
    private static Stage primaryStage;
    private static BorderPane rootLayout;
    private String NotreIp;
	@Override
	public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("ChangeTonIp");
        Platform.setImplicitExit(false);
        initRootLayout();
        showPersonOverview();

	}

	public static void main(String[] args) {
		IPChanger app = new IPChanger();
		app.launch(args);
	}
	
	public void initRootLayout() {
	    try {
	        FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(IPChanger.class.getResource("view/IPChanger.fxml"));
	        rootLayout = (BorderPane) loader.load();

	        // Show the scene containing the root layout.
	        Scene scene = new Scene(rootLayout);
	        
	        primaryStage.setScene(scene);

	        primaryStage.show();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	public void showPersonOverview() {
		  try {
		        NotreIp = "0.0.0.0";

		        // Load person overview.
		        FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(IPChanger.class.getResource("view/IPChangerInside.fxml"));
		        AnchorPane personOverview = (AnchorPane) loader.load();
		        IPChangerController controller = loader.getController();
		        BufferedReader br = new BufferedReader(new FileReader("src/Properties/SecondaryProperties.txt"));
		            StringBuilder sb = new StringBuilder();
		            String line = br.readLine();

		            while (line != null) {
		                sb.append(line);
		                sb.append(System.lineSeparator());
		                line = br.readLine();
		            }
		            String everything = sb.toString();
		            String[] lines = sb.toString().split("\\n");

		            for(String s: lines){
		            String[] words = s.toString().split("=");
	            	System.out.println(words[0]);
	            	if(words[0].equals("host ")){
	            		NotreIp = words[1].substring(1);
	            	}
		            }
		        // Set person overview into the center of root layout.
		        rootLayout.setCenter(personOverview);
		        controller.setMainApp(this);
            	controller.setIP(NotreIp);;

		    } catch (IOException e) {
		        e.printStackTrace();
		    }
  }
}


