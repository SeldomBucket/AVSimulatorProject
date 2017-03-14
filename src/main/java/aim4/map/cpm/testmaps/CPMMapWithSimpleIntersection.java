package aim4.map.cpm.testmaps;

import aim4.map.DataCollectionLine;
import aim4.map.Road;
import aim4.map.connections.SimpleIntersection;
import aim4.map.cpm.CPMBasicMap;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

/**
 * A map with an intersection where only 2 roads cross over
 * in the centre of the map, and each road has only one lane.
 */
public class CPMMapWithSimpleIntersection extends CPMBasicMap {


    /**
     * Create a map with an intersection where only 2 roads cross over
     * in the centre of the map, and each road has only one lane.
     */
    public CPMMapWithSimpleIntersection(int laneWidth, double speedLimit,
                                        double initTime, double width,
                                        double height) {
        super(laneWidth, speedLimit, initTime);

        this.dimensions = new Rectangle2D.Double(0, 0, width, height);

        // Set size of array for the data collection lines.
        // One on entry and one on each exit
        dataCollectionLines = new ArrayList<DataCollectionLine>(3);

        // Create the vertical Road
        //SOUTH
        double x1 = width/2;
        double y1 = height;
        double x2 = x1;
        double y2 = 0;
        Road southBoundRoad = createRoadWithOneLane("Southbound Avenue", x1, y1, x2, y2);
        exitLanes.add(southBoundRoad.getOnlyLane());

        verticalRoads.add(southBoundRoad);

        // Create the horizontal Roads
        // EAST
        x1 = 0;
        y1 = height/2;
        x2 = width;
        y2 = y1;
        Road eastBoundRoad = createRoadWithOneLane("Eastbound Avenue", x1, y1, x2, y2);
        entranceLane = eastBoundRoad.getOnlyLane();
        exitLanes.add(eastBoundRoad.getOnlyLane());

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
        List<Road> roadsForIntersection = new ArrayList<Road>(2);
        roadsForIntersection.add(eastBoundRoad);
        roadsForIntersection.add(southBoundRoad);
        SimpleIntersection intersection1 = new SimpleIntersection(roadsForIntersection);
        intersections.add(intersection1);

        initializeSpawnPoints(initTime);
    }
}
