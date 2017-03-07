package aim4.map.cpm.testmaps;

import aim4.map.*;
import aim4.map.connections.Junction;
import aim4.map.cpm.CPMBasicMap;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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
        super(laneWidth, speedLimit, initTime);

        this.dimensions = new Rectangle2D.Double(0, 0, width, height);

        // Set size of array for the data collection lines.
        // One on entry and one on each exit
        dataCollectionLines = new ArrayList<DataCollectionLine>(3);

        // Create the vertical Roads

        //NORTH
        double x1 = width/2;
        double y1 = (height/2) - halfLaneWidth;
        double x2 = x1;
        double y2 = height;
        Road northBoundRoad = createRoadWithOneLane("Northbound Avenue", x1, y1, x2, y2);
        exitLanes.add(northBoundRoad.getLanes().get(0));
        entranceLane = northBoundRoad.getLanes().get(0);

        verticalRoads.add(northBoundRoad);

        // Create the horizontal Roads
        // EAST
        x1 = 0;
        y1 = height/2;
        x2 = width;
        y2 = y1;
        Road eastBoundRoad = createRoadWithOneLane("Eastbound Avenue", x1, y1, x2, y2);
        entranceLane = eastBoundRoad.getLanes().get(0);
        exitLanes.add(eastBoundRoad.getLanes().get(0));

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
