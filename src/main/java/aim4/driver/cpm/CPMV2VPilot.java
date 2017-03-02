package aim4.driver.cpm;

import aim4.driver.AutoDriver;
import aim4.driver.Driver;
import aim4.driver.aim.pilot.BasicPilot;
import aim4.map.RightAngledCorner;
import aim4.map.lane.Lane;
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

    /**
     * The distance, expressed in units of the Vehicle's velocity, at which to
     * switch to a new lane when turning. {@value} seconds.
     */
    public static final double TRAVERSING_LANE_CHANGE_LEAD_TIME = 1.5; // sec

    // ///////////////////////////////
    // PRIVATE FIELDS
    // ///////////////////////////////

    private CPMBasicAutoVehicle vehicle;

    private AutoDriver driver;

    // ///////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////

    /**
     * Create an pilot to control a vehicle.
     *
     * @param vehicle      the vehicle to control
     * @param driver       the driver
     */
    public CPMV2VPilot(CPMBasicAutoVehicle vehicle, AutoDriver driver) {
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

    /**
     * Set the steering action when the vehicle is traversing an intersection.
     */
    public void takeSteeringActionForTraversingCorner(RightAngledCorner corner) {
        // TODO: CPM What if there are more than one lane in the roads of the corner?
        System.out.println("Steering around Corner!");
        Lane departureLane = corner.getExitLanes().get(0);
        // If we're not already in the departure lane
        if (driver.getCurrentLane() != departureLane) {
            // Find out how far from the road of the departure lane we are
            double distToLane = departureLane.nearestDistance(vehicle.gaugePosition());;
            // If we're close enough...
            double traversingLaneChangeDistance =
                    TRAVERSING_LANE_CHANGE_LEAD_TIME * vehicle.gaugeVelocity();
            if (distToLane < traversingLaneChangeDistance) {
                // Change to it
                driver.setCurrentLane(departureLane);
            }

        }
        // Otherwise we are still in the entry lane and should continue
        // Use the basic lane-following behavior
        followCurrentLane();
    }
}
