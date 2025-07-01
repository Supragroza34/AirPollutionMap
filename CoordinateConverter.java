
/**
 * This class handles conversion between real-world coordinates (Easting/Northing)
 * and pixel coordinates on the map display.
 * 
 * @author Tejas Raj
 * @version 2.0
 */
public class CoordinateConverter {
    // Real-world coordinates of the map corners
    private final double MIN_EASTING;
    private final double MAX_EASTING;
    private final double MIN_NORTHING;
    private final double MAX_NORTHING;

    // Map image size
    private final double MAP_WIDTH;
    private final double MAP_HEIGHT;

    /**
     * Constructor for the CoordinateConverter
     * 
     * @param minEasting The minimum easting (left/west edge of map)
     * @param maxEasting The maximum easting (right/east edge of map)
     * @param minNorthing The minimum northing (bottom/south edge of map)
     * @param maxNorthing The maximum northing (top/north edge of map)
     * @param mapWidth The width of the map in pixels
     * @param mapHeight The height of the map in pixels
     */
    public CoordinateConverter(double minEasting, double maxEasting, 
                              double minNorthing, double maxNorthing,
                              double mapWidth, double mapHeight) {
        this.MIN_EASTING = minEasting;
        this.MAX_EASTING = maxEasting;
        this.MIN_NORTHING = minNorthing;
        this.MAX_NORTHING = maxNorthing;
        this.MAP_WIDTH = mapWidth;
        this.MAP_HEIGHT = mapHeight;
    }

    /**
     * Convert pixel X coordinate to real-world Easting
     * 
     * @param xCoordinate The pixel X coordinate
     * @return The real-world Easting value
     */
    public double convertToRealEasting(double xCoordinate) {
        return MIN_EASTING + ((xCoordinate / MAP_WIDTH) * (MAX_EASTING - MIN_EASTING));
    }

    /**
     * Convert pixel Y coordinate to real-world Northing
     * 
     * @param yCoordinate The pixel Y coordinate
     * @return The real-world Northing value
     */
    public double convertToRealNorthing(double yCoordinate) {
        return MIN_NORTHING + ((1 - (yCoordinate / MAP_HEIGHT)) * (MAX_NORTHING - MIN_NORTHING));
    }

    /**
     * Convert Easting (real-world X) to pixel X coordinate
     * 
     * @param easting The real-world Easting value
     * @return The pixel X coordinate
     */
    public double convertToPixelX(double easting) {
        return ((easting - MIN_EASTING) / (MAX_EASTING - MIN_EASTING)) * MAP_WIDTH;
    }

    /**
     * Convert Northing (real-world Y) to pixel Y coordinate
     * (Reversed because Y coordinates in images increase downward)
     * 
     * @param northing The real-world Northing value
     * @return The pixel Y coordinate
     */
    public double convertToPixelY(double northing) {
        return MAP_HEIGHT - ((northing - MIN_NORTHING) / (MAX_NORTHING - MIN_NORTHING)) * MAP_HEIGHT;
    }

    /**
     * Check if the given pixel coordinates are within the map bounds
     * 
     * @param pixelX The pixel X coordinate
     * @param pixelY The pixel Y coordinate
     * @return True if within bounds, false otherwise
     */
    public boolean isWithinBounds(double pixelX, double pixelY) {
        return pixelX >= 0 && pixelX <= MAP_WIDTH && pixelY >= 0 && pixelY <= MAP_HEIGHT;
    }
}