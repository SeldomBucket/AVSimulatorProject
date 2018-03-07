package aim4.map.mixedcpm.parking;

import aim4.map.Road;
import aim4.map.cpm.CPMRoadMap;
import aim4.map.lane.Lane;
import aim4.map.lane.LineSegmentLane;
import aim4.map.mixedcpm.MixedCPMBasicMap;
import aim4.vehicle.mixedcpm.MixedCPMBasicVehicleModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ManualParkingArea
 *
 * Manages the
 */
public class ManualParkingArea extends CPMRoadMap {
    private List<ManualParkingRoad> parkingRoads;
    private Road entryRoad;
    private Road exitRoad;
    private MixedCPMBasicMap map;
    private ArrayList<Road> removedRoads;

    /**
     *
     * @param topRoad
     * @param bottomRoad
     */
    public ManualParkingArea(Road topRoad, Road bottomRoad, MixedCPMBasicMap map){
        super(map.getLaneWidth(), map.getMaximumSpeedLimit());
        assert topRoad.getOnlyLane().getStartPoint().getX() < bottomRoad.getOnlyLane().getStartPoint().getX();
        assert topRoad.getOnlyLane().getEndPoint().getX() < bottomRoad.getOnlyLane().getEndPoint().getX();

        this.entryRoad = topRoad;
        this.exitRoad = bottomRoad;

        this.parkingRoads = new ArrayList<>();
        this.map = map;

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

        map.update();
    }


    //////////////////////////////
    //                          //
    //      Private Methods     //
    //                          //
    //////////////////////////////

    private ManualStall findSpaceForVehicle(StallInfo stallInfo){
        ManualStall tempStall = null;
        stallInfo.getLength();

        if (parkingRoads.size() == 0){
            return addNewParkingRoadAndFindSpace(stallInfo);
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
        tempStall = addNewParkingRoadAndFindSpace(stallInfo);
        if (tempStall != null){ return tempStall; }

        //      Next, extend the last stack to allow for longer vehicles

        return null;
    }

    private ManualStall addNewParkingRoadAndFindSpace(StallInfo stallInfo){

        // Using new StallInfo, set up a new road (with the stall stack size
        // Set up connections to end roads
        ManualParkingRoad road = addNewParkingRoad(stallInfo.getLength());
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

    private ManualParkingRoad addNewParkingRoad(Double initialStackWidth) {
        double spacePointer = getEmptySpacePointer();
        // TODO if can't fit new parking road and initial stack return null
        if (this.dimensions.getWidth() < spacePointer + initialStackWidth + this.laneWidth){
            return null;
        }

        LineSegmentLane lane = new LineSegmentLane( this.entryRoad.getOnlyLane().getStartPoint().getX() + spacePointer + initialStackWidth,
                                                    this.entryRoad.getOnlyLane().getStartPoint().getY(),
                                                    this.exitRoad.getOnlyLane().getStartPoint().getX() + spacePointer + initialStackWidth,
                                                    this.exitRoad.getOnlyLane().getStartPoint().getY(),
                                                    this.getLaneWidth(),
                                                    this.getMaximumSpeedLimit());
        ArrayList<Lane> onlyLaneList = new ArrayList<Lane>(){{add(lane);}};

        Road road = new Road("road1", onlyLaneList,this.map);

        ManualParkingRoad parkingRoad = new ManualParkingRoad(road, this, initialStackWidth);

        this.makeSimpleIntersection(this.entryRoad, parkingRoad.getCentreRoad());
        this.makeSimpleIntersection(this.exitRoad, parkingRoad.getCentreRoad());

        parkingRoads.add(parkingRoad);

        return parkingRoad;
    }

    private boolean removeParkingRoad(UUID roadID) {
        for (ManualParkingRoad parkingRoad: parkingRoads) {
            if (parkingRoad.getID() == roadID){
                removedRoads.add(parkingRoad.getCentreRoad());
                parkingRoads.remove(parkingRoad);
                this.roads.remove(parkingRoad.getCentreRoad());
                // TODO REMOVE THE ROAD FROM THE MAP SOMEHOW?
                break;
            }
        }
        return false;
    }
}
