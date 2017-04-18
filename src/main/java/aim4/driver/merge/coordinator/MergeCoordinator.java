package aim4.driver.merge.coordinator;

import aim4.driver.Coordinator;
import aim4.driver.merge.MergeAutoDriver;
import aim4.driver.merge.pilot.MergeAutoPilot;
import aim4.map.merge.MergeMap;
import aim4.vehicle.merge.MergeAutoVehicleDriverModel;

/**
 * Created by Callum on 13/04/2017.
 */

/**
 * For all Coordinators that need to deal with merges.
 */
public abstract class MergeCoordinator implements Coordinator {
    // PUBLIC ABSTRACT
    public abstract String getStateString();
    // NESTED CLASSES //
    /**
     * An interface of the state handler.
     */
    protected static interface StateHandler {
        /**
         * Perform the action defined by the state handler at the driver state.
         *
         * @return true if the driver agent should proceed to the next action
         * immediately.
         */
        boolean perform();
    }

    protected static StateHandler terminalStateHandler =
            new StateHandler() {
                @Override
                public boolean perform() {
                    return false; //do nothing
                }
            };

    //PROTECTED FIELDS //
    //VEHICLE AND DRIVER

    /** The Vehicle being coordinated by this coordinator */
    protected MergeAutoVehicleDriverModel vehicle;
    /** The driverof which this coordinator is part */
    protected MergeAutoDriver driver;
    /** The sub-agent controlling the physical manipulation of the vehicle */
    protected MergeAutoPilot pilot;
    /** The map the driver is coordinating through */
    protected MergeMap map;
}
