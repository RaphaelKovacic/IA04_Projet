package graphicInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

import Class_For_JSON.DeputeAttRumeur;
import graphicInterface.model.Depute;
import graphicInterface.model.DeputeForList;
import graphicInterface.model.HistoryData;
import graphicInterface.model.Loi;
import graphicInterface.view.AchievementViewController;
import graphicInterface.view.ChangePartyController;
import graphicInterface.view.ChoiceOverviewController;
import graphicInterface.view.HistoryViewController;
import graphicInterface.view.LaunchGameController;
import graphicInterface.view.LaunchPopUpViewController;
import graphicInterface.view.ListDeputesViewController;
import graphicInterface.view.PersonOverviewController;
import graphicInterface.view.ProposeLoiViewController;
import graphicInterface.view.RootLayoutController;
import graphicInterface.view.StatisticsViewController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainApp extends Application {
    private static Stage primaryStage;
    private static BorderPane rootLayout;
    private static ObservableList<Depute> DeputeData = FXCollections.observableArrayList();
    private static ObservableList<HistoryData> listeHistorique = FXCollections.observableArrayList();
    private static ObservableList<Loi> listeLoi = FXCollections.observableArrayList();
    private static ObservableList<String> listeParti = FXCollections.observableArrayList();
    private static ObservableList<DeputeForList> listeDeput = FXCollections.observableArrayList();

    private static int  tabAchiev[] = {0,0,0};
    private String style;
	@Override
	public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("ParlementViewer");
        Platform.setImplicitExit(false);
        initRootLayout();
        showPersonOverview();
        showHistoryOverview();
	}

	public static void main(String[] args) {
	        	 MainApp app = new MainApp();
				app.launch(args);
	}
	
	public void initRootLayout() {
	    try {
	        FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(MainApp.class
	                .getResource("view/RootLayout.fxml"));
	        rootLayout = (BorderPane) loader.load();

	        // Show the scene containing the root layout.
	        Scene scene = new Scene(rootLayout);

	        primaryStage.setScene(scene);

	        // Give the controller access to the main app.
	        RootLayoutController controller = loader.getController();
	        controller.setMainApp(this);

	        primaryStage.show();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public void showPersonOverview() {
		  try {
		        // Load person overview.
		        FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(MainApp.class.getResource("view/PersonOverview.fxml"));
		        AnchorPane personOverview = (AnchorPane) loader.load();

		        // Set person overview into the center of root layout.
		        rootLayout.setCenter(personOverview);
		       PersonOverviewController controller = loader.getController();
		        controller.setMainApp(this);

		    } catch (IOException e) {
		        e.printStackTrace();
		    }
    }
	public void showHistoryOverview() {
		  try {
		        // Load person overview.
		        FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(MainApp.class.getResource("view/HistoryView.fxml"));
		        AnchorPane historyView = (AnchorPane) loader.load();

		        // Set person overview into the center of root layout.
		        rootLayout.setBottom(historyView);
			       HistoryViewController controller1 = loader.getController();
			        controller1.setMainApp(this);

		    } catch (IOException e) {
		        e.printStackTrace();
		    }
  }
	public static int showChoicePopUp(int value, String loiEnCours, List<String> l_Actions) {
		final SimpleIntegerProperty choix = new SimpleIntegerProperty(-1);
	    final CountDownLatch latch = new CountDownLatch(1);
		
	    Platform.runLater(new Runnable() {
	        @Override public void run() {
	        	 try {
	     	    	
	     	        // Load the fxml file and create a new stage for the popup dialog.
	     	        FXMLLoader loader = new FXMLLoader();
	     	        loader.setLocation(MainApp.class.getResource("view/ChoiceOverview.fxml"));
	     	        AnchorPane page = (AnchorPane) loader.load();

	     	        // Create the dialog Stage.
	     	        Stage dialogStage = new Stage();
	     	        dialogStage.setTitle("Choix");
	     	        dialogStage.initModality(Modality.WINDOW_MODAL);
	     	        dialogStage.initOwner(primaryStage);
	     	        Scene scene = new Scene(page);
	     	        dialogStage.setScene(scene);

	     	        // Set the person into the controller.
	     	        ChoiceOverviewController controller = loader.getController();
	     	        controller.setDialogStage(dialogStage);
	     	        controller.setChoice(value,l_Actions);
	     	        controller.setLoiEnCours(loiEnCours);
	     	        // Show the dialog and wait until the user closes it
	     	        dialogStage.showAndWait();
	     	        controller.disableButtons();
	     	        choix.set(controller.getChoix());
	     	        latch.countDown();
	     	    } catch (IOException e) {
	     	        e.printStackTrace();
	     	    }	        }
	      });

	    try {
	      latch.await();
	    } catch (InterruptedException e) {
	      Platform.exit();
	    }


	        return choix.intValue();
	}
	
	public static int showLaunch() {
	    final CountDownLatch latch = new CountDownLatch(1);
		final SimpleIntegerProperty choix = new SimpleIntegerProperty(-1);

	    Platform.runLater(new Runnable() {
	        @Override public void run() {
	        	 try {
	     	    	
	     	        // Load the fxml file and create a new stage for the popup dialog.
	     	        FXMLLoader loader = new FXMLLoader();
	     	        loader.setLocation(MainApp.class.getResource("view/LaunchGameView.fxml"));
	     	        AnchorPane page = (AnchorPane) loader.load();

	     	        // Create the dialog Stage.
	     	        Stage dialogStage = new Stage();
	     	        dialogStage.setTitle("Choix");
	     	        dialogStage.initModality(Modality.WINDOW_MODAL);
	     	        dialogStage.initOwner(primaryStage);
	     	        Scene scene = new Scene(page);
	     	        dialogStage.setScene(scene);

	     	       LaunchGameController controller = loader.getController();
	     	        controller.setDialogStage(dialogStage);
	     	        // Show the dialog and wait until the user closes it
	     	        dialogStage.showAndWait();
	     	        choix.set(1);
	     	        latch.countDown();
	     	    } catch (IOException e) {
	     	        e.printStackTrace();
	     	    }	        }
	      });

	    try {
	      latch.await();
	    } catch (InterruptedException e) {
	      Platform.exit();
	    }
	    
        return choix.intValue();

	}

	
	public static int showLois(List<Class_For_JSON.Loi> loi_a_choisir) {
	    final CountDownLatch latch = new CountDownLatch(1);
		final SimpleIntegerProperty choixID = new SimpleIntegerProperty(-1);

	    Platform.runLater(new Runnable() {
	        @Override public void run() {
	        	 try {
	     	    	
	     	        // Load the fxml file and create a new stage for the popup dialog.
	     	        FXMLLoader loader = new FXMLLoader();
	     	        loader.setLocation(MainApp.class.getResource("view/ProposeLoiView.fxml"));
	     	        AnchorPane page = (AnchorPane) loader.load();

	     	        // Create the dialog Stage.
	     	        Stage dialogStage = new Stage();
	     	        dialogStage.setTitle("Choix");
	     	        dialogStage.initModality(Modality.WINDOW_MODAL);
	     	        dialogStage.initOwner(primaryStage);
	     	        Scene scene = new Scene(page);
	     	        dialogStage.setScene(scene);

	     	        ProposeLoiViewController controller = loader.getController();
	     	        controller.setDialogStage(dialogStage);
	     	        // Show the dialog and wait until the user closes it
					for (int y = 0; y < loi_a_choisir.size(); y++) {
						addLoi(loi_a_choisir.get(y).getId(),loi_a_choisir.get(y).getNom(), loi_a_choisir.get(y).afficheString());;
					}
					controller.setLois(listeLoi);
	     	        dialogStage.showAndWait();
	     	        Loi choix = controller.getLoi();
	     	        choixID.set(choix.getId());
	     	       removeLoiData();
	     	        latch.countDown();
	     	    } catch (IOException e) {
	     	        e.printStackTrace();
	     	    }	        }
	      });

	    try {
	      latch.await();
	    } catch (InterruptedException e) {
	      Platform.exit();
	    }
		return choixID.get();
	}


	public static int showDeputes(List<DeputeAttRumeur> list_DeputeAttRumeur) {
	    final CountDownLatch latch = new CountDownLatch(1);
		final SimpleIntegerProperty choixID = new SimpleIntegerProperty(-1);

	    Platform.runLater(new Runnable() {
	        @Override public void run() {
	        	 try {
	     	    	
	     	        // Load the fxml file and create a new stage for the popup dialog.
	     	        FXMLLoader loader = new FXMLLoader();
	     	        loader.setLocation(MainApp.class.getResource("view/ListDeputesView.fxml"));
	     	        AnchorPane page = (AnchorPane) loader.load();

	     	        // Create the dialog Stage.
	     	        Stage dialogStage = new Stage();
	     	        dialogStage.setTitle("Choix");
	     	        dialogStage.initModality(Modality.WINDOW_MODAL);
	     	        dialogStage.initOwner(primaryStage);
	     	        Scene scene = new Scene(page);
	     	        dialogStage.setScene(scene);
	     	        ListDeputesViewController controller = loader.getController();
	     	        controller.setDialogStage(dialogStage);
	     	        // Show the dialog and wait until the user closes it
					for (int y = 0; y < list_DeputeAttRumeur.size(); y++) {
						addDepuList(list_DeputeAttRumeur.get(y).get_Id(), list_DeputeAttRumeur.get(y).affiche_a_utilisateur_String());
					}
					controller.setLois(getDeputeListData());
	     	        dialogStage.showAndWait();
	     	        Integer choix = controller.getDeputeID();
	     	        choixID.set(choix);
	     	       removeDepuListData();
	     	        latch.countDown();
	     	    } catch (IOException e) {
	     	        e.printStackTrace();
	     	    }	        }
	      });

	    try {
	      latch.await();
	    } catch (InterruptedException e) {
	      Platform.exit();
	    }
		return choixID.get();
	}
	
	public static String showPartis(List<String> Partis) {
	    final CountDownLatch latch = new CountDownLatch(1);
		final SimpleStringProperty choix = new SimpleStringProperty("");

	    Platform.runLater(new Runnable() {
	        @Override public void run() {
	        	 try {
	     	    	
	     	        // Load the fxml file and create a new stage for the popup dialog.
	     	        FXMLLoader loader = new FXMLLoader();
	     	        loader.setLocation(MainApp.class.getResource("view/ChangePartyView.fxml"));
	     	        AnchorPane page = (AnchorPane) loader.load();

	     	        // Create the dialog Stage.
	     	        Stage dialogStage = new Stage();
	     	        dialogStage.setTitle("Choix");
	     	        dialogStage.initModality(Modality.WINDOW_MODAL);
	     	        dialogStage.initOwner(primaryStage);
	     	        Scene scene = new Scene(page);
	     	        dialogStage.setScene(scene);

	     	       ChangePartyController controller = loader.getController();
	     	        controller.setDialogStage(dialogStage);
	     	        // Show the dialog and wait until the user closes it
					for (int y = 0; y < Partis.size(); y++) {
						addParti(Partis.get(y));
					}
					controller.setPartis(listeParti);
	     	        dialogStage.showAndWait();
	     	        choix.set(controller.getParti());
	     	       removeStringData();
	     	        latch.countDown();
	     	    } catch (IOException e) {
	     	        e.printStackTrace();
	     	    }	        }
	      });

	    try {
	      latch.await();
	    } catch (InterruptedException e) {
	      Platform.exit();
	    }
		return choix.get();
	}

	
	public void showStatistics() {
	    try {
	        // Load the fxml file and create a new stage for the popup.
	        FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(MainApp.class.getResource("view/Statistics.fxml"));
	        AnchorPane page = (AnchorPane) loader.load();
	        Stage dialogStage = new Stage();
	        dialogStage.setTitle("Statistics");
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        dialogStage.initOwner(primaryStage);
	        Scene scene = new Scene(page);
	        dialogStage.setScene(scene);

	        // Set the persons into the controller.
	        StatisticsViewController controller = loader.getController();
	        ArrayList<String> L_Parti = new ArrayList<String>();
			L_Parti.add("Altruistes");L_Parti.add("Erudits");L_Parti.add("Audacieux");
			L_Parti.add("Sinceres");L_Parti.add("Fraternels");

	        controller.setPartis(L_Parti);
	        controller.setDeputeData(getDeputeData());

	        dialogStage.show();

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public void showAchievements() {
	    try {
	        // Load the fxml file and create a new stage for the popup.
	        FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(MainApp.class.getResource("view/AchievementView.fxml"));
	        AnchorPane page = (AnchorPane) loader.load();
	        Stage dialogStage = new Stage();
	        dialogStage.setTitle("Achievements");
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        dialogStage.initOwner(primaryStage);
	        Scene scene = new Scene(page);
	        dialogStage.setScene(scene);

	        // Set the persons into the controller.
	        AchievementViewController controller = loader.getController();
	        controller.setAchiev(this.tabAchiev);
	        
	        if(this.tabAchiev[0] == 1){
	        	
	        	controller.unlock_achiev1();
	        	
	        }
	        if(this.tabAchiev[1] == 1){
	        	
	        	controller.unlock_achiev2();
	        	
	        }
	        
	        if(this.tabAchiev[2] == 1){
	        	
	        	controller.unlock_achiev3();
	        	
	        }
	        
	        dialogStage.show();

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void addDepute(String a, String b){
    	
    	DeputeData.add(new Depute(a,b));
    	
    }
    public static void addLoi(int i, String a,String b){
    	
    	listeLoi.add(new Loi(i,a,b));
    }
    public static void addDepute(String a, String b, String c, float popularite, float credibilite, float notoriete, float charisme, String idDepute){

    	DeputeData.add(new Depute(a,b,c,popularite,credibilite,notoriete,charisme,idDepute));
    	
    }
    public static void addHistorique(String a, int b){

    	listeHistorique.add(new HistoryData(a, b));
    	
    }
    public ObservableList<Depute> getDeputeData() {
        return this.DeputeData;
    }
    public ObservableList<Loi> getLoiData() {
        return this.listeLoi;
    }
    public static void removeLoiData() {
         listeLoi.clear();
    }
    
    public ObservableList<String> getPartiData() {
        return this.listeParti;
    }
    public static void removeStringData() {
         listeParti.clear();
    }
    public static void addParti(String parti){
    	
    	listeParti.add(parti);
    }
    
    public static ObservableList<DeputeForList> getDeputeListData() {
        return listeDeput;
    }
    public static void removeDepuListData() {
    	listeDeput.clear();
    }
    public static void addDepuList(int id, String description){
    	
    	listeDeput.add(new DeputeForList(id,description));
    }
    
    
    private static Depute parcoursDeputeData(String idDepute_){
    	Depute dep_;
    	int i = 0;
    	for(i = 0 ; i < DeputeData.size(); i++ ){
    	dep_ = DeputeData.get(i);
    	if( dep_.getIdDepute() == idDepute_){
    		
        	return dep_;

    	}
    	}
    	return null;
    }
    
    public static void setDeputeData(String idDepute_,String parti_ ,float popu_,float credi_ ,float noto_,float char_ ){
    	if(idDepute_ != null){
    	Depute depute_ = parcoursDeputeData(idDepute_);
    	if(depute_ != null){
    	if(parti_ != null)
    	depute_.setParti(parti_);
    	
    	depute_.setPopularite(popu_);
    	depute_.setCredibiltie(credi_);
    	depute_.setCharisme(char_);
    	}
    	}
    }
    public ObservableList<HistoryData> getHistoryData() {
        return this.listeHistorique;
    }
    public MainApp getMainAppClass(){
    	return this;
    }
    public static void setTabAchiev(int pos,int valeur){
    	
    	tabAchiev[pos] = valeur;
    	
    }
}


