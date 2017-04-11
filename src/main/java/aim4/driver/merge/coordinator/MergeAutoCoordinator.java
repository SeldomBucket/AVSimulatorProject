package aim4.driver.merge.coordinator;

import aim4.driver.Coordinator;
import aim4.driver.merge.MergeAutoDriver;
import aim4.driver.merge.navigator.MergeAutoNavigator;
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

    public enum MergeDrivingState {
        PLANNING,
        DEFAULT_DRIVING_BEHAVIOUR,
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

    //MergeDrivingState
    private MergeDrivingState state;
    private double lastStateChangeTime = 0.0;
    private EnumMap<MergeDrivingState, StateHandler> stateHandlers;

    //CONSTRUCTOR//
    public MergeAutoCoordinator(MergeAutoVehicleDriverModel vehicle, MergeAutoDriver driver, MergeMap map) {
        this.vehicle = vehicle;
        this.driver = driver;
        this.map = map;
        this.pilot = new MergeAutoPilot(vehicle, driver);
        this.navigator = new MergeAutoNavigator(vehicle.getSpec(), map);

        initStateHandlers();

        setState(MergeDrivingState.PLANNING);
    }

    //STATE CONTROLLERS//
    public MergeDrivingState getState() {
        return state;
    }

    private void setState(MergeDrivingState state) {
        this.state = state;
        lastStateChangeTime = vehicle.gaugeTime();
    }

    private void initStateHandlers() {
        stateHandlers = new EnumMap<MergeDrivingState, StateHandler>(MergeDrivingState.class);

        stateHandlers.put(MergeDrivingState.PLANNING, new PlanningStateHandler());
        stateHandlers.put(MergeDrivingState.DEFAULT_DRIVING_BEHAVIOUR, new DefaultDrivingBehaviourHandler());
        stateHandlers.put(MergeDrivingState.TRAVERSING_MERGE, new TraversingMergeStateHandler());
        stateHandlers.put(MergeDrivingState.TERMINAL_STATE, terminalStateHandler);
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
                setState(MergeDrivingState.TRAVERSING_MERGE);
            } else {
                setState(MergeDrivingState.DEFAULT_DRIVING_BEHAVIOUR);
            }
            return true;
        }
    }

    private class DefaultDrivingBehaviourHandler implements StateHandler {
        @Override
        public boolean perform() {
            pilot.followCurrentLane();
            pilot.simpleThrottleAction();
            setState(MergeDrivingState.PLANNING);
            return false;
        }
    }

    private class TraversingMergeStateHandler implements StateHandler {
        @Override
        public boolean perform() {
            MergeConnection mergeConnection = driver.inMerge();
            if(mergeConnection == null){
                //Vehicle cleared connection. Return to normal driving
                setState(MergeDrivingState.DEFAULT_DRIVING_BEHAVIOUR);
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
        return state == MergeDrivingState.TERMINAL_STATE;
    }
}
