package aim4.map.cpm.parking;

import aim4.map.Road;
import aim4.map.cpm.CPMMap;
import aim4.util.ArrayListRegistry;
import aim4.util.Registry;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * An area made up of ParkingLanes where vehicles can park.
 * This parking area is built from the starting point given.
 * Parking lanes are added vertically, from top to bottom.
 * The lanes are created from left to right, heading EAST (0 rads).
 * Essentially, the start point given must be the top left
 * corner of the parking area to be created. For my design,
 * this should be t
 */
public class ParkingArea {

    /////////////////////////////////
    // CONSTANTS
    /////////////////////////////////

    /**The map this parking area belongs to.*/
    private CPMMap map;
    /**The starting point of the parking area. */
    private Point2D startPoint;
    // lengths
    /**The length of the lane used for parking */
    private double parkingLength;
    /**The length of the lane used to access the parking section.*/
    private double accessLength;
    /**The width of the vertical roads the parking lanes will overlap with. */
    private double overlappingRoadWidth;
    /**The total length of the parking area*/
    private double totalLength;
    // parking roads/lanes
    /**The number of parking lanes. */
    private int numberOfParkingLanes;
    // TODO CPM Do we need this or is the registry enough?
    /**The set of parking lanes. */
    private List<ParkingLane> parkingLanes = new ArrayList<ParkingLane>();
    /**The parking lane registry. */
    private Registry<ParkingLane> parkingLaneRegistry = new ArrayListRegistry<ParkingLane>();
    /**The set of roads. */
    private List<Road> roads;
    /**The road which has been extended to be the car park entrance. */
    private Road entryRoad;
    /**The width of each parking lane. */
    private double parkingLaneWidth;


    public ParkingArea(Point2D startPoint, CPMMap map,
                       int numberOfParkingLanes, double parkingLength,
                       double parkingLaneWidth, double accessLength){
        this.startPoint = startPoint;
        this.map = map;
        this.numberOfParkingLanes = numberOfParkingLanes;
        this.roads = new ArrayList<Road>(numberOfParkingLanes);
        this.parkingLength = parkingLength;
        this.parkingLaneWidth = parkingLaneWidth;
        this.accessLength = accessLength;
        this.overlappingRoadWidth = map.getLaneWidth();
        this.totalLength = (2*accessLength) + (2*overlappingRoadWidth) + parkingLength;

        // TODO CPM decide if we need, maybe not if dynamically building the map
        // validateParameters();
        addParkingLanes();
    }

    public ParkingArea(int startPointX, int startPointY,
                       CPMMap map, int numberOfParkingLanes, double parkingLength,
                       double parkingLaneWidth, double accessLength) {
        this(new Point(startPointX, startPointY), map, numberOfParkingLanes,
                parkingLength, parkingLaneWidth, accessLength);
    }

    /**
     *Ensure that the parameters given allow us to create a parking area.
     */
    private void validateParameters() {
        // The start point must be on the map
        if (!map.getDimensions().contains(startPoint)){
            throw new RuntimeException("The start point of a parking area " +
                                                "must be on the map. ");
        }

        // The length and height of the parking area from the start point must remain on the map
        double parkingAreaMaxX = startPoint.getX() + totalLength;
        double parkingAreaMaxY = startPoint.getY() - (numberOfParkingLanes*parkingLaneWidth);
        if (!map.getDimensions().contains(parkingAreaMaxX, parkingAreaMaxY)){
            throw new RuntimeException("The parking area is too big to fit on the map.");
        }
    }

    /**
     * Add roads and parking lanes to the parking area.
     */
    private void addParkingLanes(){
        // Calculate points for first lane from the start point
        double laneStartPointX = startPoint.getX();
        double laneEndPointX = laneStartPointX + totalLength;
        double lanePointY = startPoint.getY() - (parkingLaneWidth/2);

        for (int i = 0 ; i < numberOfParkingLanes ; i++){
            // Create a road for the parking lane to belong to
            Road road = new Road("Parking road " + i, map);
            // TODO CPM Is this the best place to do this?
            ParkingLane parkingLane;
            if (i == 0) {
                // We need this lane to start on the edge of the map
                // This will be the entrance lane
                parkingLane = new ParkingLane(0,
                        lanePointY,
                        laneEndPointX,
                        lanePointY,
                        parkingLaneWidth,
                        accessLength,
                        overlappingRoadWidth,
                        map.getMaximumSpeedLimit());
                entryRoad = road;
            } else {
                // Create a new parking lane
                parkingLane = new ParkingLane(laneStartPointX,
                        lanePointY,
                        laneEndPointX,
                        lanePointY,
                        parkingLaneWidth,
                        accessLength,
                        overlappingRoadWidth,
                        map.getMaximumSpeedLimit());
            }
            // Register the lane and add it to the road
            int laneId = parkingLaneRegistry.register(parkingLane);
            parkingLane.setId(laneId);
            road.addTheRightMostLane(parkingLane);
            parkingLanes.add(parkingLane);
            roads.add(road);
            // Set up points for next lane
            lanePointY = lanePointY - parkingLaneWidth;
        }
        assert(parkingLanes.size() == numberOfParkingLanes);
        assert(roads.size() == numberOfParkingLanes);
    }


    public CPMMap getMap() { return map; }

    public Point2D getStartPoint() { return startPoint; }

    public double getParkingLength() { return parkingLength; }

    public double getAccessLength() { return accessLength; }

    public double getOverlappingRoadWidth() { return overlappingRoadWidth; }

    public double getTotalLength() { return totalLength; }

    public int getNumberOfParkingLanes() { return numberOfParkingLanes; }

    public List<ParkingLane> getParkingLanes() { return parkingLanes; }

    public Registry<ParkingLane> getParkingLaneRegistry() { return parkingLaneRegistry; }

    public List<Road> getRoads() { return roads; }

    public double getParkingLaneWidth() { return parkingLaneWidth; }

    public Road getEntryRoad() { return entryRoad; }
}
