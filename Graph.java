import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import java.util.*;

/**
 * A JavaFX class for visualizing pollution data in different chart formats.
 * 
 * @author Ved Patel and Nitin Anatharaju
 * @version 2.0
 */
public class Graph {
    private DataLoader dataLoader;
    private CityDataFilter cityDataFilter;
    private String selectedCity;

    /**
     * Default constructor for Graph.
     */
    public Graph() {
        // Empty constructor
    }

    /**
     * Constructor with dependencies for the Graph class.
     * 
     * @param dataLoader The data loader to retrieve pollution datasets
     * @param cityDataFilter The filter to retrieve data for specific cities
     */
    public Graph(DataLoader dataLoader, CityDataFilter cityDataFilter) {
        this.dataLoader = dataLoader;
        this.cityDataFilter = cityDataFilter;
        this.selectedCity = "London"; // Default city
    }

    /**
     * Sets the data loader for retrieving pollution data.
     * 
     * @param dataLoader The data loader to use
     */
    public void setDataLoader(DataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    /**
     * Sets the city data filter for filtering pollution data by city.
     * 
     * @param cityDataFilter The city data filter to use
     */
    public void setCityDataFilter(CityDataFilter cityDataFilter) {
        this.cityDataFilter = cityDataFilter;
    }
    
    /**
     * Sets the currently selected city for chart generation.
     * 
     * @param city The name of the city to select
     */
    public void setSelectedCity(String city) {
        this.selectedCity = city;
    }

    /**
     * Creates a line chart showing pollution trends over multiple years.
     * 
     * @param pollutant The pollutant to show in the chart
     * @param years List of years to include in the chart
     * @return A LineChart showing pollution levels over time
     */
    public LineChart<String, Number> lineGraph(String pollutant, List<String> years) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Year");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Pollution Level (" + pollutant + ")");

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle(pollutant + " pollution levels over time in " + selectedCity);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(selectedCity + " Pollution");

        // Load and process data for each year
        for (String year : years) {
            DataSet dataset = dataLoader.getDataset(pollutant, year);
            if (dataset != null) {
                List<DataPoint> cityList = cityDataFilter.filterCityArea(dataset.getData(), selectedCity);
                if (!cityList.isEmpty()) {
                    double avgValue = calculateAverage(cityList);
                    series.getData().add(new XYChart.Data<>(year, avgValue));
                }
            }
        }

        lineChart.getData().add(series);
        lineChart.setPrefSize(400, 300);

        return lineChart;
    }

    /**
     * Calculates the average pollution value from a list of data points.
     * 
     * @param dataPoints List of data points to average
     * @return The average pollution value, or 0 if the list is empty
     */
    private double calculateAverage(List<DataPoint> dataPoints) {
        if (dataPoints.isEmpty()) {
            return 0;
        }
        
        double sum = 0;
        for (DataPoint point : dataPoints) {
            sum += point.value();
        }

        return sum / dataPoints.size();
    }

    /**
     * Creates a bar chart showing average pollution levels for different pollutants.
     * 
     * @param pollutants List of pollutants to include in the chart
     * @param year The year for which to show pollution data
     * @return A BarChart showing average pollution levels for each pollutant
     */
    public BarChart<String, Number> createAveragePollution(List<String> pollutants, String year) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Pollutant Type");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Average Pollution Level");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Average Pollution Levels in " + selectedCity + " (" + year + ")");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(year + " Average");

        // Add data for each pollutant if the year is selected
        if (year != null) {
            for (String pollutant : pollutants) {
                DataSet dataset = dataLoader.getDataset(pollutant, year);
                if (dataset != null) {
                    List<DataPoint> filteredData = cityDataFilter.filterCityArea(dataset.getData(), selectedCity);
                    if (!filteredData.isEmpty()) {
                        double avgValue = calculateAverage(filteredData);
                        series.getData().add(new XYChart.Data<>(pollutant, avgValue));
                    }
                }
            }
            barChart.getData().add(series);
        }

        return barChart;
    }

    /**
     * Creates a pie chart showing the distribution of different pollutants.
     * 
     * @param years List of years to include in the average calculation
     * @return A PieChart showing the distribution of pollutants
     */
    public PieChart pieChart(List<String> years) {
        double totalNo2 = 0, totalPm10 = 0, totalPm25 = 0;
        int yearCount = years.size();

        for (String year : years) {
            totalNo2 += getAveragePollutantValue("NO2", year);
            totalPm10 += getAveragePollutantValue("PM10", year);
            totalPm25 += getAveragePollutantValue("PM2.5", year);
        }

        // Calculate the average over all selected years
        double avgNo2 = totalNo2 / yearCount;
        double avgPm10 = totalPm10 / yearCount;
        double avgPm25 = totalPm25 / yearCount;

        PieChart.Data slice1 = new PieChart.Data("NO2", avgNo2);
        PieChart.Data slice2 = new PieChart.Data("PM10", avgPm10);
        PieChart.Data slice3 = new PieChart.Data("PM2.5", avgPm25);

        PieChart pieChart = new PieChart();
        pieChart.getData().addAll(slice1, slice2, slice3);
        pieChart.setTitle("Average Pollutant Distribution in " + selectedCity + " (" + String.join(", ", years) + ")");

        return pieChart;
    }

    /**
     * Gets the average value for a specific pollutant in a given year.
     * 
     * @param pollutant The pollutant type to analyze
     * @param year The year to analyze
     * @return The average pollution value, or 0 if no data is available
     */
    private double getAveragePollutantValue(String pollutant, String year) {
        DataSet dataset = dataLoader.getDataset(pollutant, year);
        if (dataset != null) {
            List<DataPoint> cityList = cityDataFilter.filterCityArea(dataset.getData(), selectedCity);
            if (!cityList.isEmpty()) {
                return calculateAverage(cityList);
            }
        }
        return 0; // Default if no data available
    }
    
    /**
     * Creates a comparison line chart for multiple cities.
     * 
     * @param pollutant The pollutant to compare across cities
     * @param year The year for which to compare pollution data
     * @param cities List of cities to include in the comparison
     * @return A LineChart comparing pollution levels across different cities
     */
    public LineChart<String, Number> createCityComparison(String pollutant, String year, List<String> cities) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("City");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Pollution Level (" + pollutant + ")");

        LineChart<String, Number> comparisonChart = new LineChart<>(xAxis, yAxis);
        comparisonChart.setTitle(pollutant + " Pollution Comparison (" + year + ")");
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(year + " " + pollutant + " Levels");
        
        for (String city : cities) {
            DataSet dataset = dataLoader.getDataset(pollutant, year);
            if (dataset != null) {
                List<DataPoint> cityData = cityDataFilter.filterCityArea(dataset.getData(), city);
                if (!cityData.isEmpty()) {
                    double avgValue = calculateAverage(cityData);
                    series.getData().add(new XYChart.Data<>(city, avgValue));
                }
            }
        }
        
        comparisonChart.getData().add(series);
        return comparisonChart;
    }
}