package aim4.mixedcpm.map;

import aim4.map.Road;
import aim4.map.connections.Junction;
import aim4.map.lane.LineSegmentLane;
import aim4.map.mixedcpm.parking.*;
import org.junit.*;
import util.mixedcpm.MockCPMMap;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class ManualParkingRoadTests {

    // TODO ED Do the mocks of everything

    private ManualParkingRoad manualParkingRoad;
    private double stallLength = 5;
    private double laneWidth = 3;
    private double speedLimit = 10;
    private double horizontalLaneLength = 10;
    private double verticalLaneLength = 10.5;

    private Road centreRoad;
    private Road topRoad;
    private Road bottomRoad;

    @BeforeClass
    public static void classSetup(){

    }

    @Before
    public void testSetup(){

        MockCPMMap mockMap = new MockCPMMap(laneWidth, speedLimit);

        centreRoad = new Road("centreRoad", mockMap);
        topRoad = new Road("topRoad", mockMap);
        bottomRoad = new Road("bottomRoad", mockMap);

        centreRoad.addTheRightMostLane(new LineSegmentLane( stallLength + laneWidth/2,
                                                            laneWidth/2,
                                                            stallLength + laneWidth/2,
                                                            laneWidth + verticalLaneLength,
                                                            laneWidth,
                                                            speedLimit));

        topRoad.addTheRightMostLane(new LineSegmentLane(0,
                                                        laneWidth/2,
                                                        horizontalLaneLength,
                                                        laneWidth/2,
                                                        laneWidth,
                                                        speedLimit));

        bottomRoad.addTheRightMostLane(new LineSegmentLane( 0,
                                                            laneWidth + verticalLaneLength,
                                                            horizontalLaneLength,
                                                            laneWidth + verticalLaneLength,
                                                            laneWidth,
                                                            speedLimit));

        ManualParkingArea parkingArea = new ManualParkingArea(topRoad,
                                                              bottomRoad,
                                                              mockMap,
                                                              new Rectangle2D.Double());

        manualParkingRoad = new ManualParkingRoad(centreRoad, parkingArea, stallLength);
    }

    /**
     * Test that the road is set up correctly
     * Check left stall stack is correct size
     * assertEquals(initialStackSize, leftStack.getBounds().getWidth(), 0);
     */
    @Test
    public void testManualParkingRoadInitialisation(){
        // Check centre road
        assertEquals(centreRoad, manualParkingRoad.getCentreRoad());

        // Check junctions with top and bottom roads

        // Check stall stacks initialisation
        assertEquals(stallLength, manualParkingRoad.getStallStackPair()[0].getBounds().getWidth(),0);
        assertEquals(stallLength, manualParkingRoad.getStallStackPair()[0].getMaxStallLength(),0);

        assertEquals(0, manualParkingRoad.getStallStackPair()[1].getBounds().getWidth(),0);
        assertEquals(0, manualParkingRoad.getStallStackPair()[1].getMaxStallLength(),0);
    }

    /**
     * Test that when a ManualStall is added to the left stack:
     *      the centre road of the ManualStall is set up correctly
     *      the stall stacks adjust accordingly
     *      the junctions are set up correctly
     */
    @Test
    public void testAddManualStallLeftStack(){
        double stallWidth = stallLength;
        StallInfo stallInfo = new StallInfo(stallLength , stallWidth, StallTypes.NoPaddingTest);
        ManualStall stall = manualParkingRoad.findNewSpace(stallInfo, ManualParkingRoad.SearchParameter.anyGap);
        Road stallRoad = stall.getRoad();
        double stallRoadWidth = stallRoad.getOnlyLane().getWidth();

        // Check manual stall road initialised properly
        assertEquals(manualParkingRoad.getStallStackPair()[0].getBounds().getMinX(),
                        stallRoad.getOnlyLane().getStartPoint().getX(),0);
        assertEquals(manualParkingRoad.getStallStackPair()[0].getBounds().getMinY() + stallRoadWidth/2,
                        stallRoad.getOnlyLane().getStartPoint().getY(),0);
        assertEquals(manualParkingRoad.getCentreRoad().getOnlyLane().getShape().getBounds().getMaxX(),
                        stallRoad.getOnlyLane().getEndPoint().getX(),0);
        assertEquals(manualParkingRoad.getStallStackPair()[0].getBounds().getMinY() + stallRoadWidth/2,
                        stallRoad.getOnlyLane().getEndPoint().getY(),0);

        // Check junctions are set up correctly
        assertTrue(stallRoad.getJunctions().get(0).getRoads().contains(centreRoad));
        assertEquals(stallRoad.getJunctions().get(0), stall.getJunction());
    }

    /**
     * Test that when a ManualStall is added to the right stack:
     *      the centre road of the ManualStall is set up correctly
     *      the stall stacks adjust accordingly
     *      the junctions are set up correctly
     */
    @Test
    public void testAddManualStallRightStack(){
        double stallWidth = stallLength;
        double stallLength1 = stallLength/2;
        double stallWidth1 = stallLength/2;
        StallInfo stallInfo0 = new StallInfo(stallWidth, stallLength, StallTypes.NoPaddingTest);
        StallInfo stallInfo1 = new StallInfo(stallWidth1, stallLength1, StallTypes.NoPaddingTest);

        manualParkingRoad.findNewSpace(stallInfo0, ManualParkingRoad.SearchParameter.anyGap);
        ManualStall testStall = manualParkingRoad.findNewSpace(stallInfo1, ManualParkingRoad.SearchParameter.anyGap);
        Road stallRoad = testStall.getRoad();
        double stallRoadWidth = stallRoad.getOnlyLane().getWidth();

        // Check manual stall road initialised properly
        assertEquals(manualParkingRoad.getCentreRoad().getOnlyLane().getShape().getBounds().getMinX(),
                        stallRoad.getOnlyLane().getStartPoint().getX(),0);
        assertEquals(manualParkingRoad.getStallStackPair()[1].getBounds().getMinY() + stallRoadWidth/2,
                        stallRoad.getOnlyLane().getStartPoint().getY(),0);
        assertEquals(manualParkingRoad.getStallStackPair()[1].getBounds().getMaxX(),
                        stallRoad.getOnlyLane().getEndPoint().getX(),0);
        assertEquals(manualParkingRoad.getStallStackPair()[1].getBounds().getMinY() + stallRoadWidth/2,
                        stallRoad.getOnlyLane().getEndPoint().getY(),0);

        // Check junctions are set up correctly
        assertTrue(stallRoad.getJunctions().get(0).getRoads().contains(centreRoad));
        assertEquals(stallRoad.getJunctions().get(0), testStall.getJunction());
    }

    /**
     * Test that when a ManualStall is removed:
     *
     */
    @Test
    public void testRemoveManualStall(){
        double stallWidth = manualParkingRoad.getStallStackPair()[0].getBounds().getHeight()/3;
        StallInfo stallInfo = new StallInfo(stallWidth, stallLength, StallTypes.NoPaddingTest);

        ManualStall testStall = manualParkingRoad.findNewSpace(stallInfo, ManualParkingRoad.SearchParameter.anyGap);

        assertNotNull(testStall);

        Junction stallJunction = testStall.getJunction();
        ArrayList<Junction> junctions = manualParkingRoad.getJunctions();

        assertTrue(manualParkingRoad.getStallStackPair()[0].getManualStalls().contains(testStall));
        assertEquals(1, junctions.size());
        assertTrue(junctions.contains(stallJunction));

        manualParkingRoad.getStallStackPair()[0].removeManualStall(testStall.getStallID());

        junctions = manualParkingRoad.getJunctions();

        assertFalse(manualParkingRoad.getStallStackPair()[0].getManualStalls().contains(testStall));
        assertEquals(0, junctions.size());
        assertFalse(junctions.contains(stallJunction));
    }

    /**
     * Test all search parameters:
     *      ManualParkingRoad.SearchParameter.emptyStack;
     */
    @Test
    public void testAddManualStallsSearchParameterEmptyStack(){
        double stallWidth = manualParkingRoad.getStallStackPair()[0].getBounds().getHeight()/3;
        StallInfo stallInfo = new StallInfo(stallWidth, stallLength, StallTypes.NoPaddingTest);

        ManualStall testStall0 = manualParkingRoad.findNewSpace(stallInfo, ManualParkingRoad.SearchParameter.emptyStack);
        ManualStall testStall1 = manualParkingRoad.findNewSpace(stallInfo, ManualParkingRoad.SearchParameter.emptyStack);

        assertNotNull(testStall0);
        assertNull(testStall1);
        assertEquals(0, manualParkingRoad.getStallStackPair()[0].getManualStalls().size());
        assertEquals(1, manualParkingRoad.getStallStackPair()[1].getManualStalls().size());

        assertTrue(manualParkingRoad.getStallStackPair()[1].getManualStalls().contains(testStall0));
    }


    /**
     * Test all search parameters:
     *      ManualParkingRoad.SearchParameter.correctLength;
     */
    @Test
    public void testAddManualStallsSearchParameterCorrectLength(){
        double stallWidth = manualParkingRoad.getStallStackPair()[0].getBounds().getHeight()/3;
        StallInfo stallInfoExactlyCorrect = new StallInfo(stallWidth, stallLength, StallTypes.NoPaddingTest);
        StallInfo stallInfoCorrectLength = new StallInfo(stallWidth-1, stallLength, StallTypes.NoPaddingTest);
        StallInfo stallInfoCorrectWidth = new StallInfo(stallWidth, stallLength-1, StallTypes.NoPaddingTest);
        StallInfo stallInfoBothIncorrect = new StallInfo(stallWidth-1, stallLength-1, StallTypes.NoPaddingTest);

        // First stall is to set the ideal stall width
        ManualStall testStall0 = manualParkingRoad.findNewSpace(stallInfoExactlyCorrect, ManualParkingRoad.SearchParameter.correctLength);
        // These three shouldn't be added as they don't fit exactly
        ManualStall testStall1 = manualParkingRoad.findNewSpace(stallInfoBothIncorrect, ManualParkingRoad.SearchParameter.correctLength);
        ManualStall testStall2 = manualParkingRoad.findNewSpace(stallInfoCorrectLength, ManualParkingRoad.SearchParameter.correctLength);
        ManualStall testStall3 = manualParkingRoad.findNewSpace(stallInfoCorrectWidth, ManualParkingRoad.SearchParameter.correctLength);

        assertNotNull(testStall0);
        assertNull(testStall1);
        assertNotNull(testStall2);
        assertNull(testStall3);
        assertEquals(2, manualParkingRoad.getStallStackPair()[0].getManualStalls().size());
        assertEquals(0, manualParkingRoad.getStallStackPair()[1].getManualStalls().size());

        assertTrue(manualParkingRoad.getStallStackPair()[0].getManualStalls().contains(testStall0));
        assertTrue(manualParkingRoad.getStallStackPair()[0].getManualStalls().contains(testStall2));
    }


    /**
     * Test all search parameters:
     *      ManualParkingRoad.SearchParameter.exactSize;
     */
    @Test
    public void testAddManualStallsSearchParameterExactSize(){
        double stallWidth = manualParkingRoad.getStallStackPair()[0].getBounds().getHeight()/3;
        StallInfo stallInfoExactlyCorrect = new StallInfo(stallWidth, stallLength, StallTypes.NoPaddingTest);
        StallInfo stallInfoBothIncorrect = new StallInfo(stallWidth-1, stallLength-1, StallTypes.NoPaddingTest);
        StallInfo stallInfoLengthCorrect = new StallInfo(stallWidth-1, stallLength, StallTypes.NoPaddingTest);
        StallInfo stallInfoWidthCorrect = new StallInfo(stallWidth, stallLength-1, StallTypes.NoPaddingTest);

        // First stall is to set the ideal stall width
        ManualStall testStall0 = manualParkingRoad.findNewSpace(stallInfoExactlyCorrect, ManualParkingRoad.SearchParameter.anyGap);
        // Check this is added when looking for exact match
        ManualStall testStall1 = manualParkingRoad.findNewSpace(stallInfoExactlyCorrect, ManualParkingRoad.SearchParameter.exactSize);
        // These three shouldn't be added as they don't fit exactly
        ManualStall testStall2 = manualParkingRoad.findNewSpace(stallInfoBothIncorrect, ManualParkingRoad.SearchParameter.exactSize);
        ManualStall testStall3 = manualParkingRoad.findNewSpace(stallInfoLengthCorrect, ManualParkingRoad.SearchParameter.exactSize);
        ManualStall testStall4 = manualParkingRoad.findNewSpace(stallInfoWidthCorrect, ManualParkingRoad.SearchParameter.exactSize);

        assertNotNull(testStall0);
        assertNotNull(testStall1);
        assertNull(testStall2);
        assertNull(testStall3);
        assertNull(testStall4);
        assertEquals(2, manualParkingRoad.getStallStackPair()[0].getManualStalls().size());
        assertEquals(0, manualParkingRoad.getStallStackPair()[1].getManualStalls().size());

        assertTrue(manualParkingRoad.getStallStackPair()[0].getManualStalls().contains(testStall0));
        assertTrue(manualParkingRoad.getStallStackPair()[0].getManualStalls().contains(testStall1));
    }

    /**
     * Test filling both stall stacks results in a rejected request for a ManualStall
     */
    @Test
    public void testFillBothStacksAndAddNewStall(){
        double stallWidth = manualParkingRoad.getStallStackPair()[0].getBounds().getHeight()/2;
        StallInfo stallInfo = new StallInfo(stallWidth, stallLength, StallTypes.NoPaddingTest);
        ManualStall testStall0 = manualParkingRoad.findNewSpace(stallInfo, ManualParkingRoad.SearchParameter.anyGap);
        ManualStall testStall1 = manualParkingRoad.findNewSpace(stallInfo, ManualParkingRoad.SearchParameter.anyGap);
        ManualStall testStall2 = manualParkingRoad.findNewSpace(stallInfo, ManualParkingRoad.SearchParameter.anyGap);
        ManualStall testStall3 = manualParkingRoad.findNewSpace(stallInfo, ManualParkingRoad.SearchParameter.anyGap);
        ManualStall testStall4 = manualParkingRoad.findNewSpace(stallInfo, ManualParkingRoad.SearchParameter.anyGap);

        assertNotNull(testStall0);
        assertNotNull(testStall1);
        assertNotNull(testStall2);
        assertNotNull(testStall3);
        assertEquals(2, manualParkingRoad.getStallStackPair()[0].getManualStalls().size());
        assertEquals(2, manualParkingRoad.getStallStackPair()[1].getManualStalls().size());
        assertNull(testStall4);
    }


    @After
    public void testTearDown(){

    }

    @AfterClass
    public static void classTearDown(){

    }
}
