import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.layout.VBox;
import javafx.scene.image.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

/**
 * Welcome page for the UK Air Pollution application.
 *
 * @author Nitin Ananatharaju 
 * @version 2.0
 */
public class WelcomePage extends Application
{
    //GUI component variables being created
    private Button myButton;
    private Button instructionsButton;
    private Label label;
    private Label label2;
    private Label instructionsBox;
    private BorderPane pane;
    private VBox layout;
    private HBox horizontalTop;

    /**
     * The start method is the main entry point for every JavaFX application.
     * It is called after the init() method has returned and after
     * the system is ready for the application to begin running.
     *
     * @param  stage the primary stage for this application.
     */
    @Override
    public void start(Stage stage)
    {
        // Create a Button that says click here
        this.myButton = new Button("Click here!");
        this.label = new Label("Welcome to London Air Pollution Simulation!");
        label.getStyleClass().add("welcome-title");
        this.instructionsButton = new Button("Instructions");
        this.label2 = new Label("Loading Simulation...");
        
        horizontalTop = new HBox(20,instructionsButton,myButton);
        horizontalTop.setAlignment(Pos.CENTER); //aligns the buttons
        
        Image mapImage = new Image("file:London.png");
        ImageView mapView = new ImageView(mapImage); //Creating a node so it can be added to the pane
        mapView.setFitWidth(900);  // Set width according to your scene size
        mapView.setFitHeight(700); // Set height according to your scene size
        
        layout = new VBox(10); //creating vertical box for storing title and instructions
        layout.setAlignment(Pos.CENTER); //centers title
        layout.getChildren().addAll(label,horizontalTop);
        
        
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(mapView, layout);
        stackPane.setAlignment(Pos.CENTER);
        
        pane = new BorderPane();
        pane.setCenter(stackPane);
        
        
       
        //Creating a text box for instructions
        this.instructionsBox = new Label("Instructions: Hover your mouse over the pollution map\n1 You can click on points and see what the pollution are for the selected periods\n2 You can also see recent statistics and graphs to help your understanding of how pollution levels have changed\n3 Flick through all of the statistics selections to ");
        instructionsBox.getStyleClass().add("instructions-box");
       
        // Create a new border pane
        this.pane = new BorderPane(); //dont need to add it manually to pane, the layout of border pane does it automatically
        pane.setTop(layout);
        pane.setCenter(mapView);

        //set an action on the button using method reference - explicitly fixed for single click
        myButton.setOnAction(this::buttonClick);
        instructionsButton.setOnAction(this::buttonClicked);

        // JavaFX must have a Scene (window content) inside a Stage (window)
        Scene scene = new Scene(pane, 900,700);
        
        // Load CSS
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        
        stage.setTitle("London Air Pollution");
        stage.setScene(scene);

        // Show the Stage (window)
        stage.show();
    }
   
    /**
     * This prints out a message saying loading simulation and opens the main application
     */
    private void buttonClick(ActionEvent event)
    {
        horizontalTop.getChildren().remove(myButton); //click here button disappears
        pane.setCenter(null); //removes the map
        pane.setCenter(label2); //Adds the loading simulation label
        
        PauseTransition delay = new PauseTransition(Duration.seconds(1)); //creating a 1 second delay between the label showing and simulation loading
        delay.setOnFinished(e -> { //event handler helps to make sure the simulation starts after delay
            // Launch the new MainApp instead of Version1
            MainApp mainApp = new MainApp();
            mainApp.start(new Stage()); // Open new stage
        });
        delay.play();
    }
   
    /**
     * This will display the instructions on how to use the application
     */
    private void buttonClicked(ActionEvent event)
    {
        // Check if instructionsBox is already in the layout
        if (!layout.getChildren().contains(instructionsBox)) {
            layout.getChildren().remove(instructionsButton); //instructions button gets deleted
            layout.getChildren().add(instructionsBox); //instructions get replaced by text box of instructions
        }
    }

    /**
     * Main method for standalone execution
     */
    public static void main(String[] args) {
        launch(args);
    }
}