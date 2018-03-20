package aim4.driver.mixedcpm;

import aim4.driver.AutoDriver;
import aim4.driver.BasicDriver;
import aim4.driver.mixedcpm.coordinator.MixedCPMManualCoordinator;
import aim4.map.SpawnPoint;
import aim4.map.connections.Corner;
import aim4.map.connections.Junction;
import aim4.map.connections.SimpleIntersection;
import aim4.map.cpm.CPMSpawnPoint;
import aim4.map.cpm.parking.ParkingLane;
import aim4.map.mixedcpm.MixedCPMMap;
import aim4.vehicle.AutoVehicleDriverModel;
import aim4.vehicle.mixedcpm.MixedCPMBasicManualVehicle;

import java.awt.geom.Area;

public class MixedCPMManualDriver extends BasicDriver implements AutoDriver {


    /////////////////////////////////
    // PRIVATE FIELDS
    /////////////////////////////////

    /** The vehicle this driver will control */
    protected MixedCPMBasicManualVehicle vehicle;

    /** The sub-agent that controls coordination */
    protected MixedCPMManualCoordinator coordinator;

    /** The map */
    protected MixedCPMMap map;

    /** Where this DriverAgent is coming from. */
    protected CPMSpawnPoint spawnPoint;

    /////////////////////////////////
    // CONSTRUCTORS
    /////////////////////////////////

    public MixedCPMManualDriver(MixedCPMBasicManualVehicle vehicle, MixedCPMMap map) {
        this.vehicle = vehicle;
        this.map = map;
        coordinator = null;
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

    /////////////////////////////////
    // PUBLIC METHODS
    /////////////////////////////////

    /**
     * Set where this driver agent is coming from.
     *
     * @param spawnPoint the spawn point that generated the driver
     */
    public void setSpawnPoint(CPMSpawnPoint spawnPoint) {
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
            coordinator = new MixedCPMManualCoordinator(vehicle, (MixedCPMManualDriver)vehicle.getDriver());
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
        if (map.getJunctions() == null){
            return null;
        }
        for (Junction junction : map.getJunctions()){
            if (intersectsArea(vehicle, junction.getArea())){
                return junction;
            }
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

    public boolean inParkingLane() {
        if (currentLane instanceof ParkingLane) {
            return true;
        }
        return false;
    }

    public ParkingLane getParkingLaneCurrentlyIn(){
        assert(currentLane instanceof ParkingLane);
        return (ParkingLane) currentLane;
    }

    @Override
    public MixedCPMBasicManualVehicle getVehicle() {
        return vehicle;
    }

    public MixedCPMManualCoordinator.ParkingStatus getParkingStatus() {
        return coordinator.getParkingStatus();
    }
}
