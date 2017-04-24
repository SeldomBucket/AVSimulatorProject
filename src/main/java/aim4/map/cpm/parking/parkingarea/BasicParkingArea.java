package aim4.map.cpm.parking.parkingarea;

import aim4.map.Road;
import aim4.map.cpm.CPMMap;
import aim4.map.cpm.parking.ParkingLane;
import aim4.util.ArrayListRegistry;
import aim4.util.Registry;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Becci on 24-Apr-17.
 */
public abstract class BasicParkingArea implements ParkingArea {

    /**The map this parking area belongs to.*/
    protected CPMMap map;
    /**The starting point of the parking area. */
    protected Point2D startPoint;
    /**The length of the lane used for parking */
    protected double parkingLength;
    /**The length of the lane used to access the parking section.*/
    protected double accessLength;
    /**The width of the vertical roads the parking lanes will overlap with. */
    protected double overlappingRoadWidth;
    /**The total length of the parking area*/
    protected double totalLength;
    // TODO CPM Do we need this or is the registry enough?
    /**The set of parking lanes. */
    protected List<ParkingLane> parkingLanes = new ArrayList<ParkingLane>();
    /**The parking lane registry. */
    protected Registry<ParkingLane> parkingLaneRegistry = new ArrayListRegistry<ParkingLane>();
    /**The set of roads. */
    protected List<Road> roads;
    /**The first road in the parking area. */
    protected Road entryRoad;
    /**The last road in the parking area. */
    protected Road lastRoad;

    public BasicParkingArea(Point2D startPoint, CPMMap map, double parkingLength, double accessLength){
        this.startPoint = startPoint;
        this.map = map;
        this.parkingLength = parkingLength;
        this.accessLength = accessLength;
        this.overlappingRoadWidth = map.getLaneWidth();
        this.totalLength = (2*accessLength) + (2*overlappingRoadWidth) + parkingLength;
    }

    public CPMMap getMap() { return map; }

    public Point2D getStartPoint() { return startPoint; }

    public double getParkingLength() { return parkingLength; }

    public double getAccessLength() { return accessLength; }

    public double getOverlappingRoadWidth() { return overlappingRoadWidth; }

    public double getTotalLength() { return totalLength; }

    public Road getEntryRoad() { return entryRoad; }

    public Road getLastRoad() { return lastRoad; }

    public List<ParkingLane> getParkingLanes() { return parkingLanes; }

    public Registry<ParkingLane> getParkingLaneRegistry() { return parkingLaneRegistry; }

    public List<Road> getRoads() { return roads; }
}
