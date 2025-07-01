import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import java.util.*;

/**
 * This class handles visualization of data points on the map
 * Using the CityVisualizationHelper to get city-specific parameters
 * 
 * @author Nitin Anantharaju
 * @version 2.0
 */
public class MapVisualizer {
    private final Pane mapPane;
    private final CoordinateConverter converter;
    private final String currentCity;
    private final CityVisualizationHelper.VisualizationParams vizParams;
    
    /**
     * Constructor for the MapVisualizer
     * 
     * @param mapPane The JavaFX pane that displays the map
     * @param converter The coordinate converter for translating between real-world and pixel coordinates
     * @param cityName The name of the current city being visualized
     */
    public MapVisualizer(Pane mapPane, CoordinateConverter converter, String cityName) {
        this.mapPane = mapPane;
        this.converter = converter;
        this.currentCity = cityName;
        this.vizParams = CityVisualizationHelper.getParamsForCity(cityName);
    }
    
    /**
     * Clear all data visualizations from the map
     */
    public void clearMapData() {
        // Remove all rectangles and circles from the mapPane
        mapPane.getChildren().removeIf(node -> node instanceof Rectangle || node instanceof Circle);
    }
    
    /**
     * Plot a point at the given real-world coordinates.
     * 
     * @param easting The real-world easting coordinate
     * @param northing The real-world northing coordinate
     * @return True if the point was successfully plotted, false otherwise
     */
    public boolean plotPoint(double easting, double northing) {
        // Convert to pixel coordinates
        double pixelX = converter.convertToPixelX(easting);
        double pixelY = converter.convertToPixelY(northing);
        
        // Ensure the point is within bounds
        if (converter.isWithinBounds(pixelX, pixelY)) {
            // Plot the point on the map
            Circle point = new Circle(pixelX, pixelY, 5, Color.RED);
            mapPane.getChildren().add(point);
            return true;
        }
        return false;
    }
    
    /**
     * Visualize the pollution data on the map using colored rectangles
     * with city-specific parameters from CityVisualizationHelper.
     * 
     * @param dataPoints The list of data points to visualize
     */
    public void visualizePollutionData(List<DataPoint> dataPoints) {
        if (dataPoints == null || dataPoints.isEmpty()) {
            return;
        }
        
        double min = Collections.min(dataPoints.stream().map(DataPoint::value).toList());
        double max = Collections.max(dataPoints.stream().map(DataPoint::value).toList());
        double difference = max - min;
        
        double squareWidth = vizParams.getSquareWidth();
        double squareHeight = vizParams.getSquareHeight();
        double scaleAdjustment = vizParams.getScaleAdjustment();
        
        // Normalizing values and displaying rectangles
        for (DataPoint point : dataPoints) {
            double normValue = (point.value() - min) / difference;
            float r = (float) normValue;
            float g = (float) (1.0d - normValue);
            float b = 0.0f;
            
            // Apply city-specific adjustments
            Rectangle rectangle = new Rectangle(
                squareWidth * scaleAdjustment, 
                squareHeight * scaleAdjustment, 
                new Color(r, g, b, 0.275)
            );
            
            rectangle.setX(converter.convertToPixelX(point.x()) - (squareWidth * scaleAdjustment) / 2);
            rectangle.setY(converter.convertToPixelY(point.y()) - (squareHeight * scaleAdjustment) / 2);
            mapPane.getChildren().add(rectangle);
        }
    }
    
    /**
     * Visualize high pollution areas (areas with normalized value >= threshold)
     * with city-specific parameters from CityVisualizationHelper.
     * 
     * @param dataPoints The list of data points to filter and visualize
     */
    public void visualizeHighPollutionAreas(List<DataPoint> dataPoints) {
        if (dataPoints == null || dataPoints.isEmpty()) {
            return;
        }
        
        double min = Collections.min(dataPoints.stream().map(DataPoint::value).toList());
        double max = Collections.max(dataPoints.stream().map(DataPoint::value).toList());
        double difference = max - min;
        
        double squareWidth = vizParams.getSquareWidth();
        double squareHeight = vizParams.getSquareHeight();
        double scaleAdjustment = vizParams.getScaleAdjustment();
        double threshold = vizParams.getHighPollutionThreshold();
        
        // Normalizing values and displaying rectangles for high pollution areas
        for (DataPoint point : dataPoints) {
            double normValue = (point.value() - min) / difference;
            
            if (normValue >= threshold) {
                Rectangle rectangle = new Rectangle(
                    squareWidth * scaleAdjustment, 
                    squareHeight * scaleAdjustment, 
                    new Color(1, 0, 0, 0.275)
                );
                
                rectangle.setX(converter.convertToPixelX(point.x()) - (squareWidth * scaleAdjustment) / 2);
                rectangle.setY(converter.convertToPixelY(point.y()) - (squareHeight * scaleAdjustment) / 2);
                mapPane.getChildren().add(rectangle);
            }
        }
    }
    
    /**
     * Visualize low pollution areas (areas with normalized value <= threshold)
     * with city-specific parameters from CityVisualizationHelper.
     * 
     * @param dataPoints The list of data points to filter and visualize
     */
    public void visualizeLowPollutionAreas(List<DataPoint> dataPoints) {
        if (dataPoints == null || dataPoints.isEmpty()) {
            return;
        }
        
        double min = Collections.min(dataPoints.stream().map(DataPoint::value).toList());
        double max = Collections.max(dataPoints.stream().map(DataPoint::value).toList());
        double difference = max - min;
        
        double squareWidth = vizParams.getSquareWidth();
        double squareHeight = vizParams.getSquareHeight();
        double scaleAdjustment = vizParams.getScaleAdjustment();
        double threshold = vizParams.getLowPollutionThreshold();
        
        // Normalizing values and displaying rectangles for low pollution areas
        for (DataPoint point : dataPoints) {
            double normValue = (point.value() - min) / difference;
            
            if (normValue <= threshold) {
                Rectangle rectangle = new Rectangle(
                    squareWidth * scaleAdjustment, 
                    squareHeight * scaleAdjustment, 
                    new Color(0, 1, 0, 0.275)
                );
                
                rectangle.setX(converter.convertToPixelX(point.x()) - (squareWidth * scaleAdjustment) / 2);
                rectangle.setY(converter.convertToPixelY(point.y()) - (squareHeight * scaleAdjustment) / 2);
                mapPane.getChildren().add(rectangle);
            }
        }
    }
    
    /**
     * Get the maximum distance for finding the nearest data point
     * 
     * @return The maximum distance value
     */
    public double getMaxNearestPointDistance() {
        return vizParams.getMaxNearestPointDistance();
    }
}