package aim4.mixedcpm.map;

import aim4.map.Road;
import aim4.map.connections.Junction;
import aim4.map.lane.LineSegmentLane;
import aim4.map.mixedcpm.parking.*;
import org.junit.*;
import util.mixedcpm.MockMixedCPMMap;

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

        MockMixedCPMMap mockMap = new MockMixedCPMMap(laneWidth, speedLimit);

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
        StallSpec stallSpec = new StallSpec(stallLength , stallWidth, StallType.NoPadding);
        ManualStall stall = manualParkingRoad.findNewSpace(stallSpec, ManualParkingRoad.SearchParameter.anyGap);
        Road stallRoad = stall.getRoad();
        double stallRoadWidth = stallRoad.getOnlyLane().getWidth();

        // Check manual stall road initialised properly
        assertEquals(manualParkingRoad.getCentreRoad().getOnlyLane().getShape().getBounds().getMaxX(),
                        stallRoad.getOnlyLane().getStartPoint().getX(),0);
        assertEquals(manualParkingRoad.getStallStackPair()[0].getBounds().getMinY() + stallRoadWidth/2,
                        stallRoad.getOnlyLane().getStartPoint().getY(),0);
        assertEquals(manualParkingRoad.getStallStackPair()[0].getBounds().getMinX(),
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
        StallSpec stallSpec0 = new StallSpec(stallWidth, stallLength, StallType.NoPadding);
        StallSpec stallSpec1 = new StallSpec(stallWidth1, stallLength1, StallType.NoPadding);

        manualParkingRoad.findNewSpace(stallSpec0, ManualParkingRoad.SearchParameter.anyGap);
        ManualStall testStall = manualParkingRoad.findNewSpace(stallSpec1, ManualParkingRoad.SearchParameter.emptyStack);
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
        StallSpec stallSpec = new StallSpec(stallWidth, stallLength, StallType.NoPadding);

        ManualStall testStall = manualParkingRoad.findNewSpace(stallSpec, ManualParkingRoad.SearchParameter.anyGap);

        assertNotNull(testStall);

        Junction stallJunction = testStall.getJunction();
        ArrayList<Junction> junctions = manualParkingRoad.getJunctions();

        assertTrue(manualParkingRoad.getStallStackPair()[0].getManualStalls().contains(testStall));
        assertEquals(1, junctions.size());
        assertTrue(junctions.contains(stallJunction));

        manualParkingRoad.getStallStackPair()[0].removeManualStall(testStall.getName());

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
        StallSpec stallSpec = new StallSpec(stallWidth, stallLength, StallType.NoPadding);

        ManualStall testStall0 = manualParkingRoad.findNewSpace(stallSpec, ManualParkingRoad.SearchParameter.emptyStack);
        ManualStall testStall1 = manualParkingRoad.findNewSpace(stallSpec, ManualParkingRoad.SearchParameter.emptyStack);

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
        StallSpec stallSpecExactlyCorrect = new StallSpec(stallWidth, stallLength, StallType.NoPadding);
        StallSpec stallSpecCorrectLength = new StallSpec(stallWidth-1, stallLength, StallType.NoPadding);
        StallSpec stallSpecCorrectWidth = new StallSpec(stallWidth, stallLength-1, StallType.NoPadding);
        StallSpec stallSpecBothIncorrect = new StallSpec(stallWidth-1, stallLength-1, StallType.NoPadding);

        // First stall is to set the ideal stall width
        ManualStall testStall0 = manualParkingRoad.findNewSpace(stallSpecExactlyCorrect, ManualParkingRoad.SearchParameter.correctLength);
        // These three shouldn't be added as they don't fit exactly
        ManualStall testStall1 = manualParkingRoad.findNewSpace(stallSpecBothIncorrect, ManualParkingRoad.SearchParameter.correctLength);
        ManualStall testStall2 = manualParkingRoad.findNewSpace(stallSpecCorrectLength, ManualParkingRoad.SearchParameter.correctLength);
        ManualStall testStall3 = manualParkingRoad.findNewSpace(stallSpecCorrectWidth, ManualParkingRoad.SearchParameter.correctLength);

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
        double stallWidth = (manualParkingRoad.getStallStackPair()[0].getBounds().getHeight()-1)/3;
        StallSpec stallSpecExactlyCorrect = new StallSpec(stallWidth, stallLength, StallType.NoPadding);
        StallSpec stallSpecBothIncorrect = new StallSpec(stallWidth-1, stallLength-1, StallType.NoPadding);
        StallSpec stallSpecLengthCorrect = new StallSpec(stallWidth-1, stallLength, StallType.NoPadding);
        StallSpec stallSpecWidthCorrect = new StallSpec(stallWidth, stallLength-1, StallType.NoPadding);

        // First stall is to set the ideal stall width
        ManualStall testStall0 = manualParkingRoad.findNewSpace(stallSpecExactlyCorrect, ManualParkingRoad.SearchParameter.anyGap);
        // Check this is added when looking for exact match
        ManualStall testStall1 = manualParkingRoad.findNewSpace(stallSpecExactlyCorrect, ManualParkingRoad.SearchParameter.exactSize);
        // These three shouldn't be added as they don't fit exactly
        ManualStall testStall2 = manualParkingRoad.findNewSpace(stallSpecBothIncorrect, ManualParkingRoad.SearchParameter.exactSize);
        ManualStall testStall3 = manualParkingRoad.findNewSpace(stallSpecLengthCorrect, ManualParkingRoad.SearchParameter.exactSize);
        ManualStall testStall4 = manualParkingRoad.findNewSpace(stallSpecWidthCorrect, ManualParkingRoad.SearchParameter.exactSize);

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
        StallSpec stallSpec = new StallSpec(stallWidth, stallLength, StallType.NoPadding);
        ManualStall testStall0 = manualParkingRoad.findNewSpace(stallSpec, ManualParkingRoad.SearchParameter.anyGap);
        ManualStall testStall1 = manualParkingRoad.findNewSpace(stallSpec, ManualParkingRoad.SearchParameter.anyGap);
        ManualStall testStall2 = manualParkingRoad.findNewSpace(stallSpec, ManualParkingRoad.SearchParameter.anyGap);
        ManualStall testStall3 = manualParkingRoad.findNewSpace(stallSpec, ManualParkingRoad.SearchParameter.anyGap);
        ManualStall testStall4 = manualParkingRoad.findNewSpace(stallSpec, ManualParkingRoad.SearchParameter.anyGap);

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
        manualParkingRoad = null;
    }

    @AfterClass
    public static void classTearDown(){

    }
}
