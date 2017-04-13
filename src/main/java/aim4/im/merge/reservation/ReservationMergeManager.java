package aim4.im.merge.reservation;

import aim4.config.Constants;
import aim4.driver.Driver;
import aim4.driver.merge.MergeAutoDriver;
import aim4.im.ReservationManager;
import aim4.map.connections.MergeConnection;
import aim4.map.lane.Lane;
import aim4.map.merge.MergeMap;
import aim4.msg.merge.v2i.Request;
import aim4.vehicle.VehicleSpec;
import aim4.vehicle.VehicleUtil;
import aim4.vehicle.merge.MergeBasicAutoVehicle;

import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by Callum on 13/04/2017.
 */
public class ReservationMergeManager implements
        ReservationManager<ReservationMergeManager.Query,
                           ReservationMergeManager.Plan,
                           Integer> {

    //NESTED CLASSES//
    /**
     * The configuration of the ReservationMergeManager
     */
    public static class Config {
        /**
         * The simulation time step;
         */
        private double timeStep;
        /**
         * The length of a discrete time step in the merge
         */
        private double mergeTimeStep;

        public Config(double timeStep, double mergeTimeStep) {
            this.timeStep = timeStep;
            this.mergeTimeStep = mergeTimeStep;
        }

        //ACCESSORS//
        public double getTimeStep() {
            return this.timeStep;
        }

        public double getMergeTimeStep() {
            return mergeTimeStep;
        }
    }

    public static class Query {
        /** The VIN of the vehicle */
        private int vin;
        /** The arrival time */
        private double arrivalTime;
        /** The arrival velocity */
        private double arrivalVelocity;
        /** The ID of the arrival lane */
        private int arrivalLineId;
        /** The ID of the departure lane */
        private int departureLaneId;
        /** The vehicle specification for request message */
        private Request.VehicleSpecForRequestMsg spec;
        /** The maximum turn velocity */
        private double maxTurnVelocity;
        /** Whether the acceleration is allowed */
        private boolean accelerating;

        /**
         * Create a query.
         *
         * @param vin              the VIN of the vehicle
         * @param arrivalTime      the arrival time
         * @param arrivalVelocity  the arrival velocity
         * @param arrivalLineId    the arrival lane ID
         * @param departureLaneId  the departure lane ID
         * @param spec             the vehicle specification
         * @param maxTurnVelocity  the maximum turn velocity
         * @param accelerating     Whether the acceleration is allowed
         */
        public Query(int vin, double arrivalTime, double arrivalVelocity,
                     int arrivalLineId, int departureLaneId, Request.VehicleSpecForRequestMsg spec,
                     double maxTurnVelocity, boolean accelerating) {
            this.vin = vin;
            this.arrivalTime = arrivalTime;
            this.arrivalVelocity = arrivalVelocity;
            this.arrivalLineId = arrivalLineId;
            this.departureLaneId = departureLaneId;
            this.spec = spec;
            this.maxTurnVelocity = maxTurnVelocity;
            this.accelerating = accelerating;
        }

        /**
         * Get the VIN of a vehicle.
         *
         * @return the VIN of a vehicle
         */
        public int getVin() {
            return vin;
        }

        /**
         * Get the arrival time.
         *
         * @return the arrival time
         */
        public double getArrivalTime() {
            return arrivalTime;
        }

        /**
         * Get the arrival velocity.
         *
         * @return the arrival velocity
         */
        public double getArrivalVelocity() {
            return arrivalVelocity;
        }

        /**
         * Get the arrival lane ID.
         *
         * @return the arrival lane ID
         */
        public int getArrivalLaneId() {
            return arrivalLineId;
        }

        /**
         * Get the departure lane ID.
         *
         * @return the departure lane ID
         */
        public int getDepartureLaneId() {
            return departureLaneId;
        }

        /**
         * Get the specification of the vehicle for the request message.
         *
         * @return the specification of the vehicle
         */
        public Request.VehicleSpecForRequestMsg getSpec() {
            return spec;
        }

        /**
         * Get the maximum turn velocity.
         *
         * @return the maximum turn velocity
         */
        public double getMaxTurnVelocity() {
            return maxTurnVelocity;
        }

        /**
         * Whether the vehicle is allowed to accelerate.
         *
         * @return whether the vehicle is allowed to accelerate
         */
        public boolean isAccelerating() {
            return accelerating;
        }
    }

    public static class Plan {
        /** The VIN of the vehicle */
        private int vin;
        /** The exit time */
        private double exitTime;
        /** The exit velocity */
        private double exitVelocity;
        /** The list of time reservations */
        private List<ReservationMerge.TimeReservation> workingList;
        /** The acceleration profile */
        private Queue<double[]> accelerationProfile;

        /**
         * Create the plan for the reservation.
         *
         * @param vin                  the VIN of the vehicle
         * @param exitTime             the exit time
         * @param exitVelocity         the exit velocity
         * @param workingList          the list of time tiles reserved
         * @param accelerationProfile  the acceleration profile
         */
        public Plan(int vin,
                    double exitTime,
                    double exitVelocity,
                    List<ReservationMerge.TimeReservation> workingList,
                    Queue<double[]> accelerationProfile) {
            this.vin = vin;
            this.exitTime = exitTime;
            this.exitVelocity = exitVelocity;
            this.workingList = workingList;
            this.accelerationProfile = accelerationProfile;
        }

        /**
         * Get the VIN of the vehicle.
         *
         * @return the VIN of the vehicle
         */
        public int getVin() {
            return vin;
        }

        /**
         * Get the exit time.
         *
         * @return the exit time
         */
        public double getExitTime() {
            return exitTime;
        }

        /**
         * Get the exit velocity.
         *
         * @return the exit velocity
         */
        public double getExitVelocity() {
            return exitVelocity;
        }

        /**
         * Get the list of time tiles reserved.
         *
         * @return the list of time tiles reserved
         */
        public List<ReservationMerge.TimeReservation> getWorkingList() {
            return workingList;
        }

        /**
         * Get the acceleration profile.
         *
         * @return the acceleration profile
         */
        public Queue<double[]> getAccelerationProfile() {
            return accelerationProfile;
        }
    }

    //PRIVATE FIELDS//
    /**
     * The configuration of this Reservation Merge Manager
     */
    private Config config;
    /**
     * The current time
     */
    private double currentTime;
    /**
     * The merge connection
     */
    private MergeConnection merge;
    /**
     * The reservation system
     */
    private ReservationMerge reservationMerge;
    /**The map the reservation manager is positioned on*/
    private MergeMap layout;

    //CONSTRUCTOR//
    public ReservationMergeManager(Config config,
                                   MergeConnection merge,
                                   ReservationMerge reservationMerge,
                                   MergeMap layout){
        this(0.0, config, merge, reservationMerge, layout);
    }

    public ReservationMergeManager(double currentTime,
                                   Config config,
                                   MergeConnection merge,
                                   ReservationMerge reservationMerge,
                                   MergeMap layout){
        this.currentTime = currentTime;
        this.config = config;
        this.merge = merge;
        this.reservationMerge = reservationMerge;
        this.layout = layout;
    }

    //PUBLIC METHODS//
    //ACTION
    public void act(double timeStep) {
        reservationMerge.cleanUp(currentTime);
        currentTime += timeStep;
    }

    //ACCESSORS
    public Config getConfig() {
        return config;
    }

    public MergeConnection getMerge() {
        return merge;
    }

    //PLAN//

    /**
     * Find a set of times for a particular traversal proposal in a request message. This attempt can be either with
     * attempting to setMaxAccelWithTargetVelocity to maximum velocity or with a constant velocity.
     * @param q  the query object
     * @return a set of times on the trajectory and the exit velocity of the vehicle if the reservation is successful;
     * otherwise return null.
     */
    @Override
    public Plan query(Query q) {
        // Position the Vehicle to be ready to start the simulation
        Lane arrivalLane =
                layout.getLaneRegistry().get(q.getArrivalLaneId());
        Lane departureLane =
                layout.getLaneRegistry().get(q.getDepartureLaneId());

        // Create a test vehicle to use in the internal simulation.
        MergeBasicAutoVehicle testVehicle =
                createTestVehicle(q.getSpec(),
                                  q.getArrivalVelocity(),
                                  q.getMaxTurnVelocity(),
                                  arrivalLane);

        // Create a dummy driver to steer it
        Driver dummy = new MergeAutoDriver(testVehicle, layout);
        // Assign driver to vehicle
        testVehicle.setDriver(dummy);

        //Keep track of times making up this reservation
        TimesSimulationResult timesSimResult =
                findTimesBySimulationResult(testVehicle,
                                            dummy,
                                            q.getArrivalTime(),
                                            q.isAccelerating()
                );

        if(timesSimResult != null) {
            List<ReservationMerge.TimeReservation> workingList = timesSimResult.getWorkingList();
            double exitTime = workingList.get(workingList.size()-1).getTime();

            Queue<double[]> accelerationProfile =
                    calcAccelerationProfile(q.getArrivalTime(),
                                            q.getArrivalVelocity(),
                                            q.getMaxTurnVelocity(),
                                            q.getSpec().getMaxAcceleration(),
                                            timesSimResult.getExitTime(),
                                            q.isAccelerating());

            return new Plan(q.getVin(), exitTime, testVehicle.gaugeVelocity(), workingList, accelerationProfile);
        } else {
            return null;
        }

    }

    @Override
    public Integer accept(Plan plan) {
        boolean b = reservationMerge.reserve(plan.getVin(), plan.getWorkingList());
        assert b;
        return plan.getVin();
    }

    @Override
    public void cancel(Integer reservationID) {
        reservationMerge.cancel(reservationID);
    }

    //PRIVATE METHODS//
    /**
     * Create a test vehicle to use in the internal simulation.
     *
     * @param spec             the specification of the test vehicle
     * @param arrivalVelocity  the arrival velocity of the vehicle
     * @param maxVelocity      the Vehicle's maximum velocity, in meters per
     *                         second
     * @param arrivalLane      the arrival lane of the vehicle in this proposal
     *
     * @return             whether or not a reservation could be made
     */
    private MergeBasicAutoVehicle createTestVehicle(Request.VehicleSpecForRequestMsg spec, double arrivalVelocity, double maxVelocity, Lane arrivalLane) {
        VehicleSpec newSpec = new VehicleSpec(
                "TestVehicle",
                spec.getMaxAcceleration(),
                spec.getMaxDeceleration(),
                maxVelocity,
                spec.getMinVelocity(),
                spec.getLength(),
                spec.getWidth(),
                spec.getFrontAxleDisplacement(),
                spec.getRearAxleDisplacement(),
                0.0, // wheelSpan
                0.0, // wheelRadius
                0.0, // wheelWidth
                spec.getMaxSteeringAngle(),
                spec.getMaxTurnPerSecond());

        MergeBasicAutoVehicle testVehicle = new MergeBasicAutoVehicle(
                newSpec,
                merge.getEntryPoint(arrivalLane), //Position
                arrivalLane.getInitialHeading(), //Heading
                0.0, //Steering angle
                arrivalVelocity, //Velocity
                0.0, // Target velocity
                0.0, // Acceleration
                0.0 // The current time
        );
        return testVehicle;
    }

    /**
     * The record for holding the result of the times found
     * by the internal simulation along with the exitTime.
     */
    private static class TimesSimulationResult {
        /** The time tiles */
        List<ReservationMerge.TimeReservation> workingList;
        /** The exit time */
        double exitTime;

        /**
         * Create a record for holding the result of the time tiles found
         * by the internal simulation.
         *
         * @param workingList  the time tiles
         * @param exitTime     the exit time
         */
        public TimesSimulationResult(List<ReservationMerge.TimeReservation> workingList,
                                               double exitTime) {
            this.workingList = workingList;
            this.exitTime = exitTime;
        }

        /**
         * Get the time tiles.
         *
         * @return the time tiles
         */
        public List<ReservationMerge.TimeReservation> getWorkingList() {
            return workingList;
        }

        /**
         * Get the exit time.
         *
         * @return the exit time
         */
        public double getExitTime() {
            return exitTime;
        }
    }

    /**
     * Find a list of unreserved tiletimes by simulation
     *
     * @param dummy         the dummy driver
     * @param arrivalTime   the arrival time of the vehicle
     * @param accelerating  whether or not to setMaxAccelWithMaxTargetVelocity to maximum velocity
     *                      during the traversal
     *
     * @return A list of tiles that can be reserved by the vehicle. If returns
     *         null, the trajectory hits some reserved tiles and the reservation
     *         fails.
     */
    private TimesSimulationResult
    findTimesBySimulationResult(MergeBasicAutoVehicle testVehicle,
                                Driver dummy,
                                double arrivalTime,
                                boolean accelerating) {
        // The area of the merge
        Area area = merge.getArea();
        // The following must be true because the test vehicle
        // starts at the entry point of the merge.
        assert area.contains(testVehicle.getPointAtMiddleFront(Constants.DOUBLE_EQUAL_PRECISION));

        // The list of times that will make up this reservation
        List<ReservationMerge.TimeReservation> workingList = new ArrayList<ReservationMerge.TimeReservation>();

        // A discrete representation of the time throughout the internal simulation
        // Notice that currentIntTime != arrivalTime
        int currentIntTime = reservationMerge.calcDiscreteTime(arrivalTime);
        // The duration in the current time interval
        double currentDuration = reservationMerge.calcRemainingTime(arrivalTime);

        // drive the test vehicle until it leaves the merge
        while(VehicleUtil.intersects(testVehicle, area)) {
            moveTestVehicle(testVehicle, dummy, currentDuration, accelerating);
            currentIntTime++;  // Record that we've moved forward one time step
            workingList.add(reservationMerge.new TimeReservation(currentIntTime));
            currentDuration = reservationMerge.getMergeTimeStep();
        }

        return new TimesSimulationResult(workingList,reservationMerge.calcTime(currentIntTime));
    }

    /**
     * Advance the test vehicle by one time step
     *
     * @param testVehicle   the test vehicle
     * @param dummy         the dummy driver
     * @param accelerating  whether or not to setMaxAccelWithMaxTargetVelocity to maximum velocity
     *                      during the traversal
     */
    private void moveTestVehicle(MergeBasicAutoVehicle testVehicle,
                                 Driver dummy,
                                 double duration,
                                 boolean accelerating) {
        // Give the dummy a chance to steer
        dummy.act();
        // Now control the vehicle's acceleration
        if(accelerating) {
            // Accelerate at maximum rate, topping out at maximum velocity
            testVehicle.setMaxAccelWithMaxTargetVelocity();   // TODO: use other function instead of
            // setMaxAccelWithMaxTargetVelocity()
        } else {
            // Maintain a constant speed
            testVehicle.coast();
        }
        // Now move the vehicle
        testVehicle.move(duration);
    }

    /**
     * Compute the acceleration profile.
     *
     * @param arrivalTime      the arrival time of the vehicle
     * @param arrivalVelocity  the arrival velocity of the vehicle
     * @param maxVelocity      the maximum velocity of the vehicle
     * @param maxAcceleration  the maximum acceleration of the vehicle
     * @param exitTime         the time at which the vehicle exits the
     *                         intersection
     * @param accelerating  whether or not to setMaxAccelWithMaxTargetVelocity to maximum velocity
     *                      during the traversal
     *
     * @return  a sequence of acceleration pair (acceleration, duration)
     */
    private Queue<double[]> calcAccelerationProfile(double arrivalTime,
                                                    double arrivalVelocity,
                                                    double maxVelocity,
                                                    double maxAcceleration,
                                                    double exitTime,
                                                    boolean accelerating) {
        // Calculate the accelerations
        Queue<double[]> accelerationProfile = new LinkedList<double[]>();
        // Figure out how long we took to traverse the merge
        double traversalTime = exitTime - arrivalTime;
        if (traversalTime <= 0.0) {
            System.err.printf("traversalTime = %.10f\n", traversalTime);
        }
        assert traversalTime > 0.0;
        if (accelerating && (maxVelocity > arrivalVelocity)) {
            // How much of the time did we spend accelerating
            double accelerationDuration =
                    Math.min(traversalTime, (maxVelocity-arrivalVelocity)/maxAcceleration);
            // Add in the time spent accelerating, if any
            assert accelerationDuration > 0.0;
            accelerationProfile.add(
                    new double[] { maxAcceleration, accelerationDuration });
            // Fill the remaining time with constant speed, if any remains
            if(accelerationDuration < traversalTime) {
                accelerationProfile.add(
                        new double[] { 0.0, traversalTime - accelerationDuration });
            }
        } else {  // Fixed speed reservation
            // Just add in the time we crossed, all at constant speed
            accelerationProfile.add(new double[] { 0.0, traversalTime });
        }
        return accelerationProfile;
    }
}
