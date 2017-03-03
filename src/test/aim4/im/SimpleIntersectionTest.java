package aim4.im;

import aim4.map.Road;
import aim4.map.connections.SimpleIntersection;
import aim4.map.cpm.testmaps.CPMMapWithSimpleIntersection;
import aim4.map.lane.Lane;
import org.junit.Test;

import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SimpleIntersectionTest {
    CPMMapWithSimpleIntersection map = new CPMMapWithSimpleIntersection(4, // laneWidth
            10.0, // speedLimit
            0.0, // initTime
            500, // width
            500); // height
    SimpleIntersection testIntersection = map.getIntersections().get(0);

    @Test
    public void testGetRoads() throws Exception {
        List<Road> roads = testIntersection.getRoads();

        // There should be 2 roads involved
        int size = roads.size();
        assertEquals(2, size);
    }

    @Test
    public void testGetLanes() throws Exception {
        List<Lane> lanes = testIntersection.getLanes();

        // There should be 2 lanes involved
        int size = lanes.size();
        assertEquals(2, size);
    }

    @Test
    public void testGetArea() throws Exception {
        Area area = testIntersection.getArea();

        assert(area.isRectangular());
    }

    @Test
    public void testGetCentroid() throws Exception {

    }

    @Test
    public void testGetEntryRoads() throws Exception {
        List<Road> roads = testIntersection.getEntryRoads();

        // There should be 2 entry roads
        int size = roads.size();
        assertEquals(2, size);

        // The roads should be east and south
        List<String> entryRoadNames = new ArrayList<String>();
        for (Road road : roads){
            entryRoadNames.add(road.getName());
        }
        assert(entryRoadNames.contains("Southbound Avenue"));
        assert(entryRoadNames.contains("Eastbound Avenue"));
    }

    @Test
    public void testGetEntryLanes() throws Exception {
        List<Lane> entryLanes = testIntersection.getEntryLanes();

        // There should be 2 entry lanes
        assert(entryLanes.size() == 2);

        // They should include both east and south
        Lane entryLane1 = map.getRoadByName("Eastbound Avenue").getLanes().get(0);
        Lane entryLane2 = map.getRoadByName("Southbound Avenue").getLanes().get(0);

        assert(entryLanes.contains(entryLane1));
        assert(entryLanes.contains(entryLane2));
    }

    @Test
    public void testIsEnteredBy() throws Exception {
        Road entryRoad = map.getRoadByName("Eastbound Avenue");
        Lane entryLane = entryRoad.getLanes().get(0);
        assert(testIntersection.isEnteredBy(entryLane));

        entryRoad = map.getRoadByName("Southbound Avenue");
        entryLane = entryRoad.getLanes().get(0);
        assert(testIntersection.isEnteredBy(entryLane));
    }

    @Test
    public void testGetEntryPoint() throws Exception {
        Road entryRoad = map.getRoadByName("Eastbound Avenue");
        Lane entryLane = entryRoad.getLanes().get(0);
        Point2D entryPoint = testIntersection.getEntryPoint(entryLane);

        assertEquals(248.0, entryPoint.getX(), 0.01);
        assertEquals(250.0, entryPoint.getY(), 0.01);

        entryRoad = map.getRoadByName("Southbound Avenue");
        entryLane = entryRoad.getLanes().get(0);
        entryPoint = testIntersection.getEntryPoint(entryLane);

        assertEquals(250.0, entryPoint.getX(), 0.01);
        assertEquals(252.0, entryPoint.getY(), 0.01);
    }

    @Test
    public void testGetEntryHeading() throws Exception {
        Road entryRoad = map.getRoadByName("Eastbound Avenue");
        Lane entryLane = entryRoad.getLanes().get(0);
        double expectedEntryHeading = 0.0;
        double actualEntryHeading = testIntersection.getEntryHeading(entryLane);
        assertEquals(expectedEntryHeading, actualEntryHeading, 0.01);

        entryRoad = map.getRoadByName("Southbound Avenue");
        entryLane = entryRoad.getLanes().get(0);
        expectedEntryHeading = Math.toRadians(270);
        actualEntryHeading = testIntersection.getEntryHeading(entryLane);
        assertEquals(expectedEntryHeading, actualEntryHeading, 0.01);
    }

    @Test
    public void testGetExitRoads() throws Exception {
        List<Road> roads = testIntersection.getExitRoads();

        // There should be 2 exits
        int size = roads.size();
        assertEquals(2, size);

        // The exits road should be eastbound and southbound
        List<String> exitRoadNames = new ArrayList<String>();
        for (Road road : roads){
            exitRoadNames.add(road.getName());
        }
        assert(exitRoadNames.contains("Southbound Avenue"));
        assert(exitRoadNames.contains("Eastbound Avenue"));
    }

    @Test
    public void testGetExitLanes() throws Exception {
        List<Lane> exitLanes = testIntersection.getExitLanes();

        // There should be 2 exit lanes
        assert(exitLanes.size() == 2);

        // They should include both east and south
        Lane exitLane1 = map.getRoadByName("Eastbound Avenue").getLanes().get(0);
        Lane exitLane2 = map.getRoadByName("Southbound Avenue").getLanes().get(0);

        assert(exitLanes.contains(exitLane1));
        assert(exitLanes.contains(exitLane2));
    }

    @Test
    public void testIsExitedBy() throws Exception {
        Road exitRoad = map.getRoadByName("Southbound Avenue");
        Lane exitLane = exitRoad.getLanes().get(0);
        assert(testIntersection.isExitedBy(exitLane));

        exitRoad = map.getRoadByName("Eastbound Avenue");
        exitLane = exitRoad.getLanes().get(0);
        assert(testIntersection.isExitedBy(exitLane));
    }

    @Test
    public void testGetExitPoint() throws Exception {
        Road exitRoad = map.getRoadByName("Southbound Avenue");
        Lane exitLane = exitRoad.getLanes().get(0);
        Point2D exitPoint = testIntersection.getExitPoint(exitLane);

        assertEquals(250.0, exitPoint.getX(), 0.01);
        assertEquals(248.0, exitPoint.getY(), 0.01);

        exitRoad = map.getRoadByName("Eastbound Avenue");
        exitLane = exitRoad.getLanes().get(0);
        exitPoint = testIntersection.getExitPoint(exitLane);

        assertEquals(252.0, exitPoint.getX(), 0.01);
        assertEquals(250.0, exitPoint.getY(), 0.01);
    }

    @Test
    public void testGetExitHeading() throws Exception {
        Road exitRoad = map.getRoadByName("Eastbound Avenue");
        Lane exitLane = exitRoad.getLanes().get(0);
        double expectedExitHeading = Math.toRadians(0);
        double actualExitHeading = testIntersection.getExitHeading(exitLane);

        assertEquals(expectedExitHeading, actualExitHeading, 0.01);

        exitRoad = map.getRoadByName("Southbound Avenue");
        exitLane = exitRoad.getLanes().get(0);
        expectedExitHeading = Math.toRadians(270);
        actualExitHeading = testIntersection.getExitHeading(exitLane);

        assertEquals(expectedExitHeading, actualExitHeading, 0.01);
    }

    @Test
    public void testCalcTurnDirection() throws Exception {

    }
}