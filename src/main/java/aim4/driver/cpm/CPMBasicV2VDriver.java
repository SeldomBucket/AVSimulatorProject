package aim4.driver.cpm;

import aim4.driver.AutoDriver;
import aim4.driver.BasicDriver;
import aim4.driver.aim.coordinator.Coordinator;
import aim4.map.BasicMap;
import aim4.map.connections.BasicConnection;
import aim4.map.connections.Corner;
import aim4.map.SpawnPoint;
import aim4.map.connections.Junction;
import aim4.map.connections.SimpleIntersection;
import aim4.map.cpm.CPMMap;
import aim4.map.cpm.parking.ParkingLane;
import aim4.vehicle.AutoVehicleDriverModel;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;

import java.awt.geom.Area;

/**
 * An agent that drives a {@link aim4.vehicle.cpm.CPMBasicAutoVehicle} while
 * coordinating with other Vehicles.  Such an agent consists
 * of two sub-agents, a Coordinator and a Pilot. The two
 * agents communicate by setting state in this class.
 */
public class CPMBasicV2VDriver extends BasicDriver
                            implements AutoDriver {

    /////////////////////////////////
    // PRIVATE FIELDS
    /////////////////////////////////

    /** The vehicle this driver will control */
    private CPMBasicAutoVehicle vehicle;

    /** The sub-agent that controls coordination */
    private Coordinator coordinator;

    /** The map */
    private BasicMap map;

    /** Where this DriverAgent is coming from. */
    private SpawnPoint spawnPoint;

    /////////////////////////////////
    // CONSTRUCTORS
    /////////////////////////////////

    public CPMBasicV2VDriver(CPMBasicAutoVehicle vehicle, BasicMap map) {
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
    private static boolean intersectsArea(AutoVehicleDriverModel vehicle, Area area) {
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
     * {@inheritDoc} -- find out where this came from
     */
    //@Override
    public void setSpawnPoint(SpawnPoint spawnPoint) {
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
            coordinator = new CPMBasicCoordinator(vehicle, vehicle.getDriver());
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
        assert map instanceof CPMMap;
        if (((CPMMap) map).getCorners() == null){
            return null;
        }
        for (Corner corner : ((CPMMap) map).getCorners()){
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
        assert map instanceof CPMMap;
        if (((CPMMap) map).getJunctions() == null){
            return null;
        }
        for (Junction junction : ((CPMMap) map).getJunctions()){
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
        assert map instanceof CPMMap;
        if (((CPMMap) map).getIntersections() == null){
            return null;
        }
        for (SimpleIntersection intersection : ((CPMMap) map).getIntersections()){
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

    @Override
    public CPMBasicAutoVehicle getVehicle() {
        return vehicle;
    }
}
