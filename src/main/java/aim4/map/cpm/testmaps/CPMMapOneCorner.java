package aim4.map.cpm.testmaps;

import aim4.map.DataCollectionLine;
import aim4.map.Road;
import aim4.map.cpm.CPMBasicMap;
import aim4.map.cpm.parking.ParkingArea;
import aim4.map.cpm.parking.StatusMonitor;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A map with one corner.
 */
public class CPMMapOneCorner extends CPMBasicMap{

    /**
     * Create a map with one corner.
     */
    public CPMMapOneCorner(int laneWidth, double speedLimit,
                                    double initTime) {
        super(laneWidth, speedLimit, initTime);

        double width = 100;
        double height = 100;

        this.dimensions = new Rectangle2D.Double(0, 0, width, height);

        // Create the vertical Road
        // SOUTH
        double x1 = width - BORDER - halfLaneWidth;
        double y1 = height - BORDER;
        double x2 = x1;
        double y2 = 0;
        Road southBoundRoad = createRoadWithOneLane("Southbound Avenue", x1, y1, x2, y2);
        exitLanes.add(southBoundRoad.getOnlyLane());

        verticalRoads.add(southBoundRoad);

        // Create the horizontal Roads
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

        // Set size of array for the data collection lines.
        // One on entry and one on exit
        dataCollectionLines = new ArrayList<DataCollectionLine>(2);

        // generate the data collection lines
        dataCollectionLines.add(
                new DataCollectionLine(
                        "Entrance on Eastbound",
                        dataCollectionLines.size(),
                        new Point2D.Double(28, 72),
                        new Point2D.Double(28, 70),
                        true));
        dataCollectionLines.add(
                new DataCollectionLine(
                        "Exit on Westbound",
                        dataCollectionLines.size(),
                        new Point2D.Double(70, 28),
                        new Point2D.Double(72, 28),
                        true));

        // Now we can create corners where roads meet.
        makeCorner(eastBoundRoad, southBoundRoad);

        initializeSpawnPoints(initTime);
    }

    @Override
    public StatusMonitor getStatusMonitor() {
        return null;
    }

    @Override
    public ParkingArea getParkingArea() {
        return null;
    }
}
