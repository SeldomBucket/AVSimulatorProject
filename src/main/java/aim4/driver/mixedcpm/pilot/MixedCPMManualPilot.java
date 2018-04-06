package aim4.driver.mixedcpm.pilot;

import aim4.driver.Driver;
import aim4.driver.BasicPilot;
import aim4.driver.mixedcpm.MixedCPMManualDriver;
import aim4.driver.mixedcpm.navigator.MixedCPMManualNavigator;
import aim4.map.connections.BasicConnection;
import aim4.map.connections.Corner;
import aim4.map.lane.Lane;
import aim4.map.mixedcpm.MixedCPMMapUtil;
import aim4.sim.simulator.mixedcpm.MixedCPMAutoDriverSimulator;
import aim4.vehicle.VehicleDriverModel;
import aim4.vehicle.VehicleUtil;
import aim4.vehicle.mixedcpm.MixedCPMBasicManualVehicle;
import aim4.driver.mixedcpm.coordinator.MixedCPMManualCoordinator.*;

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

    private boolean vehicleBelowCentreOfStallPhase2;

    /**
     * State of the vehicle when lining up and parking with the stall
     *
     * Phase 1: Making sure the heading of the vehicle is
     *          the same as the heading of the stall
     *      0  -  ENTER_STALL_INITIALLY,
     *      1  -  STOP_AT_END_OF_STALL,
     *      2  -  TURN_WHEELS_AND_REVERSE_TO_LINE_UP_HEADING,
     *      3  -  REVERSE_STRAIGHT_AFTER_LINING_UP_HEADING,
     *      4  -  GO_TO_END_OF_STALL_WITH_CORRECT_HEADING,
     *
     * Phase 2: Moving vehicle to be completely inside stall
     *          (by lining up the y position)
     *      5  -  TURN_WHEELS_TO_LINE_UP_Y,
     *      6  -  REVERSE_TO_LINE_UP_Y,
     *      7  -  TURN_WHEELS_TO_STRAIGHTEN_VEHICLE,
     *      8  -  REVERSE_TO_LINE_UP_HEADING_AGAIN,
     *      9  -  REENTER_STALL_WITH_CORRECT_Y_AND_HEADING,
     *      10 -  CHECK_ENTIRELY_INSIDE_STALL
     */
    private int parkingMechanicsState = 0;


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

            double heading = vehicle.getHeading();

            if (vehicleHeadingMatchesLane()
                    && !vehicleOutsideStartOfStall()
                    && !frontOfVehicleNearOrPastEndOfLane()
                    && parkingMechanicsState < 5) {
                // Move to phase 2 if vehicle is lined up and between beginning and end of stall
                parkingMechanicsState = 5;
            }

            // See parkingMechanicsState comment for description of this switch case
            switch (parkingMechanicsState){
                case 0:
                    if (frontOfVehicleNearOrPastEndOfLane()) {
                        parkingMechanicsState = 1;
                    }else{
                        turnWheelsFullyTowardsStall();
                        simpleThrottleAction();
                        dontPassParkingEndPoint(currentParkingStatus);
                    }
                    break;
                case 1:
                    if (Math.abs(vehicle.getVelocity()) < 0.01) {
                        parkingMechanicsState = 2;
                    }else{
                        vehicle.slowToStop();
                    }
                    break;
                case 2:
                    if (vehicleHeadingMatchesLane()) {
                        parkingMechanicsState = 3;
                    }else {
                        if (vehicle.getTargetStall().isLeftOfParkingRoad()) {
                            if (heading < vehicle.getTargetStall().getLane().getInitialHeading()) {
                                // Under rotated and on left
                                setWheelsFullLeft();
                            } else {
                                // Over rotated and on left
                                setWheelsFullRight();
                            }
                        } else {
                            if (heading < vehicle.getTargetStall().getLane().getInitialHeading()) {
                                // Over rotated and on right
                                setWheelsFullLeft();
                            } else {
                                // Under rotated and on right
                                setWheelsFullRight();
                            }
                        }
                        simpleThrottleActionReverse();
                    }
                    break;
                case 3:
                    if (!frontOfVehicleNearOrPastEndOfLane()) {
                        parkingMechanicsState = 4;
                    }else{
                        setWheelsStraight();
                        simpleThrottleActionReverse();
                    }
                    break;
                case 4:
                    if (frontOfVehicleNearOrPastEndOfLane()) {
                        parkingMechanicsState = 0;
                    }else{
                        setWheelsStraight();
                        simpleThrottleAction();
                        dontPassParkingEndPoint(currentParkingStatus);
                    }
                    break;
                case 5:
                    vehicleBelowCentreOfStallPhase2 = vehicle.gaugePointAtRear().getY() > this.driver.getCurrentLane().getEndPoint().getY();

                    if (vehicle.getTargetStall().isLeftOfParkingRoad()) {
                        if (vehicleBelowCentreOfStallPhase2) {
                            // Below centre and on left
                            setWheelsFullRight();
                        } else {
                            // Above centre and on left
                            setWheelsFullLeft();
                        }
                    } else {
                        if (vehicleBelowCentreOfStallPhase2) {
                            // Below centre and on right
                            setWheelsFullLeft();
                        } else {
                            // Above centre and on right
                            setWheelsFullRight();
                        }
                    }
                    parkingMechanicsState = 6;
                    break;
                case 6:
                    if (rearOfVehicleInYTolerance()) {
                        parkingMechanicsState = 7;
                    } else {
                        simpleThrottleActionReverse();
                    }
                    break;
                case 7:
                    if (vehicle.getTargetStall().isLeftOfParkingRoad()) {
                        if (!vehicleBelowCentreOfStallPhase2) {
                            // Below centre and on left
                            setWheelsFullRight();
                        } else {
                            // Above centre and on left
                            setWheelsFullLeft();
                        }
                    } else {
                        if (!vehicleBelowCentreOfStallPhase2) {
                            // Below centre and on right
                            setWheelsFullLeft();
                        } else {
                            // Above centre and on right
                            setWheelsFullRight();
                        }
                    }
                    parkingMechanicsState = 8;
                    break;
                case 8:
                    if (vehicleHeadingMatchesLane()){
                        parkingMechanicsState = 9;
                    }else {
                        simpleThrottleActionReverse();
                    }
                    break;
                case 9:
                    if (frontOfVehicleNearOrPastEndOfLane()) {
                        parkingMechanicsState = 10;
                    }else {
                        followCurrentLane();
                        //setWheelsStraight();
                        simpleThrottleAction();
                        dontPassParkingEndPoint(currentParkingStatus);
                    }
                case 10:
                    if (frontOfVehicleNearOrPastEndOfLane())
                    {
                        if (!vehicleEntirelyInsideStall()) {
                            parkingMechanicsState = 5;
                        }
                    }
                    break;
            }
        }
    }

    public void reverseOutOfStall() {
        driver.setCurrentLane(vehicle.getTargetStall().getParkingRoad().getCentreRoad().getOnlyLane());
        if (vehicle.getTargetStall().isLeftOfParkingRoad()) {
            setWheelsFullRight();
        } else {
            setWheelsFullLeft();
        }
        simpleThrottleActionReverse();
    }

    public boolean reversedOutOfStall(){
        boolean currentLaneIsManualStall = vehicle.getTargetStall().getParkingRoad().getCentreRoad().getOnlyLane() == driver.getCurrentLane();
        return currentLaneIsManualStall && vehicleOutsideStartOfStall();
    }

    private boolean vehicleEntirelyInsideStall(){

        Rectangle2D vehicleShape = this.getVehicle().gaugeShape().getBounds2D();
        boolean linedUpWithBottom = vehicleShape.getMaxY() <= this.vehicle.getTargetStall().getMaxY();
        boolean linedUpWithTop = vehicleShape.getMinY() >= this.vehicle.getTargetStall().getMinY();
        boolean linedUpWithLeft = vehicleShape.getMaxX() <= this.vehicle.getTargetStall().getMaxX();
        boolean linedUpWithRight = vehicleShape.getMinX() >= this.vehicle.getTargetStall().getMinX();

        if (MixedCPMAutoDriverSimulator.mapType() == MixedCPMMapUtil.MapType.STATIC) {
            return frontOfVehicleNearOrPastEndOfLane();

        }else{
            return linedUpWithBottom && linedUpWithLeft && linedUpWithTop && linedUpWithRight;
        }
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

//        double minY = vehicle.getTargetStall().getMaxY() - vehicle.getSpec().getWidth()/2;
//        double maxY = vehicle.getTargetStall().getMaxY() + vehicle.getSpec().getWidth()/2;
//        double tolerance;
//
//        tolerance = Math.abs(maxY-minY)/2;

        double tolerance = Math.abs(vehicle.getTargetStall().getWidth()-vehicle.getSpec().getWidth())/2-TOLERANCE_FOR_PARKING;

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

    private boolean vehicleOutsideStartOfStall(){
        Point2D pointBetweenFrontWheels = vehicle.gaugePosition();

        if (vehicle.getTargetStall().isLeftOfParkingRoad()) {
            return pointBetweenFrontWheels.getX() >= vehicle.getTargetStall().getMaxX();
        } else {
            return pointBetweenFrontWheels.getX() <= vehicle.getTargetStall().getMinX();
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
        return vehicleHeading < laneHeading + 0.08 &&
                vehicleHeading > laneHeading - 0.08;
    }

    public boolean linedUpWithStall(){
        return vehicleEntirelyInsideStall();// && !frontOfVehicleNearOrPastEndOfLane();
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
            setWheelsFullRight();
        }else{
            setWheelsFullLeft();
        }
    }

    public Lane getConnectionDepartureLane(){
        return connectionDepartureLane;
    }

    public void clearDepartureLane(){
        this.connectionDepartureLane = null;
    }
}
