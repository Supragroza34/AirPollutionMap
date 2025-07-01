import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;

/**
 * This class manages map images and properties for different cities
 * 
 * @author Amey Tripathi
 * @version 2.0
 */
public class CityMapManager {
    private final Map<String, CityMapInfo> cityMaps;
    
    /**
     * Constructor for CityMapManager
     */
    public CityMapManager() {
        this.cityMaps = new HashMap<>();
        
        // Initialize with default cities
        addCity("London", "London.png", 1200, 900);
        addCity("Manchester", "Manchester.png", 1200, 900);
        addCity("Newcastle", "Newcastle.png", 1200, 900);
    }
    
    /**
     * Add a city with its map information
     * 
     * @param cityName The name of the city
     * @param mapImagePath The file path to the map image
     * @param mapWidth The width of the map in pixels
     * @param mapHeight The height of the map in pixels
     */
    public void addCity(String cityName, String mapImagePath, double mapWidth, double mapHeight) {
        cityMaps.put(cityName, new CityMapInfo(mapImagePath, mapWidth, mapHeight));
    }
    
    /**
     * Get city map information
     * 
     * @param cityName The name of the city
     * @return The city map info or null if not found
     */
    public CityMapInfo getCityMapInfo(String cityName) {
        return cityMaps.get(cityName);
    }
    
    /**
     * Try to load the image for a city
     * 
     * @param cityName The name of the city
     * @return The image or null if it couldn't be loaded
     */
    public Image loadCityMapImage(String cityName) {
        CityMapInfo info = cityMaps.get(cityName);
        if (info == null) {
            return null;
        }
        
        try {
            return new Image("file:" + info.mapImagePath);
        } catch (Exception e) {
            System.out.println("Error loading map image for " + cityName + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Inner class to represent city map information
     */
    public static class CityMapInfo {
        private final String mapImagePath;
        private final double mapWidth;
        private final double mapHeight;
        
        /**
         * Constructor for CityMapInfo
         */
        public CityMapInfo(String mapImagePath, double mapWidth, double mapHeight) {
            this.mapImagePath = mapImagePath;
            this.mapWidth = mapWidth;
            this.mapHeight = mapHeight;
        }
        
        public String getMapImagePath() { 
            return mapImagePath; 
        }
        
        public double getMapWidth() { 
            return mapWidth; 
        }
        
        public double getMapHeight() { 
            return mapHeight; 
        }
    }
}