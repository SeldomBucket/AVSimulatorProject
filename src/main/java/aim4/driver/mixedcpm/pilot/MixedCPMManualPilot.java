package aim4.driver.mixedcpm.pilot;

import aim4.driver.Driver;
import aim4.driver.BasicPilot;
import aim4.driver.mixedcpm.MixedCPMManualDriver;
import aim4.driver.mixedcpm.navigator.MixedCPMManualNavigator;
import aim4.map.connections.BasicConnection;
import aim4.map.connections.Corner;
import aim4.map.lane.Lane;
import aim4.vehicle.VehicleDriverModel;
import aim4.vehicle.VehicleUtil;
import aim4.vehicle.mixedcpm.MixedCPMBasicManualVehicle;
import aim4.driver.mixedcpm.coordinator.MixedCPMManualCoordinator.*;

import java.awt.geom.Point2D;


/**
 * An agent that pilots an AutoVehicleDriverModel autonomously. This agent
 * attempts to emulate the behavior of a real-world autonomous driver agent in
 * terms of physically controlling the Vehicle.
 */
public class MixedCPMManualPilot extends BasicPilot{

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

    /**
     * The tolerance for the vehicle in the parking space - the vehicle should
     * be no more than 0.5m either side of the centre line of the lane.
     * {@value} meters;
     */
    public static final double TOLERANCE_FOR_PARKING = 0.5; // meters

    // ///////////////////////////////
    // PRIVATE FIELDS
    // ///////////////////////////////

    private MixedCPMBasicManualVehicle vehicle;

    private MixedCPMManualDriver driver;

    private MixedCPMManualNavigator navigator;

    private Lane connectionDepartureLane;

    private boolean reversingInParkingLane = false;

    // ///////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////

    /**
     * Create an pilot to control a vehicle.
     *
     * @param vehicle      the vehicle to control
     * @param driver       the driver
     */
    public MixedCPMManualPilot(MixedCPMBasicManualVehicle vehicle, MixedCPMManualDriver driver, MixedCPMManualNavigator navigator) {
        this.vehicle = vehicle;
        this.driver = driver;
        this.navigator = navigator;
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
     * The simple throttle action.
     */
    public void simpleThrottleActionReverse() {
        cruiseReverse();
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

    public void parkInLane(ParkingStatus currentParkingStatus){
        Point2D pointBetweenFrontWheels = this.getVehicle().gaugePointBetweenFrontWheels();
        Point2D currentLaneEndPoint = this.driver.getCurrentLane().getEndPoint();
        if (currentParkingStatus == ParkingStatus.PARKING) {

            double stoppingDistance =
                    VehicleUtil.calcDistanceToStop(vehicle.gaugeVelocity(),
                            vehicle.getSpec().getMaxDeceleration());
            double followingDistance = stoppingDistance + MINIMUM_FOLLOWING_DISTANCE;

            // if we're at the end of the stall stack

            if (frontOfVehicleNearEndOfLane() && !reversingInParkingLane) {
                if (pointBetweenFrontWheels.getY() >
                        currentLaneEndPoint.getY() + TOLERANCE_FOR_PARKING ||
                        pointBetweenFrontWheels.getY() <
                        currentLaneEndPoint.getY() - TOLERANCE_FOR_PARKING) {
                    // reverse straight for a bit

                    simpleThrottleActionReverse();
                    reversingInParkingLane = true;
                }
            }
            if (middleOfVehiclePastStartOfLane() && reversingInParkingLane) {
                // Follow lane as normal
                followCurrentLane();
                simpleThrottleAction();
                dontPassParkingEndPoint(currentParkingStatus);
                reversingInParkingLane = false;
            }

        }
    }

    public boolean frontOfVehicleNearEndOfLane(){
        Point2D centreOfVehicle = this.getVehicle().gaugePosition();
        Point2D currentLaneEndPoint = this.driver.getCurrentLane().getEndPoint();
        if (vehicle.getTargetStall().getLane().getStartPoint().getX()
                < vehicle.getTargetStall().getLane().getEndPoint().getX()){
            // If start point on left of end point
            return centreOfVehicle.getX() >= currentLaneEndPoint.getX()-MINIMUM_FOLLOWING_DISTANCE;
        }else{
            // If start point on right of end point
            return centreOfVehicle.getX() <= currentLaneEndPoint.getX()+MINIMUM_FOLLOWING_DISTANCE;
        }
    }

    public boolean middleOfVehiclePastStartOfLane(){
        Point2D pointBetweenFrontWheels = this.getVehicle().gaugePointBetweenFrontWheels();
        Point2D currentLaneStartPoint = this.driver.getCurrentLane().getStartPoint();
        if (vehicle.getTargetStall().getLane().getStartPoint().getX()
                < vehicle.getTargetStall().getLane().getEndPoint().getX()){
            // If start point on left of end point
            return pointBetweenFrontWheels.getX() <= currentLaneStartPoint.getX();
        }else{
            // If start point on right of end point
            return pointBetweenFrontWheels.getX() >= currentLaneStartPoint.getX();
        }
    }

    /**
     * If the end of the parking section is close, we need to slow to a stop
     * If we are PARKING.
     * Here, we treat the parking end point like a vehicle so use following distance.
     * Makes sure the vehicle doesn't go past the end of the lane
     */
    public void dontPassParkingEndPoint(ParkingStatus currentParkingStatus){
        if (currentParkingStatus == ParkingStatus.PARKING) {
            double stoppingDistance =
                    VehicleUtil.calcDistanceToStop(vehicle.gaugeVelocity(),
                            vehicle.getSpec().getMaxDeceleration());

            double followingDistance = stoppingDistance + MINIMUM_FOLLOWING_DISTANCE;
            double distanceToEndPoint = vehicle.distanceToParkingEndPoint();
            if (distanceToEndPoint < followingDistance) {
                vehicle.slowToStop();
            }
        }
    }

    public boolean isParked(){
        double stoppingDistance =
                VehicleUtil.calcDistanceToStop(vehicle.gaugeVelocity(),
                        vehicle.getSpec().getMaxDeceleration());
        double followingDistance = stoppingDistance + MINIMUM_FOLLOWING_DISTANCE;
        double distanceToEndPoint = vehicle.distanceToParkingEndPoint();
        return getVehicle().gaugeVelocity() == 0 && distanceToEndPoint < followingDistance;
    }

    /**
     * Set the steering action when the vehicle is traversing a corner.
     */
    public void takeSteeringActionForTraversing(BasicConnection connection,
                                                ParkingStatus parkingStatus) {
        // Check if we already have a departure lane.
        if (connectionDepartureLane == null) {
            // Determine the departure lane
            if (connection instanceof Corner) {
                // There is only one exit to a Corner
                connectionDepartureLane = connection.getExitLanes().get(0);
            } else {
                // There could be a choice of where to go, use the navigator
                connectionDepartureLane = navigator.navigateConnection(connection,
                        parkingStatus);
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
