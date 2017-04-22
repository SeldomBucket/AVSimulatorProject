package aim4.vehicle.merge;

import aim4.driver.Driver;
import aim4.driver.merge.MergeAutoDriver;
import aim4.map.merge.RoadNames;
import aim4.vehicle.BasicAutoVehicle;
import aim4.vehicle.VehicleSpec;

import java.awt.geom.Point2D;

/**
 * Created by Callum on 14/03/2017.
 */
public class MergeBasicAutoVehicle extends BasicAutoVehicle implements MergeAutoVehicleSimModel {
    protected MergeAutoDriver driver;
    //Result properties
    private RoadNames startingRoad;
    private double startTime;
    private double finishTime;
    private double delayTime;
    private double finalVelocity;
    private double maxVelocity;
    private double minVelocity;
    private double finalXPos;
    private double finalYPos;

    /**
     * Construct a vehicle
     *
     * @param spec           the vehicle's specification
     * @param pos            the initial position of the Vehicle
     * @param heading        the initial heading of the Vehicle
     * @param velocity       the initial velocity of the Vehicle
     * @param steeringAngle  the initial steering angle of the Vehicle
     * @param acceleration   the initial acceleration of the Vehicle
     * @param targetVelocity the initial target velocity
     * @param currentTime    the current time
     */
    public MergeBasicAutoVehicle(VehicleSpec spec, Point2D pos,
                                 double heading, double velocity, double steeringAngle,
                                 double acceleration, double targetVelocity, double currentTime) {
        super(spec, pos, heading, velocity, steeringAngle, acceleration, targetVelocity, currentTime);
    }

    //ACCESSORS
    //Driver
    @Override
    public MergeAutoDriver getDriver() {
        return driver;
    }

    @Override
    public void setDriver(Driver driver) {
        assert driver instanceof MergeAutoDriver;
        this.driver = (MergeAutoDriver) driver;
    }

    //Result accessors


    @Override
    public RoadNames getStartingRoad() {
        return startingRoad;
    }

    @Override
    public void setStartingRoad(RoadNames startingRoad) {
        this.startingRoad = startingRoad;
    }

    @Override
    public double getFinishTime() {
        return finishTime;
    }

    @Override
    public void setFinishTime(double finishTime) {
        this.finishTime = finishTime;
    }

    @Override
    public double getDelay() {
        return delayTime;
    }

    @Override
    public void setDelay(double delayTime) {
        this.delayTime = delayTime;
    }

    @Override
    public double getFinalVelocity() {
        return finalVelocity;
    }

    @Override
    public void setFinalVelocity(double finalVelocity) {
        this.finalVelocity = finalVelocity;
    }

    @Override
    public double getMaxVelocity() {
        return maxVelocity;
    }

    @Override
    public void setMaxVelocity(double maxVelocity) {
        this.maxVelocity = maxVelocity;
    }

    @Override
    public double getMinVelocity() {
        return minVelocity;
    }

    @Override
    public void setMinVelocity(double minVelocity) {
        this.minVelocity = minVelocity;
    }

    @Override
    public double getFinalXPos() {
        return finalXPos;
    }

    @Override
    public void setFinalXPos(double finalXPos) {
        this.finalXPos = finalXPos;
    }

    @Override
    public double getFinalYPos() {
        return finalYPos;
    }

    @Override
    public void setFinalYPos(double finalYPos) {
        this.finalYPos = finalYPos;
    }

    @Override
    public double getStartTime() {
        return startTime;
    }

    @Override
    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }
}
