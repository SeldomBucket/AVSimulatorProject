package aim4.mixedcpm.map;

import aim4.map.Road;
import aim4.map.connections.Junction;
import aim4.map.mixedcpm.parking.*;
import aim4.map.mixedcpm.testmaps.ManualCPMMapTest;
import org.junit.*;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * This test suite sets up a ManualParkingArea and checks all the
 */
public class ManualParkingAreaTest {

    ManualCPMMapTest testMap;
    ManualParkingArea testArea;
    Road topRoad, bottomRoad;
    static final double AREA_HEIGHT = 20;
    static final double AREA_WIDTH = 50;
    static final double LANE_WIDTH = 3;
    static final double SPEED_LIMIT = 10;
    static final double INIT_TIME = 0;

    @BeforeClass
    public static void classSetup(){

    }

    @Before
    public void testSetup(){
        testMap = new ManualCPMMapTest( AREA_HEIGHT,
                                        AREA_WIDTH,
                                        LANE_WIDTH,
                                        SPEED_LIMIT,
                                        INIT_TIME);
        testArea = testMap.getManualParkingArea();
        topRoad = testMap.getRoadByName("topRoad");
        bottomRoad = testMap.getRoadByName("bottomRoad");
    }

    /**
     * Test that topRoad and bottomRoad are in both the ManualParkingArea and the containing map
     */
    @Test
    public void testManualParkingAreaEntryAndExitRoads(){
        assertEquals(topRoad, testArea.getRoadByName("topRoad"));
        assertEquals(bottomRoad, testArea.getRoadByName("bottomRoad"));
    }

    /**
     * Test that the initialisation of the ManualParkingRoad by a ManualParkingArea works
     * (i.e. it's in all the right lists, it's )
     */
    @Test
    public void testManualParkingAreaParkingRoadInitialisation(){

        String roadName = "testRoad";
        double initialStackWidth = 5;

        testArea.addNewParkingRoad(roadName, initialStackWidth);
        Road road = testArea.getRoadByName(roadName);
        ManualParkingRoad manualParkingRoad = testArea.getParkingRoadByName(roadName);

        // Check the road is in the map as well as the parking area
        assertEquals(road, testMap.getRoadByName(roadName));

        // Check the centre road of the ManualParkingRoad is set up correctly by the ManualParkingArea
        assertEquals(road, manualParkingRoad.getCentreRoad());

        // Check the centre road is in the right position
        double expectedPosition = this.testArea.getDimensions().getMinX() + initialStackWidth;
        assertEquals(expectedPosition, road.getOnlyLane().getShape().getBounds2D().getMinX(), 0);

        // Test parking road removal
        testArea.removeParkingRoad(manualParkingRoad);

        assertEquals(null, testArea.getParkingRoadByName(roadName));
        assertEquals(null, testArea.getRoadByName(roadName));
        assertEquals(null, testMap.getRoadByName(roadName));
    }

    /**
     * Stall stacks shouldn't intersect with the top, bottom, and centre roads
     * & should lie directly next to them
     */
    @Test
    public void testManualParkingAreaStallStacksInitialisation(){
        String roadName = "testRoad";
        int initialStackSize = 5;
        testArea.addNewParkingRoad(roadName, initialStackSize);
        ManualParkingRoad manualParkingRoad = testArea.getParkingRoadByName(roadName);
        Road centreRoad = manualParkingRoad.getCentreRoad();

        StallStack leftStack = manualParkingRoad.getStallStackPair()[0];
        StallStack rightStack = manualParkingRoad.getStallStackPair()[1];

        // Check left stall stack is correct size
        assertEquals(initialStackSize, leftStack.getBounds().getWidth(), 0);

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

    }

    /**
     * Test adding a second lane to the manual parking area
     * correct road position, correct stack positions
     */
    @Test
    public void testManualParkingAreaTwoParkingLanes(){

        String roadName1 = "testRoad1";
        String roadName2 = "testRoad2";
        int initialStackSize = 5;
        testArea.addNewParkingRoad(roadName1, initialStackSize);
        ManualParkingRoad manualParkingRoad1 = testArea.getParkingRoadByName(roadName1);
        Road road1 = manualParkingRoad1.getCentreRoad();
        StallStack road1RightStack = manualParkingRoad1.getStallStackPair()[1];

        //Set the right stack of road 1 to have a stack size of 5
        road1RightStack.addManualStall(new StallInfo(1, initialStackSize, StallTypes.NoPaddingTest));

        testArea.addNewParkingRoad(roadName2, initialStackSize);
        ManualParkingRoad manualParkingRoad2 = testArea.getParkingRoadByName(roadName2);

        Road road2 = manualParkingRoad2.getCentreRoad();
        StallStack road2LeftStack = manualParkingRoad2.getStallStackPair()[0];

        // Check road2 is in the correct place
        double expectedRoad2MinX = road1.getOnlyLane().getShape().getBounds2D().getMaxX() + initialStackSize * 2;
        assertEquals(expectedRoad2MinX, road2.getOnlyLane().getShape().getBounds2D().getMinX(), 0);

        // Check stall stacks on adjacent lanes don't intersect
        assertFalse(road1RightStack.getBounds().intersects(road2LeftStack.getBounds()));

        // Check stall stacks for adjacent lanes are back to back
        assertEquals(road1RightStack.getBounds().getMaxX(), road2LeftStack.getBounds().getMinX(), 0);
    }

    @Test
    public void testRemoveEmptyParkingRoad(){

        // TODO ED THIS TEST
        String roadName = "testRoad";
        int initialStackSize = 5;
        testArea.addNewParkingRoad(roadName, initialStackSize);
        ManualParkingRoad manualParkingRoad = testArea.getParkingRoadByName(roadName);
        Junction topRoadJunction = topRoad.getJunctions().get(0);
        Junction bottomRoadJunction = bottomRoad.getJunctions().get(0);

        testArea.removeParkingRoad(manualParkingRoad);

        ArrayList<Junction> junctions = new ArrayList<>(testArea.getJunctions());

        assertEquals(0, topRoad.getJunctions().size());
        assertEquals(0, bottomRoad.getJunctions().size());

        assertFalse(junctions.contains(topRoadJunction));
        assertFalse(junctions.contains(bottomRoadJunction));

        assertFalse(testArea.getParkingRoads().contains(manualParkingRoad));
    }

    @Test
    public void testJunctionsOnNewParkingRoad(){

        // TODO ED THIS TEST
        String roadName = "testRoad";
        int initialStackSize = 5;
        ManualParkingRoad manualParkingRoad = testArea.addNewParkingRoad(roadName, initialStackSize);
        Road centreRoad = manualParkingRoad.getCentreRoad();
        ArrayList<Junction> junctions = centreRoad.getJunctions();
        Junction topRoadJunction = topRoad.getJunctions().get(0);
        Junction bottomRoadJunction = bottomRoad.getJunctions().get(0);

        assertTrue(junctions.contains(topRoadJunction));
        assertTrue(junctions.contains(bottomRoadJunction));

        assertTrue(topRoadJunction.getRoads().contains(centreRoad));
        assertTrue(bottomRoadJunction.getRoads().contains(centreRoad));

        assertTrue(centreRoad.getJunctions().contains(topRoadJunction));
        assertTrue(centreRoad.getJunctions().contains(bottomRoadJunction));
    }

    // TODO ED Make these tests
    // Test junctions are set up correctly when adding manual parking road
    // Test removeParkingRoad when it has some spaces - make sure the spaces roads are all removed too

    @After
    public void testTearDown(){
        testMap = null;
        testArea = null;
    }

    @AfterClass
    public static void classTearDown(){

    }
}
