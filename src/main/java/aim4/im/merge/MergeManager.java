package aim4.im.merge;

import aim4.map.Road;
import aim4.map.connections.MergeConnection;
import aim4.map.lane.Lane;
import aim4.util.Registry;
import aim4.vehicle.aim.AIMVehicleSimModel;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Created by Callum on 13/04/2017.
 */

/**
 * An agent managing a merge connection. This is an abstract class that sets up the properties of the intersection when
 * it is created.
 */
public abstract class MergeManager {
    //PRIVATE FIELDS//
    /** The ID number of this merge manager. */
    protected int id;
    /*The current time for this manager*/
    protected double currentTime;
    /*The merge managed by this manager*/
    private MergeConnection merge;

    //CONSTRUCTORS//

    /**
     * Creates a MergeManager
     * @param merge The merge connection this manager will manage vehicles through.
     * @param currentTime The current simulation time.
     * @param mmRegistry a Merge Manager registry
     */
    public MergeManager(MergeConnection merge, double currentTime, Registry<MergeManager> mmRegistry){
        this.merge = merge;
        this.currentTime = currentTime;
        this.id = mmRegistry.register(this);

        registerWithLanes();
    }

    /**
     * Registers this MergeManager with the lanes it manages.
     */
    private void registerWithLanes() {
        for(Lane lane : merge.getLanes()){
            lane.getLaneMM().registerMergeManager(this);
        }
    }

    //PUBIC METHODS//
    //ACT

    /**
     * Takes any actions required for a certain period of time.
     * @param timeStep the size of the timestep to simulate in seconds
     */
    public void act(double timeStep) {
        currentTime += timeStep;
    }

    //ACCESSORS
    /**
     * Get the unique ID number of this MergeManager.
     *
     * @return the ID number of this MergeManager
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the current time
     * @return The current simulation time.
     */
    public double getCurrentTime() {
        return currentTime;
    }

    /**
     * Returns the MergeConnection managed
     * @return The MergeConnection managed by this MergeManager.
     */
    public MergeConnection getMergeConnection() {
        return merge;
    }

    //MANAGEMENT

    /**
     * Returns true if this MergeManager manages the road provided.
     * @param r A Road to test
     * @return A boolean indicating whether this MergeManager manages the road provided.
     */
    public boolean manages(Road r) {
        return merge.getLanes().contains(r.getIndexLane());
    }

    /**
     * Returns true if this MergeManager manages the lane provided.
     * @param l A lane to test
     * @return A boolean indicating whether this MergeManager manages the lane provided.
     */
    public boolean manages(Lane l) {
        return merge.getLanes().contains(l);
    }

    //CHECKS
    /**
     * Determine whether the given Vehicle is currently entirely contained
     * within the Area governed by this IntersectionManager.
     *
     * @param vehicle the Vehicle
     * @return        whether the Vehicle is currently entirely contained within
     *                the Area governed by this IntersectionManager
     */
    public boolean contains(AIMVehicleSimModel vehicle) {
        // Get all corners of the vehicle and make sure they are inside the
        // intersection.
        for(Point2D corner : vehicle.getCornerPoints()) {
            if (!merge.getArea().contains(corner)) {
                return false;
            }
        }
        // If all corners are inside, the whole thing is considered inside.
        return true;
    }

    /**
     * Returns true if the Rectangle2D provided intersects the MergeConnection the MergeManager is managing.
     * @param rectangle The rectangle to check
     * @return A boolean indicating whether the rectangle provided intersects the MergeConnection managed.
     */
    public boolean intersects(Rectangle2D rectangle) {
        return merge.getArea().intersects(rectangle);
    }
}
