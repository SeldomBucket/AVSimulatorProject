package aim4.cpm.map.parking;

import aim4.map.Road;
import aim4.map.cpm.testmaps.CPMMapWithParkingArea;
import aim4.map.cpm.parking.ParkingArea;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * TEST SUITE PURPOSE: Check that when a Parking Area is created, it has all the correct attributes.
 */
public class ParkingAreaInitialisationTest {
    CPMMapWithParkingArea map1Lane = new CPMMapWithParkingArea(4, // laneWidth
            10.0, // speedLimit
            0.0, // initTime
            1, // numberOfParkingLanes
            10, // parkingLength
            5); // accessLength

    CPMMapWithParkingArea map2Lanes = new CPMMapWithParkingArea(4, // laneWidth
            10.0, // speedLimit
            0.0, // initTime
            2, // numberOfParkingLanes
            10, // parkingLength
            5); // accessLength

    ParkingArea testParkingArea1Lane = map1Lane.getParkingArea();
    ParkingArea testParkingArea2Lanes = map2Lanes.getParkingArea();

    @Test
    public void testGetMap() {
        assert(testParkingArea1Lane.getMap() == map1Lane);
        assert(testParkingArea2Lanes.getMap() == map2Lanes);
    }

    @Test
     public void testGetParkingLength() {
        assertEquals(10, testParkingArea1Lane.getParkingLength(), 0.01);
        assertEquals(10, testParkingArea2Lanes.getParkingLength(), 0.01);
    }

    @Test
    public void testGetAccessLength() {
        assertEquals(5, testParkingArea1Lane.getAccessLength(), 0.01);
        assertEquals(5, testParkingArea2Lanes.getAccessLength(), 0.01);
    }

    @Test
    public void testGetOverlappingRoadWidth() {
        double expectedOverlappingRoadWidth1 = map1Lane.getLaneWidth();
        double expectedOverlappingRoadWidth2 = map2Lanes.getLaneWidth();

        assertEquals(expectedOverlappingRoadWidth1,
                testParkingArea1Lane.getOverlappingRoadWidth(), 0.01);
        assertEquals(expectedOverlappingRoadWidth2,
                testParkingArea2Lanes.getOverlappingRoadWidth(), 0.01);
    }

    @Test
    public void testGetTotalLength() {
        double expectedTotalLength = (2*4) + (2*5) + 10;

        assertEquals(expectedTotalLength, testParkingArea1Lane.getTotalLength(), 0.01);
        assertEquals(expectedTotalLength, testParkingArea2Lanes.getTotalLength(), 0.01);
    }

    @Test
    public void testGetNumberOfParkingLanes() {
        assert(testParkingArea1Lane.getNumberOfParkingLanes() == 1);
        assert(testParkingArea2Lanes.getNumberOfParkingLanes() == 2);
    }

    @Test
    public void testGetParkingLaneWidth() {
        assertEquals(4, testParkingArea1Lane.getParkingLaneWidth(), 0.01);
        assertEquals(4, testParkingArea2Lanes.getParkingLaneWidth(), 0.01);
    }

    @Test
    public void testGetEntryRoad() {
        Road expectedEntryRoad = map1Lane.getRoadByName("Parking road 0");
        assertEquals(expectedEntryRoad, testParkingArea1Lane.getEntryRoad());

        expectedEntryRoad = map2Lanes.getRoadByName("Parking road 0");
        assertEquals(expectedEntryRoad, testParkingArea2Lanes.getEntryRoad());
    }

    @Test
    public void testGetLastRoad() {
        Road expectedLastRoad = map1Lane.getRoadByName("Parking road 0");
        assertEquals(expectedLastRoad, testParkingArea1Lane.getLastRoad());

        expectedLastRoad = map2Lanes.getRoadByName("Parking road 1");
        assertEquals(expectedLastRoad, testParkingArea2Lanes.getLastRoad());
    }


}