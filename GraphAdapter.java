/**
 * This class extends the Graph class to add getters for pollutant and year.
 * Since we've modified the original structure, this adapter helps ensure
 * the statistics component can access the selected pollutant and year.
 * 
 * @author Everyone
 * @version 2.0
 */
public class GraphAdapter extends Graph {
    private String selectedPollutant;
    private String selectedYear;
    
    /**
     * Constructor with dependencies for the GraphAdapter.
     * 
     * @param dataLoader The data loader to retrieve pollution datasets
     * @param cityDataFilter The filter to retrieve data for specific cities
     */
    public GraphAdapter(DataLoader dataLoader, CityDataFilter cityDataFilter) {
        super(dataLoader, cityDataFilter);
    }
    
    /**
     * Sets the selected pollutant for the graph.
     * 
     * @param pollutant The pollutant type to set as selected (e.g., "NO2", "PM10", "PM2.5")
     */
    public void setSelectedPollutant(String pollutant) {
        this.selectedPollutant = pollutant;
    }
    
    /**
     * Sets the selected year for the graph.
     * 
     * @param year The year to set as selected (e.g., "2018", "2019", etc.)
     */
    public void setSelectedYear(String year) {
        this.selectedYear = year;
    }
    
    /**
     * Gets the currently selected pollutant.
     * 
     * @return The name of the currently selected pollutant, or null if none selected
     */
    public String getSelectedPollutant() {
        return selectedPollutant;
    }
    
    /**
     * Gets the currently selected year.
     * 
     * @return The currently selected year as a string, or null if none selected
     */
    public String getSelectedYear() {
        return selectedYear;
    }
}