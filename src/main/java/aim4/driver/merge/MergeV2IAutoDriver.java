package aim4.driver.merge;

import aim4.driver.merge.coordinator.MergeAutoCoordinator;
import aim4.driver.merge.coordinator.MergeQueueCoordinator;
import aim4.driver.merge.coordinator.MergeV2IAutoCoordinator;
import aim4.im.merge.MergeManager;
import aim4.map.merge.MergeMap;
import aim4.sim.setup.merge.enums.ProtocolType;
import aim4.vehicle.AutoVehicleDriverModel;
import aim4.vehicle.merge.MergeV2IAutoVehicleDriverModel;

import java.awt.geom.Area;

/**
 * Created by Callum on 13/04/2017.
 */
public class MergeV2IAutoDriver extends MergeAutoDriver {
    // PRIVATE FIELDS //
    /**
     * The MergeManager with which the driver is currently interfacing
     */
    private MergeManager currentMM;
    /** Memoization cache for {@link #nextMergeManager()}. */
    private transient MergeManager memoNextMergeManager;
    /** Memoization cache for {@link #inCurrentMerge()}. */
    private transient Boolean memoInCurrentMerge;

    private ProtocolType protocolType;

    public MergeV2IAutoDriver(MergeV2IAutoVehicleDriverModel vehicle, MergeMap map, ProtocolType protocolType) {
        super(vehicle, map);
        this.currentMM = null;
        this.protocolType = protocolType;
    }

    // PUBLIC METHODS //
    // ACTION
    @Override
    public void act() {
        clearMemoizationCaches();
        if (coordinator == null || coordinator.isTerminated()) {
            MergeManager mm = nextMergeManager();
            if (mm != null) {
                assert(vehicle instanceof MergeV2IAutoVehicleDriverModel);
                currentMM = mm;
                if(protocolType == ProtocolType.AIM_GRID || protocolType == ProtocolType.AIM_NO_GRID)
                    coordinator = new MergeV2IAutoCoordinator((MergeV2IAutoVehicleDriverModel) vehicle, this, map);
                else if(protocolType == ProtocolType.QUEUE)
                    coordinator = new MergeQueueCoordinator((MergeV2IAutoVehicleDriverModel) vehicle, this, map);
                else
                    coordinator = new MergeAutoCoordinator(vehicle, this, map);
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

    @Override
    public String getStateString() {
        return coordinator.getStateString();
    }

    // MM
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

    // PRIVATE AND PROTECTED METHODS //
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
    protected void clearMemoizationCaches() {
        super.clearMemoizationCaches();
        memoNextMergeManager = null;
        memoInCurrentMerge = null;
    }

}
