package aim4.driver.merge;

import aim4.driver.AutoDriver;
import aim4.driver.merge.coordinator.MergeAutoCoordinator;
import aim4.map.connections.MergeConnection;
import aim4.map.merge.MergeMap;
import aim4.vehicle.merge.MergeAutoVehicleDriverModel;

import java.awt.geom.Area;

/**
 * Created by Callum on 14/03/2017.
 */
public class MergeAutoDriver extends MergeDriver implements AutoDriver {
    // PROTECTED FIELDS //

    /*The vehicle controlled by this MergeAutoDriver*/
    protected MergeAutoVehicleDriverModel vehicle;
    /*The sub-agent controlling vehicle co-ordination*/
    protected MergeAutoCoordinator coordinator;
    /*The map navigated by this MergeAutoDriver*/
    protected MergeMap map;

    public MergeAutoDriver(MergeAutoVehicleDriverModel vehicle, MergeMap map) {
        this.vehicle = vehicle;
        this.map = map;
        coordinator = null;
    }

    @Override
    public void act() {
        if(coordinator == null || coordinator.isTerminated()) {
            this.coordinator = new MergeAutoCoordinator(vehicle, this, map);
        }

        if(!coordinator.isTerminated()) {
            coordinator.act();
        }
    }


    @Override
    public MergeAutoVehicleDriverModel getVehicle() {
        return this.vehicle;
    }

    public MergeConnection inMerge() {
        if(map.getMergeConnections() == null)
            return null;
        for(MergeConnection merge : map.getMergeConnections()) {
            if(intersectsArea(vehicle, merge.getArea())){
                return merge;
            }
        }
        return null;
    }

    protected boolean intersectsArea(MergeAutoVehicleDriverModel vehicle, Area area) {
        // As a quick check, see if the front or rear point is in the area
        // Most of the time this should work
        if(area.contains(vehicle.gaugePosition()) || area.contains(vehicle.gaugePointAtRear())){
            return true;
        } else {
            // We actually have to check to see if the Area of the
            // Vehicle and the given Area have a nonempty intersection
            Area vehicleArea = new Area(vehicle.gaugeShape());
            // Important that it is in this order, as it is destructive to the caller
            vehicleArea.intersect(area);
            return !vehicleArea.isEmpty();
        }
    }
}
