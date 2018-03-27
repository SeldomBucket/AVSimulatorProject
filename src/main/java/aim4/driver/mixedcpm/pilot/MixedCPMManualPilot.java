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

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


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
    public static final double TOLERANCE_FOR_PARKING = 0.1; // meters

    // ///////////////////////////////
    // PRIVATE FIELDS
    // ///////////////////////////////

    private MixedCPMBasicManualVehicle vehicle;

    private MixedCPMManualDriver driver;

    private MixedCPMManualNavigator navigator;

    private Lane connectionDepartureLane;

    private boolean reversingInParkingLane = false;

    private boolean parkingPhase1Completed = false;

    private int phase2Phase = 0;


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
        if (currentParkingStatus == ParkingStatus.PARKING) {
            if (!parkingPhase1Completed) {
                // PHASE 1 - Forward and reverse until heading is correct for stall
                // First, get it near enough in the stall
                if (frontOfVehicleNearOrPastEndOfLane() && !reversingInParkingLane) {
                    if (!vehicleEntirelyInsideStall()) {
                        // if Y alignment is out
                        // reverse for a bit
                        if (!vehicle.getTargetStall().isLeftOfParkingRoad()) {
                            setWheelsFullLeft();
                        } else {
                            setWheelsFullRight();
                        }
                        simpleThrottleActionReverse();
                        reversingInParkingLane = true;
                    }
                } else if (vehiclePastStartOfLane() && reversingInParkingLane) {// vehiclePastStartOfLane()
                    // Follow lane as normal
                    followCurrentLane();
                    simpleThrottleAction();
                    dontPassParkingEndPoint(currentParkingStatus);
                    reversingInParkingLane = false;
                } else if (vehicleHeadingMatchesLane()) {
                    followCurrentLane();
                    simpleThrottleAction();
                    dontPassParkingEndPoint(currentParkingStatus);
                    reversingInParkingLane = false;
                } else if (!reversingInParkingLane) {
                    turnWheelsFullyTowardsStall();
                    simpleThrottleAction();
                    dontPassParkingEndPoint(currentParkingStatus);
                } else if (reversingInParkingLane) {
                    simpleThrottleActionReverse();
                }

                if (vehicleHeadingMatchesLane() && !vehiclePastStartOfLane() && !frontOfVehicleNearOrPastEndOfLane()){
                    parkingPhase1Completed = true;
                }
            }else{
                // PHASE 2 - line up correctly in lane
                double steeringAngle = vehicle.getSteeringAngle();
                double heading = vehicle.getHeading();
                Point2D rear = vehicle.gaugePointAtRear();
                Point2D front = vehicle.gaugePosition();

                boolean vehicleBelowCentreOfStall = vehicle.gaugePointAtRear().getY() > this.driver.getCurrentLane().getEndPoint().getY();

                switch (phase2Phase){
                    case 0:
                        if (!vehicleEntirelyInsideStall()){
                            if(vehicleBelowCentreOfStall){
                                setWheelsFullLeft();
                            }else{
                                setWheelsFullRight();
                            }
                            double steeringAngle2 = vehicle.getSteeringAngle();
                            simpleThrottleActionReverse();
                            phase2Phase = 1;
                        }
                        break;
                    case 1:
                        if (rearOfVehicleInYTolerance()) {
                            if (vehicle.gaugePosition().getY() > this.driver.getCurrentLane().getEndPoint().getY()) {
                                setWheelsFullRight();
                            } else {
                                setWheelsFullLeft();
                            }
                            simpleThrottleActionReverse();
                            phase2Phase = 2;
                        }
                        break;
                    case 2:
                        if (vehicleHeadingMatchesLane()){
                            followCurrentLane();
                            simpleThrottleAction();
                            dontPassParkingEndPoint(currentParkingStatus);
                        }
                        break;
                }
            }
        }
    }

    private boolean vehicleEntirelyInsideStall(){
        Rectangle2D vehicleShape = this.getVehicle().gaugeShape().getBounds2D();
        boolean linedUpWithBottom = vehicleShape.getMaxY() <= this.vehicle.getTargetStall().getMaxY();
        boolean linedUpWithTop = vehicleShape.getMinY() >= this.vehicle.getTargetStall().getMinY();
        boolean linedUpWithLeft = vehicleShape.getMaxX() <= this.vehicle.getTargetStall().getMaxX();
        boolean linedUpWithRight = vehicleShape.getMinX() >= this.vehicle.getTargetStall().getMinX();

        return linedUpWithBottom && linedUpWithLeft && linedUpWithTop && linedUpWithRight;
    }

    private boolean frontOfVehicleInYTolerance(){

        Point2D pointBetweenFrontWheels = this.getVehicle().gaugePosition();
        Point2D currentLaneEndPoint = this.driver.getCurrentLane().getEndPoint();
        Rectangle2D laneShape = this.driver.getCurrentLane().getShape().getBounds2D();

        double tolerance = Math.abs(laneShape.getHeight()-vehicle.getSpec().getWidth())/2-TOLERANCE_FOR_PARKING;

        boolean frontInPosition = pointBetweenFrontWheels.getY() > currentLaneEndPoint.getY() - tolerance &&
                pointBetweenFrontWheels.getY() < currentLaneEndPoint.getY() + tolerance;

        return frontInPosition;
    }

    private boolean rearOfVehicleInYTolerance(){

        Point2D pointBetweenRearWheels = this.getVehicle().gaugePointAtRear();
        Point2D currentLaneEndPoint = this.driver.getCurrentLane().getEndPoint();
        Rectangle2D laneShape = this.driver.getCurrentLane().getShape().getBounds2D();

        double tolerance = Math.abs(laneShape.getHeight()-vehicle.getSpec().getWidth())/2-TOLERANCE_FOR_PARKING;

        boolean rearInPosition = pointBetweenRearWheels.getY() > currentLaneEndPoint.getY() - tolerance &&
                pointBetweenRearWheels.getY() < currentLaneEndPoint.getY() + tolerance;

        return rearInPosition;
    }

    private boolean frontOfVehicleNearOrPastEndOfLane(){

        double stoppingDistance =
                VehicleUtil.calcDistanceToStop(vehicle.gaugeVelocity(),
                        vehicle.getSpec().getMaxDeceleration());
        Point2D centreOfVehicle = this.getVehicle().gaugePosition();
        Point2D currentLaneEndPoint = this.driver.getCurrentLane().getEndPoint();
        if (vehicle.getTargetStall().isLeftOfParkingRoad()){
            return centreOfVehicle.getX() <= currentLaneEndPoint.getX()+(MINIMUM_FOLLOWING_DISTANCE+stoppingDistance);
        }else{
            return centreOfVehicle.getX() >= currentLaneEndPoint.getX()-(MINIMUM_FOLLOWING_DISTANCE+stoppingDistance);
        }
    }

    private boolean vehiclePastStartOfLane(){
        Point2D pointBetweenFrontWheels = this.getVehicle().gaugePosition();
        Point2D currentLaneStartPoint = this.driver.getCurrentLane().getStartPoint();

        if (vehicle.getTargetStall().isLeftOfParkingRoad()) {
            return pointBetweenFrontWheels.getX() >= currentLaneStartPoint.getX();
        } else {
            return pointBetweenFrontWheels.getX() <= currentLaneStartPoint.getX();
        }
    }

    /**
     * If the end of the parking section is close, we need to slow to a stop
     * If we are PARKING.
     * Here, we treat the parking end point like a vehicle so use following distance.
     * Makes sure the vehicle doesn't go past the end of the lane
     */
    public void dontPassParkingEndPoint(ParkingStatus currentParkingStatus){
        if (currentParkingStatus == ParkingStatus.PARKING || currentParkingStatus == ParkingStatus.PARKED) {
            if (frontOfVehicleNearOrPastEndOfLane()) {
                vehicle.slowToStop();
            }
        }
    }

    public boolean vehicleHeadingMatchesLane(){
        double laneHeading = driver.getCurrentLane().getInitialHeading();
        double vehicleHeading = vehicle.getHeading();
        return vehicleHeading < laneHeading + 0.05 &&
                vehicleHeading > laneHeading - 0.05;
    }

    public boolean linedUpWithStall(){
        return vehicleEntirelyInsideStall() && !frontOfVehicleNearOrPastEndOfLane();
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



    public void turnWheelsFullyTowardsStall(){
        if (vehicle.getTargetStall().isLeftOfParkingRoad()){
            setWheelsFullLeft();
        }else{
            setWheelsFullRight();
        }
    }

    public Lane getConnectionDepartureLane(){
        return connectionDepartureLane;
    }

    public void clearDepartureLane(){
        this.connectionDepartureLane = null;
    }
}
