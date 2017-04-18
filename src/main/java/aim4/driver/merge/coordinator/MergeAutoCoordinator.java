package aim4.driver.merge.coordinator;

import aim4.driver.merge.MergeAutoDriver;
import aim4.driver.merge.pilot.MergeAutoPilot;
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
public class MergeAutoCoordinator extends MergeCoordinator {
    // STATES //
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
         * The agent is crossing the merge.
         */
        TRAVERSING_MERGE,
        /**
         * Signals the end of the simulation for the vehicle.
         */
        TERMINAL_STATE
    }

    //PRIVATE FIELDS//
    //VEHICLE AND DRIVER
    //STATE
    /** The current state of the agent. */
    private State state;
    /** The most recent time at which the state was changed */
    private double lastStateChangeTime = 0.0;
    /** The state handlers */
    private EnumMap<State, StateHandler> stateHandlers;

    //CONSTRUCTOR//
    public MergeAutoCoordinator(MergeAutoVehicleDriverModel vehicle, MergeAutoDriver driver, MergeMap map) {
        this.vehicle = vehicle;
        this.driver = driver;
        this.map = map;
        this.pilot = new MergeAutoPilot(vehicle, driver);

        initStateHandlers();

        setState(State.PLANNING);
    }

    //STATE CONTROLLERS//
    public State getState() {
        return state;
    }

    public String getStateString() {
        return getState().toString();
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
