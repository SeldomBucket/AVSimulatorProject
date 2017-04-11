package aim4.driver.merge.pilot;

import aim4.driver.BasicPilot;
import aim4.driver.Driver;
import aim4.driver.merge.MergeAutoDriver;
import aim4.driver.merge.MergeDriverSimModel;
import aim4.map.Road;
import aim4.map.connections.MergeConnection;
import aim4.map.lane.Lane;
import aim4.map.merge.RoadNames;
import aim4.vehicle.VehicleDriverModel;
import aim4.vehicle.VehicleUtil;
import aim4.vehicle.merge.MergeAutoVehicleDriverModel;

import java.awt.*;
import java.awt.geom.Point2D;

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
        Lane targetLane = connection.getExitLanes().get(0);
        if(getVehicle().gaugeHeading() == targetLane.getInitialHeading()) {
            if (driver.getCurrentLane() != targetLane) {
                driver.setCurrentLane(targetLane);
            }
            followCurrentLane();
        } else {
            getVehicle().turnTowardPoint(calculateMergeTurnTarget(connection));
        }
    }

    private Point2D calculateMergeTurnTarget(MergeConnection connection) {
        //Get lanes
        Lane targetLane = connection.getExitLanes().get(0);
        Lane mergeLane = null;
        for(Road r : connection.getEntryRoads()){
            if(r.getName().equals(RoadNames.MERGING_ROAD.toString())){
                mergeLane = r.getOnlyLane();
                break;
            }
        }
        assert mergeLane != null;
        //Get Merge Entry Point and Target Exit Point
        Point2D mergeEntry = connection.getEntryPoint(mergeLane);
        Point2D exit = connection.getExitPoint(targetLane);

        /*
        Turn target will always have Y-coordinate equal to the exit point's.
        Turn target will always be the distance from the merge entry to the exit away from the vehicle.

        To find the target for the vehicle we need to use the Sine Rule.
        We have a right-angled triangle where:
        - The hypotenuse goes from the vehicle location to the point we want the vehicle to turn towards on the centre
        line of the target lane.
        - The opposite runs from the vehicle location straight down to the centre line of the target lane.
        - The adjacent runs along the centre line between the end of the opposite and hypotenuse sides.

        We first calculate the interior angle between the hypotenuse and adjacent. Using the sine rule we get:
        intAngle = sin-1((opposite*sin(90)) / hypotenuse).

        We can then use this to get the angle between the hypotenuse and the opposite.
        xAngle = 180 - intAngle - 90.

        Now using the sine rule again we can calculate the X-adjustment between the vehicle position and the target lane
        xAdjustment = (hypotenuse * sin(xAngle))/sin(90)
        */
        double yGap = vehicle.gaugePosition().getY() - exit.getY(); //opposite
        double curveDist = Point2D.distance(
                mergeEntry.getX(), mergeEntry.getY(), exit.getX(), exit.getY()); //hypotenuse

        double intAngle = Math.asin((yGap * Math.sin(90)) / curveDist);
        double xAngle = 180 - intAngle - 90;
        double xAdjustment = (curveDist * Math.sin(xAngle))/Math.sin(90);

        double targetX = getVehicle().gaugePosition().getX() + xAdjustment;
        double targetY = exit.getY();

        return new Point2D.Double(targetX, targetY);
    }

}
