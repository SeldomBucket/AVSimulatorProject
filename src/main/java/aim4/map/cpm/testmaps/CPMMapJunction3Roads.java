package aim4.map.cpm.testmaps;

import aim4.map.DataCollectionLine;
import aim4.map.Road;
import aim4.map.connections.Junction;
import aim4.map.cpm.CPMBasicMap;
import aim4.map.cpm.parking.parkingarea.SingleLaneWidthParkingArea;
import aim4.map.cpm.parking.statusmonitor.BasicStatusMonitor;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Map with a T-Junction with 3 roads.
 */
public class CPMMapJunction3Roads extends CPMBasicMap {

    double laneWidth;
    double halfLaneWidth;

    /**
     * Create a map with a T-Junction with 3 roads.
     */
    public CPMMapJunction3Roads(int laneWidth, double speedLimit,
                                double initTime, double width,
                                double height) {
        super(speedLimit, initTime);
        this.laneWidth = laneWidth;
        this.halfLaneWidth = laneWidth / 2;

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
        Road northBoundRoad = createRoadWithOneLane("Northbound Avenue", x1, y1, x2, y2, laneWidth);
        exitLanes.add(northBoundRoad.getOnlyLane());
        entranceLane = northBoundRoad.getOnlyLane();

        verticalRoads.add(northBoundRoad);

        // Create the horizontal Roads
        // EAST Entry
        x1 = 0;
        y1 = height/2;
        x2 = width/2;
        y2 = y1;
        Road eastBoundEntryRoad = createRoadWithOneLane("Eastbound Entry Avenue", x1, y1, x2, y2, laneWidth);
        entranceLane = eastBoundEntryRoad.getOnlyLane();
        exitLanes.add(eastBoundEntryRoad.getOnlyLane());

        horizontalRoads.add(eastBoundEntryRoad);

        // EAST Exit
        x1 = width/2;
        y1 = height/2;
        x2 = width;
        y2 = y1;
        Road eastBoundExitRoad = createRoadWithOneLane("Eastbound Exit Avenue", x1, y1, x2, y2, laneWidth);
        entranceLane = eastBoundExitRoad.getOnlyLane();
        exitLanes.add(eastBoundExitRoad.getOnlyLane());

        horizontalRoads.add(eastBoundExitRoad);

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
        roadsForJunction.add(eastBoundEntryRoad);
        roadsForJunction.add(eastBoundExitRoad);
        roadsForJunction.add(northBoundRoad);
        Junction junction = new Junction(roadsForJunction);
        junctions.add(junction);

        initializeSpawnPoints(initTime);
    }

    @Override
    public double getLaneWidth() {
        return laneWidth;
    }

    @Override
    public BasicStatusMonitor getStatusMonitor() {
        return null;
    }

    @Override
    public SingleLaneWidthParkingArea getParkingArea() {
        return null;
    }
}
