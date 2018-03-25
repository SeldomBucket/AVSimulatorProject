package aim4.mixedcpm.map;

import aim4.map.mixedcpm.parking.ManualStall;
import aim4.map.mixedcpm.parking.StallInfo;
import aim4.map.mixedcpm.parking.StallStack;

import aim4.map.mixedcpm.parking.StallType;
import util.mixedcpm.MockManualParkingArea;
import util.mixedcpm.MockManualParkingRoad;

import org.junit.*;

import static org.junit.Assert.*;

public class StallStackTests {

    private StallStack existingLengthStallStack,
                        noLengthStallStack,
                        existingLengthStallStackRoadRight;

    private double stallStackHeight = 10;
    private double maxStallLength = 5;
    private double laneWidth = 3;
    private double speedLimit = 10;

    @BeforeClass
    public static void classSetUp(){

    }

    @Before
    public void testSetUp(){

        MockManualParkingRoad parkingRoadLeft = new MockManualParkingRoad(-1);
        MockManualParkingRoad parkingRoadRight = new MockManualParkingRoad(maxStallLength +1);
        MockManualParkingArea mockManualParkingArea = new MockManualParkingArea(laneWidth, speedLimit);

        noLengthStallStack = new StallStack(0,
                                            0,
                                            stallStackHeight,
                                            0,
                                            false,
                                            parkingRoadLeft,
                                            mockManualParkingArea);

        existingLengthStallStack = new StallStack(0,
                                                  0,
                                                  stallStackHeight,
                                                  maxStallLength,
                                                  false,
                                                  parkingRoadLeft,
                                                  mockManualParkingArea);

        existingLengthStallStackRoadRight = new StallStack(0,
                                                           0,
                                                           stallStackHeight,
                                                           maxStallLength,
                                                           false,
                                                           parkingRoadRight,
                                                           mockManualParkingArea);

    }

    @Test
    public void testStackInitialisation(){
        assertEquals(0, existingLengthStallStack.getBounds().getMinX(),0);
        assertEquals(maxStallLength, existingLengthStallStack.getBounds().getMaxX(),0);
        assertEquals(0, existingLengthStallStack.getBounds().getMinY(),0);
        assertEquals(stallStackHeight, existingLengthStallStack.getBounds().getMaxY(),0);
    }

    @Test
    public void testAddFirstSpaceExactLength(){
        double vehicleWidth = stallStackHeight/5;
        double vehicleLength = maxStallLength;
        StallInfo stallInfo = new StallInfo(vehicleWidth, vehicleLength, StallType.NoPaddingTest);
        ManualStall testStall = existingLengthStallStack.addManualStall(stallInfo);
        assertEquals(maxStallLength, testStall.getLength(),0);
        assertEquals(vehicleWidth, testStall.getMaxY(),0);
        assertEquals(0, testStall.getMinY(),0);
        assertEquals(0, testStall.getMinX(), 0);
    }

    @Test
    public void testAddSpaceTooShortRoadLeft(){
        double vehicleWidth = stallStackHeight/5;
        double vehicleLength = maxStallLength/2;
        StallInfo stallInfo = new StallInfo(vehicleWidth, vehicleLength, StallType.NoPaddingTest);
        ManualStall testStall = existingLengthStallStack.addManualStall(stallInfo);
        assertEquals(maxStallLength/2, testStall.getLength(),0);
        assertEquals(0, testStall.getMinX(), 0);
        assertEquals(existingLengthStallStack.getBounds().getMaxY(), testStall.getMaxY(),0);
        assertEquals(existingLengthStallStack.getBounds().getMaxY()-vehicleWidth, testStall.getMinY(),0);
    }

    @Test
    public void testAddSpaceTooShortRoadRight(){
        double vehicleWidth = stallStackHeight/5;
        double vehicleLength = maxStallLength/2;
        StallInfo stallInfo = new StallInfo(vehicleWidth, vehicleLength, StallType.NoPaddingTest);
        ManualStall testStall = existingLengthStallStackRoadRight.addManualStall(stallInfo);
        assertEquals(maxStallLength/2, testStall.getLength(),0);
        assertEquals(maxStallLength/2, testStall.getMinX(), 0);
        assertEquals(existingLengthStallStackRoadRight.getBounds().getMaxY(), testStall.getMaxY(),0);
        assertEquals(existingLengthStallStackRoadRight.getBounds().getMaxY()-vehicleWidth, testStall.getMinY(),0);
    }

    @Test
    public void testAddSpaceTooLong(){
        double vehicleWidth = stallStackHeight/5;
        double vehicleLength = maxStallLength*2;
        StallInfo stallInfo = new StallInfo(vehicleWidth, vehicleLength, StallType.NoPaddingTest);
        ManualStall testStall = existingLengthStallStack.addManualStall(stallInfo);
        assertEquals(null, testStall);
    }

    @Test
    public void testAddFirstSpaceToEmptyStack(){
        double vehicleWidth = stallStackHeight/5;
        double vehicleLength = maxStallLength;
        StallInfo stallInfo = new StallInfo(vehicleWidth, vehicleLength, StallType.NoPaddingTest);
        ManualStall testStall = noLengthStallStack.addManualStall(stallInfo);
        assertEquals(maxStallLength, testStall.getLength(),0);
        assertEquals(maxStallLength, noLengthStallStack.getMaxStallLength(),0);
        assertEquals(0, testStall.getMinX(), 0);
        assertEquals(vehicleWidth, testStall.getMaxY(),0);
        assertEquals(0, testStall.getMinY(),0);
    }

    @Test
    public void testFillStackAllSpacesSameSizeAndExactLength(){
        double vehicleWidth = stallStackHeight/5;
        double vehicleLength = maxStallLength;
        StallInfo stallInfo = new StallInfo(vehicleWidth, vehicleLength, StallType.NoPaddingTest);
        ManualStall testStall0 = existingLengthStallStack.addManualStall(stallInfo);
        ManualStall testStall1 = existingLengthStallStack.addManualStall(stallInfo);
        ManualStall testStall2 = existingLengthStallStack.addManualStall(stallInfo);
        ManualStall testStall3 = existingLengthStallStack.addManualStall(stallInfo);
        ManualStall testStall4 = existingLengthStallStack.addManualStall(stallInfo);
        ManualStall testStall5 = existingLengthStallStack.addManualStall(stallInfo);

        assertEquals(maxStallLength, testStall0.getLength(),0);
        assertEquals(maxStallLength, testStall1.getLength(),0);
        assertEquals(maxStallLength, testStall2.getLength(),0);
        assertEquals(maxStallLength, testStall3.getLength(),0);
        assertEquals(maxStallLength, testStall4.getLength(),0);
        assertEquals(null, testStall5);

        assertTrue(existingLengthStallStack.getManualStalls().contains(testStall0));
        assertTrue(existingLengthStallStack.getManualStalls().contains(testStall1));
        assertTrue(existingLengthStallStack.getManualStalls().contains(testStall2));
        assertTrue(existingLengthStallStack.getManualStalls().contains(testStall3));
        assertTrue(existingLengthStallStack.getManualStalls().contains(testStall4));

        //Check positions of all the manual stalls
        assertEquals(0, testStall0.getMinX(), 0);
        assertEquals(0, testStall0.getMinY(), 0);
        assertEquals(vehicleWidth, testStall0.getMaxY(), 0);

        assertEquals(0, testStall1.getMinX(), 0);
        assertEquals(vehicleWidth*1, testStall1.getMinY(), 0);
        assertEquals(vehicleWidth*2, testStall1.getMaxY(), 0);

        assertEquals(0, testStall2.getMinX(), 0);
        assertEquals(vehicleWidth*2, testStall2.getMinY(), 0);
        assertEquals(vehicleWidth*3, testStall2.getMaxY(), 0);

        assertEquals(0, testStall3.getMinX(), 0);
        assertEquals(vehicleWidth*3, testStall3.getMinY(), 0);
        assertEquals(vehicleWidth*4, testStall3.getMaxY(), 0);

        assertEquals(0, testStall4.getMinX(), 0);
        assertEquals(vehicleWidth*4, testStall4.getMinY(), 0);
        assertEquals(vehicleWidth*5, testStall4.getMaxY(), 0);
    }

    @Test
    public void testFillStackAllSpacesSameSizeAndTooShort(){
        double vehicleWidth = stallStackHeight/5;
        double vehicleLength = maxStallLength/2;
        StallInfo stallInfo = new StallInfo(vehicleWidth, vehicleLength, StallType.NoPaddingTest);
        ManualStall testStall0 = existingLengthStallStack.addManualStall(stallInfo);
        ManualStall testStall1 = existingLengthStallStack.addManualStall(stallInfo);
        ManualStall testStall2 = existingLengthStallStack.addManualStall(stallInfo);
        ManualStall testStall3 = existingLengthStallStack.addManualStall(stallInfo);
        ManualStall testStall4 = existingLengthStallStack.addManualStall(stallInfo);
        ManualStall testStall5 = existingLengthStallStack.addManualStall(stallInfo);

        assertEquals(vehicleLength, testStall0.getLength(),0);
        assertEquals(vehicleLength, testStall1.getLength(),0);
        assertEquals(vehicleLength, testStall2.getLength(),0);
        assertEquals(vehicleLength, testStall3.getLength(),0);
        assertEquals(vehicleLength, testStall4.getLength(),0);
        assertEquals(null, testStall5);

        assertTrue(existingLengthStallStack.getManualStalls().contains(testStall0));
        assertTrue(existingLengthStallStack.getManualStalls().contains(testStall1));
        assertTrue(existingLengthStallStack.getManualStalls().contains(testStall2));
        assertTrue(existingLengthStallStack.getManualStalls().contains(testStall3));
        assertTrue(existingLengthStallStack.getManualStalls().contains(testStall4));

        //Check positions of all the manual stalls
        // (should have filled the stall from the bottom)
        assertEquals(0, testStall0.getMinX(), 0);
        assertEquals(vehicleWidth*4, testStall0.getMinY(), 0);
        assertEquals(vehicleWidth*5, testStall0.getMaxY(), 0);

        assertEquals(0, testStall1.getMinX(), 0);
        assertEquals(vehicleWidth*3, testStall1.getMinY(), 0);
        assertEquals(vehicleWidth*4, testStall1.getMaxY(), 0);

        assertEquals(0, testStall2.getMinX(), 0);
        assertEquals(vehicleWidth*2, testStall2.getMinY(), 0);
        assertEquals(vehicleWidth*3, testStall2.getMaxY(), 0);

        assertEquals(0, testStall3.getMinX(), 0);
        assertEquals(vehicleWidth*1, testStall3.getMinY(), 0);
        assertEquals(vehicleWidth*2, testStall3.getMaxY(), 0);

        assertEquals(0, testStall4.getMinX(), 0);
        assertEquals(vehicleWidth*0, testStall4.getMinY(), 0);
        assertEquals(vehicleWidth*1, testStall4.getMaxY(), 0);

    }

    @Test
    public void testRemoveManualStall(){
        double vehicleWidth = stallStackHeight/5;
        double vehicleLength = maxStallLength;
        StallInfo stallInfo = new StallInfo(vehicleWidth, vehicleLength, StallType.NoPaddingTest);
        ManualStall testStall = existingLengthStallStack.addManualStall(stallInfo);

        assertTrue(existingLengthStallStack.getManualStalls().contains(testStall));
        existingLengthStallStack.removeManualStall(testStall.getName());
        assertFalse(existingLengthStallStack.getManualStalls().contains(testStall));

    }

    @Test
    public void testFillGapExactInMiddleOfStackAllSpacesSameSizeAndExactLength(){
        double vehicleWidth = stallStackHeight/5;
        double vehicleLength = maxStallLength;
        StallInfo stallInfo = new StallInfo(vehicleWidth, vehicleLength, StallType.NoPaddingTest);
        existingLengthStallStack.addManualStall(stallInfo);
        existingLengthStallStack.addManualStall(stallInfo);
        existingLengthStallStack.addManualStall(stallInfo);
        ManualStall stallToRemove = existingLengthStallStack.addManualStall(stallInfo);
        existingLengthStallStack.addManualStall(stallInfo);

        // Create gap
        existingLengthStallStack.removeManualStall(stallToRemove.getName());

        ManualStall stallToFitInGap = existingLengthStallStack.addManualStall(stallInfo);

        assertEquals(maxStallLength, stallToFitInGap.getLength(),0);

        assertTrue(existingLengthStallStack.getManualStalls().contains(stallToFitInGap));

        //Check position of the manual stall which should have been added in the gap
        assertEquals(0, stallToFitInGap.getMinX(), 0);
        assertEquals(vehicleWidth*3, stallToFitInGap.getMinY(), 0);
        assertEquals(vehicleWidth*4, stallToFitInGap.getMaxY(), 0);

    }

    @Test
    public void testFillGapLargeInMiddleOfStackAllSpacesSameSizeAndExactLength(){
        double vehicleWidth = stallStackHeight/5;
        double vehicleLength = maxStallLength;
        StallInfo stallInfo = new StallInfo(vehicleWidth, vehicleLength, StallType.NoPaddingTest);
        existingLengthStallStack.addManualStall(stallInfo);
        existingLengthStallStack.addManualStall(stallInfo);
        ManualStall stallToRemove0 = existingLengthStallStack.addManualStall(stallInfo);
        ManualStall stallToRemove1 = existingLengthStallStack.addManualStall(stallInfo);
        existingLengthStallStack.addManualStall(stallInfo);

        // Create gap
        existingLengthStallStack.removeManualStall(stallToRemove0.getName());
        existingLengthStallStack.removeManualStall(stallToRemove1.getName());

        ManualStall stallToFitInGap = existingLengthStallStack.addManualStall(stallInfo);

        assertEquals(maxStallLength, stallToFitInGap.getLength(),0);

        assertTrue(existingLengthStallStack.getManualStalls().contains(stallToFitInGap));

        //Check position of the manual stall which should have been added in the gap
        assertEquals(0, stallToFitInGap.getMinX(), 0);
        assertEquals(vehicleWidth*2, stallToFitInGap.getMinY(), 0);
        assertEquals(vehicleWidth*3, stallToFitInGap.getMaxY(), 0);

    }

    @Test
    public void testCantFillGapSmallInMiddleOfStackAllSpacesSameSizeAndExactLength(){
        double vehicleWidth = stallStackHeight/5;
        double vehicleLargeWidth = stallStackHeight/4;
        double vehicleLength = maxStallLength;
        StallInfo stallInfo = new StallInfo(vehicleWidth, vehicleLength, StallType.NoPaddingTest);
        StallInfo stallInfoTooWide = new StallInfo(vehicleLargeWidth, vehicleLength, StallType.NoPaddingTest);
        existingLengthStallStack.addManualStall(stallInfo);
        existingLengthStallStack.addManualStall(stallInfo);
        ManualStall stallToRemove0 = existingLengthStallStack.addManualStall(stallInfo);
        existingLengthStallStack.addManualStall(stallInfo);
        existingLengthStallStack.addManualStall(stallInfo);

        // Create gap
        existingLengthStallStack.removeManualStall(stallToRemove0.getName());

        ManualStall stallToFitInGap = existingLengthStallStack.addManualStall(stallInfoTooWide);

        assertEquals(null, stallToFitInGap);
    }

    @Test
    public void testFillEmptyStallStackExactlyWithDifferentWidthsExactLength(){
        double vehicleWidth0 = 2;
        double vehicleWidth1 = 3;
        double vehicleLength = maxStallLength;
        StallInfo stallInfo0 = new StallInfo(vehicleWidth0, vehicleLength, StallType.NoPaddingTest);
        StallInfo stallInfo1 = new StallInfo(vehicleWidth1, vehicleLength, StallType.NoPaddingTest);
        ManualStall testStall0 = existingLengthStallStack.addManualStall(stallInfo0);
        ManualStall testStall1 = existingLengthStallStack.addManualStall(stallInfo0);
        ManualStall testStall2 = existingLengthStallStack.addManualStall(stallInfo1);
        ManualStall testStall3 = existingLengthStallStack.addManualStall(stallInfo1);
        ManualStall testStall4 = existingLengthStallStack.addManualStall(stallInfo0);

        assertEquals(vehicleLength, testStall0.getLength(),0);
        assertEquals(vehicleLength, testStall1.getLength(),0);
        assertEquals(vehicleLength, testStall2.getLength(),0);
        assertEquals(vehicleLength, testStall3.getLength(),0);
        assertEquals(null, testStall4);

        assertTrue(existingLengthStallStack.getManualStalls().contains(testStall0));
        assertTrue(existingLengthStallStack.getManualStalls().contains(testStall1));
        assertTrue(existingLengthStallStack.getManualStalls().contains(testStall2));
        assertTrue(existingLengthStallStack.getManualStalls().contains(testStall3));

        //Check positions of all the manual stalls
        // (should have filled the stall from the bottom)
        assertEquals(0, testStall0.getMinX(), 0);
        assertEquals(vehicleWidth0*0, testStall0.getMinY(), 0);
        assertEquals(vehicleWidth0*1, testStall0.getMaxY(), 0);

        assertEquals(0, testStall1.getMinX(), 0);
        assertEquals(vehicleWidth0*1, testStall1.getMinY(), 0);
        assertEquals(vehicleWidth0*2, testStall1.getMaxY(), 0);

        assertEquals(0, testStall2.getMinX(), 0);
        assertEquals(existingLengthStallStack.getBounds().getMaxY()-vehicleWidth1*1, testStall2.getMinY(), 0);
        assertEquals(existingLengthStallStack.getBounds().getMaxY()-vehicleWidth1*0, testStall2.getMaxY(), 0);

        assertEquals(0, testStall3.getMinX(), 0);
        assertEquals(existingLengthStallStack.getBounds().getMaxY()-vehicleWidth1*2, testStall3.getMinY(), 0);
        assertEquals(existingLengthStallStack.getBounds().getMaxY()-vehicleWidth1*1, testStall3.getMaxY(), 0);
    }

    //TODO StallStack Tests for the following:
    // Gaps in stall stack filled or not (too large gaps, too small gaps, exact size gaps)
    // Mixed sizes added to an empty stall

    @After
    public void testTearDown(){
        existingLengthStallStack = null;
        existingLengthStallStackRoadRight = null;
        noLengthStallStack = null;
    }

    @AfterClass
    public static void classTearDown(){

    }
}
