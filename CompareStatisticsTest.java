

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

public class CompareStatisticsTest {
    private CompareStatistics compareStatistics;
    private DataLoader mockDataLoader;
    private CityDataFilter mockCityDataFilter;

    @BeforeEach
    public void setUp() {
        mockDataLoader = new DataLoader(); 
        mockCityDataFilter = new CityDataFilter(); 
        compareStatistics = new CompareStatistics(mockDataLoader, mockCityDataFilter);
    }

    @Test
    public void testCalculateAverage() {
        // Create mock data points
        List<DataPoint> dataPoints = Arrays.asList(
            new DataPoint(1, 10, 100, 200),
            new DataPoint(2, 10, 100, 210),
            new DataPoint(3, 10, 100, 220)
        );

        // Access private method via reflection or move to public
        double average = compareStatistics.calculateAverage(dataPoints);
        assertEquals(210.0, average, 0.01, "Average should be 210.0");
    }

    @Test
    public void testCompareAveragePollution_MissingData() {
        String result = compareStatistics.compareAveragePollution("NO2", "2018", "2020", "London");
        assertEquals("Missing data for one or both selected years.", result);
    }

    @Test
    public void testCompareMaxPollution_NoData() {
        String result = compareStatistics.compareMaxPollution("NO2", "2019", "2021", "New York");
        assertEquals("Missing data for one or both selected years.", result);
    }
}