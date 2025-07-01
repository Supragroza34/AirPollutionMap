import java.util.*;

/**
 * Class for comparing pollution statistics between different years.
 * 
 * @author Ved Patel
 * @version 2.0
 */
public class CompareStatistics {
    private DataLoader dataLoader;
    private CityDataFilter cityDataFilter;
    private Graph graph;

    /**
     * Constructor for CompareStatistics which initialises all components for caculating statistics.
     * 
     * @param dataLoader The data loader object
     * @param cityDataFilter The city data filter object
     */
    public CompareStatistics(DataLoader dataLoader, CityDataFilter cityDataFilter) {
        this.dataLoader = dataLoader;
        this.cityDataFilter = cityDataFilter;
        this.graph = new Graph();
    }

    /**
     * Compare average pollution values between two years
     * 
     * @param pollutant The pollutant to compare
     * @param year1 First year for comparison
     * @param year2 Second year for comparison
     * @param cityName Name of the city to compare data for
     * @return A formatted string with comparison results
     */
    public String compareAveragePollution(String pollutant, String year1, String year2, String cityName) {
        // Load datasets for both years
        DataSet dataset1 = dataLoader.getDataset(pollutant, year1);
        DataSet dataset2 = dataLoader.getDataset(pollutant, year2);
        
        if (dataset1 == null || dataset2 == null) {
            return "Missing data for one or both selected years.";
        }
        
        // Filter data for the city
        List<DataPoint> cityData1 = cityDataFilter.filterCityArea(dataset1.getData(), cityName);
        List<DataPoint> cityData2 = cityDataFilter.filterCityArea(dataset2.getData(), cityName);
        
        if (cityData1.isEmpty() || cityData2.isEmpty()) {
            return "No data available for " + cityName + " in one or both selected years.";
        }
        
        // Calculate averages
        double avg1 = calculateAverage(cityData1);
        double avg2 = calculateAverage(cityData2);
        
        // Calculate percentage difference (from year2 to year1)
        double percentageDifference = ((avg1 - avg2) / avg2) * 100;
        
        // Format the result
        StringBuilder result = new StringBuilder();
        result.append(String.format("COMPARISON OF %s LEVELS IN %s\n\n", pollutant, cityName.toUpperCase()));
        result.append(String.format("Average %s in %s: %.2f %s\n", pollutant, year1, avg1, dataset1.getUnits()));
        result.append(String.format("Average %s in %s: %.2f %s\n\n", pollutant, year2, avg2, dataset2.getUnits()));
        
        if (percentageDifference > 0) {
            result.append(String.format("Increase from %s to %s: %.2f%% higher\n", year2, year1, percentageDifference));
        } else if (percentageDifference < 0) {
            result.append(String.format("Decrease from %s to %s: %.2f%% lower\n", year2, year1, Math.abs(percentageDifference)));
        } else {
            result.append(String.format("No change in average %s levels between %s and %s\n", pollutant, year1, year2));
        }
        
        return result.toString();
    }
    
    /**
     * Compare maximum pollution points between two years
     * 
     * @param pollutant The pollutant to compare
     * @param year1 The first year for comparison
     * @param year2 The second year for comparison
     * @param cityName The name of the city to compare data for
     * @return A formatted string with comparison results
     */
    public String compareMaxPollution(String pollutant, String year1, String year2, String cityName) {
        // Load datasets for both years
        DataSet dataset1 = dataLoader.getDataset(pollutant, year1);
        DataSet dataset2 = dataLoader.getDataset(pollutant, year2);
        
        if (dataset1 == null || dataset2 == null) {
            return "Missing data for one or both selected years.";
        }
        
        // Filter data for the city
        List<DataPoint> cityData1 = cityDataFilter.filterCityArea(dataset1.getData(), cityName);
        List<DataPoint> cityData2 = cityDataFilter.filterCityArea(dataset2.getData(), cityName);
        
        if (cityData1.isEmpty() || cityData2.isEmpty()) {
            return "No data available for " + cityName + " in one or both selected years.";
        }
        
        // Find max points
        DataPoint maxPoint1 = findMaxPoint(cityData1);
        DataPoint maxPoint2 = findMaxPoint(cityData2);
        
        // Calculate percentage difference (from year2 to year1)
        double percentageDifference = ((maxPoint1.value() - maxPoint2.value()) / maxPoint2.value()) * 100;
        
        // Format the result
        StringBuilder result = new StringBuilder();
        result.append(String.format("COMPARISON OF MAXIMUM %s LEVELS IN %s\n\n", pollutant, cityName.toUpperCase()));
        result.append(String.format("%s - Maximum Point\n", year1));
        result.append(String.format("  Grid Code: %d\n", maxPoint1.gridCode()));
        result.append(String.format("  Location: Easting = %.2f, Northing = %.2f\n", (double)maxPoint1.x(), (double)maxPoint1.y()));
        result.append(String.format("  Value: %.2f %s\n\n", (double)maxPoint1.value(), dataset1.getUnits()));
        
        result.append(String.format("%s - Maximum Point\n", year2));
        result.append(String.format("  Grid Code: %d\n", maxPoint2.gridCode()));
        result.append(String.format("  Location: Easting = %.2f, Northing = %.2f\n", (double)maxPoint2.x(), (double)maxPoint2.y()));
        result.append(String.format("  Value: %.2f %s\n\n", (double)maxPoint2.value(), dataset2.getUnits()));
        
        if (percentageDifference > 0) {
            result.append(String.format("The maximum %s level in %s was %.2f%% higher than in %s\n", 
                pollutant, year1, percentageDifference, year2));
        } else if (percentageDifference < 0) {
            result.append(String.format("The maximum %s level in %s was %.2f%% lower than in %s\n", 
                pollutant, year1, Math.abs(percentageDifference), year2));
        } else {
            result.append(String.format("No change in maximum %s levels between %s and %s\n", 
                pollutant, year1, year2));
        }
        
        return result.toString();
    }
    
    /**
     * Calculate the average value from a list of data points.
     * 
     * @param dataPoints The list of data points
     * @return The average value
     */
    public double calculateAverage(List<DataPoint> dataPoints) {
        if (dataPoints.isEmpty()) {
            return 0.0;
        }
        
        double sum = 0.0;
        for (DataPoint point : dataPoints) {
            sum += point.value();
        }
        
        return sum / dataPoints.size();
    }
    
    /**
     * Find the data point with the maximum value.
     * 
     * @param dataPoints The list of data points
     * @return The data point with the maximum value
     */
    private DataPoint findMaxPoint(List<DataPoint> dataPoints) {
        if (dataPoints.isEmpty()) {
            return null;
        }
        
        DataPoint maxPoint = dataPoints.get(0);
        for (DataPoint point : dataPoints) {
            if (point.value() > maxPoint.value()) {
                maxPoint = point;
            }
        }
        
        return maxPoint;
    }
}