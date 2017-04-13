package aim4.driver.merge.coordinator;

import aim4.driver.Coordinator;
import aim4.driver.merge.MergeAutoDriver;
import aim4.driver.merge.navigator.MergeAutoNavigator;
import aim4.driver.merge.pilot.MergeAutoPilot;
import aim4.im.merge.V2IMergeManager;
import aim4.map.Road;
import aim4.map.connections.MergeConnection;
import aim4.map.lane.Lane;
import aim4.map.merge.MergeMap;
import aim4.map.merge.RoadNames;
import aim4.vehicle.merge.MergeAutoVehicleDriverModel;

import java.util.EnumMap;

/**
 * Created by Callum on 25/03/2017.
 */
public class MergeAutoCoordinator implements Coordinator {

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
     * The maximum number of Lanes from each Road that the Coordinator will
     * include in its request message when it approaches an intersection.
     * Set at {@value}.
     */
    private static final int MAX_LANES_TO_TRY_PER_ROAD = 1;

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

    ////NESTED CLASSES////

    //STATE HANDLING//
    /**
     * An interface of the state handler.
     */
    private static interface StateHandler {
        /**
         * Perform the action defined by the state handler at the driver state.
         *
         * @return true if the driver agent should proceed to the next action
         * immediately.
         */
        boolean perform();
    }

    private static StateHandler terminalStateHandler =
            new StateHandler() {
                @Override
                public boolean perform() {
                    return false; //do nothing
                }
            };

    public enum State {
        /**
         * Agent is planning what to do next
         */
        PLANNING,
        /**
         * The agent follows the current lane and does not enter the intersection
         */
        DEFAULT_DRIVING_BEHAVIOUR,
        /**
         * 
         */
        TRAVERSING_MERGE,
        TERMINAL_STATE
    }

    //PRIVATE FIELDS//
    //Vehicle and Driver
    private MergeAutoVehicleDriverModel vehicle;
    private MergeAutoDriver driver;
    private MergeAutoNavigator navigator;
    private MergeAutoPilot pilot;
    private MergeMap map;

    //State
    private State state;
    private double lastStateChangeTime = 0.0;
    private EnumMap<State, StateHandler> stateHandlers;

    //CONSTRUCTOR//
    public MergeAutoCoordinator(MergeAutoVehicleDriverModel vehicle, MergeAutoDriver driver, MergeMap map) {
        this.vehicle = vehicle;
        this.driver = driver;
        this.map = map;
        this.pilot = new MergeAutoPilot(vehicle, driver);
        this.navigator = new MergeAutoNavigator(vehicle.getSpec(), map);

        initStateHandlers();

        setState(State.PLANNING);
    }

    //STATE CONTROLLERS//
    public State getState() {
        return state;
    }

    private void setState(State state) {
        this.state = state;
        lastStateChangeTime = vehicle.gaugeTime();
    }

    private void initStateHandlers() {
        stateHandlers = new EnumMap<State, StateHandler>(State.class);

        stateHandlers.put(State.PLANNING, new PlanningStateHandler());
        stateHandlers.put(State.DEFAULT_DRIVING_BEHAVIOUR, new DefaultDrivingBehaviourHandler());
        stateHandlers.put(State.TRAVERSING_MERGE, new TraversingMergeStateHandler());
        stateHandlers.put(State.TERMINAL_STATE, terminalStateHandler);
    }

    private void callStateHandlers() {
        boolean shouldContinue = true;
        while (shouldContinue) {
            if (stateHandlers.containsKey(state)) {
                shouldContinue = stateHandlers.get(state).perform();
            } else {
                throw new RuntimeException("Unknown state.");
            }
        }
    }

    //STATE HANDLERS//
    private class PlanningStateHandler implements StateHandler {
        @Override
        public boolean perform() {
            if(driver.inMerge() != null) {
                setState(State.TRAVERSING_MERGE);
            } else {
                setState(State.DEFAULT_DRIVING_BEHAVIOUR);
            }
            return true;
        }
    }

    private class DefaultDrivingBehaviourHandler implements StateHandler {
        @Override
        public boolean perform() {
            pilot.followCurrentLane();
            pilot.simpleThrottleAction();
            setState(State.PLANNING);
            return false;
        }
    }

    private class TraversingMergeStateHandler implements StateHandler {
        @Override
        public boolean perform() {
            MergeConnection mergeConnection = driver.inMerge();
            if(mergeConnection == null){
                //Vehicle cleared connection. Return to normal driving
                setState(State.DEFAULT_DRIVING_BEHAVIOUR);
                Lane target = null;
                for(Road r : map.getRoads())
                    if(r.getName().equals(RoadNames.TARGET_ROAD.toString()))
                        target = r.getOnlyLane();
                assert target != null;
                driver.setCurrentLane(target);
                pilot.followCurrentLane();
            } else {
                pilot.steerThroughMergeConnection(mergeConnection);
            }
            pilot.simpleThrottleAction();
            return false;
        }
    }

    //ACTION//
    @Override
    public void act() {
        callStateHandlers();
    }

    //GETTERS
    @Override
    public boolean isTerminated() {
        return state == State.TERMINAL_STATE;
    }
}
