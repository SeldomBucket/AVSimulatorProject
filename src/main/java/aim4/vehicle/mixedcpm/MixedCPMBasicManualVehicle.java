package aim4.vehicle.mixedcpm;

import aim4.driver.AutoDriver;
import aim4.driver.Driver;
import aim4.driver.mixedcpm.coordinator.MixedCPMManualCoordinator;
import aim4.driver.mixedcpm.MixedCPMManualDriver;
import aim4.map.Road;
import aim4.map.connections.BasicConnection;
import aim4.map.lane.Lane;
import aim4.map.lane.LineSegmentLane;
import aim4.map.mixedcpm.parking.ManualStall;
import aim4.map.mixedcpm.parking.StallInfo;
import aim4.map.mixedcpm.parking.StallType;
import aim4.vehicle.BasicAutoVehicle;
import aim4.vehicle.VehicleSpec;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class MixedCPMBasicManualVehicle extends BasicAutoVehicle {

    /////////////////////////////////
    // PRIVATE FIELDS
    /////////////////////////////////

    /**
     * The Driver controlling this vehicle.
     */
    protected MixedCPMManualDriver driver;

    /**
     * The target stall for this vehicle, assigned
     * on entry and on re-entry of the car park.
     */
    protected ManualStall targetStall;

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
    protected double timeUntilExit;

    /**
     * The distance the vehicle has travelled whilst in the car park.
     * This is an estimate as it doesn't consider the trajectories
     * through road connections (corners, junctions, intersections).
     */
    protected double estimatedDistanceTravelled = 0;

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

    /**
     * The time that the vehicle entered the car park.
     */
    protected double entryTime;

    /**
     * The time that the vehicle exited the car park.
     */
    protected double exitTime;

    /**
     * The time it took for the vehicle to exit the car
     * park once its parking time had elapsed.
     */
    protected double retrievalTime;

    /**
     * The list of lanes in order which the vehicle will
     * have to traverse to get to the target stall
     */
    protected ArrayList<Lane> pathToTargetStall;

    // messaging

    /**
     * The inbox for messages from the car park StatusMonitor.
     * There will only ever be one message, which will be a
     * ManualStall (or null, if no room for vehicle to park).
     */
    protected ManualStall I2Vinbox;

    /**
     * The inbox for messages from other vehicles. There will only ever
     * be one message, which will be from the vehicle behind it, which
     * will ask it to relocate. The message will indicate what this
     * vehicle should change it's parking status to.
     */
    protected MixedCPMManualCoordinator.ParkingStatus V2Vinbox;


    protected StallInfo stallInfo;

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
    public MixedCPMBasicManualVehicle(VehicleSpec spec,
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
        this.targetStall = null;
        this.parkingTime = parkingTime;
        this.timeUntilExit = parkingTime;
        this.hasEntered = false;
        // TODO Different types of vehicle
        this.stallInfo = new StallInfo(spec.getWidth(), spec.getLength(), StallType.Standard);

    }

    public StallInfo getStallInfo(){
        return this.stallInfo;
    }

    @Override
    public AutoDriver getDriver() {
        return driver;
    }

    @Override
    public void setDriver(Driver driver) {
        assert driver instanceof MixedCPMManualDriver;
        this.driver = (MixedCPMManualDriver) driver;
    }

    public double getParkingTime() { return parkingTime; }

    public double getTimeUntilExit() { return timeUntilExit; }

    public boolean hasEnteredCarPark() {
        return hasEntered;
    }

    public void setHasEntered() {
        assert !hasEntered;
        hasEntered = true;
    }

    public ManualStall getTargetStall() {
        return targetStall;
    }

    public void setTargetStall(ManualStall targetStall) {
        if (this.targetStall == null) {
            this.targetStall = targetStall;
        }

    }

    public void clearTargetParkingLane(){
        targetStall = null;
    }

    public double getEstimatedDistanceTravelled() { return estimatedDistanceTravelled; }

    public BasicConnection getLastConnection() { return lastConnection; }

    public double getExitTime() {
        return exitTime;
    }

    public void setExitTime(double exitTime) {
        this.exitTime = exitTime;
    }

    public double getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(double entryTime) {
        this.entryTime = entryTime;
    }

    public void sendMessageToI2VInbox(ManualStall manualStall) {
        I2Vinbox = manualStall;
    }

    public ManualStall getMessagesFromI2VInbox() {
        return I2Vinbox;
    }

    public void clearI2Vinbox() {
        System.out.println("I2V inbox cleared");
        I2Vinbox = null;
    }

    public void sendMessageToV2VInbox(MixedCPMManualCoordinator.ParkingStatus status) {
        V2Vinbox = status;
    }

    public MixedCPMManualCoordinator.ParkingStatus getMessagesFromV2VInbox() {
        return V2Vinbox;
    }

    public void clearV2Vinbox() {
        System.out.println("V2V inbox cleared");
        V2Vinbox = null;
    }

    public void updateTimeUntilExit(double timeStep) {
        if (timeUntilExit > 0) {
            timeUntilExit -= timeStep;
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
