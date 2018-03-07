package aim4.map.mixedcpm.parking;

import aim4.map.Road;
import aim4.map.mixedcpm.MixedCPMRoadMap;
import aim4.map.mixedcpm.MixedCPMBasicMap;
import aim4.vehicle.mixedcpm.MixedCPMBasicVehicleModel;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ManualParkingArea
 *
 * Manages the parking area for manual vehicles
 */
public class ManualParkingArea extends MixedCPMRoadMap {
    private List<ManualParkingRoad> parkingRoads;
    private Road entryRoad;
    private Road exitRoad;
    private MixedCPMBasicMap map;

    // TODO ED make size of area changeable, as long as the outside of the

    /**
     * Constructors for the ManualParkingArea
     * @param topRoad the top road of the area (must be parallel to the bottom road)
     * @param bottomRoad the bottom road of the area
     * @param map the map this ManualParkingArea belongs to
     * @param dimensions the dimensions of this map
     */
    public ManualParkingArea(Road topRoad, Road bottomRoad, MixedCPMBasicMap map, Rectangle2D dimensions){
        super(map.getLaneWidth(), map.getMaximumSpeedLimit());
        this.dimensions = dimensions;

        // TODO ED make sure that the top and bottom roads are parallel and top is above bottom
        assert topRoad.getOnlyLane().getStartPoint().getX() < bottomRoad.getOnlyLane().getStartPoint().getX();
        assert topRoad.getOnlyLane().getEndPoint().getX() < bottomRoad.getOnlyLane().getEndPoint().getX();

        this.entryRoad = topRoad;
        this.exitRoad = bottomRoad;

        this.parkingRoads = new ArrayList<>();
        this.map = map;
    }

    /**
     * add a vehicle to the map, finding a stall for them
     * @param vehicle the vehicle to be added
     */
    public void addVehicleToMap(MixedCPMBasicVehicleModel vehicle){
        // TODO ED call findSpaceForVehicle and give the location of that space

    }

    /**
     * Find a stall based on the stallInfo
     * @param stallInfo the parameters of the stall to find the space
     * @return the ManualStall if it can fit, null otherwise
     */
    public ManualStall findSpace(StallInfo stallInfo){
        ManualStall tempStall;

        String roadName = UUID.randomUUID().toString();
        if (parkingRoads.size() == 0){

            return addNewParkingRoadAndFindSpace(roadName, stallInfo);
        }

        // Find a space for the vehicle
        // Search parkingRoads for a suitable space and add if possible

        //        First search for stack with correct height & same ideal width        //
        for (ManualParkingRoad road: parkingRoads) {
            tempStall = road.findNewSpace(stallInfo, ManualParkingRoad.SearchParameter.exactSize);
            if (tempStall != null){ return tempStall; }
        }

        //        Next, search for stack with correct height only        //
        for (ManualParkingRoad road: parkingRoads) {
            tempStall = road.findNewSpace(stallInfo, ManualParkingRoad.SearchParameter.correctHeight);
            if (tempStall != null){ return tempStall; }
        }

        //       Next, search for empty stacks       //
        for (ManualParkingRoad road: parkingRoads) {
            tempStall = road.findNewSpace(stallInfo, ManualParkingRoad.SearchParameter.emptyStack);
            if (tempStall != null){ return tempStall; }
        }

        //        Next, add new road and use that        //
        tempStall = addNewParkingRoadAndFindSpace(roadName, stallInfo);
        if (tempStall != null){ return tempStall; }

        //      Next, extend the last stack to allow for longer vehicles

        return null;
    }

    /**
     * update the parking roads and make sure the last one is labelled properly
     */
    public void update(){
        for (int i = parkingRoads.size()-1; i >= 0; i--){
            ManualParkingRoad currentRoad = parkingRoads.get(i);
            if (i == parkingRoads.size()){
                currentRoad.setLastRoad(true);
                if (currentRoad.getParkingSpaces().size() == 0){
                    removeParkingRoad(currentRoad);
                }
            }else{
                currentRoad.setLastRoad(true);
            }
        }

        map.update();
    }


    //////////////////////////////
    //                          //
    //      Private Methods     //
    //                          //
    //////////////////////////////

    /**
     * adds a new parking road and finds a space based on stallInfo
     * @param roadName the name of the road
     * @param stallInfo the parameters of the stall to find in the new road
     * @return the Manual stall if it is found, null otherwise
     */
    private ManualStall addNewParkingRoadAndFindSpace(String roadName, StallInfo stallInfo){

        // Using new StallInfo, set up a new road (with the stall stack size
        // Set up connections to end roads
        ManualParkingRoad road = addNewParkingRoad(roadName, stallInfo.getLength());
        if (road != null){
            return road.findNewSpace(stallInfo, ManualParkingRoad.SearchParameter.exactSize);
        }else {
            return null;
        }
    }

    /**
     * gets the x position of the first empty space
     * @return a pointer to the empty space
     */
    private double getEmptySpacePointer(){
        double sum = 0;
        for (ManualParkingRoad parkingRoad : parkingRoads){
            sum += parkingRoad.getEntireWidth();
        }
        return sum;
    }

    /**
     * add a new parking road
     * @param roadName the name of the new road
     * @param initialStackWidth the initial width of the left stack
     * @return the ManualParkingRoad if it was possible, null otherwise
     */
    public ManualParkingRoad addNewParkingRoad(String roadName, double initialStackWidth) {
        double spacePointer = getEmptySpacePointer();
        if (this.dimensions.getWidth() < spacePointer + initialStackWidth + this.laneWidth){
            return null;
        }

        Road road = this.makeRoadWithOneLane(roadName,
                                                this.entryRoad.getOnlyLane().getStartPoint().getX() + this.halfLaneWidth + spacePointer + initialStackWidth,
                                                this.entryRoad.getOnlyLane().getStartPoint().getY(),
                                                this.exitRoad.getOnlyLane().getStartPoint().getX() + this.halfLaneWidth + spacePointer + initialStackWidth,
                                                this.exitRoad.getOnlyLane().getStartPoint().getY());

        this.makeJunction(this.entryRoad, road);
        this.makeJunction(this.exitRoad, road);

        ManualParkingRoad parkingRoad = new ManualParkingRoad(road, this, initialStackWidth);

        this.parkingRoads.add(parkingRoad);

        return parkingRoad;
    }

    /**
     * remove a parking road and all junctions/references to it
     * @param road the ParkingRoad to remove
     */
    private void removeParkingRoad(ManualParkingRoad road) {
        for (ManualParkingRoad parkingRoad: parkingRoads) {
            if (parkingRoad == road){
                this.map.removeRoad(parkingRoad.getCentreRoad());
                break;
            }
        }
    }
}
