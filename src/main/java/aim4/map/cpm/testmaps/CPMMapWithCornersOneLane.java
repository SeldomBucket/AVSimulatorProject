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
 * Map with 2 corners. Roads make backwards C shape.
 * Roads only have 1 lane.
 */
public class CPMMapWithCornersOneLane extends CPMBasicMap {



    /**
     * Create a very simple map.
     * For now, have 3 roads in backwards C shape.
     */
    public CPMMapWithCornersOneLane(int laneWidth, double speedLimit,
                                    double initTime, double width,
                                    double height) {
        super(laneWidth, speedLimit, initTime, width, height);

        // Set size of array for the data collection lines.
        // One on entry and one on exit
        dataCollectionLines = new ArrayList<DataCollectionLine>(2);

        // Create the vertical Road
        // SOUTH
        Road southBoundRoad = new Road("Southbound Avenue", this);

        // Add a lane to the road
        // Need to find the centre of the lane before creating it
        double x1 = width - BORDER;
        double y1 = height - BORDER;
        double x2 = x1;
        double y2 = BORDER;
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

        verticalRoads.add(southBoundRoad);

        // Create the horizontal Roads
        // WEST
        Road westBoundRoad = new Road("Westbound Avenue", this);

        // Add a lane to the road
        // Need to find the centre of the lane before creating it
        x1 = width - BORDER;
        y1 = BORDER + halfLaneWidth;
        x2 = 0;
        y2 = y1;
        Lane westLane = new LineSegmentLane(x1, // x1
                y1, // y1
                x2, // x2
                y2, // y2
                laneWidth, // width
                speedLimit);
        int westLaneId = laneRegistry.register(westLane);
        westLane.setId(westLaneId);
        westBoundRoad.addTheRightMostLane(westLane);
        laneToRoad.put(westLane, westBoundRoad);
        exitLanes.add(westLane);

        horizontalRoads.add(westBoundRoad);

        // EAST
        Road eastBoundRoad = new Road("Eastbound Avenue", this);

        // Add a lane to the road
        // Need to find the centre of the lane before creating it
        x1 = 0;
        y1 = height - BORDER - halfLaneWidth;
        x2 = width - BORDER;
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

        horizontalRoads.add(eastBoundRoad);

        roads = new ArrayList<Road>(horizontalRoads);
        roads.addAll(verticalRoads);
        roads = Collections.unmodifiableList(roads);

        // generate the data collection lines
        dataCollectionLines.add(
                new DataCollectionLine(
                        "Entrance on Eastbound",
                        dataCollectionLines.size(),
                        new Point2D.Double(BORDER, (height-BORDER)),
                        new Point2D.Double(BORDER, (height-BORDER-laneWidth)),
                        true));
        dataCollectionLines.add(
                new DataCollectionLine(
                        "Exit on Westbound",
                        dataCollectionLines.size(),
                        new Point2D.Double(BORDER, BORDER),
                        new Point2D.Double(BORDER, (BORDER+laneWidth)),
                        true));

        // Now we can create corners where roads meet.
        makeCorner(eastBoundRoad, southBoundRoad);
        makeCorner(southBoundRoad, westBoundRoad);

        initializeSpawnPoints(initTime);
    }
}
