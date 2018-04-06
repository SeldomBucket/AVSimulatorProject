package aim4.mixedcpm.map;

import aim4.map.Road;
import aim4.map.connections.Junction;
import aim4.map.mixedcpm.parking.*;
import aim4.map.mixedcpm.maps.AdjustableManualCarPark;
import org.junit.*;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * This test suite sets up a ManualParkingArea and checks all the
 */
public class ManualParkingAreaTest {

    AdjustableManualCarPark testMap;
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
        testMap = new AdjustableManualCarPark( AREA_HEIGHT,
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
        road1RightStack.addManualStall(new StallInfo(1, initialStackSize, StallType.NoPadding));

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

    @Test
    public void testAddFirstStall(){
        double stallHeight = testArea.getDimensions().getHeight()/5;
        double stallWidth = 5;
        StallInfo stallInfo = new StallInfo(stallWidth, stallHeight, StallType.NoPadding);
        ManualStall testStall = testArea.findSpace(stallInfo);

        assertNotNull(testStall);
        assertEquals(1, testArea.getParkingRoads().size());

        StallStack leftStallStack = testArea.getParkingRoads().get(0).getStallStackPair()[0];

        assertEquals(1, leftStallStack.getManualStalls().size());
        assertTrue(leftStallStack.getManualStalls().contains(testStall));

        //Test junctions?
    }

    @Test
    public void testAddTwoStallsSameSize(){
        double stallHeight = testArea.getDimensions().getHeight()/5;
        double stallWidth = 5;
        StallInfo stallInfo = new StallInfo(stallWidth, stallHeight, StallType.NoPadding);
        ManualStall testStall0 = testArea.findSpace(stallInfo);
        ManualStall testStall1 = testArea.findSpace(stallInfo);

        assertNotNull(testStall0);
        assertNotNull(testStall1);
        assertEquals(1, testArea.getParkingRoads().size());

        StallStack leftStallStack = testArea.getParkingRoads().get(0).getStallStackPair()[0];

        assertEquals(2, leftStallStack.getManualStalls().size());
        assertTrue(leftStallStack.getManualStalls().contains(testStall0));
        assertTrue(leftStallStack.getManualStalls().contains(testStall1));
    }

    @Test
    public void testAddTwoStallsDifferentWidthsSameLength(){
        double stallHeight = testArea.getDimensions().getHeight()/5;
        double stallWidth0 = 5;
        double stallWidth1 = 4;
        StallInfo stallInfo0 = new StallInfo(stallWidth0, stallHeight, StallType.NoPadding);
        StallInfo stallInfo1 = new StallInfo(stallWidth1, stallHeight, StallType.NoPadding);
        ManualStall testStall0 = testArea.findSpace(stallInfo0);
        ManualStall testStall1 = testArea.findSpace(stallInfo1);

        assertNotNull(testStall0);
        assertNotNull(testStall1);
        assertEquals(1, testArea.getParkingRoads().size());

        StallStack leftStallStack = testArea.getParkingRoads().get(0).getStallStackPair()[0];

        assertEquals(2, leftStallStack.getManualStalls().size());
        assertTrue(leftStallStack.getManualStalls().contains(testStall0));
        assertTrue(leftStallStack.getManualStalls().contains(testStall1));

        assertEquals(leftStallStack.getBounds().getMinY(), testStall0.getMinY(), 0);
        assertEquals(leftStallStack.getBounds().getMaxY()-testStall1.getWidth(), testStall1.getMinY(), 0);
    }

    @Test
    public void testAddMultipleStallsDifferentLengths(){
        double stallLength0 = 5;
        double stallLength1 = 4;
        double stallLength2 = 3;
        double stallLength3 = 2;
        double stallWidth = 5;
        StallInfo stallInfo0 = new StallInfo(stallWidth, stallLength0, StallType.NoPadding);
        StallInfo stallInfo1 = new StallInfo(stallWidth, stallLength1, StallType.NoPadding);
        StallInfo stallInfo2 = new StallInfo(stallWidth, stallLength2, StallType.NoPadding);
        StallInfo stallInfo3 = new StallInfo(stallWidth, stallLength3, StallType.NoPadding);

        ManualStall testStall0 = testArea.findSpace(stallInfo0);
        ManualStall testStall1 = testArea.findSpace(stallInfo1);
        ManualStall testStall2 = testArea.findSpace(stallInfo2);
        ManualStall testStall3 = testArea.findSpace(stallInfo3);

        assertNotNull(testStall0);
        assertNotNull(testStall1);
        assertNotNull(testStall2);
        assertNotNull(testStall3);

        assertEquals(2, testArea.getParkingRoads().size());

        StallStack leftStallStackRoad1 = testArea.getParkingRoads().get(0).getStallStackPair()[0];
        StallStack rightStallStackRoad1 = testArea.getParkingRoads().get(0).getStallStackPair()[1];
        StallStack leftStallStackRoad2 = testArea.getParkingRoads().get(1).getStallStackPair()[0];
        StallStack rightStallStackRoad2 = testArea.getParkingRoads().get(1).getStallStackPair()[1];

        assertEquals(1, leftStallStackRoad1.getManualStalls().size());
        assertEquals(1, rightStallStackRoad1.getManualStalls().size());
        assertEquals(1, leftStallStackRoad2.getManualStalls().size());
        assertEquals(1, rightStallStackRoad2.getManualStalls().size());

        assertTrue(leftStallStackRoad1.getManualStalls().contains(testStall0));
        assertTrue(rightStallStackRoad1.getManualStalls().contains(testStall1));
        assertTrue(leftStallStackRoad2.getManualStalls().contains(testStall2));
        assertTrue(rightStallStackRoad2.getManualStalls().contains(testStall3));

        assertEquals(leftStallStackRoad1.getBounds().getMinY(), testStall0.getMinY(), 0);
        assertEquals(rightStallStackRoad1.getBounds().getMinY(), testStall1.getMinY(), 0);
        assertEquals(leftStallStackRoad2.getBounds().getMinY(), testStall0.getMinY(), 0);
        assertEquals(rightStallStackRoad2.getBounds().getMinY(), testStall1.getMinY(), 0);
    }

    @Test
    public void testAddStallsToOverfillWidthOfParkingArea(){
        //Different lengths means that each stall should go into a new stall stack,
        // and the last one shouldn't fit in the parking area
        double stallLength0 = testArea.getDimensions().getWidth()/4;
        double stallLength1 = testArea.getDimensions().getWidth()/4+1;
        double stallLength2 = testArea.getDimensions().getWidth()/4+2;
        double stallLength3 = testArea.getDimensions().getWidth()/4+3;
        double stallWidth = 5;
        StallInfo stallInfo0 = new StallInfo(stallWidth, stallLength0, StallType.NoPadding);
        StallInfo stallInfo1 = new StallInfo(stallWidth, stallLength1, StallType.NoPadding);
        StallInfo stallInfo2 = new StallInfo(stallWidth, stallLength2, StallType.NoPadding);
        StallInfo stallInfo3 = new StallInfo(stallWidth, stallLength3, StallType.NoPadding);

        ManualStall testStall0 = testArea.findSpace(stallInfo0);
        ManualStall testStall1 = testArea.findSpace(stallInfo1);
        ManualStall testStall2 = testArea.findSpace(stallInfo2);
        ManualStall testStall3 = testArea.findSpace(stallInfo3);

        assertEquals(2, testArea.getParkingRoads().size());

        assertNotNull(testStall0);
        assertNotNull(testStall1);
        assertNotNull(testStall2);
        assertNull(testStall3);
    }

    @Test
    public void testTryToAddTooManyParkingRoads(){
        //Different lengths means that each stall should go into a new stall stack
        double stallLength0 = testArea.getDimensions().getWidth()/4;
        double stallLength1 = testArea.getDimensions().getWidth()/4-1;
        double stallLength2 = testArea.getDimensions().getWidth()/4-2;
        double stallLength3 = testArea.getDimensions().getWidth()/4-3;
        double stallLength4 = testArea.getDimensions().getWidth()/4-4;
        double stallWidth = 5;
        StallInfo stallInfo0 = new StallInfo(stallWidth, stallLength0, StallType.NoPadding);
        StallInfo stallInfo1 = new StallInfo(stallWidth, stallLength1, StallType.NoPadding);
        StallInfo stallInfo2 = new StallInfo(stallWidth, stallLength2, StallType.NoPadding);
        StallInfo stallInfo3 = new StallInfo(stallWidth, stallLength3, StallType.NoPadding);
        StallInfo stallInfo4 = new StallInfo(stallWidth, stallLength4, StallType.NoPadding);

        ManualStall testStall0 = testArea.findSpace(stallInfo0);
        ManualStall testStall1 = testArea.findSpace(stallInfo1);
        ManualStall testStall2 = testArea.findSpace(stallInfo2);
        ManualStall testStall3 = testArea.findSpace(stallInfo3);
        ManualStall testStall4 = testArea.findSpace(stallInfo4);

        assertEquals(2, testArea.getParkingRoads().size());

        assertNotNull(testStall0);
        assertNotNull(testStall1);
        assertNotNull(testStall2);
        assertNotNull(testStall3);
        assertNull(testStall4);
    }

    @Test
    public void testRemoveLastStallFromLastStallStack(){
        double stallLength0 = testArea.getDimensions().getWidth()/4;
        double stallLength1 = testArea.getDimensions().getWidth()/4-1;
        double stallWidth = 5;
        StallInfo stallInfo0 = new StallInfo(stallWidth, stallLength0, StallType.NoPadding);
        StallInfo stallInfo1 = new StallInfo(stallWidth, stallLength1, StallType.NoPadding);

        ManualStall testStall0 = testArea.findSpace(stallInfo0);
        ManualStall testStall1 = testArea.findSpace(stallInfo1);

        assertEquals(1, testArea.getParkingRoads().size());

        assertNotNull(testStall0);
        assertNotNull(testStall1);

        StallStack lastStallStack = testArea.getParkingRoads().get(0).getStallStackPair()[1];

        assertEquals(stallLength1, lastStallStack.getBounds().getWidth(), 0);

        testArea.removeManualStall(testStall1.getName());

        assertEquals(0, lastStallStack.getManualStalls().size(), 0);
        assertEquals(0, lastStallStack.getBounds().getWidth(), 0);
    }

    @Test
    public void testRemoveLastStallFromLastManualParkingRoad(){
        double stallLength = testArea.getDimensions().getWidth()/4;
        double stallWidth = 5;
        StallInfo stallInfo = new StallInfo(stallWidth, stallLength, StallType.NoPadding);

        ManualStall testStall = testArea.findSpace(stallInfo);

        assertEquals(1, testArea.getParkingRoads().size());

        assertNotNull(testStall);

        StallStack stallStack = testArea.getParkingRoads().get(0).getStallStackPair()[0];

        assertEquals(stallLength, stallStack.getBounds().getWidth(), 0);

        testArea.removeManualStall(testStall.getName());

        testArea.update();

        assertEquals(0, testArea.getParkingRoads().size());
    }

    // TODO ED Make these tests
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
