package aim4.map.mixedcpm.parking;

import aim4.map.Road;
import aim4.map.connections.Corner;
import aim4.map.connections.Junction;
import aim4.map.connections.SimpleIntersection;
import aim4.map.cpm.CPMRoadMap;
import aim4.vehicle.mixedcpm.MixedCPMBasicVehicleModel;

import java.util.ArrayList;
import java.util.List;

/**
 * ManualParkingArea
 *
 * Manages the
 */
public class ManualParkingArea extends CPMRoadMap{
    private List<ManualParkingRoad> parkingRoads;

    public ManualParkingArea(){
        this.parkingRoads = new ArrayList<>();
        // Set up in and out roads at top and bottom of area
    }

    public void addVehicleToMap(MixedCPMBasicVehicleModel vehicle){
        // call findSpaceForVehicle and give the location of that space
    }

    private StallStack findSpaceForVehicle(StallInfo stallInfo){
        // Find a space for the vehicle
        stallInfo.getLength();
        // Search parkingRoads for a suitable space and add if possible
        //      First search for stack with correct height & same ideal width
        //      Next, search for stack with correct height only
        //      Next, search for empty stacks
        //      Next, add new road and use that
        //      Next, extend the last stack to allow for longer vehicles
        return null;
    }

    private StallStack addNewParkingRoadAndFindSpace(){
        return null;
    }

    private boolean addNewParkingRoad(StallInfo stallInfo)
    {
        // Using new StallInfo, set up a new road (with the stall stack size
        // Set up connections to end roads

        stallInfo.getLength();
        return false;
    }
}
