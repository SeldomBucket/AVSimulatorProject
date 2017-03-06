package aim4.map.cpm.testmaps;

import aim4.im.RoadBasedIntersection;
import aim4.map.DataCollectionLine;
import aim4.map.Road;
import aim4.map.connections.Corner;
import aim4.map.SpawnPoint;
import aim4.map.connections.Junction;
import aim4.map.connections.SimpleIntersection;
import aim4.map.cpm.CPMBasicMap;
import aim4.map.cpm.CPMMap;
import aim4.map.lane.Lane;
import aim4.map.lane.LineSegmentLane;
import aim4.util.ArrayListRegistry;
import aim4.util.Registry;
import aim4.vehicle.VinRegistry;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

/**
 * Map with a conventional intersection, four roads intersecting
 * (as in AIM simulation). Class created to test if we can
 * instantiate RoadBasedIntersection.
 */
public class CPMMapWithSimpleIntersection extends CPMBasicMap {


    /**
     * Create a map with an intersection where only 2 roads cross over
     * in the centre of the map, and each road has only one lane.
     */
    public CPMMapWithSimpleIntersection(int laneWidth, double speedLimit,
                                        double initTime, double width,
                                        double height) {
        super(laneWidth, speedLimit, initTime, width, height);

        // Set size of array for the data collection lines.
        // One on entry and one on exit
        dataCollectionLines = new ArrayList<DataCollectionLine>(2);

        // Create the vertical Road
        //SOUTH
        Road southBoundRoad = new Road("Southbound Avenue", this);

        // Add a lane to the road
        // Need to find the centre of the lane before creating it
        double x1 = width/2;
        double y1 = height;
        double x2 = x1;
        double y2 = 0;
        Lane southLane = new LineSegmentLane(x1,
                y1,
                x2,
                y2,
                laneWidth, // width
                speedLimit);
        int southLaneId = laneRegistry.register(southLane);
        southLane.setId(southLaneId);
        southBoundRoad.addTheRightMostLane(southLane);
        laneToRoad.put(southLane, southBoundRoad);
        exitLanes.add(southLane);

        verticalRoads.add(southBoundRoad);

        // Create the horizontal Roads
        // EAST
        Road eastBoundRoad = new Road("Eastbound Avenue", this);

        // Add a lane to the road
        // Need to find the centre of the lane before creating it
        x1 = 0;
        y1 = height/2;
        x2 = width;
        y2 = y1;
        Lane eastLane = new LineSegmentLane(x1, // x1
                y1, // y1
                x2, // x2
                y2, // y2
                laneWidth, // width
                speedLimit);
        int eastLaneId = laneRegistry.register(eastLane);
        eastLane.setId(eastLaneId);
        eastBoundRoad.addTheRightMostLane(eastLane);
        laneToRoad.put(eastLane, eastBoundRoad);
        entranceLane = eastLane;
        exitLanes.add(eastLane);

        horizontalRoads.add(eastBoundRoad);

        // store all the roads we've created
        roads = new ArrayList<Road>(horizontalRoads);
        roads.addAll(verticalRoads);
        roads = Collections.unmodifiableList(roads);

        // generate the data collection lines
        dataCollectionLines.add(
                new DataCollectionLine(
                        "Entrance on Eastbound",
                        dataCollectionLines.size(),
                        new Point2D.Double(BORDER, ((height/2)+halfLaneWidth)),
                        new Point2D.Double(BORDER, ((height/2)-halfLaneWidth)),
                        true));
        dataCollectionLines.add(
                new DataCollectionLine(
                        "Exit on Eastbound",
                        dataCollectionLines.size(),
                        new Point2D.Double((width-BORDER), ((height/2)+halfLaneWidth)),
                        new Point2D.Double((width-BORDER), ((height/2)-halfLaneWidth)),
                        true));
        dataCollectionLines.add(
                new DataCollectionLine(
                        "Exit on Southbound",
                        dataCollectionLines.size(),
                        new Point2D.Double(((width/2)-halfLaneWidth), BORDER),
                        new Point2D.Double(((width/2)+halfLaneWidth), BORDER),
                        true));

        // Now we have created the roads, we need to create the intersection
        List<Road> roadsForIntersection = new ArrayList<Road>(3);
        roadsForIntersection.add(eastBoundRoad);
        roadsForIntersection.add(southBoundRoad);
        SimpleIntersection intersection1 = new SimpleIntersection(roadsForIntersection);
        intersections.add(intersection1);

        initializeSpawnPoints(initTime);
    }
}
