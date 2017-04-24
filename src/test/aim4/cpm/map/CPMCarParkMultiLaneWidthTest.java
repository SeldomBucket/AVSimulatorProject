package aim4.cpm.map;

import javafx.util.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Test the methods in CPMCarParkMultiLaneWidth map.
 */
public class CPMCarParkMultiLaneWidthTest {

    @Before
    public void setup(){

    }

    @Test
    public void testGetMaxLaneWidth() throws Exception {
        List<Pair<Integer, Double>> testParkingLaneSets =
                new ArrayList<Pair<Integer, Double>>(3);
        testParkingLaneSets.add(new Pair<Integer, Double>(3, 2.0));
        testParkingLaneSets.add(new Pair<Integer, Double>(1, 1.0));
        testParkingLaneSets.add(new Pair<Integer, Double>(4, 2.5));

        // double maxLaneWidth =
    }
}
