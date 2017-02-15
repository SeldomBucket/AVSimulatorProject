package aim4.map.cpm;

import aim4.map.Road;
import junit.framework.TestCase;

import java.util.List;

public class VerySimpleMapTest extends TestCase {

    public void testNumberOfRoadsOnInitialisation() throws Exception {
        System.out.println("Testing there are 3 roads when VeryBasicMap is initialised.");
        VerySimpleMap map = new VerySimpleMap();
        List<Road> roads = map.getRoads();

        int expectedSize = 3;
        assertEquals(expectedSize, roads.size());
    }
}