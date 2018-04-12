package aim4.vehicle.mixedcpm;

import aim4.driver.AutoDriver;
import aim4.driver.Driver;
import aim4.driver.mixedcpm.MixedCPMDriver;
import aim4.driver.mixedcpm.coordinator.MixedCPMManualCoordinator;
import aim4.map.connections.BasicConnection;
import aim4.vehicle.BasicAutoVehicle;
import aim4.vehicle.VehicleSpec;

import java.awt.geom.Point2D;

import static java.lang.Math.abs;

public class MixedCPMBasicVehicle extends BasicAutoVehicle {

    /**
     * The inbox for messages from other vehicles. There will only ever
     * be one message, which will be from the vehicle behind it, which
     * will ask it to relocate. The message will indicate what this
     * vehicle should change it's parking status to.
     */
    protected MixedCPMManualCoordinator.ParkingStatus V2Vinbox;
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

    protected MixedCPMDriver driver;


    public MixedCPMBasicVehicle(VehicleSpec spec,
                                Point2D pos,
                                double heading,
                                double velocity,
                                double steeringAngle,
                                double acceleration,
                                double targetVelocity,
                                double currentTime,
                                double parkingTime){
        super(spec, pos, heading, velocity, steeringAngle, acceleration,
                targetVelocity, currentTime);

        this.parkingTime = parkingTime;
        this.timeUntilExit = parkingTime;
        this.hasEntered = false;
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

    @Override
    public AutoDriver getDriver() {
        return driver;
    }

    @Override
    public void setDriver(Driver driver) {
        assert driver instanceof MixedCPMDriver;
        this.driver = (MixedCPMDriver) driver;
    }
}
