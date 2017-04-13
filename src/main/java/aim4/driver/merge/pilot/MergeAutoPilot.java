package aim4.driver.merge.pilot;

import aim4.driver.BasicPilot;
import aim4.driver.Driver;
import aim4.driver.merge.MergeAutoDriver;
import aim4.driver.merge.MergeDriverSimModel;
import aim4.map.connections.MergeConnection;
import aim4.map.lane.Lane;
import aim4.vehicle.VehicleDriverModel;
import aim4.vehicle.VehicleUtil;
import aim4.vehicle.merge.MergeAutoVehicleDriverModel;

/**
 * Created by Callum on 26/03/2017.
 */
public class MergeAutoPilot extends BasicPilot {
    //CONSTS//
    private static final double MINIMUM_FOLLOWING_DISTANCE = 0.5; //metres
    /**
     * The distance, expressed in units of the Vehicle's velocity, at which to
     * switch to a new lane when turning. {@value} seconds.
     */
    public static final double TRAVERSING_LANE_CHANGE_LEAD_TIME = 1.5; // sec

    //PRIVATE FIELDS//
    private MergeAutoVehicleDriverModel vehicle;
    private MergeAutoDriver driver;

    //CONSTRUCTOR//
    public MergeAutoPilot(MergeAutoVehicleDriverModel vehicle, MergeAutoDriver driver) {
        this.vehicle = vehicle;
        this.driver = driver;
    }

    //GETTERS//
    @Override
    public MergeAutoVehicleDriverModel getVehicle() {
        return this.vehicle;
    }

    @Override
    public MergeAutoDriver getDriver() {
        return this.driver;
    }

    //DRIVING ACTIONS//
    public void simpleThrottleAction() {
        cruise();
        dontHitVehicleInFront();
    }

    private void dontHitVehicleInFront() {
        double stoppingDistance = VehicleUtil.calcDistanceToStop(
                vehicle.gaugeVelocity(),
                vehicle.getSpec().getMaxDeceleration());
        double followingDistance = stoppingDistance + MINIMUM_FOLLOWING_DISTANCE;
        if(VehicleUtil.distanceToCarInFront(vehicle) < followingDistance){
            vehicle.slowToStop();
        }
    }

    public void steerThroughMergeConnection(MergeConnection connection) {
        if (driver.getVehicle().gaugeHeading() != 0) {
            // Find out how far from the road of the departure lane we are
            double distToLane = connection.getExitLanes().get(0).nearestDistance(vehicle.gaugePosition());
            // If we're close enough...
            double traversingLaneChangeDistance =
                    TRAVERSING_LANE_CHANGE_LEAD_TIME * vehicle.gaugeVelocity();
            if (distToLane < traversingLaneChangeDistance) {
                // Change to it
                driver.setCurrentLane(connection.getExitLanes().get(0));
            }

        }
        followCurrentLane();
    }

}
