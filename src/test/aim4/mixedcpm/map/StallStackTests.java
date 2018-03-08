package aim4.mixedcpm.map;

import aim4.map.mixedcpm.parking.ManualStall;
import aim4.map.mixedcpm.parking.StallInfo;
import aim4.map.mixedcpm.parking.StallStack;

import aim4.map.mixedcpm.parking.StallTypes;
import org.junit.runners.Parameterized;
import util.mixedcpm.MockManualParkingRoad;

import org.junit.*;

import java.util.Collection;

import static org.junit.Assert.*;

public class StallStackTests {

    StallStack stallStack;
    double stallStackHeight = 10;
    double maxStallLength = 5;

    @BeforeClass
    public static void classSetUp(){

    }

    @Before
    public void testSetUp(){

        MockManualParkingRoad parkingRoad = new MockManualParkingRoad();
        stallStack = new StallStack(0,
                                    0,
                                    stallStackHeight,
                                    maxStallLength,
                                    false,
                                    parkingRoad);
    }

    @Test
    public void testStackInitialisation(){
        assertEquals(0, stallStack.getBounds().getMinX(),0);
        assertEquals(maxStallLength, stallStack.getBounds().getMaxX(),0);
        assertEquals(0, stallStack.getBounds().getMinY(),0);
        assertEquals(stallStackHeight, stallStack.getBounds().getMaxY(),0);
    }

    @Test
    public void testAddFirstSpaceExactLength(){
        double vehicleWidth = stallStackHeight/5;
        double vehicleLength = maxStallLength;
        StallInfo stallInfo = new StallInfo(vehicleWidth, vehicleLength, StallTypes.NoPaddingTest);
        ManualStall testStall = stallStack.addManualStall(stallInfo);
        assertEquals(maxStallLength, testStall.getLength(),0);
    }

    @Test
    public void testAddFirstSpaceTooShort(){
        double vehicleWidth = stallStackHeight/5;
        double vehicleLength = maxStallLength/2;
        StallInfo stallInfo = new StallInfo(vehicleWidth, vehicleLength, StallTypes.NoPaddingTest);
        ManualStall testStall = stallStack.addManualStall(stallInfo);
        assertEquals(maxStallLength/2, testStall.getLength(),0);
    }


    @Test
    public void testAddFirstSpaceTooLong(){
        double vehicleWidth = stallStackHeight/5;
        double vehicleLength = maxStallLength*2;
        StallInfo stallInfo = new StallInfo(vehicleWidth, vehicleLength, StallTypes.NoPaddingTest);
        ManualStall testStall = stallStack.addManualStall(stallInfo);
        assertEquals(null, testStall);
    }

    //TODO StallStack Tests for the following:
    // Space too wide
    // Second space exact length/too short/too long

    @After
    public void testTearDown(){

    }

    @AfterClass
    public static void classTearDown(){

    }
}
