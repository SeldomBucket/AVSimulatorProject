package aim4.vehicle.merge;

import aim4.driver.Driver;
import aim4.driver.merge.MergeAutoDriver;
import aim4.vehicle.BasicAutoVehicle;
import aim4.vehicle.VehicleSpec;

import java.awt.geom.Point2D;

/**
 * Created by Callum on 14/03/2017.
 */
public class MergeBasicAutoVehicle extends BasicAutoVehicle implements MergeAutoVehicleSimModel {
    private MergeAutoDriver driver;

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

    @Override
    public MergeAutoDriver getDriver() {
        return driver;
    }

    @Override
    public void setDriver(Driver driver) {
        assert driver instanceof MergeAutoDriver;
        this.driver = (MergeAutoDriver) driver;
    }
}
