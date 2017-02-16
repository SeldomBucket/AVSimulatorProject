package aim4.driver.cpm;

import aim4.driver.Driver;
import aim4.driver.aim.pilot.BasicPilot;
import aim4.vehicle.VehicleDriverModel;
import aim4.vehicle.VehicleUtil;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;

/**
 * An agent that pilots an AutoVehicleDriverModel autonomously. This agent
 * attempts to emulate the behavior of a real-world autonomous driver agent in
 * terms of physically controlling the Vehicle.
 */
public class CPMV2VPilot extends BasicPilot{

    // ///////////////////////////////
    // CONSTANTS
    // ///////////////////////////////

    /**
     * The minimum distance to maintain between the Vehicle controlled by this
     * AutonomousPilot and the one in front of it. {@value} meters.
     */
    public static final double MINIMUM_FOLLOWING_DISTANCE = 0.5; // meters

    // ///////////////////////////////
    // PRIVATE FIELDS
    // ///////////////////////////////

    private CPMBasicAutoVehicle vehicle;

    private CPMBasicV2VDriver driver;

    // ///////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////

    /**
     * Create an pilot to control a vehicle.
     *
     * @param vehicle      the vehicle to control
     * @param driver       the driver
     */
    public CPMV2VPilot(CPMBasicAutoVehicle vehicle, CPMBasicV2VDriver driver) {
        this.vehicle = vehicle;
        this.driver = driver;
    }

    // ///////////////////////////////
    // PUBLIC METHODS
    // ///////////////////////////////

    @Override
    public VehicleDriverModel getVehicle() {
        return vehicle;
    }

    @Override
    public Driver getDriver() {
        return driver;
    }

    /////////////////////////////////
    // PUBLIC METHODS
    /////////////////////////////////

    /**
     * The simple throttle action.
     */
    public void simpleThrottleAction() {
        cruise();
        dontHitVehicleInFront();
    }

    /**
     * Stop before hitting the car in front of us.
     *
     */
    private void dontHitVehicleInFront() {
//    double stoppingDistance = distIfStopNextTimeStep(vehicle);
        double stoppingDistance =
                VehicleUtil.calcDistanceToStop(vehicle.gaugeVelocity(),
                        vehicle.getSpec().getMaxDeceleration());
        double followingDistance = stoppingDistance + MINIMUM_FOLLOWING_DISTANCE;
        if (VehicleUtil.distanceToCarInFront(vehicle) < followingDistance) {
            vehicle.slowToStop();
        }
    }
}
