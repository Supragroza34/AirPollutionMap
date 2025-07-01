import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class filters data points to ensure they are within specified geographic boundaries.
 * Used to ensure only data within specific city areas is displayed.
 * 
 * @author Amey Tripathi and Tejas Raj
 * @version 2.0
 */
public class CityDataFilter {
    // Map to store city boundaries
    private final Map<String, CityBoundary> cityBoundaries;
    
    /**
     * Constructor for CityDataFilter
     */
    public CityDataFilter() {
        this.cityBoundaries = new HashMap<>();
        
        // Initialize with default cities
        addCity("London", 510394, 553297, 168504, 193305);
        addCity("Manchester", 372737, 394335, 391515, 405386);
        addCity("Newcastle", 412855, 437525, 557644, 571305);
    }
    
    /**
     * Add a city with its boundaries
     * 
     * @param cityName The name of the city
     * @param minEasting The minimum easting (left/west edge)
     * @param maxEasting The maximum easting (right/east edge)
     * @param minNorthing The minimum northing (bottom/south edge)
     * @param maxNorthing The maximum northing (top/north edge)
     */
    public void addCity(String cityName, double minEasting, double maxEasting, 
                        double minNorthing, double maxNorthing) {
        cityBoundaries.put(cityName, new CityBoundary(minEasting, maxEasting, minNorthing, maxNorthing));
    }
    
    /**
     * Get all available city names
     * 
     * @return List of city names
     */
    public List<String> getAvailableCities() {
        return new ArrayList<>(cityBoundaries.keySet());
    }
    
    /**
     * Get a city's boundary
     * 
     * @param cityName The name of the city
     * @return The city boundary or null if not found
     */
    public CityBoundary getCityBoundary(String cityName) {
        return cityBoundaries.get(cityName);
    }
    
    /**
     * Filter data points to only include those within the specified city boundaries
     * 
     * @param allData The complete list of data points
     * @param cityName The name of the city to filter for
     * @return A filtered list containing only points within the city boundaries
     */
    public List<DataPoint> filterCityArea(List<DataPoint> allData, String cityName) {
        CityBoundary boundary = cityBoundaries.get(cityName);
        
        if (boundary == null) {
            return new ArrayList<>(); // Return empty list if city not found
        }
        
        List<DataPoint> filteredList = new ArrayList<>();
        
        for (DataPoint point : allData) {
            if (point.x() >= boundary.minEasting && point.x() <= boundary.maxEasting &&
                point.y() >= boundary.minNorthing && point.y() <= boundary.maxNorthing) {
                filteredList.add(point);
            }
        }
        
        return filteredList;
    }
    
    /**
     * Inner class to represent city boundaries
     */
    public static class CityBoundary {
        private final double minEasting;
        private final double maxEasting;
        private final double minNorthing;
        private final double maxNorthing;
        
        /**
         * Constructor for CityBoundary
         */
        public CityBoundary(double minEasting, double maxEasting, double minNorthing, double maxNorthing) {
            this.minEasting = minEasting;
            this.maxEasting = maxEasting;
            this.minNorthing = minNorthing;
            this.maxNorthing = maxNorthing;
        }
        
        public double getMinEasting() { 
            return minEasting; 
        }
        
        public double getMaxEasting() { 
            return maxEasting; 
        }
        
        public double getMinNorthing() { 
            return minNorthing; 
        }
        
        public double getMaxNorthing() { 
            return maxNorthing; 
        }
    }
}