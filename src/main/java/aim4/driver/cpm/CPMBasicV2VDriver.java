package aim4.driver.cpm;

import aim4.driver.AutoDriver;
import aim4.driver.BasicDriver;
import aim4.map.SpawnPoint;
import aim4.map.cpm.VerySimpleMap;
import aim4.vehicle.VehicleDriverModel;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;

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

    /** The map */
    private VerySimpleMap simpleMap;

    /** Where this DriverAgent is coming from. */
    private SpawnPoint spawnPoint;

    /////////////////////////////////
    // CONSTRUCTORS
    /////////////////////////////////

    public CPMBasicV2VDriver(CPMBasicAutoVehicle vehicle, VerySimpleMap simpleMap) {
        this.vehicle = vehicle;
        this.simpleMap = simpleMap;
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
     * Take control actions for driving the agent's Vehicle.  This allows
     * both the Coordinator and the Pilot to act (in that order).
     */
    @Override
    public void act() {
        super.act();
    }

    @Override
    public CPMBasicAutoVehicle getVehicle() {
        return vehicle;
    }
}
