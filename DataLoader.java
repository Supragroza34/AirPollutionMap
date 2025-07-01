import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This class loads air pollution data files from disk and manages datasets.
 * It handles loading multiple datasets for different pollutants and years.
 * 
 * @author Tejas Raj
 * @version 2.0
 */
public class DataLoader {
    private static final String COMMA_DELIMITER = ",";
    private Map<String, DataSet> datasetMap = new HashMap<>();
    
    /**
     * Loads all datasets for the given years and pollutants
     * 
     * @param years List of years to load data for
     * @param pollutants List of pollutant types to load data for
     */
    public void loadAllDatasets(List<String> years, List<String> pollutants) {
        System.out.println("Loading all datasets...");

        for (String pollutant : pollutants) {
            for (String year : years) {
                String filepath = getFilePath(year, pollutant);
                if (filepath != null) {
                    DataSet dataset = loadDataFile(filepath);
                    if (dataset != null) {
                        datasetMap.put(pollutant + "-" + year, dataset);
                        System.out.println("Loaded data for " + pollutant + " in " + year);
                    } else {
                        System.out.println("Data not found for " + pollutant + " in " + year);
                    }
                }
            }
        }
    }
    
    /**
     * Get a dataset for a specific pollutant and year
     * 
     * @param pollutant The pollutant type
     * @param year The year
     * @return The dataset or null if not found
     */
    public DataSet getDataset(String pollutant, String year) {
        return datasetMap.get(pollutant + "-" + year);
    }
    
    /**
     * Helper method to generate file path based on pollutant and year
     * 
     * @param year The year for the dataset
     * @param pollutant The pollutant type
     * @return The file path
     */
    private String getFilePath(String year, String pollutant) {
        switch (pollutant) {
            case "NO2":
                return "UKAirPollutionData/NO2/mapno2" + year + ".csv";
            case "PM10":
                return "UKAirPollutionData/pm10/mappm10" + year + "g.csv";
            case "PM2.5":
                return "UKAirPollutionData/pm2.5/mappm25" + year + "g.csv";
            default:
                return null;
        }
    }

    /** 
     * Read a data file from disk. The data must be a csv file, and must be in the
     * DEFRA air pollution file format. The data is returned in a DataSet object.
     * 
     * @param fileName The file to load
     * @return A DataSet object holding the complete dataset
     */
    public DataSet loadDataFile(String fileName) {
        System.out.println("Loading file " + fileName+ "...");
        
        URL url = getClass().getResource(fileName);
        System.out.println(url);
        try (BufferedReader br = new BufferedReader(new FileReader(new File(url.toURI()).getAbsolutePath()))) {
            // the first four lines of the file hold special information; read them in:
            String pollutant = readDataHeader(br);
            String year = readDataHeader(br);
            String metric = readDataHeader(br);
            String units = readDataHeader(br);
            
            // discard the next two lines. the first is empty, and the next holds 
            // the column labels for the data points.
            br.readLine();
            br.readLine();

            DataSet dataSet = new DataSet(pollutant, year, metric, units);
            
            // read all the data lines
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                dataSet.addData(values);
            }
            System.out.println("Loading file... done.");
            return dataSet;
        }        
        catch(IOException | URISyntaxException e) {
            System.out.println("Could not read file " + fileName);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Read one piece of information out of the header of the data file.
     * Each line in the header has the format
     *    DATA,,,
     * That is, it is a four column csv entry where the first column holds the data we
     * want and the other three columns are empty.
     * We read and return only the data point from the first column.
     * 
     * @return The data from the next header line of the file currently being read.
     */
    private String readDataHeader(BufferedReader br)
        throws java.io.IOException
    {
        String line = br.readLine();
        String[] values = line.split(COMMA_DELIMITER);
        return values[0];
    }
}