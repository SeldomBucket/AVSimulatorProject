package aim4.mixedcpm.map;

import aim4.map.Road;
import aim4.map.lane.Lane;
import aim4.map.lane.LineSegmentLane;
import aim4.map.mixedcpm.parking.*;
import org.junit.*;
import util.mixedcpm.MockCPMMap;
import util.mixedcpm.MockManualParkingArea;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class ManualParkingRoadTests {

    // TODO ED Do the mocks of everything

    private ManualParkingRoad manualParkingRoad;
    private double stallLength = 5;
    private double laneWidth = 3;
    private double speedLimit = 10;

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
                                                            laneWidth + 10,
                                                            laneWidth,
                                                            speedLimit));

        topRoad.addTheRightMostLane(new LineSegmentLane(0,
                                                        laneWidth/2,
                                                        10,
                                                        laneWidth/2,
                                                        laneWidth,
                                                        speedLimit));

        bottomRoad.addTheRightMostLane(new LineSegmentLane( 0,
                                                            laneWidth + 10,
                                                            10,
                                                            laneWidth + 10,
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
     * Test that when a ManualStall is added:
     *      the centre road of the ManualStall is set up correctly
     *      the stall stacks adjust accordingly
     *      the junctions are set up correctly
     */
    @Test
    public void testAddManualStall(){
        StallInfo stallInfo = new StallInfo(stallLength , stallLength, StallTypes.NoPaddingTest);
        ManualStall stall = manualParkingRoad.findNewSpace(stallInfo, ManualParkingRoad.SearchParameter.anyFreeSpace);
        Road stallRoad = stall.getRoad();

        assertTrue(stallRoad.getJunctions().get(0).getRoads().contains(centreRoad));
        assertEquals(stallRoad.getJunctions().get(0), stall.getJunction());
    }

    /**
     * Test that when a ManualStall is removed:
     *
     */

    /**
     * Test filling both stall stacks results in a rejected request for a ManualStall
     */


    @After
    public void testTearDown(){

    }

    @AfterClass
    public static void classTearDown(){

    }
}
