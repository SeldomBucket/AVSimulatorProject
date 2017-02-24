package aim4.map.cpm;

import aim4.map.Road;
import junit.framework.TestCase;

import java.util.List;

public class CPMMapWithCornersTest extends TestCase {

    public void testNumberOfRoadsOnInitialisation() throws Exception {
        System.out.println("Testing there are 3 roads when VeryBasicMap is initialised.");
        CPMMapWithCorners map = new CPMMapWithCorners();
        List<Road> roads = map.getRoads();

        int expectedSize = 3;
        assertEquals(expectedSize, roads.size());
    }
}