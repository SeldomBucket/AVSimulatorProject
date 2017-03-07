package aim4.map.cpm;

import aim4.map.Road;
import aim4.map.cpm.testmaps.CPMMapWithCornersOneLane;
import junit.framework.TestCase;

import java.util.List;

public class CPMMapWithCornersOneLaneTest extends TestCase {

    public void testNumberOfRoadsOnInitialisation() throws Exception {
        CPMMapWithCornersOneLane map = new CPMMapWithCornersOneLane(5, // laneWidth
                10.0, // speedLimit
                0.0, // initTime
                100, //width
                100);
        List<Road> roads = map.getRoads();

        int expectedSize = 3;
        assertEquals(expectedSize, roads.size());
    }
}