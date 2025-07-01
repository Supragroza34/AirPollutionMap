import java.util.*;

/**
 * This class calculates statistics about pollution data.
 * 
 * @author Ved Patel
 * @version 2.0
 */
public class StatisticsCalculator {
    
    /**
     * Calculate statistics for a list of data points
     * 
     * @param dataPoints The list of data points
     * @param pollutant The name of the pollutant
     * @param year The year of the data
     * @param units The units of measurement
     * @return A formatted string with statistics information
     */
    public String calculateStatistics(List<DataPoint> dataPoints, String pollutant, String year, String units) {
        if (dataPoints == null || dataPoints.isEmpty()) {
            return "No data available for statistics calculation.";
        }
        
        // Calculate statistics
        double sum = 0;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        DataPoint minPoint = null;
        DataPoint maxPoint = null;

        for (DataPoint point : dataPoints) {
            double value = point.value();
            sum += value;

            if (value < min) {
                min = value;
                minPoint = point;
            }
            if (value > max) {
                max = value;
                maxPoint = point;
            }
        }

        double avg = sum / dataPoints.size();

        // Format the statistics as a string
        StringBuilder sb = new StringBuilder();
        sb.append("Statistics for ").append(pollutant).append(" in ").append(year).append("\n\n");
        sb.append("Total data points: ").append(dataPoints.size()).append("\n");
        sb.append("Average value: ").append(String.format("%.2f", avg)).append(" ").append(units).append("\n");
        sb.append("Minimum value: ").append(String.format("%.2f", min)).append(" ").append(units);

        if (minPoint != null) {
            sb.append(" (at Easting: ").append(minPoint.x()).append(", Northing: ").append(minPoint.y()).append(")\n");
        } else {
            sb.append("\n");
        }

        sb.append("Maximum value: ").append(String.format("%.2f", max)).append(" ").append(units);

        if (maxPoint != null) {
            sb.append(" (at Easting: ").append(maxPoint.x()).append(", Northing: ").append(maxPoint.y()).append(")\n");
        } else {
            sb.append("\n");
        }
        
        return sb.toString();
    }
}