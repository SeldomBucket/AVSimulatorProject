package aim4.driver.merge.coordinator;

import aim4.config.SimConfig;
import aim4.driver.DriverUtil;
import aim4.driver.aim.coordinator.*;
import aim4.driver.merge.MergeV2IAutoDriver;
import aim4.driver.merge.pilot.MergeAutoPilot;
import aim4.im.merge.V2IMergeManager;
import aim4.map.Road;
import aim4.map.lane.Lane;
import aim4.map.merge.MergeMap;
import aim4.map.merge.RoadNames;
import aim4.msg.merge.i2v.Confirm;
import aim4.msg.merge.i2v.I2VMergeMessage;
import aim4.msg.merge.i2v.Reject;
import aim4.msg.merge.v2i.Away;
import aim4.msg.merge.v2i.Cancel;
import aim4.msg.merge.v2i.Done;
import aim4.msg.merge.v2i.Request;
import aim4.util.Util;
import aim4.vehicle.AccelSchedule;
import aim4.vehicle.VehicleUtil;
import aim4.vehicle.merge.MergeV2IAutoVehicleDriverModel;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * Created by Callum on 13/04/2017.
 */
public class MergeV2IAutoCoordinator extends MergeCoordinator {
    // CONSTANTS //
    /**
     * The maximum amount of error in the clock of the vehicle. {@value} seconds.
     */
    private static final double MAX_CLOCK_ERROR = 0.5;

    /**
     * The maximum amount of time, in seconds, in the future, for which the
     * policy will accept reservation requests. This value
     * should be roughly the same as the corresponding value in the IM.
     */
    private static final double MAXIMUM_FUTURE_RESERVATION_TIME =
            V2IMergeManager.MAXIMUM_FUTURE_RESERVATION_TIME - MAX_CLOCK_ERROR;

    /**
     * The precision at which the arrival velocity is considered valid.
     */
    private static final double ARRIVAL_VELOCITY_PRECISION = 3.0;

    /**
     * The minimum amount of time, in seconds, in the future for which the
     * Coordinator will attempt to make a reservation. This is needed because a
     * reservation cannot be made for <i>right now</i>&mdash;it will take time
     * for the request to be sent, processed, and returned. {@value} seconds.
     */
    private static final double MINIMUM_FUTURE_RESERVATION_TIME = 0.1;

    /**
     * The maximum amount of time, in seconds, after sending a request that the
     * Coordinator will wait before giving up and trying again.  If it is less
     * than zero, the vehicle will wait for the request forever. {@value}
     * seconds.
     */
    private static final double REQUEST_TIMEOUT = -1.0;

    /**
     * The delay of sending another request message if the previous
     * preparation for sending a request message is failed.
     */
    private static final double SENDING_REQUEST_DELAY = 0.02;

    /**
     * The maximum expected time that MM needs to reply a request message.
     */
    private static final double MAX_EXPECTED_MM_REPLY_TIME = 0.04;

    /**
     * The slight reduction of the acceleration of the vehicle
     * when computing an estimation of arrival time and velocity.
     */
    private static final double ARRIVAL_ESTIMATE_ACCEL_SLACK = 1.0;

    // NESTED CLASSES //
    public enum State {
        /**
         * The agent is planning what to do next
         */
        PLANNING,
        /**
         * The agent follows the current lane and does not enter the merge.
         */
        DEFAULT_DRIVING_BEHAVIOUR,
        /**
         * The agent is determing what the parameters of the requested reservation will be.
         */
        PREPARING_RESERVATION,
        /**
         * The agent has sent a reservation request and is awaiting a response from the MergeManager.
         */
        AWAITING_RESPONSE,
        /**
         * The agent has received a confirmation from the MergeManager and must not attempt to keep the confirmed
         * reservation.
         */
        MAINTAINING_RESERVATION,
        /**
         * The agent is navigating the merge in accordance with the reservation made with the MergeManager.
         */
        TRAVERSING,
        /**
         * The agent has exited the merge but is still in the controlled zone after the merge.
         */
        CLEARING,
        /**
         * Signals the end of the interaction with the current manager
         */
        TERMINAL_STATE
    }

    // PRIVATE FIELDS //
    // STATE
    /** The current state of the agent. */
    private State state;
    /** The most recent time at which the state was changed */
    private double lastStateChangeTime = 0.0;
    /** The state handlers */
    private EnumMap<State, StateHandler> stateHandlers;

    //COMMUNICATION
    /**
     * The reservation parameter
     */
    private ReservationParameter rparameter;
    /**
     * The ID number of the latest reservation the agent has received a
     * confirmation for from the MergeManager.
     */
    private int latestReservationNumber;
    /**
     * The next request Id
     */
    private int nextRequestId;
    /**
     * The next time at which the vehicle is allowed to send out request messages
     */
    private double nextAllowedSendingRequestTime;

    // CONSTRUCTOR //
    public MergeV2IAutoCoordinator(MergeV2IAutoVehicleDriverModel vehicle,
                                   MergeV2IAutoDriver driver,
                                   MergeMap map) {
        this.vehicle = vehicle;
        this.driver = driver;
        this.map = map;
        this.pilot = new MergeAutoPilot(vehicle, driver);

        initStateHandlers();

        assert(driver.nextMergeManager() != null);

        // We don't have a reservation yet
        rparameter = null;
        // We should be allowed to transmit now
        nextAllowedSendingRequestTime = vehicle.gaugeTime();
        // Reset our counter for the latest reservation number so that
        // we won't ignore future reservations
        latestReservationNumber = -1;
        // next request id is 0
        nextRequestId = 0;

        // Set the intial state
        setState(State.PLANNING);
    }

    // ACTIONS //
    /**
     * Receive, process, and send messages between Vehicles and
     * MergeManagers, and maintain the reservation status in
     * the Vehicle.
     */
    @Override
    public void act() {
        // process the messages
        messageHandler();
        // call state handlers (and generate outgoing messages)
        callStateHandlers();
    }

    /**
     * Process the message in the inbox.
     */
    private void messageHandler() {
        // the incoming message queue one by one
        List<I2VMergeMessage> msgs = getVehicle().pollAllMessagesFromI2VInbox();
        for(I2VMergeMessage msg : msgs) {
            // interpret the message (and potentially change the state)
            processMessages(msg);
        }
    }

    /**
     * The main loop for calling the state handlers
     */
    private void callStateHandlers() {
        boolean shouldContinue = true;
        while(shouldContinue) {
            if (stateHandlers.containsKey(state)) {
                shouldContinue = stateHandlers.get(state).perform();
            } else {
                throw new RuntimeException("Unknown state.");
            }
        }
    }

    // STATE CONTROLS //
    /**
     * Get the current state of the Coordinator.
     *
     * @return the current state of the coordinator.
     */
    public State getState() {
        return state;
    }

    public String getStateString() {
        return getState().toString();
    }

    /**
     * Whether of not the coordinator has finished its job.
     */
    @Override
    public boolean isTerminated() {
        return state == State.TERMINAL_STATE;
    }

    /**
     * Whether or not the DriverAgent is waiting for a response from the
     * Merge Manager.
     *
     * @return whether or not this DriverAgent is waiting for a response from the
     *         Merge Manager.
     */
    public boolean isAwaitingResponse() {
        return state == State.AWAITING_RESPONSE;
    }

    // FOR PILOT //
    /** Get the confirm message for this driver agent's reservation
     *
     * @return the confirm message; null if there is no confirm message
     */
    public ReservationParameter getReservationParameter() {
        return rparameter;
    }
    /**
     * Calculate the amount of time, in seconds, until the reservation's arrival
     * time.
     *
     * @return the amount of time, in seconds, until the reserved arrival time;
     *         -1.0 if there is no reservation
     */
    public double timeToReservation() {
        if (rparameter != null) {
            return rparameter.getArrivalTime() - vehicle.gaugeTime();
        } else {
            return -1.0;
        }
    }

    // MESSAGE PROCESSING //
    /**
     * Called every time {@link #act()} is called, to process any waiting
     * messages.
     */
    private void processMessages(I2VMergeMessage msg) {
        switch(msg.getMessageType()) {
            case CONFIRM:
                processConfirmMessage((Confirm)msg);
                break;
            case REJECT:
                processRejectMessage((Reject)msg);
                break;
            default:
                throw new NotImplementedException();
        }
    }

    /**
     * Process a received Confirm message.  Sets all the appropriate variables
     * relating to the current reservation in the driver agent, provided that
     * this reservation is newer than any reservation we have or have had in
     * the past.
     *
     * @param msg the Confirm message to process
     */
    private void processConfirmMessage(Confirm msg) {
        switch(state) {
            case AWAITING_RESPONSE:
                processConfirmMessageForAwaitingResponseState(msg);
                break;
            default:
                System.err.printf("vin %d receives a confirm message when it is not " +
                                "at the AWAITING_RESPONSE state\n",
                        vehicle.getVIN());
        }
    }

    private void processConfirmMessageForAwaitingResponseState(Confirm msg) {
        latestReservationNumber = msg.getReservationId();
        setReservationParameter(msg);

        // Check with pilot to see whether it is feasible to maintain the reservation
        double time1 = vehicle.gaugeTime();
        double v1 = vehicle.gaugeVelocity();
        double timeEnd = rparameter.getArrivalTime();
        double vEnd = rparameter.getArrivalVelocity();
        double dTotal = getDriver().distanceToNextMerge();
        double vTop = DriverUtil.calculateMaxFeasibleVelocity(vehicle);
        double accel = vehicle.getSpec().getMaxAcceleration();
        double decel = vehicle.getSpec().getMaxDeceleration();

        AccelSchedule as = null;
        try {
            as = MaxAccelReservationCheck.check(time1, v1,
                    timeEnd, vEnd,
                    dTotal,
                    vTop,
                    accel,
                    decel);
        } catch(ReservationCheckException e) {
            System.err.printf("Cancel the reservation because vehicle " +
                    "can't accept the reservation.\n");
            System.err.printf("Reason: %s\n", e.getMessage());
        }
        if (as != null) {
            // Great, it can accelerate to the merge according to the
            // new acceleration schedule
            vehicle.setAccelSchedule(as);
            setState(State.MAINTAINING_RESERVATION);
        } else {
            // must cancel the reservation
            sendCancelMessage(latestReservationNumber);
            removeReservationParameter();
            // remove the acceleration profile.
            vehicle.removeAccelSchedule();
            setState(State.PLANNING);
        }
    }

    /**
     * Process a received Reject message.  Sets the driver state according to
     * the reason given in the Reject message.  Also handles the case where
     * this is a response to a Request message when we already have a confirmed
     * reservation.
     *
     * @param msg the Reject message to process
     */
    private void processRejectMessage(Reject msg) {
        switch(state) {
            case AWAITING_RESPONSE:
                processRejectMessageForAwaitingResponseState(msg);
                break;
            default:
                System.err.printf("vin %d receives a reject message when it is not " +
                                "at the V2I_AWAITING_RESPONSE state\n",
                        vehicle.getVIN());
        }
    }

    /**
     * Process the reject message when the vehicle is at the Awaiting Response
     * state.
     *
     * @param msg the reject message.
     */
    private void processRejectMessageForAwaitingResponseState(Reject msg) {
        switch(msg.getReason()) {
            case NO_CLEAR_PATH:
                // normal reason for rejection, just go back to the planning state.
                goBackToPlanningStateUponRejection(msg);
                break;
            case CONFIRMED_ANOTHER_REQUEST:
                // TODO: RETHINK WHAT WE SHOULD DO
                goBackToPlanningStateUponRejection(msg);
                break;
            case BEFORE_NEXT_ALLOWED_COMM:
                throw new RuntimeException("MergeV2IAutoCoordinator: Cannot send reqest "+
                        "message before the next allowed " +
                        "communication time");
            case ARRIVAL_TIME_TOO_LARGE:
                System.err.printf("vin %d\n", vehicle.getVIN());
                throw new RuntimeException("MergeV2IAutoCoordinator: cannot make reqest whose "+
                        "arrival time is too far in the future");
            case ARRIVAL_TIME_TOO_LATE:
                // This means that by the time our message got to IM, the arrival time
                // had already passed.  It indicates an error in the proposal
                // preparation in coordinator.
                throw new RuntimeException("MergeV2IAutoCoordinator: Arrival time of request " +
                        "has already passed.");
            default:
                System.err.printf("%s\n", msg.getReason());
                throw new RuntimeException("MergeV2IAutoCoordinator: Unknown reason for " +
                        "rejection.");
        }
    }

    /**
     * Reset the coordinator to the planning state.
     *
     * @param msg the reject message.
     */
    private void goBackToPlanningStateUponRejection(Reject msg) {
        nextAllowedSendingRequestTime =
                Math.max(msg.getNextAllowedCommunication(),
                        vehicle.gaugeTime() + SENDING_REQUEST_DELAY);
        vehicle.removeAccelSchedule();
        setState(State.PLANNING);
    }

    // STATE HANDLERS //
    /**
     * Initialise the state handlers
     */
    private void initStateHandlers() {
        stateHandlers = new EnumMap<State, StateHandler>(State.class);

        stateHandlers.put(State.PLANNING,
                new PlanningStateHandler());

        stateHandlers.put(State.DEFAULT_DRIVING_BEHAVIOUR,
                new DefaultDrivingBehaviourStateHandler());

        stateHandlers.put(State.PREPARING_RESERVATION,
                new PreparingReservationStateHandler());

        stateHandlers.put(State.AWAITING_RESPONSE,
                new AwaitingResponseStateHandler());

        stateHandlers.put(State.MAINTAINING_RESERVATION,
                new MaintainingReservationStateHandler());

        stateHandlers.put(State.TRAVERSING,
                new TraversingStateHandler());

        stateHandlers.put(State.CLEARING,
                new ClearingStateHandler());

        stateHandlers.put(State.TERMINAL_STATE,
                terminalStateHandler);
    }

    private class PlanningStateHandler implements StateHandler {
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean perform() {
            //Clean up
            removeReservationParameter();
            if (vehicle.gaugeTime() >= nextAllowedSendingRequestTime) {
                if (!SimConfig.MUST_STOP_BEFORE_INTERSECTION ||
                        getDriver().distanceToNextMerge() <= MergeAutoPilot.DEFAULT_STOP_DISTANCE_BEFORE_MERGE +
                                SimConfig.ADDITIONAL_STOP_DIST_BEFORE_INTERSECTION) {
                    //prepare reservation
                    setState(State.PREPARING_RESERVATION);
                    return true;
                }
            }
            setState(State.DEFAULT_DRIVING_BEHAVIOUR);
            return true;
        }
    }

    private class DefaultDrivingBehaviourStateHandler implements StateHandler {
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean perform() {
            pilot.followCurrentLane();
            pilot.simpleThrottleActionDontEnterMerge();
            setState(State.PLANNING);
            return false;
        }
    }

    private class PreparingReservationStateHandler implements StateHandler {
        /**
         * Estimates the arrival parameters at the intersection given a maximum
         * velocity.
         *
         * @param maxArrivalVelocity   the maximum desired arrival velocity
         *
         * @return the estimated arrival parameters at the intersection
         */
        private ArrivalEstimationResult estimateArrival(double maxArrivalVelocity) {
            // The basic parameters
            double time1 = vehicle.gaugeTime();
            double v1 = vehicle.gaugeVelocity();
            double dTotal = getDriver().distanceToNextMerge();
            // vTop is equal to max(road's speed limit, vehicle' max speed)
            double vTop = DriverUtil.calculateMaxFeasibleVelocity(vehicle);
            double vEndMax = Math.min(vTop, maxArrivalVelocity);
            double accel = vehicle.getSpec().getMaxAcceleration();
            double decel = vehicle.getSpec().getMaxDeceleration();

            // If the im reply time heuristic is used.
            // If an acceleration schedule exists,
            AccelSchedule estimateToStop = vehicle.getAccelSchedule();
            if (estimateToStop != null) {
                // update the initial time, velocity, and distance to
                // take the expected reply time into account
                double vd[] =
                        estimateToStop.calcFinalDistanceAndVelocity(time1, v1, time1
                                + MAX_EXPECTED_MM_REPLY_TIME);
                double d2 = vd[0];
                double v2 = vd[1];
                if (d2 <= dTotal) {
                    // after MAX_EXPECTED_IM_REPLY_TIME second, the vehicle still hasn't
                    // arrive at the intersection, therefore the estimation
                    // would start at the reply time.
                    time1 += MAX_EXPECTED_MM_REPLY_TIME;
                    v1 = v2;
                    dTotal -= d2;
                } else {
                    // the vehicle arrives at the intersection probably
                    // before IM replies,
                    throw new RuntimeException("Error in V2ICoordinator::" +
                            "V2IPreparingReservationStateHandler::" +
                            "estimateArrival: vehicle should not " +
                            "have been able to reach the " +
                            "intersection before the IM reply ");
                    // in the future, maybe consider the following
//            vd = estimateToStop.calcFinalTimeAndVelocity(time1, v1, dTotal);
//            assert vd != null;  // because d2 > dTotal
//            time1 += vd[0];
//            v1 = vd[1];
//            dTotal = 0.0;
                }
                // To avoid the numerical errors that a zero velocity
                // becomes negative, fix it to be zero when it is the case.
                if (Util.isDoubleZero(v1)) {
                    v1 = 0.0;   // TODO: think how to get rid of this adjustment
                }
            } else { // if there is no acceleration schedule
                if (Util.isDoubleNotZero(v1)) {
                    // vehicle is still moving, so use the simple heuristic
                    // to make sure that there is enough time for vehicle to
                    // arrive at the intersection when checking a confirmation.
                    accel -= ARRIVAL_ESTIMATE_ACCEL_SLACK;
                    decel += ARRIVAL_ESTIMATE_ACCEL_SLACK;
                    if (accel < 0.0) {
                        accel = 0.0;
                    }
                    if (decel > 0.0) {
                        decel = 0.0;
                    }
                } else { // else the vehicle has stopped.
                    // no need to project the time and distance since the vehicle
                    // is not moving, just update the initial time.
                    time1 += MAX_EXPECTED_MM_REPLY_TIME;
                }
            }
            /*
            //Otherwise
            accel -= ARRIVAL_ESTIMATE_ACCEL_SLACK;
            decel += ARRIVAL_ESTIMATE_ACCEL_SLACK;
            if (accel < 0.0) { accel = 0.0; }
            if (decel > 0.0) { decel = 0.0; }
            */

            ArrivalEstimationResult result = null;
            try {
                result = VelocityFirstArrivalEstimation
                        .estimate(time1, v1, dTotal, vTop, vEndMax, accel, decel);
            } catch(ArrivalEstimationException e) {
                return null;
            }
            return result;

        }

        /**
         * Establish the parameters by which the vehicle can traverse the upcoming
         * merge.  This is used to prepare parameters for both V2I and V2V
         * merges.
         *
         * @return the parameters by which the vehicle can traverse the upcoming
         *         merge; null if there is no proposal
         */
        private Request.Proposal prepareProposal() {
            // Get lanes
            Lane departureLane =
                    getDriver().nextMergeManager().getMergeConnection().getExitLanes().get(0);
            Lane arrivalLane =
                    getDriver().getCurrentLane().getLaneMM().laneToNextMerge(vehicle.gaugePosition());

            //Calculate maximum velocity to arrive at the merge
            double maximumVelocity = VehicleUtil.maxTurnVelocity(vehicle.getSpec(),
                    arrivalLane,
                    departureLane,
                    getDriver().getCurrentMM(),
                    map);

            double arrivalTime = 0;
            double arrivalVelocity = 0;
            double minArrivalTime = vehicle.gaugeTime() + MINIMUM_FUTURE_RESERVATION_TIME;

            ArrivalEstimationResult result = estimateArrival(maximumVelocity);
            arrivalVelocity = result.getArrivalVelocity();
            arrivalTime = Math.max(result.getArrivalTime(), minArrivalTime);

            int arrivalLaneID = arrivalLane.getId();
            int depatureLaneID = departureLane.getId();

            Request.Proposal proposal = null;
            if(arrivalTime < vehicle.gaugeTime() + MAXIMUM_FUTURE_RESERVATION_TIME)
                proposal = new Request.Proposal(
                        arrivalLaneID,
                        depatureLaneID,
                        arrivalTime,
                        arrivalVelocity,
                        maximumVelocity
                );

            return proposal;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean perform() {
            if (vehicle.getAccelSchedule() != null) {
                System.err.printf("vin %d should not have an acceleration schedule " +
                                "when it consider preparing a proposal.",
                        vehicle.getVIN());
            }
            assert vehicle.getAccelSchedule() == null;

            AccelSchedule accelScheduleToStop = decelToStopAtMerge();

            if (accelScheduleToStop != null) {
                vehicle.setAccelSchedule(accelScheduleToStop);
            } else {  // no matter why the vehicle can't stop at the intersection
                // just stop immediately.
                pilot.followCurrentLane();
                vehicle.slowToStop();
            }

            Request.Proposal proposal = null;
            if (isLaneClearToMerge()) {
                proposal = prepareProposal();
            }
            if (proposal != null) {
                sendRequestMessage(proposal);
                setState(State.AWAITING_RESPONSE);
                return true;  // let the state controller for AWAITING_RESPONSE
                // to control the vehicle.
            } else {
                // In any failure cases, just wait a bit and start all over again.
                nextAllowedSendingRequestTime =
                        vehicle.gaugeTime() + SENDING_REQUEST_DELAY; // wait a bit
                setState(State.PLANNING);
                vehicle.removeAccelSchedule();
                return true; // the use of SENDING_REQUEST_DELAY prevents infinite loop
                // between PLANNING and PREPARING_RESERVATION
            }
        }
    }

    private class AwaitingResponseStateHandler implements StateHandler {
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean perform() {
            if (REQUEST_TIMEOUT >= 0.0 && timeSinceStateChange() > REQUEST_TIMEOUT) {
                nextAllowedSendingRequestTime =
                        vehicle.gaugeTime() + SENDING_REQUEST_DELAY; // wait a bit
                vehicle.removeAccelSchedule();
                setState(State.PLANNING);
                return true;  // no infinite loop due to SENDING_REQUEST_DELAY
            } else {
                if (vehicle.getAccelSchedule() != null) {
                    // if there is no other vehicle in front
                    if (isLaneClearToMerge()) {
                        // keep using the acceleration schedule
                        return false;
                    } else {
                        // if the vehicle in front is too close, stop using the
                        // acceleration schedule and use the reactive controller instead
                        double stoppingDistance =
                                VehicleUtil.calcDistanceToStop(vehicle.gaugeVelocity(),
                                        vehicle.getSpec().getMaxDeceleration());
                        double followingDistance =
                                stoppingDistance + MergeAutoPilot.MINIMUM_FOLLOWING_DISTANCE;
                        if (VehicleUtil.distanceToCarInFront(vehicle) > followingDistance) {
                            // the vehicle in front is far away
                            // keep using the acceleration schedule
                            return false;
                        } else {
                            // the vehicle in front is too close
                            // stop using acceleration schedule and start using reactive
                            // controller
                            vehicle.removeAccelSchedule();
                            pilot.followCurrentLane();
                            pilot.simpleThrottleActionDontEnterMerge();
                            return false;
                        }
                    }
                } else {
                    // using the reactive controller
                    pilot.followCurrentLane();
                    pilot.simpleThrottleActionDontEnterMerge();
                    return false;
                }
            }
        }
    }

    private class MaintainingReservationStateHandler implements StateHandler {
        /**
         * Check whether it is possible for the vehicle to arrive at the
         * merge at the arrival time in accordance with its reservation
         * parameters.
         */
        private boolean checkArrivalTime() {
            // The actual arrival time can be some point in
            //   ( vehicle.gaugeTime()-TIME_STEP, vehicle.gaugeTime() ]
            // The feasible arrival time interval is
            //   [rparameter.getArrivalTime()-rparameter.getEarlyError(),
            //    rparameter.getArrivalTime()-rparameter.getLateError() ]
            // check to see if both intervals intersect.
            double a1 = vehicle.gaugeTime()-SimConfig.TIME_STEP;
            double a2 = vehicle.gaugeTime();
            double b1 = rparameter.getArrivalTime() - rparameter.getEarlyError();
            double b2 = rparameter.getArrivalTime() + rparameter.getLateError();
            // does (a1,a2] intersect [b1,b2]?
            if (a1 < b1 && b1 <= a2) {
                return true;
            } else if (a1 < b2 && b2 <= a2) {
                return true;
            } else if (b1 <= a1 && a2 < b2) {
                return true;
            } else {
                return false;
            }
        }

        /**
         * Check whether it is possible for the vehicle to arrive at the
         * intersection at the arrival velocity in accordance with its reservation
         * parameters.
         */
        private boolean checkArrivalVelocity() {
            // TODO: if the vehicle is already inside the intersection,
            // the arrival velocity may be slightly different.
            // thus this procedure is not correct and need to be removed.
            double v1 = rparameter.getArrivalVelocity();
            double v2 = vehicle.gaugeVelocity();
            return Util.isDoubleEqual(v1, v2, ARRIVAL_VELOCITY_PRECISION);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean perform() {
            if (getDriver().inCurrentMerge()) {
                if (!checkArrivalTime()) {
                    String errorMessage =
                            String.format("At time %.2f, the arrival time of vin %d is incorrect.\n",
                            vehicle.gaugeTime(),
                            vehicle.getVIN()) +
                            String.format("The arrival time is time %.5f,\n",
                                    rparameter.getArrivalTime()) +
                            String.format("but the vehicle arrives at time %.5f\n",
                                    vehicle.gaugeTime()) +
                            String.format("distance to next merge = %.5f\n",
                                    vehicle.getDriver().distanceToNextMerge());
                    System.err.print(errorMessage);
                    throw new RuntimeException(errorMessage);
                } else if (!checkArrivalVelocity()) {
                    String errorMessage = String.format("At time %.2f, the arrival velocity of vin %d is " +
                                    "incorrect:\n",
                            vehicle.gaugeTime(),
                            vehicle.getVIN());
                    System.err.print(errorMessage);
                    throw new RuntimeException(errorMessage);
                } else {
                    setState(State.TRAVERSING);
                    return true;
                }
            } else {
                // Check to see if the vehicle can still keep up with the acceleration
                // profile.  The only thing to check is whether there is another
                // vehicle blocking the road.
                if (isLaneClearToMerge()) {
                    pilot.followCurrentLane();
                    // throttle action is handled by acceleration schedule
                    return false;   // everything alright, keep going
                } else {
                    System.err.printf("vin %d, can't keep up with the accel profile.\n",
                            vehicle.getVIN());
                    // must cancel the reservation
                    sendCancelMessage(latestReservationNumber);
                    removeReservationParameter();
                    // remove the acceleration profile.
                    vehicle.removeAccelSchedule();
                    setState(State.PLANNING);
                    return true; // can start planning right away.
                    // no infinite loop because of the delay MM responses
                    // to our request
                }
            }
        }
    }

    private class TraversingStateHandler implements StateHandler {
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean perform() {
            // Check to see if we are still in the merge
            if (!getDriver().inCurrentMerge()) {
                // The vehicle is out of the merge.
                Lane target = null;
                for (Road r : map.getRoads())
                    if (r.getName().equals(RoadNames.TARGET_ROAD.toString()))
                        target = r.getOnlyLane();
                assert target != null;
                driver.setCurrentLane(target);
                pilot.followCurrentLane();
                System.err.printf("Sent done message at time %.2f\n",
                        vehicle.gaugeTime());
                sendDoneMessage(latestReservationNumber);
                // And now get ready to clear
                setState(State.CLEARING);
                return true;  // can check clearance immediately
            } else {
                pilot.steerThroughMergeConnection(getDriver().inMerge());
                pilot.followAccelerationProfile(rparameter, map);
                return false;
            }
        }
    }

    private class ClearingStateHandler implements StateHandler {
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean perform() {
            // See if we are beyond the clearing distance
            if (getDriver().distanceFromPrevMerge() > rparameter.getACZDistance()) {
                // Inform the intersection that we are away
                sendAwayMessage(latestReservationNumber);

                // Finish
                setState(State.TERMINAL_STATE);

                pilot.followCurrentLane();    // the last act before termination
                pilot.simpleThrottleActionDontEnterMerge();
                return false;
            } else {
                // remain in the same state
                pilot.followCurrentLane();
                pilot.simpleThrottleActionDontEnterMerge();
                return false;
            }
        }
    }

    // PRIVATE METHODS //
    /**
     * Adds a Request message to the outgoing messages.
     *
     * @param proposal  the proposal
     */
    private void sendRequestMessage(Request.Proposal proposal) {
        List<Request.Proposal> proposals = new ArrayList<Request.Proposal>();
        proposals.add(proposal);
        Request rqst =
                new Request(vehicle.getVIN(),  // sourceID
                        getDriver().getCurrentMM().getId(), // destinationID
                        nextRequestId,
                        new Request.VehicleSpecForRequestMsg(vehicle.getSpec()),
                        proposals);
        // If so, we put the message in the outbox to be delivered to the
        // MergeManager
        getVehicle().send(rqst);
        nextRequestId++;
    }

    /**
     * Adds a Cancel message for the highest ID number reservation the
     * Coordinator has received so far to the outgoing messages, and addresses
     * it to the upcoming MergeManager.
     *
     * @param reservationID   the reservation ID
     */
    private void sendCancelMessage(int reservationID) {
        getVehicle().send(new Cancel(vehicle.getVIN(), // sourceID
                getDriver().getCurrentMM().getId(), // destinationID
                reservationID)); // reservationID
    }

    /**
     * Adds a Done message to the outgoing messages, addressed to the current
     * MergeManager (even though the vehicle is be past it). This
     * indicates to the MergeManager that the vehicle has completed
     * its traversal of the merge.
     *
     * @param reservationID   the reservation ID
     */
    private void sendDoneMessage(int reservationID) {
        getVehicle().send(new Done(vehicle.getVIN(), // sourceID
                getDriver().getCurrentMM().getId(),  // destinationID
                reservationID));  // reservationID
    }

    /**
     * Adds an Away message to the outgoing messages, addressed to the current
     * MergeManager (even though the vehicle is be past it). This
     * indicates to the MergeManager that the vehicle has gotten far
     * enough away from the merge to escape the AdmissionControlZone
     * for the Lane in which it is traveling.
     *
     * @param reservationID   the reservation ID
     */
    private void sendAwayMessage(int reservationID) {
        getVehicle().send(new Away(vehicle.getVIN(), // sourceID
                getDriver().getCurrentMM().getId(),  // destinationID
                reservationID));  // reservationID
    }

    /**
     * Record the confirm message for this driver agent's reservation
     *
     * @param msg  the confirm message
     */
    private void setReservationParameter(Confirm msg) {
        rparameter = new ReservationParameter(msg, map);
    }

    /**
     * Remove the confirm message for this driver agent's reservation
     */
    private void removeReservationParameter() {
        rparameter = null;
    }

    /**
     * Set the current state of the Coordinator. This method is
     * primarily used by the Coordinator to let the Pilot know what it should
     * do.
     *
     * @param state the new state of the driver agent
     */
    private void setState(State state) {
        this.state = state;
        lastStateChangeTime = vehicle.gaugeTime();
    }

    /**
     * Get the amount of time, in seconds, since the state of this
     * Coordinator last changed.
     *
     * @return the amount of time, in seconds, since the state of this
     *         Coordinator last changed
     */
    private double timeSinceStateChange() {
        return vehicle.gaugeTime() - lastStateChangeTime;
    }

    /**
     * Whether or not the lane in front of the vehicle is empty all the way to
     * the merge.
     *
     * @return whether or not the lane in front of the vehicle is empty all the
     *         way to the merge
     */
    private boolean isLaneClearToMerge() {
        // TODO: need to fix this to make it better.
        double d1 = getDriver().distanceToNextMerge();
        if (d1 >= Double.MAX_VALUE) return true;  // no merge
        double d2 = VehicleUtil.distanceToCarInFront(vehicle);
        if (d2 >= Double.MAX_VALUE) return true;  // no car in front
        double d3 = d1 - d2;
        return (d3 <= MergeAutoPilot.DEFAULT_STOP_DISTANCE_BEFORE_MERGE);
    }

    /**
     * Find an acceleration schedule such that the vehicle can stop
     * at the merge.
     *
     * @return an acceleration schedule such that it can stop at the
     *         merge; null if either (1) the vehicle is beyond
     *         the point of no return; or (2) the vehicle is too close
     *         to the merge.
     */
    private AccelSchedule decelToStopAtMerge() {
        // stop at the buffer distance before merge
        double dTotal =
                getDriver().distanceToNextMerge()
                        - MergeAutoPilot.DEFAULT_STOP_DISTANCE_BEFORE_MERGE;

        if (dTotal > 0.0) {
            double time1 = vehicle.gaugeTime();
            double v1 = vehicle.gaugeVelocity();
            double vTop = DriverUtil.calculateMaxFeasibleVelocity(vehicle);
            double vEndMax = 0.0;   // make sure that it stops at the merge
            double accel = vehicle.getSpec().getMaxAcceleration();
            double decel = vehicle.getSpec().getMaxDeceleration();

            ArrivalEstimationResult result = null;
            try {
                result = aim4.driver.aim.coordinator.VelocityFirstArrivalEstimation
                        .estimate(time1, v1, dTotal, vTop, vEndMax, accel, decel);
            } catch(ArrivalEstimationException e) {
                return null;
            }
            return result.getAccelSchedule();
        } else {  // already inside the acceleration zone or the merge
            return null;
        }
    }

    // ACCESSORS //
    private MergeV2IAutoVehicleDriverModel getVehicle() {
        assert vehicle instanceof MergeV2IAutoVehicleDriverModel;
        return (MergeV2IAutoVehicleDriverModel) vehicle;
    }

    private MergeV2IAutoDriver getDriver() {
        assert driver instanceof MergeV2IAutoDriver;
        return (MergeV2IAutoDriver) driver;
    }
}
