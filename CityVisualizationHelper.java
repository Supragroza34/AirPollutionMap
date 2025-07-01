import java.util.*;

/**
 * Helper class that contains visualization parameters for each city
 * This allows for fine-tuning the visualization for each city's specific needs
 * 
 * @author Amey Tripathi
 * @version 2.0
 */
public class CityVisualizationHelper {
    // Define visualization parameters for each city
    private static final Map<String, VisualizationParams> cityParams = new HashMap<>();
    
    // Initialize parameters for all supported cities
    static {
        // London parameters
        cityParams.put("London", new VisualizationParams(
            28, 37,      // Square width and height from your code
            1.0,         // Scale adjustment
            0.60,        // High pollution threshold
            0.30,        // Low pollution threshold
            2000         // Maximum distance for finding nearest point
        ));
        
        // Manchester parameters (using your provided sizes)
        cityParams.put("Manchester", new VisualizationParams(
            56, 66,      // Square width and height from your code
            1.0,         // Scale adjustment
            0.65,        // High pollution threshold 
            0.25,        // Low pollution threshold 
            2500         // Maximum distance for finding nearest point 
        ));
        
        // Newcastle parameters (using your provided sizes)
        cityParams.put("Newcastle", new VisualizationParams(
            49, 67,      // Square width and height from your code
            1.0,         // Scale adjustment
            0.55,        // High pollution threshold (less strict)
            0.35,        // Low pollution threshold (less generous) 
            2200         // Maximum distance for finding nearest point
        ));
    }
    
    /**
     * Get visualization parameters for a specific city
     * 
     * @param cityName The name of the city
     * @return The visualization parameters or default parameters if city not found
     */
    public static VisualizationParams getParamsForCity(String cityName) {
        return cityParams.getOrDefault(cityName, getDefaultParams());
    }
    
    /**
     * Get default visualization parameters (London's parameters)
     * 
     * @return Default visualization parameters
     */
    public static VisualizationParams getDefaultParams() {
        return new VisualizationParams(28, 37, 1.0, 0.60, 0.30, 2000);
    }
    
    /**
     * Inner class to hold visualization parameters for a city.
     */
    public static class VisualizationParams {
        private final double squareWidth;
        private final double squareHeight;
        private final double scaleAdjustment;
        private final double highPollutionThreshold;
        private final double lowPollutionThreshold;
        private final double maxNearestPointDistance;
        
        /**
         * Constructor for VisualizationParams
         */
        public VisualizationParams(
            double squareWidth, 
            double squareHeight, 
            double scaleAdjustment,
            double highPollutionThreshold,
            double lowPollutionThreshold,
            double maxNearestPointDistance
        ) {
            this.squareWidth = squareWidth;
            this.squareHeight = squareHeight;
            this.scaleAdjustment = scaleAdjustment;
            this.highPollutionThreshold = highPollutionThreshold;
            this.lowPollutionThreshold = lowPollutionThreshold;
            this.maxNearestPointDistance = maxNearestPointDistance;
        }
        
        public double getSquareWidth() { 
            return squareWidth; 
        }
        
        public double getSquareHeight() { 
            return squareHeight; 
        }
        
        public double getScaleAdjustment() { 
            return scaleAdjustment; 
        }
        
        public double getHighPollutionThreshold() { 
            return highPollutionThreshold; 
        }
        
        public double getLowPollutionThreshold() { 
            return lowPollutionThreshold; 
        }
        
        public double getMaxNearestPointDistance() { 
            return maxNearestPointDistance; 
        }
    }
}