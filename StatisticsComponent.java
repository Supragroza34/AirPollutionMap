import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.Node;
import java.util.*;

/**
 * Component that handles statistics visualization and generation of charts.
 * 
 * @author Everyone
 * @version 2.0
 */
public class StatisticsComponent {
    private VBox statsContent;
    private TextArea statisticsArea;
    
    private final DataLoader dataLoader;
    private final CityDataFilter cityDataFilter;
    private final GraphAdapter graph;
    private final StatisticsCalculator statisticsCalculator;
    private final CompareStatistics compareStatistics;
    
    private String selectedCity;
    private String selectedPollutant;
    private String selectedYear;
    private List<String> years = Arrays.asList("2018", "2019", "2020", "2021", "2022", "2023");
    private List<String> pollutants = Arrays.asList("NO2", "PM10", "PM2.5");
    
    /**
     * Constructor for Statistics Component
     * 
     * @param dataLoader The data loader to retrieve pollution datasets.
     * @param cityDataFilter The filter to retrieve data for specific cities.
     * @param graph The graph adapter to create visualizations.
     * @param initialCity The initially selected city name.
     */
    public StatisticsComponent(DataLoader dataLoader, CityDataFilter cityDataFilter, 
                               GraphAdapter graph, String initialCity) {
        this.dataLoader = dataLoader;
        this.cityDataFilter = cityDataFilter;
        this.graph = graph;
        this.selectedCity = initialCity;
        this.statisticsCalculator = new StatisticsCalculator();
        this.compareStatistics = new CompareStatistics(dataLoader, cityDataFilter);
        
        initializeComponent();
    }
    
    /**
     * Initialize the statistics component
     */
    private void initializeComponent() {
        // Create statistics area
        statisticsArea = new TextArea();
        statisticsArea.setEditable(false);
        
        // Create content container
        statsContent = new VBox(10);
        statsContent.setPadding(new Insets(10));
        
        // Set up the statistics selector
        ComboBox<String> statsSelector = new ComboBox<>();
        statsSelector.getItems().addAll("Average Pollution", "Max Pollution Areas", 
                                        "Pollution Trends", "City Comparison");
        statsSelector.setPromptText("Select Statistic");
        
        // Create refresh button
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> refreshStatistics(statsSelector.getValue()));
        
        // Header with dropdown and refresh button
        HBox headerBox = new HBox(10, statsSelector, refreshButton);
        
        // Add header and empty statistics area to content
        statsContent.getChildren().addAll(headerBox, statisticsArea);
        
        // Set up action for statistics selection
        statsSelector.setOnAction(e -> {
            String selected = statsSelector.getValue();
            if (selected != null) {
                updateStatisticsView(selected);
            }
        });
    }
    
    /**
     * Updates the view based on the selected statistics type.
     *      
     * @param statisticsType The type of statistics view to display
     */
    private void updateStatisticsView(String statisticsType) {
        Node header = statsContent.getChildren().get(0);
        statsContent.getChildren().clear();
        statsContent.getChildren().add(header);
        
        switch (statisticsType) {
            case "Average Pollution":
                setupAveragePollutionView();
                break;
            case "Max Pollution Areas":
                setupMaxPollutionView();
                break;
            case "Pollution Trends":
                setupPollutionTrendsView();
                break;
            case "City Comparison":
                setupCityComparisonView();
                break;
            default:
                statsContent.getChildren().add(statisticsArea);
        }
    }
    
    /**
     * Set up the average pollution view with comparision option.
     */
    private void setupAveragePollutionView() {
        // Get the header
        Node header = statsContent.getChildren().get(0);
        
        Label infoLabel = new Label("Select a pollutant and year to view average pollution data");
        
        if (selectedPollutant != null && selectedYear != null) {
            // Create comparison controls
            HBox comparisonBox = new HBox(10);
            Label compareLabel = new Label("Compare with year:");
            ComboBox<String> yearComboBox = new ComboBox<>();
            
            // Add all years except the currently selected one
            for (String year : years) {
                if (!year.equals(selectedYear)) {
                    yearComboBox.getItems().add(year);
                }
            }
            
            Button compareButton = new Button("Compare");
            comparisonBox.getChildren().addAll(compareLabel, yearComboBox, compareButton);
            comparisonBox.setAlignment(Pos.CENTER_LEFT);
            
            // Set up comparison action
            compareButton.setOnAction(e -> {
                String compareYear = yearComboBox.getValue();
                if (compareYear != null) {
                    String result = compareStatistics.compareAveragePollution(
                            selectedPollutant, selectedYear, 
                            compareYear, selectedCity);
                    
                    TextArea resultArea = new TextArea(result);
                    resultArea.setEditable(false);
                    resultArea.setPrefHeight(200);
                    
                    statsContent.getChildren().clear();
                    statsContent.getChildren().addAll(header, comparisonBox, resultArea);
                }
            });
            
            PieChart pieChart = graph.pieChart(years);
            pieChart.setPrefSize(600, 400);
            
            TextArea chartDescription = new TextArea();
            chartDescription.setEditable(false);
            chartDescription.setPrefHeight(100);
            chartDescription.setText("This chart represents the distribution of pollutants in " + 
                    selectedCity + " for " + selectedYear + ".\n\n" + 
                    "You can compare with another year using the options above.");
            
            statsContent.getChildren().addAll(comparisonBox, pieChart, chartDescription);
        } else {
            statsContent.getChildren().add(infoLabel);
        }
    }
    
    /**
     * Set up the max pollution view showing top 5 most polluted points
     * with a compare option.
     */
    private void setupMaxPollutionView() {
        Node header = statsContent.getChildren().get(0);
        
        if (selectedPollutant != null && selectedYear != null) {
            DataSet dataset = dataLoader.getDataset(selectedPollutant, selectedYear);
            if (dataset != null) {
                List<DataPoint> cityData = cityDataFilter.filterCityArea(dataset.getData(), selectedCity);
                
                if (!cityData.isEmpty()) {
                    // Create comparison controls
                    HBox comparisonBox = new HBox(10);
                    Label compareLabel = new Label("Compare with year:");
                    ComboBox<String> yearComboBox = new ComboBox<>();
                    
                    // Add all years except the currently selected one
                    for (String year : years) {
                        if (!year.equals(selectedYear)) {
                            yearComboBox.getItems().add(year);
                        }
                    }
                    
                    Button compareButton = new Button("Compare");
                    comparisonBox.getChildren().addAll(compareLabel, yearComboBox, compareButton);
                    comparisonBox.setAlignment(Pos.CENTER_LEFT);
                    
                    // Set up comparison action
                    compareButton.setOnAction(e -> {
                        String compareYear = yearComboBox.getValue();
                        if (compareYear != null) {
                            String result = compareStatistics.compareMaxPollution(
                                selectedPollutant, selectedYear, 
                                compareYear, selectedCity);
                            
                            TextArea resultArea = new TextArea(result);
                            resultArea.setEditable(false);
                            resultArea.setPrefHeight(300);
                            
                            statsContent.getChildren().clear();
                            statsContent.getChildren().addAll(header, comparisonBox, resultArea);
                        }
                    });
                    
                    // Get top 5 pollution points
                    List<DataPoint> topPoints = getTopPollutionPoints(cityData, 5);
                    
                    TextArea topPointsArea = new TextArea();
                    topPointsArea.setEditable(false);
                    topPointsArea.setPrefHeight(250);
                    
                    StringBuilder content = new StringBuilder();
                    content.append("TOP 5 MOST POLLUTED POINTS FOR ")
                        .append(selectedPollutant)
                        .append(" IN ")
                        .append(selectedCity)
                        .append(" (")
                        .append(selectedYear)
                        .append(")\n\n");
                    
                    for (int i = 0; i < topPoints.size(); i++) {
                        DataPoint point = topPoints.get(i);
                        content.append(i + 1)
                            .append(". Grid Code: ")
                            .append(point.gridCode())
                            .append("\n   Location: Easting = ")
                            .append(String.format("%.2f", (double)point.x()))
                            .append(", Northing = ")
                            .append(String.format("%.2f", (double)point.y()))
                            .append("\n   Pollution Level: ")
                            .append(String.format("%.2f", (double)point.value()))
                            .append(" ")
                            .append(dataset.getUnits())
                            .append("\n\n");
                    }
                    
                    topPointsArea.setText(content.toString());
                    
                    Label descriptionLabel = new Label("Below are the locations with the highest levels of " + 
                        selectedPollutant + " in " + selectedCity + " for " + selectedYear + 
                        ". You can compare with another year using the options above.");
                    descriptionLabel.setWrapText(true);
                    descriptionLabel.setPadding(new Insets(0, 0, 10, 0));
                    
                    statsContent.getChildren().addAll(comparisonBox, descriptionLabel, topPointsArea);
                } else {
                    Label noDataLabel = new Label("No data available for " + selectedCity + 
                        " with pollutant " + selectedPollutant + " in " + selectedYear);
                    statsContent.getChildren().add(noDataLabel);
                }
            } else {
                Label noDatasetLabel = new Label("No dataset available for " + 
                    selectedPollutant + " in " + selectedYear);
                statsContent.getChildren().add(noDatasetLabel);
            }
        } else {
            Label selectionLabel = new Label("Please select a pollutant and year to view high pollution areas.");
            statsContent.getChildren().add(selectionLabel);
        }
    }
    
    /**
     * Set up the pollution trends view showing average pollution data in differnt graph.
     */
    private void setupPollutionTrendsView() {
        // Create label for selection instructions
        Label yearSelectLabel = new Label("Please select a year from the 'Year' menu to view average pollution data.");
        
        if (selectedYear != null) {
            // Create and add bar chart
            BarChart<String, Number> barChart = graph.createAveragePollution(pollutants, selectedYear);
            barChart.setPrefSize(600, 400);
            
            TextArea description = new TextArea();
            description.setEditable(false);
            description.setPrefHeight(100);
            description.setText("This chart shows the average pollution levels for different pollutants in " + 
                selectedCity + " for " + selectedYear + ". The values represent the average concentration " +
                "measured across all monitoring points in " + selectedCity + ".");
            
            statsContent.getChildren().addAll(barChart, description);
        } else {
            statsContent.getChildren().add(yearSelectLabel);
        }
    }
    
    /**
     * Set up the city comparison view with show polltion data across different cities.
     */
    private void setupCityComparisonView() {
        if (selectedPollutant != null && selectedYear != null) {
            // Create and add city comparison chart
            LineChart<String, Number> comparisonChart = graph.createCityComparison(
                selectedPollutant, selectedYear, cityDataFilter.getAvailableCities());
            comparisonChart.setPrefSize(600, 400);
            
            TextArea description = new TextArea();
            description.setEditable(false);
            description.setPrefHeight(100);
            description.setText("This chart compares the average " + selectedPollutant + 
                " levels across different cities for " + selectedYear + ". The values represent " +
                "the average concentration measured across all monitoring points in each city.");
            
            statsContent.getChildren().addAll(comparisonChart, description);
        } else {
            Label selectionLabel = new Label("Please select a pollutant and year for city comparison.");
            statsContent.getChildren().add(selectionLabel);
        }
    }
    
    /**
     * Get the top N pollution points from a list of data points.
     * 
     * @param dataPoints The list of data points to evaluate
     * @param count The number of top points to return
     * @return A list containing the top N data points with highest pollution value
     */
    private List<DataPoint> getTopPollutionPoints(List<DataPoint> dataPoints, int count) {
        // Sort the data points by pollution value in descending order
        List<DataPoint> sortedPoints = new ArrayList<>(dataPoints);
        sortedPoints.sort((p1, p2) -> Double.compare(p2.value(), p1.value()));
        
        // Return the top N points (or fewer if there aren't enough points)
        return sortedPoints.subList(0, Math.min(count, sortedPoints.size()));
    }
    
    /**
     * Refresh the statistics view with current selection
     * 
     * @param statisticsType The type of statistics view to refresh
     */
    private void refreshStatistics(String statisticsType) {
        if (statisticsType != null) {
            updateStatisticsView(statisticsType);
        }
    }
    
    /**
     * Update statistics display with the given dataset.
     * 
     * @param cityDataList The list of data points for the selected city
     * @param dataset The dataset containing metadata about the pollutant
     */
    public void updateStatistics(List<DataPoint> cityDataList, DataSet dataset) {
        if (cityDataList != null && !cityDataList.isEmpty() && dataset != null) {
            String statsText = statisticsCalculator.calculateStatistics(
                cityDataList, dataset.getPollutant(), dataset.getYear(), dataset.getUnits());
            
            statisticsArea.setText(statsText);
        }
    }
    
    /**
     * Update the city for the statistics component.
     * 
     * @param cityName The name of the city to set as selected
     */
    public void updateCity(String cityName) {
        this.selectedCity = cityName;
        graph.setSelectedCity(cityName);
    }
    
    /**
     * Set the selected pollutant.
     * 
     * @param pollutant The pollutant to set as selected
     */
    public void setSelectedPollutant(String pollutant) {
        this.selectedPollutant = pollutant;
    }
    
    /**
     * Set the selected year.
     * 
     * @param year The year to set as selected
     */
    public void setSelectedYear(String year) {
        this.selectedYear = year;
    }
    
    /**
     * Get the statistics content pane.
     * 
     * @return The VBox containing all the statistics UI components
     */
    public VBox getStatsContent() {
        return statsContent;
    }
}