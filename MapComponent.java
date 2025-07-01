import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import java.util.Arrays;
import java.util.List;

/**
 * Component that handles map visualization and user interactions with the map.
 */
public class MapComponent {
    // UI components
    private Pane mapPane;
    private ScrollPane scrollPane;
    private Label realEastLabel;
    private Label realNorthLabel;
    private Label eastLabel;
    private Label northLabel;
    
    // Data and state
    private String selectedCity;
    private CityDataFilter cityDataFilter;
    private CityMapManager cityMapManager;
    private CoordinateConverter coordinateConverter;
    private MapVisualizer mapVisualizer;
    
    /**
     * Constructor
     */
    public MapComponent(CityDataFilter cityDataFilter, CityMapManager cityMapManager, String initialCity) {
        this.cityDataFilter = cityDataFilter;
        this.cityMapManager = cityMapManager;
        this.selectedCity = initialCity;
        
        initializeComponent();
    }
    
    /**
     * Initialize the map component
     */
    private void initializeComponent() {
        // Create the labels for coordinates
        realEastLabel = new Label("0");
        realNorthLabel = new Label("0");
        eastLabel = new Label("East Co-ordinate:");
        northLabel = new Label("North Co-ordinate:");
        
        // Initialize the map for the selected city
        updateCity(selectedCity);
    }
    
    /**
     * Update the map for a different city
     */
    public void updateCity(String cityName) {
        selectedCity = cityName;
        
        // Get city boundary
        CityDataFilter.CityBoundary boundary = cityDataFilter.getCityBoundary(cityName);
        if (boundary == null) {
            System.out.println("Error: City boundary not found for " + cityName);
            return;
        }
        
        // Get map info
        CityMapManager.CityMapInfo mapInfo = cityMapManager.getCityMapInfo(cityName);
        if (mapInfo == null) {
            System.out.println("Error: Map info not found for " + cityName);
            return;
        }
        
        // Create coordinate converter for this city
        coordinateConverter = new CoordinateConverter(
            boundary.getMinEasting(), boundary.getMaxEasting(), 
            boundary.getMinNorthing(), boundary.getMaxNorthing(), 
            mapInfo.getMapWidth(), mapInfo.getMapHeight()
        );
        
        // Load the map image
        Image mapImage = cityMapManager.loadCityMapImage(cityName);
        if (mapImage == null) {
            System.out.println("Error: Could not load map for " + cityName);
            return;
        }
        
        // Create new image view
        ImageView mapView = new ImageView(mapImage);
        mapView.fitWidthProperty().bind(mapPane.widthProperty());
        mapView.fitHeightProperty().bind(mapPane.heightProperty());
        mapView.setPreserveRatio(true); // Keeps aspect ratio intact

        
        // Create or clear the map pane
        if (mapPane == null) {
            mapPane = new Pane();
            //mapPane.setPrefSize(mapInfo.getMapWidth(), mapInfo.getMapHeight());
            mapPane.getStyleClass().add("pane");
            
            // Add mouse event handlers
            mapPane.setOnMouseMoved(this::handleMouseMoved);
            mapPane.setOnMouseClicked(this::handleMouseClicked);
            
            // Create scroll pane
            scrollPane = new ScrollPane(mapPane);
            scrollPane.setPannable(true);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
        } else {
            mapPane.getChildren().clear();
        }
        
        // Add the map image to the pane
        mapPane.getChildren().add(mapView);
        
        // Create the map visualizer
        mapVisualizer = new MapVisualizer(mapPane, coordinateConverter, cityName);
    }
    
    /**
     * Handle mouse moved event to update coordinate display
     */
    private void handleMouseMoved(MouseEvent event) {
        double pixelX = event.getX();
        double pixelY = event.getY();
        
        if (coordinateConverter.isWithinBounds(pixelX, pixelY)) {
            double realEasting = coordinateConverter.convertToRealEasting(pixelX);
            double realNorthing = coordinateConverter.convertToRealNorthing(pixelY);
            
            realEastLabel.setText(String.format("%.0f", realEasting));
            realNorthLabel.setText(String.format("%.0f", realNorthing));
        }
    }
    
    // Data and state for finding nearest points
    private DataLoader dataLoader;
    private List<DataPoint> currentCityDataList;
    private String selectedPollutant;
    private String selectedYear;
    
    /**
     * Set the data loader for accessing datasets
     */
    public void setDataLoader(DataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }
    
    /**
     * Set the current pollutant and year for data lookup
     */
    public void setSelectedData(String pollutant, String year, List<DataPoint> cityDataList) {
        this.selectedPollutant = pollutant;
        this.selectedYear = year;
        this.currentCityDataList = cityDataList;
    }
    
    /**
     * Handle mouse clicked event for showing data point information
     */
    private void handleMouseClicked(MouseEvent event) {
        // Check if this is a double-click
        if (event.getClickCount() == 2) {
            double pixelX = event.getX();
            double pixelY = event.getY();
            
            // Convert to real coordinates
            double realEasting = coordinateConverter.convertToRealEasting(pixelX);
            double realNorthing = coordinateConverter.convertToRealNorthing(pixelY);
            
            // Find the nearest data point
            DataPoint nearestPoint = findNearestDataPoint(realEasting, realNorthing);
            
            if (nearestPoint != null) {
                String output = String.format(
                    "City: %s\nGrid Code: %d\nEasting: %.2f\nNorthing: %.2f\nPollution Level: %.2f", 
                    selectedCity, 
                    nearestPoint.gridCode(), 
                    (double) nearestPoint.x(), 
                    (double) nearestPoint.y(), 
                    (double) nearestPoint.value()
                );
                
                // Show popup with data and chart
                showInfoPopupWithChart(output);
            } else {
                // Just show coordinates if no data point is found nearby
                String message = String.format("No data point found near\nEasting=%.2f, Northing=%.2f", 
                                             realEasting, realNorthing);
                showInfoPopup(message);
            }
        }
    }
    
    /**
     * Find the nearest data point to the given coordinates
     */
    private DataPoint findNearestDataPoint(double easting, double northing) {
        if (currentCityDataList == null || currentCityDataList.isEmpty()) {
            return null;
        }
        
        DataPoint nearest = null;
        double minDistance = Double.MAX_VALUE;
        
        for (DataPoint point : currentCityDataList) {
            double distance = Math.sqrt(
                Math.pow(point.x() - easting, 2) + 
                Math.pow(point.y() - northing, 2)
            );
            
            if (distance < minDistance) {
                minDistance = distance;
                nearest = point;
            }
        }
        
        // Get city-specific maximum distance
        double maxDistance = mapVisualizer.getMaxNearestPointDistance();
        
        // Only return the point if it's within the city-specific maximum distance
        return (minDistance <= maxDistance) ? nearest : null;
    }
    
    /**
     * Show an information popup
     */
    private void showInfoPopup(String message) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Location Information");
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        
        Label infoLabel = new Label(message);
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> popupStage.close());
        
        content.getChildren().addAll(infoLabel, closeButton);
        
        javafx.scene.Scene scene = new javafx.scene.Scene(content, 300, 200);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }
    
    /**
     * Show an information popup with line chart of pollution trends
     */
    private void showInfoPopupWithChart(String message) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Pollution Data");
        
        Label pollutionLabel = new Label(message);
        
        // Create line chart for pollution trends if we have a Graph instance
        LineChart<String, Number> lineChart = null;
        if (dataLoader != null && selectedPollutant != null) {
            // Create a temporary graph to generate the chart
            Graph tempGraph = new Graph(dataLoader, cityDataFilter);
            tempGraph.setSelectedCity(selectedCity);
            
            // Create chart with data for all years
            List<String> years = Arrays.asList("2018", "2019", "2020", "2021", "2022", "2023");
            lineChart = tempGraph.lineGraph(selectedPollutant, years);
            lineChart.setPrefSize(400, 300);
        }
        
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> popupStage.close());
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        
        // Add either the chart or a message if chart couldn't be created
        if (lineChart != null) {
            content.getChildren().addAll(pollutionLabel, lineChart, closeButton);
        } else {
            Label noChartLabel = new Label("(Chart data unavailable)");
            content.getChildren().addAll(pollutionLabel, noChartLabel, closeButton);
        }
        
        javafx.scene.Scene scene = new javafx.scene.Scene(content, 450, 500);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }
    
    /**
     * Plot a point at the given coordinates
     */
    public boolean plotPoint(double easting, double northing) {
        return mapVisualizer.plotPoint(easting, northing);
    }
    
    /**
     * Clear all data visualizations from the map
     */
    public void clearMapData() {
        if (mapVisualizer != null) {
            mapVisualizer.clearMapData();
        }
    }
    
    /**
     * Visualize pollution data on the map
     */
    public void visualizePollutionData(List<DataPoint> dataPoints) {
        mapVisualizer.visualizePollutionData(dataPoints);
    }
    
    /**
     * Visualize high pollution areas
     */
    public void visualizeHighPollutionAreas(List<DataPoint> dataPoints) {
        mapVisualizer.visualizeHighPollutionAreas(dataPoints);
    }
    
    /**
     * Visualize low pollution areas
     */
    public void visualizeLowPollutionAreas(List<DataPoint> dataPoints) {
        mapVisualizer.visualizeLowPollutionAreas(dataPoints);
    }
    
    /**
     * Get the ScrollPane containing the map
     */
    public ScrollPane getScrollPane() {
        return scrollPane;
    }
    
    /**
     * Get the coordinate labels as a HBox
     */
    public HBox getCoordinateLabels() {
        return new HBox(10, eastLabel, realEastLabel, northLabel, realNorthLabel);
    }
}