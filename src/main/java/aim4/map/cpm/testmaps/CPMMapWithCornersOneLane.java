package aim4.map.cpm.testmaps;

import aim4.map.DataCollectionLine;
import aim4.map.Road;
import aim4.map.cpm.CPMBasicMap;
import aim4.map.cpm.parking.StatusMonitor;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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
        super(laneWidth, speedLimit, initTime);

        this.dimensions = new Rectangle2D.Double(0, 0, width, height);

        // Set size of array for the data collection lines.
        // One on entry and one on exit
        dataCollectionLines = new ArrayList<DataCollectionLine>(2);

        // Create the vertical Road
        // SOUTH
        double x1 = width - BORDER;
        double y1 = height - BORDER;
        double x2 = x1;
        double y2 = BORDER;
        Road southBoundRoad = createRoadWithOneLane("Southbound Avenue", x1, y1, x2, y2);

        verticalRoads.add(southBoundRoad);

        // Create the horizontal Roads
        // WEST
        x1 = width - BORDER;
        y1 = BORDER + halfLaneWidth;
        x2 = 0;
        y2 = y1;
        Road westBoundRoad = createRoadWithOneLane("Westbound Avenue", x1, y1, x2, y2);
        exitLanes.add(westBoundRoad.getOnlyLane());

        horizontalRoads.add(westBoundRoad);

        // EAST
        x1 = 0;
        y1 = height - BORDER - halfLaneWidth;
        x2 = width - BORDER;
        y2 = y1;
        Road eastBoundRoad = createRoadWithOneLane("Eastbound Avenue", x1, y1, x2, y2);
        entranceLane = eastBoundRoad.getOnlyLane();

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

    @Override
    public StatusMonitor getStatusMonitor() {
        return null;
    }
}
