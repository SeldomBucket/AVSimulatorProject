package aim4.map.cpm.parking.parkingarea;

import aim4.map.Road;
import aim4.map.cpm.parking.ParkingLane;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Becci on 24-Apr-17.
 */
public interface ParkingArea {
    public ArrayList<Road> calculateListOfRoads();
    public double calculateOverLappingRoadWidth();
    public double getMaxParkingLaneWidth();
    public List<ParkingLane> getParkingLanes();

}
