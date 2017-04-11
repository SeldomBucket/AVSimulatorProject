package aim4.driver.merge.coordinator;

import aim4.driver.Coordinator;
import aim4.driver.merge.MergeAutoDriver;
import aim4.map.merge.MergeMap;
import aim4.vehicle.merge.MergeAutoVehicleDriverModel;

/**
 * Created by Callum on 25/03/2017.
 */
public class MergeAutoCoordinator implements Coordinator {


    public MergeAutoCoordinator(MergeAutoVehicleDriverModel vehicle, MergeAutoDriver mergeAutoDriver, MergeMap map) {
    }

    @Override
    public void act() {

    }

    @Override
    public boolean isTerminated() {
        return false;
    }
}
