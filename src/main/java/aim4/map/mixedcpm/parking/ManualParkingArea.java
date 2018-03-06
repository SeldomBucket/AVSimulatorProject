package aim4.map.mixedcpm.parking;

import aim4.map.Road;
import aim4.map.connections.Corner;
import aim4.map.connections.Junction;
import aim4.map.connections.SimpleIntersection;
import aim4.map.cpm.CPMRoadMap;
import aim4.vehicle.mixedcpm.MixedCPMBasicVehicleModel;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ManualParkingArea
 *
 * Manages the
 */
public class ManualParkingArea extends CPMRoadMap {
    private Point2D startingPoint;
    private List<ManualParkingRoad> parkingRoads;
    private Road entryRoad;
    private Road exitRoad;


    public ManualParkingArea(Point2D startPoint, Road topRoad, Road bottomRoad){
        this.startingPoint = startPoint;
        this.entryRoad = topRoad;
        this.exitRoad = bottomRoad;

        this.parkingRoads = new ArrayList<>();

        addNewParkingRoad();

        // Set up in and out roads at top and bottom of area
    }

    public void addVehicleToMap(MixedCPMBasicVehicleModel vehicle){
        // call findSpaceForVehicle and give the location of that space
    }

    public void update(){

        for (int i = parkingRoads.size()-1; i >= 0; i--){
            ManualParkingRoad currentRoad = parkingRoads.get(i);
            if (i == parkingRoads.size()){
                currentRoad.setLastRoad(true);
                if (currentRoad.getParkingSpaces().size() == 0){
                    removeParkingRoad(currentRoad.getID());
                }
            }else{
                currentRoad.setLastRoad(true);
            }
        }


    }


    //////////////////////////////
    //                          //
    //      Private Methods     //
    //                          //
    //////////////////////////////

    private ManualStall findSpaceForVehicle(StallInfo stallInfo){
        ManualStall tempStall = null;
        stallInfo.getLength();

        // Find a space for the vehicle
        // Search parkingRoads for a suitable space and add if possible

        //        First search for stack with correct height & same ideal width        //
        for (ManualParkingRoad road: parkingRoads) {
            tempStall = road.findNewSpace(stallInfo, ManualParkingRoad.StallSearchParameter.exactSize);
            if (tempStall != null){ return tempStall; }
        }

        //        Next, search for stack with correct height only        //
        for (ManualParkingRoad road: parkingRoads) {
            tempStall = road.findNewSpace(stallInfo, ManualParkingRoad.StallSearchParameter.correctHeight);
            if (tempStall != null){ return tempStall; }
        }

        //       Next, search for empty stacks       //
        for (ManualParkingRoad road: parkingRoads) {
            tempStall = road.findNewSpace(stallInfo, ManualParkingRoad.StallSearchParameter.emptyStack);
            if (tempStall != null){ return tempStall; }
        }

        //        Next, add new road and use that        //
        tempStall = addNewParkingRoadAndFindSpace(stallInfo);
        if (tempStall != null){ return tempStall; }

        //      Next, extend the last stack to allow for longer vehicles

        return null;
    }

    private ManualStall addNewParkingRoadAndFindSpace(StallInfo stallInfo){

        // Using new StallInfo, set up a new road (with the stall stack size
        // Set up connections to end roads

        stallInfo.getLength();
        return null;
    }

    private boolean addNewParkingRoad() {

        return false;
    }

    private boolean removeParkingRoad(UUID roadID) {
        for (ManualParkingRoad road: parkingRoads) {
            if (road.getID() == roadID){
                parkingRoads.remove(road);
                // REMOVE THE ROAD FROM THE MAP SOMEHOW?
                break;
            }
        }
        return false;
    }
}
