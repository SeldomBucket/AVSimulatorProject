package aim4.driver.merge.pilot;

import aim4.config.SimConfig;
import aim4.driver.BasicPilot;
import aim4.driver.DriverUtil;
import aim4.driver.merge.MergeAutoDriver;
import aim4.driver.merge.MergeV2IAutoDriver;
import aim4.driver.merge.coordinator.ReservationParameter;
import aim4.map.Road;
import aim4.map.connections.MergeConnection;
import aim4.map.lane.Lane;
import aim4.map.merge.MergeMap;
import aim4.map.merge.RoadNames;
import aim4.vehicle.VehicleUtil;
import aim4.vehicle.merge.MergeAutoVehicleDriverModel;

import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.Queue;

/**
 * Created by Callum on 26/03/2017.
 */
public class MergeAutoPilot extends BasicPilot {
    //CONSTS//
    public static final double MINIMUM_FOLLOWING_DISTANCE = 2; //metres
    /**
     * The distance, expressed in units of the Vehicle's velocity, at which to
     * switch to a new lane when turning. {@value} seconds.
     */
    public static final double TRAVERSING_LANE_CHANGE_LEAD_TIME = 1.5; // sec
    public static final double DEFAULT_STOP_DISTANCE_BEFORE_MERGE = 1.0;

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
    public void simpleThrottleActionDontEnterMerge() {
        simpleThrottleAction();
        dontEnterMerge();
    }

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

    /**
     * Stop before entering the merge.
     */
    private void dontEnterMerge() {
        double stoppingDistance = distIfStopNextTimeStep();

        double minDistanceToIntersection =
                stoppingDistance + DEFAULT_STOP_DISTANCE_BEFORE_MERGE;
        if (getDriver().distanceToNextMerge() <
                minDistanceToIntersection) {
            vehicle.slowToStop();
        }
    }

    /**
     * Determine how far the vehicle will go if it waits until the next time
     * step to stop.
     *
     * @return How far the vehicle will go if it waits until the next time
     *         step to stop
     */
    private double distIfStopNextTimeStep() {
        double distIfAccel = VehicleUtil.calcDistanceIfAccel(
                vehicle.gaugeVelocity(),
                vehicle.getSpec().getMaxAcceleration(),  // TODO: why max accel here?
                DriverUtil.calculateMaxFeasibleVelocity(vehicle),
                SimConfig.TIME_STEP);
        double distToStop = VehicleUtil.calcDistanceToStop(
                speedNextTimeStepIfAccel(),
                vehicle.getSpec().getMaxDeceleration());
        return distIfAccel + distToStop;
    }

    /**
     * Calculate the velocity of the vehicle at the next time step, if we choose
     * to accelerate at this time step.
     *
     * @return the velocity of the vehicle at the next time step, if we choose
     *         to accelerate at this time step
     */
    private double speedNextTimeStepIfAccel(){
        // Our speed at the next time step will be either the target speed
        // or as fast as we can go, whichever is smaller
        return Math.min(DriverUtil.calculateMaxFeasibleVelocity(vehicle),
                vehicle.gaugeVelocity() +
                        vehicle.getSpec().getMaxAcceleration() *
                                SimConfig.TIME_STEP);
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

    /**
     * Follow the acceleration profile received as part of a reservation
     * confirmation from an IntersectionManager. If none exists, or if it is
     * empty, just cruise. Modifies the acceleration profile to reflect the
     * portion it has consumed.
     *
     * TODO: do not modify the acceleration profile
     */
    public void followAccelerationProfile(ReservationParameter rp, MergeMap map) {
        Queue<double[]> accelProf = rp.getAccelerationProfile();
        // If we have no profile or we have finished with it, then just do our
        // best to maintain a cruising speed
        if ((accelProf == null) || (accelProf.isEmpty())) {
            // Maintain a cruising speed while in the intersection, but slow for
            // other vehicles. Also do not go above the maximum turn velocity.
            vehicle.setTargetVelocityWithMaxAccel(calculateMergeVelocity(rp, map));
        } else {
            // Otherwise, we need to figure out what the next directive in the
            // profile is - peek at the front of the list
            double[] currentDirective = accelProf.element();
            // Now, we have three cases. Either there is more than enough duration
            // left at this acceleration to do only this acceleration:
            if (currentDirective[1] > SimConfig.TIME_STEP) {
                // This is easy, just do the requested acceleration and decrement
                // the duration
                vehicle
                        .setAccelWithMaxTargetVelocity(currentDirective[0]);
                currentDirective[1] -= SimConfig.TIME_STEP;
            } else if (currentDirective[1] < SimConfig.TIME_STEP) {
                // Or we have to do a weighted average
                double totalAccel = 0.0;
                double remainingWeight = SimConfig.TIME_STEP;
                // Go through each of the acceleration, duration pairs and do a
                // weighted average of the first time step's worth of accelerations
                for (Iterator<double[]> iter = accelProf.iterator(); iter.hasNext();) {
                    currentDirective = iter.next();
                    if (currentDirective[1] > remainingWeight) {
                        // Yay! More than enough here to finish out
                        totalAccel += remainingWeight * currentDirective[0];
                        // Make sure to record the fact that we used up some of it
                        currentDirective[1] -= remainingWeight;
                        // And that we satisfied the whole time step
                        remainingWeight = 0.0;
                        break;
                    } else if (currentDirective[1] < remainingWeight) {
                        // Ugh, we have to do it again
                        totalAccel += currentDirective[1] * currentDirective[0];
                        remainingWeight -= currentDirective[1];
                        iter.remove(); // done with this one
                    } else { // currentDirective[1] == remainingWeight
                        // This finishes off the list perfectly
                        totalAccel += currentDirective[1] * currentDirective[0];
                        // And completes our requirements for a whole time step
                        remainingWeight = 0.0;
                        iter.remove(); // done with this oneo
                        break;
                    }
                }
                // Take care of the case in which we didn't have enough for the
                // whole time step
                if (remainingWeight > 0.0) {
                    totalAccel += remainingWeight * currentDirective[0];
                }
                // Okay, totalAccel should now have our total acceleration in it
                // So we need to divide by the total weight to get an actual
                // acceleration
                vehicle.setAccelWithMaxTargetVelocity(totalAccel
                        / SimConfig.TIME_STEP);
            } else { // Or things work out perfectly and we use this one up
                // This is easy, just do the requested acceleration and remove the
                // element from the queue
                accelProf.remove();
                vehicle
                        .setAccelWithMaxTargetVelocity(currentDirective[0]);
            }
        }
    }

    private double calculateMergeVelocity(ReservationParameter rp, MergeMap map) {
        assert getDriver() instanceof MergeV2IAutoDriver;
        return VehicleUtil.maxTurnVelocity(vehicle.getSpec(),
                rp.getArrivalLane(),
                rp.getDepartureLane(),
                ((MergeV2IAutoDriver) getDriver()).getCurrentMM(),
                map);
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
