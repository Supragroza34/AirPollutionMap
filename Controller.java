import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;

/**
 * Controller class that coordinates between UI components and data services.
 * This class manages the application state and handles the main user interactions.
 * 
 * @author Tejas Raj
 * @version 2.0
 */
public class Controller {
    // Data services
    private final DataLoader dataLoader;
    private final CityDataFilter cityDataFilter;
    private final CityMapManager cityMapManager;
    
    // UI Components
    private MapComponent mapComponent;
    private StatisticsComponent statisticsComponent;
    private ComboBox<String> citySelector;
    private Label resultLabel;
    
    // Application state
    private String selectedCity;
    private String selectedPollutant;
    private String selectedYear;
    
    // Data
    private List<String> years = java.util.Arrays.asList("2018", "2019", "2020", "2021", "2022", "2023");
    private List<String> pollutants = java.util.Arrays.asList("NO2", "PM10", "PM2.5");
    
    /**
     * Constructor
     */
    public Controller(DataLoader dataLoader, CityDataFilter cityDataFilter, 
                       CityMapManager cityMapManager, String selectedCity) {
        this.dataLoader = dataLoader;
        this.cityDataFilter = cityDataFilter;
        this.cityMapManager = cityMapManager;
        this.selectedCity = selectedCity;
        
        // Initialize UI components
        initComponents();
    }
    
    /**
     * Initialize UI components
     */
    private void initComponents() {
        // Create graph adapter for statistics
        GraphAdapter graph = new GraphAdapter(dataLoader, cityDataFilter);
        graph.setSelectedCity(selectedCity);
        
        // Initialize map component with current city
        mapComponent = new MapComponent(cityDataFilter, cityMapManager, selectedCity);
        mapComponent.setDataLoader(dataLoader);
        
        // Initialize statistics component
        statisticsComponent = new StatisticsComponent(dataLoader, cityDataFilter, graph, selectedCity);
        
        // Set up city selector
        setupCitySelector();
        
        // Create result label
        resultLabel = new Label("Select a pollutant and year to view data");
        resultLabel.getStyleClass().add("title-label");
    }
    
    /**
     * Create the main root layout for the application
     */
    public BorderPane createRootLayout() {
        BorderPane root = new BorderPane();
        
        // Create menu bar
        MenuBar menuBar = createMenuBar();
        
        // Create city selector and input controls
        HBox controlsBox = createControlsBox();
        
        // Create tab pane with map and statistics
        TabPane tabPane = createTabPane();
        
        // Add components to the root layout
        VBox topBox = new VBox(10, menuBar, controlsBox);
        root.setTop(topBox);
        root.setCenter(tabPane);
        root.setBottom(resultLabel);
        
        return root;
    }
    
    /**
     * Create the menu bar
     */
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        
        // Pollution menu
        Menu pollutionMenu = new Menu("Pollution");
        for (String pollutant : pollutants) {
            MenuItem item = new MenuItem(pollutant);
            item.setOnAction(e -> updatePollutant(pollutant));
            pollutionMenu.getItems().add(item);
        }
        
        // Year menu
        Menu yearMenu = new Menu("Year");
        for (String year : years) {
            MenuItem item = new MenuItem(year);
            item.setOnAction(e -> updateYear(year));
            yearMenu.getItems().add(item);
        }
        
        // Filter menu
        Menu filterMenu = new Menu("Filter");
        MenuItem highPollutionItem = new MenuItem("High Pollution Areas");
        highPollutionItem.setOnAction(e -> filterHighPollution());
        
        MenuItem lowPollutionItem = new MenuItem("Low Pollution Areas");
        lowPollutionItem.setOnAction(e -> filterLowPollution());
        
        MenuItem resetFilterItem = new MenuItem("Reset Filter");
        resetFilterItem.setOnAction(e -> {
            mapComponent.clearMapData();
            updateVisualization();
        });
        
        filterMenu.getItems().addAll(highPollutionItem, lowPollutionItem, resetFilterItem);
        
        // Add menus to menu bar
        menuBar.getMenus().addAll(pollutionMenu, yearMenu, filterMenu);
        
        return menuBar;
    }
    
    /**
     * Create the controls box with city selector and coordinate input
     */
    private HBox createControlsBox() {
        TextField eastingField = new TextField();
        TextField northingField = new TextField();
        
        Button convertButton = new Button("Convert & Plot");
        convertButton.setOnAction(e -> {
            try {
                double easting = Double.parseDouble(eastingField.getText());
                double northing = Double.parseDouble(northingField.getText());
                
                boolean success = mapComponent.plotPoint(easting, northing);
                
                if (success) {
                    resultLabel.setText("Point plotted at Easting: " + easting + ", Northing: " + northing);
                } else {
                    resultLabel.setText("Failed to plot point (out of map bounds).");
                }
            } catch (NumberFormatException ex) {
                resultLabel.setText("Please enter valid numeric coordinates.");
            }
        });
        
        HBox controlsBox = new HBox(10, 
                new Label("City:"), citySelector,
                new Label("Easting:"), eastingField, 
                new Label("Northing:"), northingField, 
                convertButton);
        controlsBox.setPadding(new Insets(10));
        
        return controlsBox;
    }
    
    /**
     * Create the tab pane with map and statistics tabs
     */
    private TabPane createTabPane() {
        TabPane tabPane = new TabPane();
        
        Tab mapTab = new Tab("Map Visualisation");
        mapTab.setContent(mapComponent.getScrollPane());
        mapTab.setClosable(false);
        
        Tab statsTab = new Tab("Statistics");
        statsTab.setContent(statisticsComponent.getStatsContent());
        statsTab.setClosable(false);
        
        Tab detailsTab = new Tab("City Details");
        detailsTab.setContent(createCityDetailsContent());
        detailsTab.setClosable(false);
        
        tabPane.getTabs().addAll(mapTab, statsTab, detailsTab);
        
        return tabPane;
    }
    
    /**
     * Create content for the city details tab
     */
    private TextArea createCityDetailsContent() {
        TextArea cityInfoArea = new TextArea();
        cityInfoArea.setEditable(false);
        
        // Set initial city info
        updateCityInfo(cityInfoArea, selectedCity);
        
        // Add listener to update info when city changes
        citySelector.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateCityInfo(cityInfoArea, newVal);
        });
        
        return cityInfoArea;
    }
    
    /**
     * Update city information in the details tab
     */
    private void updateCityInfo(TextArea infoArea, String cityName) {
        CityDataFilter.CityBoundary boundary = cityDataFilter.getCityBoundary(cityName);
        if (boundary == null) {
            infoArea.setText("No information available for " + cityName);
            return;
        }
        
        StringBuilder info = new StringBuilder();
        info.append("Information for ").append(cityName).append("\n\n");
        info.append("Geographic Boundaries:\n");
        info.append("Min Easting: ").append(boundary.getMinEasting()).append("\n");
        info.append("Max Easting: ").append(boundary.getMaxEasting()).append("\n");
        info.append("Min Northing: ").append(boundary.getMinNorthing()).append("\n");
        info.append("Max Northing: ").append(boundary.getMaxNorthing()).append("\n\n");
        
        // Add city-specific information
        switch (cityName) {
            case "London":
                info.append("London is the capital and largest city of England and the United Kingdom. It is known for its historical landmarks, diverse population, and significant air quality challenges due to traffic congestion and industrial activities.\n\n");
                info.append("Notable Air Quality Information:\n");
                info.append("- London implemented the Ultra Low Emission Zone (ULEZ) in April 2019 to reduce vehicle emissions\n");
                info.append("- NO2 levels have shown a general declining trend since 2018\n");
                info.append("- PM2.5 remains a significant concern, particularly in central areas\n");
                break;
            case "Manchester":
                info.append("Manchester is a major city in the northwest of England with a rich industrial history. It is one of the UK's largest and most important cities, known for its influence on industry, music, culture, and sports.\n\n");
                info.append("Notable Air Quality Information:\n");
                info.append("- Manchester faces air quality challenges due to its industrial past and current transportation infrastructure\n");
                info.append("- The city has implemented a Clean Air Plan to address pollution\n");
                info.append("- PM10 and NO2 levels are particularly monitored around major roadways\n");
                break;
            case "Newcastle":
                info.append("Newcastle upon Tyne is a city in northeast England. It was a major shipbuilding and manufacturing hub during the Industrial Revolution and has transitioned to a cultural and commercial center.\n\n");
                info.append("Notable Air Quality Information:\n");
                info.append("- Newcastle has undergone significant air quality improvements following the decline of heavy industry\n");
                info.append("- The city has implemented air quality management areas (AQMAs) to monitor and improve air quality\n");
                info.append("- NO2 from traffic remains the primary air quality concern\n");
                break;
        }
        
        infoArea.setText(info.toString());
    }
    
    /**
     * Set up the city selector dropdown
     */
    private void setupCitySelector() {
        citySelector = new ComboBox<>();
        citySelector.getItems().addAll(cityDataFilter.getAvailableCities());
        citySelector.setValue(selectedCity);
        
        citySelector.setOnAction(event -> {
            String newCity = citySelector.getValue();
            if (!newCity.equals(selectedCity)) {
                selectedCity = newCity;
                
                // Update components with new city
                mapComponent.updateCity(newCity);
                statisticsComponent.updateCity(newCity);
                
                updateVisualization();
            }
        });
    }
    
    /**
     * Update the selected pollutant
     */
    private void updatePollutant(String pollutant) {
        selectedPollutant = pollutant;
        statisticsComponent.setSelectedPollutant(pollutant);
        resultLabel.setText("Selected pollutant: " + pollutant);
        updateVisualization();
    }
    
    /**
     * Update the selected year
     */
    private void updateYear(String year) {
        selectedYear = year;
        statisticsComponent.setSelectedYear(year);
        resultLabel.setText("Selected year: " + year);
        updateVisualization();
    }
    
    /**
     * Filter high pollution areas
     */
    private void filterHighPollution() {
        mapComponent.clearMapData();
        
        if (selectedPollutant == null || selectedYear == null) {
            resultLabel.setText("Please select a pollutant and a year.");
            return;
        }
        
        DataSet dataset = dataLoader.getDataset(selectedPollutant, selectedYear);
        if (dataset != null) {
            List<DataPoint> cityDataList = cityDataFilter.filterCityArea(dataset.getData(), selectedCity);
            
            if (cityDataList.isEmpty()) {
                resultLabel.setText("No data found for the selected pollutant and year in " + selectedCity);
                return;
            }
            
            mapComponent.visualizeHighPollutionAreas(cityDataList);
            resultLabel.setText("Showing high pollution areas for " + selectedPollutant + " in " + selectedCity + " (" + selectedYear + ")");
        } else {
            resultLabel.setText("No dataset found for the selected pollutant and year.");
        }
    }
    
    /**
     * Filter low pollution areas
     */
    private void filterLowPollution() {
        mapComponent.clearMapData();
        
        if (selectedPollutant == null || selectedYear == null) {
            resultLabel.setText("Please select a pollutant and a year.");
            return;
        }
        
        DataSet dataset = dataLoader.getDataset(selectedPollutant, selectedYear);
        if (dataset != null) {
            List<DataPoint> cityDataList = cityDataFilter.filterCityArea(dataset.getData(), selectedCity);
            
            if (cityDataList.isEmpty()) {
                resultLabel.setText("No data found for the selected pollutant and year in " + selectedCity);
                return;
            }
            
            mapComponent.visualizeLowPollutionAreas(cityDataList);
            resultLabel.setText("Showing low pollution areas for " + selectedPollutant + " in " + selectedCity + " (" + selectedYear + ")");
        } else {
            resultLabel.setText("No dataset found for the selected pollutant and year.");
        }
    }
    
    /**
     * Update visualization based on current selections
     */
    private void updateVisualization() {
        mapComponent.clearMapData();
        
        if (selectedYear != null && selectedPollutant != null) {
            DataSet dataset = dataLoader.getDataset(selectedPollutant, selectedYear);
            
            if (dataset != null) {
                List<DataPoint> cityDataList = cityDataFilter.filterCityArea(dataset.getData(), selectedCity);
                
                if (!cityDataList.isEmpty()) {
                    mapComponent.visualizePollutionData(cityDataList);
                    // Update map component with current data for double-click functionality
                    mapComponent.setSelectedData(selectedPollutant, selectedYear, cityDataList);
                    statisticsComponent.updateStatistics(cityDataList, dataset);
                    resultLabel.setText("Showing " + selectedPollutant + " data for " + selectedCity + " in " + selectedYear);
                } else {
                    resultLabel.setText("No data found within " + selectedCity + " area.");
                }
            } else {
                resultLabel.setText("No dataset found for " + selectedPollutant + " in " + selectedYear);
            }
        }
    }
}