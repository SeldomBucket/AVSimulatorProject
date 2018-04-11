package aim4.map.mixedcpm.parking;

import aim4.map.Road;
import aim4.map.connections.Junction;
import aim4.map.mixedcpm.MixedCPMRoadMap;
import aim4.map.mixedcpm.MixedCPMBasicMap;
import aim4.vehicle.mixedcpm.MixedCPMBasicManualVehicle;

import java.awt.geom.Rectangle2D;
import java.util.*;

/**
 * ManualParkingArea
 *
 * Manages the parking area for manual vehicles
 */
public class ManualParkingArea extends MixedCPMRoadMap implements IManualParkingArea {
    private ArrayList<ManualParkingRoad> parkingRoads;
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
        assert topRoad.getOnlyLane().getStartPoint().getY() < bottomRoad.getOnlyLane().getStartPoint().getY();
        assert topRoad.getOnlyLane().getEndPoint().getY() < bottomRoad.getOnlyLane().getEndPoint().getY();

        this.entryRoad = topRoad;
        this.exitRoad = bottomRoad;

        this.roads.add(topRoad);
        this.roads.add(bottomRoad);

        this.parkingRoads = new ArrayList<>();
        this.map = map;
    }

    /**
     * add a vehicle to the map, finding a stall for them
     * @param vehicle the vehicle to be added
     */
    public void addVehicleToMap(MixedCPMBasicManualVehicle vehicle){
        // TODO ED call findSpaceForVehicle and give the location of that space

    }

    /**
     * Find a stall based on the stallSpec
     * @param stallSpec the parameters of the stall to find the space
     * @return the ManualStall if it can fit, null otherwise
     */
    public ManualStall findSpace(StallSpec stallSpec){
        ManualStall tempStall;

        String roadName = "ParkingRoad:" + UUID.randomUUID().toString();

        // Find a space for the vehicle
        // Search parkingRoads for a suitable space and add if possible
        if (parkingRoads.size() == 0){
            tempStall = addNewParkingRoadAndFindSpace(roadName, stallSpec);
            if (tempStall != null){
                if (tempStall.getMaxX() > dimensions.getMaxX()){
                    tempStall.delete();
                    return null;
                }
                return tempStall;
            }
        }

        //        First search for stack with correct length & same ideal width
        for (ManualParkingRoad road: parkingRoads) {
            tempStall = road.findNewSpace(stallSpec,
                                   ManualParkingRoad.SearchParameter.exactSize);
            if (tempStall != null){
                if (tempStall.getMaxX() > dimensions.getMaxX()){
                    tempStall.delete();
                    return null;
                }
                return tempStall;
            }
        }

        //        Next, search for stack with correct length only
        for (ManualParkingRoad road: parkingRoads) {
            tempStall = road.findNewSpace(stallSpec,
                               ManualParkingRoad.SearchParameter.correctLength);
            if (tempStall != null){
                if (tempStall.getMaxX() > dimensions.getMaxX()){
                    tempStall.delete();
                    return null;
                }
                return tempStall;
            }
        }

        //       Next, search for empty stacks
        for (ManualParkingRoad road: parkingRoads) {
            tempStall = road.findNewSpace(stallSpec,
                                  ManualParkingRoad.SearchParameter.emptyStack);
            if (tempStall != null){
                if (tempStall.getMaxX() > dimensions.getMaxX()){
                    tempStall.delete();
                    return null;
                }
                return tempStall;
            }
        }

        //        Next, add new road and use that
        tempStall = addNewParkingRoadAndFindSpace(roadName, stallSpec);
        if (tempStall != null){
            if (tempStall.getMaxX() > dimensions.getMaxX()){
                tempStall.delete();
                return null;
            }
            return tempStall;
        }

        //      Next, extend the last stack to allow for longer vehicles
        for (ManualParkingRoad road: parkingRoads) {
            if (road.isLastRoad()){
                if(road.getStallStackPair()[1].tryChangeLength(stallSpec.getLength())) {
                    tempStall = road.findNewSpace(stallSpec,
                            ManualParkingRoad.SearchParameter.emptyStack);
                    if (tempStall != null) {
                        if (tempStall.getMaxX() > dimensions.getMaxX()) {
                            tempStall.delete();
                            return null;
                        }
                        return tempStall;
                    }
                }
            }
        }

        return null;
    }

    public ManualParkingRoad getParkingRoadByName(String parkingRoadName){

        for (ManualParkingRoad road: parkingRoads){
            if (road.getName().equals(parkingRoadName)){
                return road;
            }
        }
        return null;
    }

    /**
     * add a new parking road
     * @param roadName the name of the new road
     * @param initialStackWidth the initial width of the left stack
     * @return the ManualParkingRoad if it was possible, null otherwise
     */
    public ManualParkingRoad addNewParkingRoad(String roadName,
                                               double initialStackWidth) {
        double spacePointer = getEmptySpacePointer();
        // Don't add if it can't fit in the space
        if (this.dimensions.getWidth() <
                spacePointer + initialStackWidth + this.laneWidth){
            return null;
        }

        double roadX = this.entryRoad.getOnlyLane().getStartPoint().getX() +
                this.halfLaneWidth +
                spacePointer +
                initialStackWidth;
        Road road = this.makeRoadWithOneLane(
                roadName,
                roadX,
                this.entryRoad.getOnlyLane().getShape().getBounds2D().getMinY(),
                roadX,
                this.exitRoad.getOnlyLane().getShape().getBounds2D().getMaxY(),
                laneWidth);

        this.makeJunction(this.entryRoad, road);
        this.makeJunction(this.exitRoad, road);

        ManualParkingRoad parkingRoad = new ManualParkingRoad(road,
                                                              this,
                                                              initialStackWidth);

        this.parkingRoads.add(parkingRoad);
        updateLastParkingLane();
        return parkingRoad;
    }

    /**
     * remove a parking road and all junctions/references to it
     * @param road the ParkingRoad to remove
     */
    public void removeParkingRoad(ManualParkingRoad road) {
        for (ManualParkingRoad parkingRoad: parkingRoads) {
            if (parkingRoad == road){
                this.removeRoad(parkingRoad.getCentreRoad());
                parkingRoads.remove(parkingRoad);
                break;
            }
        }
        updateLastParkingLane();
    }

    public void removeManualStall(String stallName){
        ManualStall stallToRemove = getManualStallByName(stallName);
        if(stallToRemove != null){
            stallToRemove.delete();
        }
    }

    public ManualStall getManualStallByName(String stallName){
        ManualStall returnStall;
        for (ManualParkingRoad parkingRoad : parkingRoads){
            returnStall = parkingRoad.getManualStallByName(stallName);
            if (returnStall != null){
                return returnStall;
            }
        }
        return null;
    }

    /**
     * update the parking roads and make sure the last one is labelled properly
     */
    public void update(){
        //map.update();
        for (int i = parkingRoads.size()-1; i>=0; i--){
            if (parkingRoads.get(i).isToBeDeleted() && !map.vehicleIsInRoad(parkingRoads.get(i))) {
                removeParkingRoad(parkingRoads.get(i));
            }
        }
    }



    //////////////////////////////
    //                          //
    //      Private Methods     //
    //                          //
    //////////////////////////////

    /**
     * adds a new parking road and finds a space based on stallSpec
     * @param roadName the name of the road
     * @param stallSpec the parameters of the stall to find in the new road
     * @return the Manual stall if it is found, null otherwise
     */
    private ManualStall addNewParkingRoadAndFindSpace(String roadName,
                                                      StallSpec stallSpec){

        // Using new StallSpec, set up a new road (with the stall stack size
        // Set up connections to end roads
        ManualParkingRoad road = addNewParkingRoad(roadName,
                                                    stallSpec.getLength());
        if (road != null){
            return road.findNewSpace(stallSpec,
                                    ManualParkingRoad.SearchParameter.correctLength);
        }else {
            return null;
        }
    }

    @Override
    public List<Junction> getJunctions() {
        HashSet<Junction> junctions = new HashSet<>(super.getJunctions());
        for (ManualParkingRoad parkingRoad:parkingRoads){
            junctions.addAll(parkingRoad.getJunctions());
        }
        return new ArrayList<>(junctions);
    }

    @Override
    public List<Road> getRoads() {
        HashSet<Road> roads = new HashSet<>(super.getRoads());
        for (ManualParkingRoad parkingRoad:parkingRoads){
            roads.addAll(parkingRoad.getRoads());
        }
        return new ArrayList<>(roads);
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

    private void updateLastParkingLane(){
        for (int i = parkingRoads.size()-1; i >= 0; i--){
            ManualParkingRoad currentRoad = parkingRoads.get(i);
            if (i == parkingRoads.size()-1){
                currentRoad.setLastRoad(true);
            }else{
                currentRoad.setLastRoad(false);
            }
        }

    }

    public ArrayList<ManualParkingRoad> getParkingRoads(){
        return parkingRoads;
    }
}
