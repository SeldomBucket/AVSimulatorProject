package aim4.map.connections;

import aim4.map.Road;
import aim4.map.cpm.testmaps.CPMMapWithTJunction;
import aim4.map.lane.Lane;
import org.junit.Test;

import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class JunctionTest {
     CPMMapWithTJunction map = new CPMMapWithTJunction(4, // laneWidth
            10.0, // speedLimit
            0.0, // initTime
            500, // width
            500); // height
    Junction testJunction = map.getJunctions().get(0);

    @Test
    public void testGetEntryLanes() throws Exception {
        List<Lane> entryLanes = testJunction.getEntryLanes();

        // There should be one entry lane
        int size = entryLanes.size();
        assertEquals(1, size);
    }

    @Test
    public void testGetExitLanes() throws Exception {
        List<Lane> exitLanes = testJunction.getExitLanes();

        // There should be two exit lanes
        int size = exitLanes.size();
        assertEquals(2, size);
    }

    @Test
    public void testGetRoads() throws Exception {
        List<Road> roads = testJunction.getRoads();

        // There should be 2 roads involved
        int size = roads.size();
        assertEquals(2, size);
    }

    @Test
    public void testGetLanes() throws Exception {
        List<Lane> lanes = testJunction.getLanes();

        // There should be 2 lanes involved
        int size = lanes.size();
        assertEquals(2, size);
    }

    @Test
    public void testGetArea() throws Exception {
        Area area = testJunction.getArea();

        assert(area.isRectangular());
    }

    @Test
    public void testGetCentroid() throws Exception {
        // TODO find out what this actually does.
    }

    @Test
    public void testGetEntryRoads() throws Exception {
        List<Road> roads = testJunction.getEntryRoads();

        // There should be only 1 entry
        int size = roads.size();
        assertEquals(1, size);

        // The road should be eastbound
        String roadName = roads.get(0).getName();
        assert(roadName.equals("Eastbound Avenue"));
    }

    @Test
    public void testIsEnteredBy() throws Exception {
        Road entryRoad = map.getRoadByName("Eastbound Avenue");
        Lane entryLane = entryRoad.getOnlyLane();
        assert(testJunction.isEnteredBy(entryLane));

        Road exitRoad = map.getRoadByName("Northbound Avenue");
        Lane exitLane = exitRoad.getOnlyLane();
        assertFalse(testJunction.isEnteredBy(exitLane));
    }

    @Test
    public void testGetEntryPoint() throws Exception {
        Road entryRoad = map.getRoadByName("Eastbound Avenue");
        Lane entryLane = entryRoad.getOnlyLane();
        Point2D entryPoint = testJunction.getEntryPoint(entryLane);

        assertEquals(248.0, entryPoint.getX(), 0.01);
        assertEquals(250.0, entryPoint.getY(), 0.01);
    }

    @Test
    public void testGetEntryHeading() throws Exception {
        // Enter on east
        Road entryRoad = map.getRoadByName("Eastbound Avenue");
        Lane entryLane = entryRoad.getOnlyLane();
        double expectedEntryHeading = 0.0;
        double actualEntryHeading = testJunction.getEntryHeading(entryLane);

        assertEquals(expectedEntryHeading, actualEntryHeading, 0.01);
    }

    @Test
    public void testGetExitRoads() throws Exception {
        List<Road> roads = testJunction.getExitRoads();

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
    }

    @Test
    public void testIsExitedBy() throws Exception {
        Road exitRoad = map.getRoadByName("Northbound Avenue");
        Lane exitLane = exitRoad.getOnlyLane();
        assert(testJunction.isExitedBy(exitLane));

        exitRoad = map.getRoadByName("Eastbound Avenue");
        exitLane = exitRoad.getOnlyLane();
        assert(testJunction.isExitedBy(exitLane));
    }

    @Test
    public void testGetExitPoint() throws Exception {
        Road exitRoad = map.getRoadByName("Northbound Avenue");
        Lane exitLane = exitRoad.getOnlyLane();
        Point2D exitPoint = testJunction.getExitPoint(exitLane);

        assertEquals(250.0, exitPoint.getX(), 0.01);
        assertEquals(252.0, exitPoint.getY(), 0.01);

        exitRoad = map.getRoadByName("Eastbound Avenue");
        exitLane = exitRoad.getOnlyLane();
        exitPoint = testJunction.getExitPoint(exitLane);

        assertEquals(252.0, exitPoint.getX(), 0.01);
        assertEquals(250.0, exitPoint.getY(), 0.01);
    }

    @Test
    public void testGetExitHeading() throws Exception {
        Road exitRoad = map.getRoadByName("Eastbound Avenue");
        Lane exitLane = exitRoad.getOnlyLane();
        double expectedExitHeading = Math.toRadians(0);
        double actualExitHeading = testJunction.getExitHeading(exitLane);

        assertEquals(expectedExitHeading, actualExitHeading, 0.01);

        exitRoad = map.getRoadByName("Northbound Avenue");
        exitLane = exitRoad.getOnlyLane();
        expectedExitHeading = Math.toRadians(90);
        actualExitHeading = testJunction.getExitHeading(exitLane);

        assertEquals(expectedExitHeading, actualExitHeading, 0.01);
    }

    @Test
    public void testCalcTurnDirection() throws Exception {

    }
}