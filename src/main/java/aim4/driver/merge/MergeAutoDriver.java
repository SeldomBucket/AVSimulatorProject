package aim4.driver.merge;

import aim4.driver.AutoDriver;
import aim4.map.lane.Lane;
import aim4.map.merge.MergeMap;
import aim4.vehicle.VehicleDriverModel;
import aim4.vehicle.aim.AIMAutoVehicleDriverModel;
import aim4.vehicle.merge.MergeAutoVehicleDriverModel;
import com.sun.scenario.effect.Effect;
import com.sun.scenario.effect.Merge;

import java.util.Set;

/**
 * Created by Callum on 14/03/2017.
 */
public class MergeAutoDriver extends MergeDriver implements AutoDriver {
    private MergeAutoVehicleDriverModel vehicle;
    private MergeMap map;

    public MergeAutoDriver(MergeAutoVehicleDriverModel vehicle, MergeMap map){
        this.vehicle = vehicle;
        this.map = map;
    }

    @Override
    public void addCurrentlyOccupiedLane(Lane lane) {

    }

    @Override
    public void act() {

    }

    @Override
    public VehicleDriverModel getVehicle() {
        return null;
    }

    @Override
    public Lane getCurrentLane() {
        return null;
    }

    @Override
    public Set<Lane> getCurrentlyOccupiedLanes() {
        return null;
    }

    @Override
    public void setCurrentLane(Lane lane) {

    }
}
