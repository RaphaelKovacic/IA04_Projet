package graphicInterface.view;

import java.util.ArrayList;
import java.util.List;

import graphicInterface.model.Depute;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;

public class StatisticsViewController {
	    @FXML
	    private BarChart<String, Integer> barChart;

	    @FXML
	    private CategoryAxis xAxis;
	    
	    @FXML
	    private ObservableList<String> partis = FXCollections.observableArrayList();
	    
	    private void initialize() {

	        // Get an array with the English month names.
	      //  String[] months = DateFormatSymbols.getInstance(Locale.ENGLISH).getMonths();
	        // Convert it to a list and add it to our ObservableList of months.

	        // Assign the month names as categories for the horizontal axis.
	        xAxis.setCategories(partis);
	    }
	    
	    public void setPartis(ArrayList<String> L_Parti){
	    	
	    	this.partis.addAll(L_Parti);
	    }
	    public void setDeputeData(List<Depute> deputes) {
	        // Count the number of people having their birthday in a specific month.
	        int[] monthCounter = new int[5];
	        for (Depute p : deputes) {
	            int month = partis.indexOf(p.getParti());

	        	monthCounter[month]++;
	        }
        	System.out.println(partis);


	        XYChart.Series<String, Integer> series = new XYChart.Series<>();

	        // Create a XYChart.Data object for each month. Add it to the series.
	        for (int i = 0; i < monthCounter.length; i++) {
	            series.getData().add(new XYChart.Data<>(partis.get(i), monthCounter[i]));
	        }

	        barChart.getData().add(series);
	    }

}
