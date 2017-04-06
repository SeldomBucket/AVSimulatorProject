package aim4.vehicle.cpm;

import aim4.driver.AutoDriver;
import aim4.driver.Driver;
import aim4.driver.cpm.CPMCoordinator.*;
import aim4.driver.cpm.CPMV2VDriver;
import aim4.map.connections.BasicConnection;
import aim4.map.cpm.parking.ParkingLane;
import aim4.vehicle.BasicAutoVehicle;
import aim4.vehicle.VehicleSpec;

import java.awt.geom.Point2D;

/**
 * The basic autonomous vehicle for CPM simulations.
 */
public class CPMBasicAutoVehicle extends BasicAutoVehicle {

    /////////////////////////////////
    // PRIVATE FIELDS
    /////////////////////////////////

    /**
     * The Driver controlling this vehicle.
     */
    protected CPMV2VDriver driver;

    /**
     * The target parking lane for this vehicle, assigned
     * on entry and on re-entry of the car park.
     */
    protected ParkingLane targetParkingLane;

    /**
     * The length of time the vehicle should park for,
     * from entering the car park (crossing the entry
     * sensored line) to when it should change it's
     * parking status to EXITING.
     */
    protected double parkingTime;

    /**
     * The time left until the vehicle needs to exit the car park.
     * Starts with parkingTime and is decreased with every timestep
     * by the coordinator (who updates the parking status when it's
     * tme to exit.)
     */
    protected double timeToExit;

    /**
     * The distance the vehicle has travelled whilst in the car park.
     * This is an estimate as it doesn't consider the trajectories
     * through road connections (corners, junctions, intersections).
     */
    protected double estimatedDistanceTravelled;

    /**
     * The last connection the vehicle traversed through.
     * This is used to calculate an estimation of the
     * distance travelled by the vehicle in the car park.
     */
    protected BasicConnection lastConnection;

    /**
     * If the vehicle has entered the car park. Used by the simulator
     * to know when to start depleting the parking time.
     */
    protected boolean hasEntered;

    // messaging

    /**
     * The inbox for messages from the car park StatusMonitor.
     * There will only ever be one message, which will be a
     * ParkingLane (or null, if no room for vehicle to park).
     */
    protected ParkingLane I2Vinbox;

    /**
     * The inbox for messages from other vehicles. There will only ever
     * be one message, which will be from the vehicle behind it, which
     * will ask it to relocate. The message will indicate what this
     * vehicle should change it's parking status to.
     */
    protected ParkingStatus V2Vinbox;

    /**
     * The vehicle that is directly in front of this one on the same
     * parking lane, used to send RELOCATING messages.
     */
    protected CPMBasicAutoVehicle vehicleInFront;

    /**
     * Construct a vehicle
     *
     * @param spec            the vehicle's specification
     * @param pos             the initial position of the Vehicle
     * @param heading         the initial heading of the Vehicle
     * @param steeringAngle   the initial steering angle of the Vehicle
     * @param velocity        the initial velocity of the Vehicle
     * @param targetVelocity  the initial target velocity
     * @param acceleration    the initial acceleration of the Vehicle
     * @param currentTime     the current time
     * @param parkingTime     how long the vehicle will park for
     */
    public CPMBasicAutoVehicle(VehicleSpec spec,
                               Point2D pos,
                               double heading,
                               double steeringAngle,
                               double velocity,
                               double targetVelocity,
                               double acceleration,
                               double currentTime,
                               double parkingTime) {
        super(spec, pos, heading, velocity, steeringAngle, acceleration,
                targetVelocity, currentTime);
        this.targetParkingLane = null;
        this.parkingTime = parkingTime;
        this.timeToExit = parkingTime;
        this.hasEntered = false;
        this.vehicleInFront = null;
        this.estimatedDistanceTravelled = 0.0;
    }

    @Override
    public AutoDriver getDriver() {
        return driver;
    }

    @Override
    public void setDriver(Driver driver) {
        assert driver instanceof CPMV2VDriver;
        this.driver = (CPMV2VDriver) driver;
    }

    public double getParkingTime() { return parkingTime; }

    public double getTimeToExit() { return timeToExit; }

    public boolean hasEnteredCarPark() {
        return hasEntered;
    }

    public void setHasEntered() {
        assert !hasEntered;
        hasEntered = true;
    }

    public ParkingLane getTargetParkingLane() {
        return targetParkingLane;
    }

    public void setTargetParkingLane(ParkingLane targetParkingLane) {
        this.targetParkingLane = targetParkingLane;
    }

    public void clearTargetParkingLane(){
        targetParkingLane = null;
    }

    public CPMBasicAutoVehicle getVehicleInFront() {
        return vehicleInFront;
    }

    public void setVehicleInFront(CPMBasicAutoVehicle vehicleInFront) {
        this.vehicleInFront = vehicleInFront;
    }

    public double getEstimatedDistanceTravelled() { return estimatedDistanceTravelled; }

    public BasicConnection getLastConnection() { return lastConnection; }

    /**
     * Find out the distance between the front of the vehicle and
     * the ParkingLane's parking end point.
     * @return the distance between the front of the vehicle and
     * the parking end point of the lane that the driver is currently on.
     */
    public double distanceToParkingEndPoint(){
        assert(driver.getCurrentLane() instanceof ParkingLane);
        Point2D endPoint = ((ParkingLane) driver.getCurrentLane()).getParkingEndPoint();
        Point2D vehiclePosition = gaugePosition();
        return vehiclePosition.distance(endPoint);
    }

    public void sendMessageToI2VInbox(ParkingLane parkingLane) {
        I2Vinbox = parkingLane;
    }

    public ParkingLane getMessagesFromI2VInbox() {
        return I2Vinbox;
    }

    public void clearI2Vinbox() {
        System.out.println("I2V inbox cleared");
        I2Vinbox = null;
    }

    public void sendMessageToV2VInbox(ParkingStatus status) {
        V2Vinbox = status;
    }

    public ParkingStatus getMessagesFromV2VInbox() {
        return V2Vinbox;
    }

    public void clearV2Vinbox() {
        System.out.println("V2V inbox cleared");
        V2Vinbox = null;
    }

    public void updateTimeToExit(double timeStep) {
        if (timeToExit > 0) {
            timeToExit -= timeStep;
        }
    }

    /**
     * The vehicle has just entered a connection, so we caluclate how far it has travelled
     * on the current lane by comparing the intersection point (centroid) of the current connection
     * and the intersection point of the last connection it was in.
     * @param currentConnection the connection it has just entered.
     */
    public void updateEstimatedDistanceTravelled(BasicConnection currentConnection) {
        // If hasn't yet been through a connection, it has entered the car park
        if (lastConnection == null) {
            // add the distance from the ENTRY sensored line to the intersection point of the current connection
            // TODO CPM should DCL deal with this?
            lastConnection = currentConnection;
        }
        else if (currentConnection == lastConnection){
            throw new RuntimeException("The vehicle has just entered the same connection.");
        }

        // The the centre points of the 2 connections we are using
        Point2D centreOfCurrentConnection = currentConnection.getCentroid();
        Point2D centreOfLastConnection = lastConnection.getCentroid();

        // Find out distance between them
        double distanceTravelled = centreOfCurrentConnection.distance(centreOfLastConnection);
        estimatedDistanceTravelled += distanceTravelled;

        // update the last connection
        lastConnection = currentConnection;
    }
}
