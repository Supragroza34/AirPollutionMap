import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Main application class for the UK Air Pollution visualization.
 * This class initializes the application and sets up the primary UI.
 */
public class MainApp extends Application {
    private Controller controller;
    
    @Override
    public void start(Stage stage) {
        // Initialize data services
        DataLoader dataLoader = new DataLoader();
        CityDataFilter cityDataFilter = new CityDataFilter();
        CityMapManager cityMapManager = new CityMapManager();
        
        // Check if a city was passed via system property (from welcome page)
        String startCity = System.getProperty("selected.city");
        String selectedCity = (startCity != null && !startCity.isEmpty()) ? startCity : "London";
        
        // Load all datasets
        dataLoader.loadAllDatasets(
            java.util.Arrays.asList("2018", "2019", "2020", "2021", "2022", "2023"),
            java.util.Arrays.asList("NO2", "PM10", "PM2.5")
        );
        
        // Create the main controller
        controller = new Controller(dataLoader, cityDataFilter, cityMapManager, selectedCity);
        
        // Set up the root layout
        BorderPane rootLayout = controller.createRootLayout();
        
        // Set scene
        Scene scene = new Scene(rootLayout, 900, 700);
        
        // Load CSS
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        
        stage.setTitle("UK Air Pollution Visualization");
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     * Main method to launch the application
     */
    public static void main(String[] args) {
        launch(args);
    }
}