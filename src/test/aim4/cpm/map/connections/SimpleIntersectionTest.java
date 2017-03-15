package aim4.cpm.map.connections;

import aim4.map.Road;
import aim4.map.connections.SimpleIntersection;
import aim4.map.cpm.testmaps.CPMMapIntersection3Roads;
import aim4.map.cpm.testmaps.CPMMapWithSimpleIntersection;
import aim4.map.lane.Lane;
import org.junit.Test;

import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SimpleIntersectionTest {
    CPMMapWithSimpleIntersection map2Roads = new CPMMapWithSimpleIntersection(4, // laneWidth
            10.0, // speedLimit
            0.0, // initTime
            500, // width
            500); // height
    SimpleIntersection testIntersection2Roads = map2Roads.getIntersections().get(0);

    CPMMapIntersection3Roads map3Roads = new CPMMapIntersection3Roads(4, // laneWidth
            10.0, // speedLimit
            0.0, // initTime
            500, // width
            500); // height
    SimpleIntersection testIntersection3Roads = map3Roads.getIntersections().get(0);


    @Test
    public void testGetRoads() throws Exception {
        List<Road> roads = testIntersection2Roads.getRoads();
        // There should be 2 roads involved
        int size = roads.size();
        assertEquals(2, size);

        roads = testIntersection3Roads.getRoads();
        // There should be 3 roads involved
        size = roads.size();
        assertEquals(3, size);
    }

    @Test
    public void testGetLanes() throws Exception {
        List<Lane> lanes = testIntersection2Roads.getLanes();
        // There should be 2 lanes involved
        int size = lanes.size();
        assertEquals(2, size);

        lanes = testIntersection3Roads.getLanes();
        // There should be 3 lanes involved
        size = lanes.size();
        assertEquals(3, size);
    }

    @Test
    public void testGetArea() throws Exception {
        Area area = testIntersection2Roads.getArea();
        assert(area.isRectangular());

        area = testIntersection3Roads.getArea();
        assert(area.isRectangular());
    }

    @Test
    public void testGetCentroid() throws Exception {
        // TODO CPM Write a test for this
    }

    @Test
    public void testGetEntryRoads() throws Exception {
        List<Road> roads = testIntersection2Roads.getEntryRoads();

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

        roads = testIntersection3Roads.getEntryRoads();

        // There should be 2 entry roads
        size = roads.size();
        assertEquals(2, size);

        // The roads should be east and south
        entryRoadNames = new ArrayList<String>();
        for (Road road : roads){
            entryRoadNames.add(road.getName());
        }
        assert(entryRoadNames.contains("Southbound Avenue"));
        assert(entryRoadNames.contains("Eastbound Entry Avenue"));
    }

    @Test
    public void testGetEntryLanes() throws Exception {
        List<Lane> entryLanes = testIntersection2Roads.getEntryLanes();

        // There should be 2 entry lanes
        assert(entryLanes.size() == 2);

        // They should include both east and south
        Lane entryLane1 = map2Roads.getRoadByName("Eastbound Avenue").getOnlyLane();
        Lane entryLane2 = map2Roads.getRoadByName("Southbound Avenue").getOnlyLane();

        assert(entryLanes.contains(entryLane1));
        assert(entryLanes.contains(entryLane2));

        entryLanes = testIntersection3Roads.getEntryLanes();

        // There should be 2 entry lanes
        assert(entryLanes.size() == 2);

        // They should include both east and south
        entryLane1 = map3Roads.getRoadByName("Eastbound Entry Avenue").getOnlyLane();
        entryLane2 = map3Roads.getRoadByName("Southbound Avenue").getOnlyLane();

        assert(entryLanes.contains(entryLane1));
        assert(entryLanes.contains(entryLane2));
    }

    @Test
    public void testIsEnteredBy() throws Exception {
        Road entryRoad = map2Roads.getRoadByName("Eastbound Avenue");
        Lane entryLane = entryRoad.getOnlyLane();
        assert(testIntersection2Roads.isEnteredBy(entryLane));

        entryRoad = map2Roads.getRoadByName("Southbound Avenue");
        entryLane = entryRoad.getOnlyLane();
        assert(testIntersection2Roads.isEnteredBy(entryLane));

        entryRoad = map3Roads.getRoadByName("Eastbound Entry Avenue");
        entryLane = entryRoad.getOnlyLane();
        assert(testIntersection3Roads.isEnteredBy(entryLane));

        entryRoad = map3Roads.getRoadByName("Southbound Avenue");
        entryLane = entryRoad.getOnlyLane();
        assert(testIntersection3Roads.isEnteredBy(entryLane));
    }

    @Test
    public void testGetEntryPoint() throws Exception {
        Road entryRoad = map2Roads.getRoadByName("Eastbound Avenue");
        Lane entryLane = entryRoad.getOnlyLane();
        Point2D entryPoint = testIntersection2Roads.getEntryPoint(entryLane);

        assertEquals(248.0, entryPoint.getX(), 0.01);
        assertEquals(250.0, entryPoint.getY(), 0.01);

        entryRoad = map2Roads.getRoadByName("Southbound Avenue");
        entryLane = entryRoad.getOnlyLane();
        entryPoint = testIntersection2Roads.getEntryPoint(entryLane);

        assertEquals(250.0, entryPoint.getX(), 0.01);
        assertEquals(252.0, entryPoint.getY(), 0.01);

        entryRoad = map3Roads.getRoadByName("Eastbound Entry Avenue");
        entryLane = entryRoad.getOnlyLane();
        entryPoint = testIntersection3Roads.getEntryPoint(entryLane);

        assertEquals(248.0, entryPoint.getX(), 0.01);
        assertEquals(250.0, entryPoint.getY(), 0.01);

        entryRoad = map3Roads.getRoadByName("Southbound Avenue");
        entryLane = entryRoad.getOnlyLane();
        entryPoint = testIntersection3Roads.getEntryPoint(entryLane);

        assertEquals(250.0, entryPoint.getX(), 0.01);
        assertEquals(252.0, entryPoint.getY(), 0.01);
    }

    @Test
    public void testGetEntryHeading() throws Exception {
        Road entryRoad = map2Roads.getRoadByName("Eastbound Avenue");
        Lane entryLane = entryRoad.getOnlyLane();
        double expectedEntryHeading = 0.0;
        double actualEntryHeading = testIntersection2Roads.getEntryHeading(entryLane);
        assertEquals(expectedEntryHeading, actualEntryHeading, 0.01);

        entryRoad = map2Roads.getRoadByName("Southbound Avenue");
        entryLane = entryRoad.getOnlyLane();
        expectedEntryHeading = Math.toRadians(270);
        actualEntryHeading = testIntersection2Roads.getEntryHeading(entryLane);
        assertEquals(expectedEntryHeading, actualEntryHeading, 0.01);

        entryRoad = map3Roads.getRoadByName("Eastbound Entry Avenue");
        entryLane = entryRoad.getOnlyLane();
        expectedEntryHeading = 0.0;
        actualEntryHeading = testIntersection3Roads.getEntryHeading(entryLane);
        assertEquals(expectedEntryHeading, actualEntryHeading, 0.01);

        entryRoad = map3Roads.getRoadByName("Southbound Avenue");
        entryLane = entryRoad.getOnlyLane();
        expectedEntryHeading = Math.toRadians(270);
        actualEntryHeading = testIntersection3Roads.getEntryHeading(entryLane);
        assertEquals(expectedEntryHeading, actualEntryHeading, 0.01);
    }

    @Test
    public void testGetExitRoads() throws Exception {
        List<Road> roads = testIntersection2Roads.getExitRoads();

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

        roads = testIntersection3Roads.getExitRoads();

        // There should be 2 exits
        size = roads.size();
        assertEquals(2, size);

        // The exits road should be eastbound and southbound
        exitRoadNames = new ArrayList<String>();
        for (Road road : roads){
            exitRoadNames.add(road.getName());
        }
        assert(exitRoadNames.contains("Southbound Avenue"));
        assert(exitRoadNames.contains("Eastbound Exit Avenue"));
    }

    @Test
    public void testGetExitLanes() throws Exception {
        List<Lane> exitLanes = testIntersection2Roads.getExitLanes();

        // There should be 2 exit lanes
        assert(exitLanes.size() == 2);

        // They should include both east and south
        Lane exitLane1 = map2Roads.getRoadByName("Eastbound Avenue").getOnlyLane();
        Lane exitLane2 = map2Roads.getRoadByName("Southbound Avenue").getOnlyLane();

        assert(exitLanes.contains(exitLane1));
        assert(exitLanes.contains(exitLane2));

        exitLanes = testIntersection3Roads.getExitLanes();

        // There should be 2 exit lanes
        assert(exitLanes.size() == 2);

        // They should include both east and south
        exitLane1 = map3Roads.getRoadByName("Eastbound Exit Avenue").getOnlyLane();
        exitLane2 = map3Roads.getRoadByName("Southbound Avenue").getOnlyLane();

        assert(exitLanes.contains(exitLane1));
        assert(exitLanes.contains(exitLane2));
    }

    @Test
    public void testIsExitedBy() throws Exception {
        Road exitRoad = map2Roads.getRoadByName("Southbound Avenue");
        Lane exitLane = exitRoad.getOnlyLane();
        assert(testIntersection2Roads.isExitedBy(exitLane));

        exitRoad = map2Roads.getRoadByName("Eastbound Avenue");
        exitLane = exitRoad.getOnlyLane();
        assert(testIntersection2Roads.isExitedBy(exitLane));

        exitRoad = map3Roads.getRoadByName("Southbound Avenue");
        exitLane = exitRoad.getOnlyLane();
        assert(testIntersection3Roads.isExitedBy(exitLane));

        exitRoad = map3Roads.getRoadByName("Eastbound Exit Avenue");
        exitLane = exitRoad.getOnlyLane();
        assert(testIntersection3Roads.isExitedBy(exitLane));
    }

    @Test
    public void testGetExitPoint() throws Exception {
        Road exitRoad = map2Roads.getRoadByName("Southbound Avenue");
        Lane exitLane = exitRoad.getOnlyLane();
        Point2D exitPoint = testIntersection2Roads.getExitPoint(exitLane);

        assertEquals(250.0, exitPoint.getX(), 0.01);
        assertEquals(248.0, exitPoint.getY(), 0.01);

        exitRoad = map2Roads.getRoadByName("Eastbound Avenue");
        exitLane = exitRoad.getOnlyLane();
        exitPoint = testIntersection2Roads.getExitPoint(exitLane);

        assertEquals(252.0, exitPoint.getX(), 0.01);
        assertEquals(250.0, exitPoint.getY(), 0.01);

        exitRoad = map3Roads.getRoadByName("Southbound Avenue");
        exitLane = exitRoad.getOnlyLane();
        exitPoint = testIntersection3Roads.getExitPoint(exitLane);

        assertEquals(250.0, exitPoint.getX(), 0.01);
        assertEquals(248.0, exitPoint.getY(), 0.01);

        exitRoad = map3Roads.getRoadByName("Eastbound Exit Avenue");
        exitLane = exitRoad.getOnlyLane();
        exitPoint = testIntersection3Roads.getExitPoint(exitLane);

        assertEquals(252.0, exitPoint.getX(), 0.01);
        assertEquals(250.0, exitPoint.getY(), 0.01);
    }

    @Test
    public void testGetExitHeading() throws Exception {
        Road exitRoad = map2Roads.getRoadByName("Eastbound Avenue");
        Lane exitLane = exitRoad.getOnlyLane();
        double expectedExitHeading = Math.toRadians(0);
        double actualExitHeading = testIntersection2Roads.getExitHeading(exitLane);

        assertEquals(expectedExitHeading, actualExitHeading, 0.01);

        exitRoad = map2Roads.getRoadByName("Southbound Avenue");
        exitLane = exitRoad.getOnlyLane();
        expectedExitHeading = Math.toRadians(270);
        actualExitHeading = testIntersection2Roads.getExitHeading(exitLane);

        assertEquals(expectedExitHeading, actualExitHeading, 0.01);

        exitRoad = map3Roads.getRoadByName("Eastbound Exit Avenue");
        exitLane = exitRoad.getOnlyLane();
        expectedExitHeading = Math.toRadians(0);
        actualExitHeading = testIntersection3Roads.getExitHeading(exitLane);

        assertEquals(expectedExitHeading, actualExitHeading, 0.01);

        exitRoad = map3Roads.getRoadByName("Southbound Avenue");
        exitLane = exitRoad.getOnlyLane();
        expectedExitHeading = Math.toRadians(270);
        actualExitHeading = testIntersection3Roads.getExitHeading(exitLane);

        assertEquals(expectedExitHeading, actualExitHeading, 0.01);
    }

    @Test
    public void testCalcTurnDirection() throws Exception {
        // TODO CPM Write test for this
    }
}