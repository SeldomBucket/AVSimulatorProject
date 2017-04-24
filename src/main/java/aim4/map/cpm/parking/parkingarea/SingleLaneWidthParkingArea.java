package aim4.map.cpm.parking.parkingarea;

import aim4.map.Road;
import aim4.map.cpm.CPMMap;
import aim4.map.cpm.parking.ParkingLane;
import aim4.map.cpm.parking.parkingarea.BasicParkingArea;
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
 * corner of the parking area to be created.
 */
public class SingleLaneWidthParkingArea extends BasicParkingArea {

    /**The number of parking lanes. */
    private int numberOfParkingLanes;
    /**The width of each parking lane. */
    private double parkingLaneWidth;

    public SingleLaneWidthParkingArea(Point2D startPoint, CPMMap map,
                                      int numberOfParkingLanes, double parkingLength,
                                      double parkingLaneWidth, double accessLength){
        super(startPoint, map, parkingLength, accessLength);
        this.numberOfParkingLanes = numberOfParkingLanes;
        this.parkingLaneWidth = parkingLaneWidth;
        this.roads = calculateListOfRoads();
        this.overlappingRoadWidth = calculateOverLappingRoadWidth();
        this.totalLength = (2*accessLength) + (2*overlappingRoadWidth) + parkingLength;

        // TODO CPM decide if we need, maybe not if dynamically building the map
        // validateParameters();
        addParkingLanes();
    }

    public SingleLaneWidthParkingArea(int startPointX, int startPointY,
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
            ParkingLane parkingLane = new ParkingLane(laneStartPointX,
                    lanePointY,
                    laneEndPointX,
                    lanePointY,
                    parkingLaneWidth,
                    accessLength,
                    overlappingRoadWidth,
                    map.getMaximumSpeedLimit(),
                    road);

            // Register the lane and add it to the road
            int laneId = parkingLaneRegistry.register(parkingLane);
            parkingLane.setId(laneId);
            road.addTheRightMostLane(parkingLane);
            parkingLanes.add(parkingLane);
            roads.add(road);
            if (i == 0) {
                entryRoad = road;
            }
            if (i+1 == numberOfParkingLanes){
                // Then this is the last road
                lastRoad = road;
                break;
            }
            // Set up points for next lane
            lanePointY = lanePointY - parkingLaneWidth;
        }
        assert(parkingLanes.size() == numberOfParkingLanes);
        assert(roads.size() == numberOfParkingLanes);
    }

    public int getNumberOfParkingLanes() { return numberOfParkingLanes; }

    public double getSingleParkingLaneWidth() { return parkingLaneWidth; }

    @Override
    public ArrayList<Road> calculateListOfRoads() {
        return new ArrayList<Road>(numberOfParkingLanes);
    }

    @Override
    public double calculateOverLappingRoadWidth() {
        return map.getLaneWidth();
    }

    @Override
    public double getMaxParkingLaneWidth() {
        return parkingLaneWidth;
    }
}
