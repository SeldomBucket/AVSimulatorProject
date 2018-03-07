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
 * Manages the
 */
public class ManualParkingArea extends MixedCPMRoadMap {
    private List<ManualParkingRoad> parkingRoads;
    private Road entryRoad;
    private Road exitRoad;
    private MixedCPMBasicMap map;
    private ArrayList<Road> removedRoads;

    // TODO ED make size of area changeable, as long as the outside of the

    /**
     *
     * @param topRoad
     * @param bottomRoad
     */
    public ManualParkingArea(Road topRoad, Road bottomRoad, MixedCPMBasicMap map, Rectangle2D dimensions){
        super(map.getLaneWidth(), map.getMaximumSpeedLimit());
        // TODO ED make bounding box (dimensions) for the area
        this.dimensions = dimensions;
        assert topRoad.getOnlyLane().getStartPoint().getX() < bottomRoad.getOnlyLane().getStartPoint().getX();
        assert topRoad.getOnlyLane().getEndPoint().getX() < bottomRoad.getOnlyLane().getEndPoint().getX();

        this.entryRoad = topRoad;
        this.exitRoad = bottomRoad;

        this.parkingRoads = new ArrayList<>();
        this.map = map;
    }

    public void addVehicleToMap(MixedCPMBasicVehicleModel vehicle){
        // TODO ED call findSpaceForVehicle and give the location of that space

    }

    public ManualStall findSpace(StallInfo stallInfo){
        ManualStall tempStall = null;
        stallInfo.getLength();
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
        // TODO ED Road name generation
        tempStall = addNewParkingRoadAndFindSpace(roadName, stallInfo);
        if (tempStall != null){ return tempStall; }

        //      Next, extend the last stack to allow for longer vehicles

        return null;
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

        map.update();
    }


    //////////////////////////////
    //                          //
    //      Private Methods     //
    //                          //
    //////////////////////////////



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

    private double getEmptySpacePointer(){
        double sum = 0;
        for (ManualParkingRoad parkingRoad : parkingRoads){
            sum += parkingRoad.getEntireWidth();
        }
        return sum;
    }

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

    private void removeParkingRoad(UUID roadID) {
        for (ManualParkingRoad parkingRoad: parkingRoads) {
            if (parkingRoad.getID() == roadID){
                this.removedRoads.add(parkingRoad.getCentreRoad());
                this.parkingRoads.remove(parkingRoad);
                this.removeRoad(parkingRoad.getCentreRoad());
                // TODO ED REMOVE THE ROAD FROM THE MAP SOMEHOW
                break;
            }
        }
    }
}
