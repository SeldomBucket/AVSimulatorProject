package aim4.driver.merge.coordinator;

import aim4.driver.Coordinator;
import aim4.driver.merge.MergeAutoDriver;
import aim4.driver.merge.navigator.MergeAutoNavigator;
import aim4.driver.merge.pilot.MergeAutoPilot;
import aim4.map.merge.MergeMap;
import aim4.vehicle.merge.MergeAutoVehicleDriverModel;

import java.util.EnumMap;

/**
 * Created by Callum on 25/03/2017.
 */
public class MergeAutoCoordinator implements Coordinator {

    ////NESTED CLASSES////

    //STATE HANDLING//
    private static interface StateHandler {
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
        PLANNING,
        DEFAULT_DRIVING_BEHAVIOUR,
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
            setState(State.DEFAULT_DRIVING_BEHAVIOUR);
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
