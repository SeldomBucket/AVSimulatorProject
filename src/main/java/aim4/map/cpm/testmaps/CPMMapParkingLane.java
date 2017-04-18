package aim4.map.cpm.testmaps;

import aim4.map.DataCollectionLine;
import aim4.map.Road;
import aim4.map.cpm.CPMBasicMap;
import aim4.map.cpm.parking.ParkingArea;
import aim4.map.cpm.parking.ParkingLane;
import aim4.map.cpm.parking.StatusMonitor;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Becci on 12-Apr-17.
 */
public class CPMMapParkingLane extends CPMBasicMap {

    ParkingLane onlyParkingLane;

    public CPMMapParkingLane(double laneWidth, double speedLimit, double initTime,
                             double accessLength, double overlappingRoadWidth, double parkingLaneWidth) {
        super(laneWidth, speedLimit, initTime);

        // Calculate the map dimensions
        double mapWidth = 100;
        double mapHeight = 100;
        this.dimensions = new Rectangle2D.Double(0, 0, mapWidth, mapHeight);

        // Create the vertical Roads
        //NORTH - LEAVES PARKING
        double x1 = mapWidth - BORDER - halfLaneWidth;
        double y1 = 0;
        double x2 = x1;
        double y2 = mapHeight;
        Road northBoundRoad = createRoadWithOneLane("Northbound Road", x1, y1, x2, y2);
        verticalRoads.add(northBoundRoad);

        // Create the horizontal Road with a parking lane
        // EAST - ENTERS CAR PARK
        x1 = 0;
        y1 = mapHeight/2;
        x2 = mapWidth - BORDER;
        y2 = y1;
        Road parkingRoad = createRoadWithOneParkingLane("Parking Road 0", x1, y1, x2, y2,
                                                            accessLength, overlappingRoadWidth,
                                                                parkingLaneWidth, speedLimit);
        horizontalRoads.add(parkingRoad);
        onlyParkingLane = (ParkingLane)parkingRoad.getOnlyLane();

        // Record all roads
        roads = new ArrayList<Road>(horizontalRoads);
        roads.addAll(verticalRoads);
        roads = Collections.unmodifiableList(roads);

        // Establish lanes that enter and exit the map
        entranceLane = parkingRoad.getOnlyLane();
        exitLanes.add(northBoundRoad.getOnlyLane());

        // Connect roads surrounding the parking area
        makeJunction(parkingRoad, northBoundRoad);

        // Set size of array for the data collection lines.
        // One on entry and one on exit
        dataCollectionLines = new ArrayList<DataCollectionLine>(2);

        // generate the data collection lines
        dataCollectionLines.add(
                new DataCollectionLine(
                        "Entrance on Parking Lane",
                        dataCollectionLines.size(),
                        new Point2D.Double(3, 51),
                        new Point2D.Double(3, 49),
                        true));
        dataCollectionLines.add(
                new DataCollectionLine(
                        "Exit on Northbound",
                        dataCollectionLines.size(),
                        new Point2D.Double(70, 72),
                        new Point2D.Double(72, 72),
                        true));

        // Initialise the spawn point
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

    public ParkingLane getOnlyParkingLane() { return onlyParkingLane; }
}
