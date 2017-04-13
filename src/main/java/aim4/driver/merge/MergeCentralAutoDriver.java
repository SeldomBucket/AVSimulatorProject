package aim4.driver.merge;

import aim4.driver.merge.coordinator.MergeAutoCoordinator;
import aim4.driver.merge.coordinator.MergeCentralAutoCoordinator;
import aim4.im.merge.MergeManager;
import aim4.map.merge.MergeMap;
import aim4.vehicle.AutoVehicleDriverModel;
import aim4.vehicle.merge.MergeAutoVehicleSimModel;

import java.awt.geom.Area;

/**
 * Created by Callum on 13/04/2017.
 */
public class MergeCentralAutoDriver extends MergeAutoDriver {
    /**
     * The MergeManager with which the driver is currently interfacing
     */
    private MergeManager currentMM;
    /** Memoization cache for {@link #distanceToNextMerge()}. */
    private transient Double memoDistanceToNextMerge;
    /** Memoization cache for {@link #distanceFromPrevMerge()}. */
    private transient Double memoDistanceFromPrevMerge;
    /** Memoization cache for {@link #nextMergeManager()}. */
    private transient MergeManager memoNextMergeManager;
    /** Memoization cache for {@link #inCurrentMerge()}. */
    private transient Boolean memoInCurrentMerge;

    // PRIVATE FIELDS //

    public MergeCentralAutoDriver(MergeAutoVehicleSimModel vehicle, MergeMap map) {
        super(vehicle, map);
        currentMM = null;
    }

    // PUBLIC METHODS //
    // ACTION
    @Override
    public void act() {
        clearMemoizationCaches();
        if (coordinator == null || coordinator.isTerminated()) {
            MergeManager mm = nextMergeManager();
            if (mm != null) {
                currentMM = mm;
                coordinator = new MergeCentralAutoCoordinator(vehicle, this, map);
            } else {
                currentMM = null;
                coordinator = new MergeAutoCoordinator(vehicle, this, map);
            }
        }
        // the newly created coordinator can be called immediately.
        if (!coordinator.isTerminated()) {
            coordinator.act();
        }
    }

    // ACCESSORS
    public MergeManager getCurrentMM() {
        return currentMM;
    }

    // IM
    /** Find the next MergeManager that the Vehicle will need to
    * interact with, in this Lane.
    * @return the nextMergeManager that the Vehicle will need
    *         to interact with, in this Lane
    */
    public MergeManager nextMergeManager() {
        if(memoNextMergeManager == null) {
            memoNextMergeManager = getCurrentLane().getLaneMM().
                    nextMergeManager(getVehicle().gaugePosition());
        }
        return memoNextMergeManager;
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

    /**
     * Whether or not the Vehicle controlled by this driver agent
     * is inside the merge managed by the current MergeManager.
     *
     * @return whether or not the Vehicle controlled by this
     *         CoordinatingDriverAgent is inside the merge managed by the
     *         current MergeManager.
     */
    public boolean inCurrentMerge() {
        if(memoInCurrentMerge == null) {
            memoInCurrentMerge = intersects(getVehicle(), currentMM.getMergeConnection().getArea());
        }
        return memoInCurrentMerge;
    }

    // PRIVATE METHODS //
    /**
     * Determine whether the given Vehicle is currently inside an area
     *
     * @param v     the vehicle
     * @param area  the area
     * @return      whether the Vehicle is currently in the area
     */
    private static boolean intersects(AutoVehicleDriverModel v, Area area) {
        // TODO: move this function to somewhere else.

        // As a quick check, see if the front or rear point is in the intersection
        // Most of the time this should work
        if(area.contains(v.gaugePosition()) || area.contains(v.gaugePointAtRear())){
            return true;
        } else {
            // We actually have to check to see if the Area of the
            // Vehicle and the Area of the IntersectionManager have a nonempty
            // intersection
            Area vehicleArea = new Area(v.gaugeShape());
            // Important that it is in this order, as it is destructive to the caller
            vehicleArea.intersect(area);
            return !vehicleArea.isEmpty();
        }
    }

    /**
     * Clear any caches we are using to memoize methods
     */
    private void clearMemoizationCaches() {
        memoNextMergeManager = null;
        memoInCurrentMerge = null;
        memoDistanceToNextMerge = null;
        memoDistanceFromPrevMerge = null;
    }

}
