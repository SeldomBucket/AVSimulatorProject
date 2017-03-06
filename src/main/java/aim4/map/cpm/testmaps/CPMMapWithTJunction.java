package aim4.map.cpm.testmaps;

import aim4.im.RoadBasedIntersection;
import aim4.map.*;
import aim4.map.connections.Corner;
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
 * Map with a T-Junction.
 * Class created to test if we can instantiate RoadBasedIntersection.
 */
public class CPMMapWithTJunction extends CPMBasicMap {

    /**
     * Create a map with a T-Junction.
     */
    public CPMMapWithTJunction(int laneWidth, double speedLimit,
                               double initTime, double width,
                               double height) {
        super(laneWidth, speedLimit, initTime, width, height);

        // Set size of array for the data collection lines.
        // One on entry and one on each exit
        dataCollectionLines = new ArrayList<DataCollectionLine>(3);

        // Create the vertical Roads

        //NORTH
        Road northBoundRoad = new Road("Northbound Avenue", this);

        // Add a lane to the road
        // Need to find the centre of the lane before creating it
        double x1 = width/2;
        double y1 = (height/2) - halfLaneWidth;
        double x2 = x1;
        double y2 = height;
        Lane northLane = new LineSegmentLane(x1,
                y1,
                x2,
                y2,
                laneWidth, // width
                speedLimit);
        int northLaneId = laneRegistry.register(northLane);
        northLane.setId(northLaneId);
        northBoundRoad.addTheRightMostLane(northLane);
        laneToRoad.put(northLane, northBoundRoad);
        exitLanes.add(northLane);

        entranceLane = northLane;

        verticalRoads.add(northBoundRoad);

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

        roads = new ArrayList<Road>(horizontalRoads);
        roads.addAll(verticalRoads);
        roads = Collections.unmodifiableList(roads);

        // generate the data collection lines
        dataCollectionLines.add(
                new DataCollectionLine(
                        "Entrance on Eastbound",
                        dataCollectionLines.size(),
                        new Point2D.Double(BORDER, (height/2)+halfLaneWidth),
                        new Point2D.Double(BORDER, (height/2)-halfLaneWidth),
                        true));
        dataCollectionLines.add(
                new DataCollectionLine(
                        "Exit on Northbound",
                        dataCollectionLines.size(),
                        new Point2D.Double(((width/2)-halfLaneWidth), (height-BORDER)),
                        new Point2D.Double(((width/2)+halfLaneWidth), (height-BORDER)),
                        true));
        dataCollectionLines.add(
                new DataCollectionLine(
                        "Exit on Eastbound",
                        dataCollectionLines.size(),
                        new Point2D.Double((width-BORDER), (height/2)+halfLaneWidth),
                        new Point2D.Double((width-BORDER), (height/2)-halfLaneWidth),
                        true));

        // Now we have created the roads, we need to create the Junction
        List<Road> roadsForJunction = new ArrayList<Road>(3);
        roadsForJunction.add(eastBoundRoad);
        roadsForJunction.add(northBoundRoad);
        Junction junction = new Junction(roadsForJunction);
        junctions.add(junction);


        initializeSpawnPoints(initTime);
    }
}
