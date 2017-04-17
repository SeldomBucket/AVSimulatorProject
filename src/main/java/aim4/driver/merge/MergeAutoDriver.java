package aim4.driver.merge;

import aim4.driver.AutoDriver;
import aim4.driver.merge.coordinator.MergeAutoCoordinator;
import aim4.driver.merge.coordinator.MergeCoordinator;
import aim4.map.connections.MergeConnection;
import aim4.map.merge.MergeMap;
import aim4.vehicle.merge.MergeAutoVehicleDriverModel;

import java.awt.geom.Area;

/**
 * Created by Callum on 14/03/2017.
 */
public class MergeAutoDriver extends MergeDriver implements AutoDriver {
    /** Memoization cache for {@link #distanceToNextMerge()}. */
    private transient Double memoDistanceToNextMerge;
    /** Memoization cache for {@link #distanceFromPrevMerge()}. */
    private transient Double memoDistanceFromPrevMerge;

    // PROTECTED FIELDS //

    /*The vehicle controlled by this MergeAutoDriver*/
    protected MergeAutoVehicleDriverModel vehicle;
    /*The sub-agent controlling vehicle co-ordination*/
    protected MergeCoordinator coordinator;
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

    @Override
    public String getStateString() {
        return this.coordinator.getStateString();
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

    /**
     * Find the distance to the next merge in the Lane in which
     * the Vehicle is, from the position at which the Vehicle is.
     *
     * @return the distance to the next merge given the current Lane
     *         and position of the Vehicle.
     */
    public double distanceToNextMerge() {
        if(memoDistanceToNextMerge == null) {
            memoDistanceToNextMerge = getCurrentLane().getLaneMM().
                    distanceToNextMerge(getVehicle().gaugePosition());
        }
        return memoDistanceToNextMerge;
    }

    /**
     * Find the distance from the previous merge in the Lane in which
     * the Vehicle is, from the position at which the Vehicle is.  This
     * subtracts the length of the Vehicle from the distance from the front
     * of the Vehicle.  It overrides the version in DriverAgent, but only to
     * memoize it.
     *
     * @return the distance from the previous merge given the current
     *         Lane and position of the Vehicle.
     */
    public double distanceFromPrevMerge() {
        if(memoDistanceFromPrevMerge == null) {
            double d = getCurrentLane().getLaneMM().
                    distanceFromPrevMerge(getVehicle().gaugePosition());
            memoDistanceFromPrevMerge = Math.max(0.0, d - getVehicle().getSpec().getLength());
        }
        return memoDistanceFromPrevMerge;
    }

    protected void clearMemoizationCaches() {
        memoDistanceToNextMerge = null;
        memoDistanceFromPrevMerge = null;
    }
}
