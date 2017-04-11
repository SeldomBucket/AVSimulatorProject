package aim4.cpm.map.connections;

import aim4.map.Road;
import aim4.map.connections.Junction;
import aim4.map.cpm.testmaps.CPMMapJunction3Roads;
import aim4.map.cpm.testmaps.CPMMapWithTJunction;
import aim4.map.lane.Lane;
import org.junit.Test;

import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * TEST SUITE PURPOSE: Check that when 2 and 3 roads are given, a junction with the correct attributes is created.
 */
public class JunctionInitialisationTest {
     CPMMapWithTJunction map2Roads = new CPMMapWithTJunction(4, // laneWidth
            10.0, // speedLimit
            0.0, // initTime
            500, // width
            500); // height
    Junction testJunction2Roads = map2Roads.getJunctions().get(0);

    CPMMapJunction3Roads map3Roads = new CPMMapJunction3Roads(4, // laneWidth
            10.0, // speedLimit
            0.0, // initTime
            500, // width
            500); // height
    Junction testJunction3Roads = map3Roads.getJunctions().get(0);

    @Test
    public void testGetEntryLanes() throws Exception {
        List<Lane> entryLanes = testJunction2Roads.getEntryLanes();
        // There should be one entry lane
        int size = entryLanes.size();
        assertEquals(1, size);

        entryLanes = testJunction3Roads.getEntryLanes();
        // There should be one entry lane
        size = entryLanes.size();
        assertEquals(1, size);
    }

    @Test
    public void testGetExitLanes() throws Exception {
        List<Lane> exitLanes = testJunction2Roads.getExitLanes();
        // There should be two exit lanes
        int size = exitLanes.size();
        assertEquals(2, size);

        exitLanes = testJunction3Roads.getExitLanes();
        // There should be two exit lanes
        size = exitLanes.size();
        assertEquals(2, size);
    }

    @Test
    public void testGetRoads() throws Exception {
        List<Road> roads = testJunction2Roads.getRoads();
        // There should be 2 roads involved
        int size = roads.size();
        assertEquals(2, size);

        roads = testJunction3Roads.getRoads();
        // There should be 3 roads involved
        size = roads.size();
        assertEquals(3, size);
    }

    @Test
    public void testGetLanes() throws Exception {
        List<Lane> lanes = testJunction2Roads.getLanes();
        // There should be 2 lanes involved
        int size = lanes.size();
        assertEquals(2, size);

        lanes = testJunction3Roads.getLanes();
        // There should be 3 lanes involved
        size = lanes.size();
        assertEquals(3, size);
    }

    @Test
    public void testGetArea() throws Exception {
        Area area = testJunction2Roads.getArea();
        assert(area.isRectangular());

        area = testJunction3Roads.getArea();
        assert(area.isRectangular());
    }

    @Test
    public void testGetEntryRoads() throws Exception {
        List<Road> roads = testJunction2Roads.getEntryRoads();
        // There should be only 1 entry
        int size = roads.size();
        assertEquals(1, size);
        // The road should be eastbound
        String roadName = roads.get(0).getName();
        assert(roadName.equals("Eastbound Avenue"));

        roads = testJunction3Roads.getEntryRoads();
        // There should be only 1 entry
        size = roads.size();
        assertEquals(1, size);
        // The road should be eastbound
        roadName = roads.get(0).getName();
        assert(roadName.equals("Eastbound Entry Avenue"));
    }

    @Test
    public void testIsEnteredBy() throws Exception {
        Road entryRoad = map2Roads.getRoadByName("Eastbound Avenue");
        Lane entryLane = entryRoad.getOnlyLane();
        assert(testJunction2Roads.isEnteredBy(entryLane));

        Road exitRoad = map2Roads.getRoadByName("Northbound Avenue");
        Lane exitLane = exitRoad.getOnlyLane();
        assertFalse(testJunction2Roads.isEnteredBy(exitLane));

        entryRoad = map3Roads.getRoadByName("Eastbound Entry Avenue");
        entryLane = entryRoad.getOnlyLane();
        assert(testJunction3Roads.isEnteredBy(entryLane));

        exitRoad = map3Roads.getRoadByName("Northbound Avenue");
        exitLane = exitRoad.getOnlyLane();
        assertFalse(testJunction3Roads.isEnteredBy(exitLane));
    }

    @Test
    public void testGetEntryPoint() throws Exception {
        Road entryRoad = map2Roads.getRoadByName("Eastbound Avenue");
        Lane entryLane = entryRoad.getOnlyLane();
        Point2D entryPoint = testJunction2Roads.getEntryPoint(entryLane);

        assertEquals(248.0, entryPoint.getX(), 0.01);
        assertEquals(250.0, entryPoint.getY(), 0.01);

        entryRoad = map3Roads.getRoadByName("Eastbound Entry Avenue");
        entryLane = entryRoad.getOnlyLane();
        entryPoint = testJunction3Roads.getEntryPoint(entryLane);

        assertEquals(248.0, entryPoint.getX(), 0.01);
        assertEquals(250.0, entryPoint.getY(), 0.01);
    }

    @Test
    public void testGetEntryHeading() throws Exception {
        // Enter on east
        Road entryRoad = map2Roads.getRoadByName("Eastbound Avenue");
        Lane entryLane = entryRoad.getOnlyLane();
        double expectedEntryHeading = 0.0;
        double actualEntryHeading = testJunction2Roads.getEntryHeading(entryLane);

        assertEquals(expectedEntryHeading, actualEntryHeading, 0.01);

        // Enter on east
        entryRoad = map3Roads.getRoadByName("Eastbound Entry Avenue");
        entryLane = entryRoad.getOnlyLane();
        expectedEntryHeading = 0.0;
        actualEntryHeading = testJunction3Roads.getEntryHeading(entryLane);

        assertEquals(expectedEntryHeading, actualEntryHeading, 0.01);
    }

    @Test
    public void testGetExitRoads() throws Exception {
        List<Road> roads = testJunction2Roads.getExitRoads();

        // There should be 2 exits
        int size = roads.size();
        assertEquals(2, size);

        // The exits road should be eastbound and northbound
        List<String> exitRoadNames = new ArrayList<String>();
        for (Road road : roads){
            exitRoadNames.add(road.getName());
        }
        assert(exitRoadNames.contains("Northbound Avenue"));
        assert(exitRoadNames.contains("Eastbound Avenue"));

        roads = testJunction3Roads.getExitRoads();

        // There should be 2 exits
        size = roads.size();
        assertEquals(2, size);

        // The exits road should be eastbound and northbound
        exitRoadNames = new ArrayList<String>();
        for (Road road : roads){
            exitRoadNames.add(road.getName());
        }
        assert(exitRoadNames.contains("Northbound Avenue"));
        assert(exitRoadNames.contains("Eastbound Exit Avenue"));
    }

    @Test
    public void testIsExitedBy() throws Exception {
        Road exitRoad = map2Roads.getRoadByName("Northbound Avenue");
        Lane exitLane = exitRoad.getOnlyLane();
        assert(testJunction2Roads.isExitedBy(exitLane));

        exitRoad = map2Roads.getRoadByName("Eastbound Avenue");
        exitLane = exitRoad.getOnlyLane();
        assert(testJunction2Roads.isExitedBy(exitLane));

        exitRoad = map3Roads.getRoadByName("Northbound Avenue");
        exitLane = exitRoad.getOnlyLane();
        assert(testJunction3Roads.isExitedBy(exitLane));

        exitRoad = map3Roads.getRoadByName("Eastbound Exit Avenue");
        exitLane = exitRoad.getOnlyLane();
        assert(testJunction3Roads.isExitedBy(exitLane));
    }

    @Test
    public void testGetExitPoint() throws Exception {
        Road exitRoad = map2Roads.getRoadByName("Northbound Avenue");
        Lane exitLane = exitRoad.getOnlyLane();
        Point2D exitPoint = testJunction2Roads.getExitPoint(exitLane);

        assertEquals(250.0, exitPoint.getX(), 0.01);
        assertEquals(252.0, exitPoint.getY(), 0.01);

        exitRoad = map2Roads.getRoadByName("Eastbound Avenue");
        exitLane = exitRoad.getOnlyLane();
        exitPoint = testJunction2Roads.getExitPoint(exitLane);

        assertEquals(252.0, exitPoint.getX(), 0.01);
        assertEquals(250.0, exitPoint.getY(), 0.01);

        exitRoad = map3Roads.getRoadByName("Northbound Avenue");
        exitLane = exitRoad.getOnlyLane();
        exitPoint = testJunction3Roads.getExitPoint(exitLane);

        assertEquals(250.0, exitPoint.getX(), 0.01);
        assertEquals(252.0, exitPoint.getY(), 0.01);

        exitRoad = map3Roads.getRoadByName("Eastbound Exit Avenue");
        exitLane = exitRoad.getOnlyLane();
        exitPoint = testJunction3Roads.getExitPoint(exitLane);

        assertEquals(252.0, exitPoint.getX(), 0.01);
        assertEquals(250.0, exitPoint.getY(), 0.01);
    }

    @Test
    public void testGetExitHeading() throws Exception {
        Road exitRoad = map2Roads.getRoadByName("Eastbound Avenue");
        Lane exitLane = exitRoad.getOnlyLane();
        double expectedExitHeading = Math.toRadians(0);
        double actualExitHeading = testJunction2Roads.getExitHeading(exitLane);

        assertEquals(expectedExitHeading, actualExitHeading, 0.01);

        exitRoad = map2Roads.getRoadByName("Northbound Avenue");
        exitLane = exitRoad.getOnlyLane();
        expectedExitHeading = Math.toRadians(90);
        actualExitHeading = testJunction2Roads.getExitHeading(exitLane);

        assertEquals(expectedExitHeading, actualExitHeading, 0.01);

        exitRoad = map3Roads.getRoadByName("Eastbound Exit Avenue");
        exitLane = exitRoad.getOnlyLane();
        expectedExitHeading = Math.toRadians(0);
        actualExitHeading = testJunction3Roads.getExitHeading(exitLane);

        assertEquals(expectedExitHeading, actualExitHeading, 0.01);

        exitRoad = map3Roads.getRoadByName("Northbound Avenue");
        exitLane = exitRoad.getOnlyLane();
        expectedExitHeading = Math.toRadians(90);
        actualExitHeading = testJunction3Roads.getExitHeading(exitLane);

        assertEquals(expectedExitHeading, actualExitHeading, 0.01);
    }
}