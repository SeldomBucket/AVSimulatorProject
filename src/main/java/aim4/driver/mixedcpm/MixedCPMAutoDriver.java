package aim4.driver.mixedcpm;

import aim4.driver.AutoDriver;
import aim4.driver.BasicDriver;
import aim4.driver.mixedcpm.coordinator.MixedCPMAutoCoordinator;
import aim4.driver.mixedcpm.coordinator.MixedCPMManualCoordinator;
import aim4.map.Road;
import aim4.map.SpawnPoint;
import aim4.map.connections.Corner;
import aim4.map.connections.Junction;
import aim4.map.connections.SimpleIntersection;
import aim4.map.lane.Lane;
import aim4.map.mixedcpm.MixedCPMMap;
import aim4.map.mixedcpm.MixedCPMSpawnPoint;
import aim4.vehicle.AutoVehicleDriverModel;
import aim4.vehicle.mixedcpm.MixedCPMBasicAutoVehicle;

import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MixedCPMAutoDriver extends MixedCPMDriver {


    /////////////////////////////////
    // PRIVATE FIELDS
    /////////////////////////////////

    /** The vehicle this driver will control */
    protected MixedCPMBasicAutoVehicle vehicle;

    /** The sub-agent that controls coordination */
    protected MixedCPMAutoCoordinator coordinator;

    /** The map */
    protected MixedCPMMap map;

    /** Where this DriverAgent is coming from. */
    protected MixedCPMSpawnPoint spawnPoint;

    /** path the vehicle has to follow to get to the target stall*/
    protected ArrayList<Lane> pathToTargetStall;

    /////////////////////////////////
    // CONSTRUCTORS
    /////////////////////////////////

    public MixedCPMAutoDriver(MixedCPMBasicAutoVehicle vehicle, MixedCPMMap map) {
        this.vehicle = vehicle;
        this.map = map;
        coordinator = null;

        this.pathToTargetStall = new ArrayList<>();
    }

    /////////////////////////////////
    // PRIVATE METHODS
    /////////////////////////////////

    /**
     * Determine whether the given Vehicle is currently inside an area
     *
     * @param vehicle     the vehicle
     * @param area        the area
     * @return            whether the Vehicle is currently in the area
     */
    protected static boolean intersectsArea(AutoVehicleDriverModel vehicle, Area area) {
        // TODO: move this function to somewhere else.

        // As a quick check, see if the front or rear point is in the area
        // Most of the time this should work
        if(area.contains(vehicle.gaugePosition()) || area.contains(vehicle.gaugePointAtRear())){
            return true;
        } else {
            // We actually have to check to see if the Area of the
            // Vehicle and the given Area have a nonempty intersection
            Area vehicleArea = new Area(vehicle.gaugeShape());
            // Important that it is in this order, as it is destructive to the caller
            vehicleArea.intersect(area);
            return !vehicleArea.isEmpty();
        }
    }

    /**
     * Determine whether the given Vehicle is currently inside an area
     *
     * @param vehicle     the vehicle
     * @param area        the area
     * @return            whether the Vehicle is currently in the area
     */
    protected static boolean frontIntersectsArea(AutoVehicleDriverModel vehicle, Area area) {
        // TODO: move this function to somewhere else.

        // As a quick check, see if the front or rear point is in the area
        // Most of the time this should work
        if(area.contains(vehicle.gaugePointBetweenFrontWheels()) || area.contains(vehicle.gaugePointAtRear())){
            return true;
        } else {
            // We actually have to check to see if the Area of the
            // Vehicle and the given Area have a nonempty intersection
            Area vehicleArea = new Area(vehicle.gaugeShape());
            // Important that it is in this order, as it is destructive to the caller
            vehicleArea.intersect(area);
            return !vehicleArea.isEmpty();
        }
    }

    /////////////////////////////////
    // PUBLIC METHODS
    /////////////////////////////////

    /**
     * Set where this driver agent is coming from.
     *
     * @param spawnPoint the spawn point that generated the driver
     */
    public void setSpawnPoint(MixedCPMSpawnPoint spawnPoint) {
        this.spawnPoint = spawnPoint;
    }

    /**
     * Get where this DriverAgent is coming from.
     *
     * @return the SpawnPoint where this DriverAgent came from
     */
    public SpawnPoint getSpawnPoint() {
        if(spawnPoint == null) {
            throw new RuntimeException("Driver is without origin!");
        }
        return spawnPoint;
    }

    /**
     * Take control actions for driving the agent's Vehicle.  This allows
     * both the Coordinator and the Pilot to act (in that order).
     */
    @Override
    public void act() {
        super.act();
        if (coordinator == null){
            // Create a new coordinator if the vehicle doesn't already have one.
            coordinator = new MixedCPMAutoCoordinator(vehicle, (MixedCPMAutoDriver)vehicle.getDriver());
        }

        // the newly created coordinator can be called immediately.
        if (!coordinator.isTerminated()) {
            coordinator.act();
        }
    }

    /**
     * Whether or not the Vehicle controlled by this driver agent
     * is inside a Corner.
     *
     * @return the Corner that the driver is in,
     * or null if not in a corner.
     */
    public Corner inCorner() {
        if (map.getCorners() == null){
            return null;
        }
        for (Corner corner : map.getCorners()){
            if (intersectsArea(vehicle, corner.getArea())){
                return corner;
            }
        }
        return null;
    }

    /**
     * Whether or not the Vehicle controlled by this driver agent
     * is inside a Junction.
     *
     * @return the Junction that the driver is in,
     * or null if not in a junction.
     */
    public Junction inJunction() {
        List<Junction> mapJunctions = map.getJunctions();

        ArrayList<Junction> junctionsBeenInBefore = new ArrayList<>();
        ArrayList<Junction> newJunction = new ArrayList<>();

        for (Junction junction : mapJunctions){
            Area area = junction.getArea();
                // if centre of vehicle intersects a non-stall junction OR
                // the front of the vehicle intersects the stall junction (to give the vehicle enough time to turn)

            if (intersectsArea(vehicle, area)){
                // if centre of vehicle intersects a non-stall junction OR
                // the front of the vehicle intersects the stall junction (to give the vehicle enough time to turn)
                if(coordinator.hasBeenInJunctionAlready(junction)){
                    junctionsBeenInBefore.add(junction);
                }else{
                    newJunction.add(junction);
                }
            }
        }
        if (newJunction.size() == 0 && junctionsBeenInBefore.size() != 0) {
            // i.e. have been in all the junctions before
            /*
            for (Junction junction:junctionsBeenInBefore) {
                if (junction.getExitRoads().get(0).getName() == "bottomRoad") {
                    // prioritise a junction with the bottom road in it
                    return junction;
                }
            }
            */
            return junctionsBeenInBefore.get(0);
        } else if(newJunction.size() != 0) {
            if (vehicle.getTargetLane() != null) {
                for (Junction junction:newJunction){
                    /*if (junction.getExitRoads().get(0).getName() == "bottomRoad")
                    {
                        // prioritise a junction with the bottom road in it
                        return junction;
                    }*/
                    if (junction.getRoads().contains(vehicle.getTargetLane())) {
                        // then prioritise a junction with your parking space in it
                        return junction;
                    }
                }
            }
            return newJunction.get(0);
        }
        return null;
    }

    /**
     * Whether or not the Vehicle controlled by this driver agent
     * is inside a SimpleIntersection.
     *
     * @return the SimpleIntersection that the driver is in,
     * or null if not in an intersection.
     */
    public SimpleIntersection inIntersection() {
        if (map.getIntersections() == null){
            return null;
        }
        for (SimpleIntersection intersection : map.getIntersections()){
            if (intersectsArea(vehicle, intersection.getArea())){
                return intersection;
            }
        }
        return null;
    }

    public boolean isInTargetLane(){
        try {
            return currentLane == vehicle.getTargetLane().getOnlyLane();
        }catch (NullPointerException ex){
            return false;
        }
    }

    public boolean isParked(){
        return coordinator.getParkingStatus() == MixedCPMAutoCoordinator.ParkingStatus.PARKED;
    }

    public boolean isOnBottomRoad(){
        return map.getBottomRoad().getOnlyLane() == this.getCurrentLane();
    }

    @Override
    public MixedCPMBasicAutoVehicle getVehicle() {
        return vehicle;
    }

    public MixedCPMAutoCoordinator.ParkingStatus getParkingStatus() {
        return coordinator.getParkingStatus();
    }
}
