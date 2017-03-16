package aim4.driver.cpm;

import aim4.driver.AutoDriver;
import aim4.driver.Driver;
import aim4.driver.aim.pilot.BasicPilot;
import aim4.map.connections.BasicConnection;
import aim4.map.connections.Corner;
import aim4.map.connections.Junction;
import aim4.map.connections.SimpleIntersection;
import aim4.map.lane.Lane;
import aim4.vehicle.VehicleDriverModel;
import aim4.vehicle.VehicleUtil;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;

import java.util.Random;

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

    private Lane connectionDepartureLane;

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
        this.connectionDepartureLane = null;
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
     * If the end of the parking section is close, we need to slow to a stop.
     * Here, we treat the parking end point like a vehicle so use following distance.
     */
    public void dontPassParkingEndPoint(){
        double stoppingDistance =
                VehicleUtil.calcDistanceToStop(vehicle.gaugeVelocity(),
                    vehicle.getSpec().getMaxDeceleration());

        double followingDistance = stoppingDistance + MINIMUM_FOLLOWING_DISTANCE;

        if (vehicle.distanceToParkingEndPoint() < followingDistance) {
            vehicle.slowToStop();
        }
    }

    /**
     * Set the steering action when the vehicle is traversing a corner.
     */
    public void takeSteeringActionForTraversing(BasicConnection connection) {
        System.out.println("Steering around a connection! Connection type: " + connection.getClass());

        // Check if we already have a departure lane.
        if (connectionDepartureLane == null) {
            // Determine the departure lane - depends if in a corner, junction or intersection
            Random random = new Random();
            if (connection instanceof Corner) {
                // There is only one exit to a Corner
                connectionDepartureLane = connection.getExitLanes().get(0);
            } else if (connection instanceof Junction) {
                // Could have 1 or 2 exits
                // TODO CPM Lets randomise for now
                if (connection.getExitLanes().size() == 1) {
                    connectionDepartureLane = connection.getExitLanes().get(0);
                } else {
                    int index = random.nextInt(2);
                    connectionDepartureLane = connection.getExitLanes().get(index);
                }
            } else if (connection instanceof SimpleIntersection) {
                // There will be 2 exits
                // TODO CPM Lets randomise for now
                int index = random.nextInt(2);
                connectionDepartureLane = connection.getExitLanes().get(index);
            }
            if (connectionDepartureLane == null) {
                throw new RuntimeException("Departure lane for the connection has not established!");
            }
        }

        // If we're not already in the departure lane
        if (driver.getCurrentLane() != connectionDepartureLane) {
            // Find out how far from the road of the departure lane we are
            double distToLane = connectionDepartureLane.nearestDistance(vehicle.gaugePosition());;
            // If we're close enough...
            double traversingLaneChangeDistance =
                    TRAVERSING_LANE_CHANGE_LEAD_TIME * vehicle.gaugeVelocity();
            if (distToLane < traversingLaneChangeDistance) {
                // Change to it
                driver.setCurrentLane(connectionDepartureLane);
            }

        }
        // Otherwise we are still in the entry lane and should continue
        // Use the basic lane-following behavior
        followCurrentLane();
    }

    public Lane getConnectionDepartureLane(){
        return connectionDepartureLane;
    }

    public void clearDepartureLane(){
        this.connectionDepartureLane = null;
    }
}
