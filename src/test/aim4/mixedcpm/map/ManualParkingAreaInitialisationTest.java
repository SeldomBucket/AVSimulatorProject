package aim4.mixedcpm.map;

import aim4.map.Road;
import aim4.map.mixedcpm.parking.ManualParkingArea;
import aim4.map.mixedcpm.parking.ManualParkingRoad;
import aim4.map.mixedcpm.testmaps.ManualCPMMapTest;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import static org.junit.Assert.*;

import java.awt.geom.Rectangle2D;

/**
 * This test suite sets up a ManualParkingArea and checks all the
 */
public class ManualParkingAreaInitialisationTest {

    ManualCPMMapTest testMap;
    ManualParkingArea testArea;

    @Before
    public void setup(){
        testMap = new ManualCPMMapTest(20,30,3,10,0);
        testArea = testMap.getManualParkingArea();
    }

    /**
     * Test that topRoad and bottomRoad are in both the ManualParkingArea and the containing map
     */
    @Test
    public void testManualParkingAreaEntryAndExitRoads(){
        assertEquals(testMap.getRoadByName("topRoad"), testArea.getRoadByName("topRoad"));
        assertEquals(testMap.getRoadByName("bottomRoad"), testArea.getRoadByName("bottomRoad"));
    }

    @Test
    public void testManualParkingAreaParkingRoadInitialisation(){

        String roadName = "testRoad";
        testArea.addNewParkingRoad(roadName, 5);
        Road road = testArea.getRoadByName(roadName);
        ManualParkingRoad manualParkingRoad = testArea.getParkingRoadByName(roadName);

        // Check the road is in the map as well as the parking area
        //assertEquals(road, testMap.getRoadByName(roadName));

        // Check the centre road of the ManualParkingRoad is set up correctly by the ManualParkingArea
        assertEquals(road, manualParkingRoad.getCentreRoad());

        // Test parking road removal
        testArea.removeParkingRoad(manualParkingRoad);

        assertEquals(null, testArea.getParkingRoadByName(roadName));
        assertEquals(null, testArea.getRoadByName(roadName));
        assertEquals(null, testMap.getRoadByName(roadName));
    }

    @After
    public void teardown(){

    }
}
