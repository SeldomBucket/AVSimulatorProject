package aim4.mixedcpm.map;

import aim4.map.Road;
import aim4.map.mixedcpm.parking.ManualParkingArea;
import aim4.map.mixedcpm.parking.ManualParkingRoad;
import aim4.map.mixedcpm.parking.StallStack;
import aim4.map.mixedcpm.testmaps.ManualCPMMapTest;
import org.junit.*;

import static org.junit.Assert.*;

/**
 * This test suite sets up a ManualParkingArea and checks all the
 */
public class ManualParkingAreaInitialisationTest {

    ManualCPMMapTest testMap;
    ManualParkingArea testArea;

    @BeforeClass
    public static void classSetup(){

    }

    @Before
    public void testSetup(){
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

    /**
     * Test that the initialisation of the ManualParkingRoad by a ManualParkingArea works
     * (i.e. it's in all the right lists, it's )
     */
    @Test
    public void testManualParkingAreaParkingRoadInitialisation(){

        String roadName = "testRoad";
        testArea.addNewParkingRoad(roadName, 5);
        Road road = testArea.getRoadByName(roadName);
        ManualParkingRoad manualParkingRoad = testArea.getParkingRoadByName(roadName);

        // Check the road is in the map as well as the parking area
        assertEquals(road, testMap.getRoadByName(roadName));

        // Check the centre road of the ManualParkingRoad is set up correctly by the ManualParkingArea
        assertEquals(road, manualParkingRoad.getCentreRoad());

        // Test parking road removal
        testArea.removeParkingRoad(manualParkingRoad);

        assertEquals(null, testArea.getParkingRoadByName(roadName));
        assertEquals(null, testArea.getRoadByName(roadName));
        assertEquals(null, testMap.getRoadByName(roadName));
    }

    /**
     * Stall stacks shouldn't intersect with the top and bottom roads
     */
    @Test
    public void testManualParkingAreaStallStacks(){
        String roadName = "testRoad";
        int initialStackSize = 5;
        testArea.addNewParkingRoad(roadName, initialStackSize);
        ManualParkingRoad manualParkingRoad = testArea.getParkingRoadByName(roadName);
        Road topRoad = testArea.getRoadByName("topRoad");
        Road bottomRoad = testArea.getRoadByName("bottomRoad");
        Road centreRoad = manualParkingRoad.getCentreRoad();

        StallStack leftStack = manualParkingRoad.getStallStackPair()[0];
        StallStack rightStack = manualParkingRoad.getStallStackPair()[1];

        // Check stall stack doesn't intersect with topRoad
        assertFalse(topRoad.getOnlyLane().getShape().getBounds2D().intersects(leftStack.getBounds()));
        assertFalse(topRoad.getOnlyLane().getShape().getBounds2D().intersects(rightStack.getBounds()));
        // and bottomRoad
        assertFalse(bottomRoad.getOnlyLane().getShape().getBounds2D().intersects(leftStack.getBounds()));
        assertFalse(bottomRoad.getOnlyLane().getShape().getBounds2D().intersects(rightStack.getBounds()));
        // and centre road
        assertFalse(centreRoad.getOnlyLane().getShape().getBounds2D().intersects(leftStack.getBounds()));
        assertFalse(centreRoad.getOnlyLane().getShape().getBounds2D().intersects(rightStack.getBounds()));

        // Check stall stack is directly next to topRoad
        assertEquals(leftStack.getBounds().getMinY(), topRoad.getOnlyLane().getShape().getBounds2D().getMaxY(), 0);
        assertEquals(rightStack.getBounds().getMinY(), topRoad.getOnlyLane().getShape().getBounds2D().getMaxY(), 0);
        // and bottomRoad
        assertEquals(leftStack.getBounds().getMaxY(), bottomRoad.getOnlyLane().getShape().getBounds2D().getMinY(), 0);
        assertEquals(rightStack.getBounds().getMaxY(), bottomRoad.getOnlyLane().getShape().getBounds2D().getMinY(), 0);
        // and centre road
        assertEquals(leftStack.getBounds().getMaxX(), centreRoad.getOnlyLane().getShape().getBounds2D().getMinX(), 0);
        assertEquals(rightStack.getBounds().getMinX(), centreRoad.getOnlyLane().getShape().getBounds2D().getMaxX(), 0);

        // Check left stall stack is correct size
        assertEquals(initialStackSize, leftStack.getBounds().getWidth(), 0);
    }

    /**
     *
     */

    @After
    public void testTearDown(){
        testMap = null;
        testArea = null;
    }

    @AfterClass
    public static void classTearDown(){

    }
}
