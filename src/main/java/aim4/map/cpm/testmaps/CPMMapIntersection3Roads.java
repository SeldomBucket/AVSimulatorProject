package aim4.map.cpm.testmaps;

import aim4.map.DataCollectionLine;
import aim4.map.Road;
import aim4.map.connections.SimpleIntersection;
import aim4.map.cpm.CPMBasicMap;
import aim4.map.cpm.parking.StatusMonitor;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A map with an intersection where 3 roads meet: one road ends
 * in the intersection, on starts and the final runs straight
 * through. Roads meet in the centre of the map, and each
 * road has only one lane.
 */
public class CPMMapIntersection3Roads extends CPMBasicMap {

    /**
     * Create a map with an intersection made up of 3 roads.
     */
    public CPMMapIntersection3Roads(int laneWidth, double speedLimit,
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
        // EAST Entry
        x1 = 0;
        y1 = height/2;
        x2 = width/2;
        y2 = y1;
        Road eastBoundEntryRoad = createRoadWithOneLane("Eastbound Entry Avenue", x1, y1, x2, y2);
        entranceLane = eastBoundEntryRoad.getOnlyLane();

        horizontalRoads.add(eastBoundEntryRoad);

        // EAST Exit
        x1 = width/2;
        y1 = height/2;
        x2 = width;
        y2 = y1;
        Road eastBoundExitRoad = createRoadWithOneLane("Eastbound Exit Avenue", x1, y1, x2, y2);
        exitLanes.add(eastBoundExitRoad.getOnlyLane());

        horizontalRoads.add(eastBoundExitRoad);

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
        roadsForIntersection.add(eastBoundEntryRoad);
        roadsForIntersection.add(eastBoundExitRoad);
        roadsForIntersection.add(southBoundRoad);

        SimpleIntersection intersection1 = new SimpleIntersection(roadsForIntersection);
        intersections.add(intersection1);

        initializeSpawnPoints(initTime);
    }

    @Override
    public StatusMonitor getStatusMonitor() {
        return null;
    }
}
