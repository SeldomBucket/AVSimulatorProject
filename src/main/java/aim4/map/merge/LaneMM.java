package aim4.map.merge;

import aim4.im.merge.MergeManager;
import aim4.map.lane.Lane;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Callum on 13/04/2017.
 */

/**
 * A lane and merge manager relationship object.
 */
public class LaneMM {
    //PRIVATE FIELDS//
    /** The lane*/
    private Lane lane;
    /** A map of normalized distances of exit points to merge managers*/
    private SortedMap<Double, MergeManager> mergeManagers = new TreeMap<Double, MergeManager>();
    /** Memoization cache for {@Link #nextMergeManager{MergeManager im)}*/
    private Map<MergeManager, MergeManager> memoGetSubsequentMergeManager = null;

    //CONSTRUCTORS//
    /**
     * Creates a lane and merge manager relationship object
     * @param lane the lane
     */
    public LaneMM(Lane lane) {
        this.lane = lane;
    }

    //PUBLIC METHODS//

    //REGISTRATION
    /**
     * Registers a {@Link MergeManager} with this Lane. If the Lane does not intersect the area controlled by the
     * MergeManager it has no effect. Otherwise the MergeManager is stored and returned under certain circumstances
     * by the <code>nextMergeManager</code> method.
     * @param mm
     */
    public void registerMergeManager(MergeManager mm) {
        //Only do this if the lane is managed by this merge
        if(mm.manages(lane)) {
            // Reset this cache
            memoGetSubsequentMergeManager = null;
            //Find out where this lane exits the merge
            Point2D exitPoint = mm.getMergeConnection().getExitPoint(lane);
            // If it's null, that means it doesn't exit.
            if(exitPoint == null) {
                exitPoint = lane.getEndPoint();
            }
            double normalizedDistanceToExit = lane.normalizedDistanceAlongLane(exitPoint);
            //Add the normalized distance to the exit point to the map, this gives us the "next merge" for any point in
            //the lane.
            mergeManagers.put(normalizedDistanceToExit, mm);
        }
    }

    //FIND MM
    /**
     * Get the first MergeManager that this Lane, or any Lane it leads
     * into enters. Recursively searches through all subsequent Lanes.
     *
     * @return the first MergeManager this Lane, or any Lane it leads
     *         into enters
     */
    public MergeManager firstMergeManager() {
        if(mergeManagers.isEmpty()){
            if(lane.hasNextLane()) {
                return lane.getNextLane().getLaneMM().firstMergeManager();
            }
            return null;
        }
        return mergeManagers.get(mergeManagers.firstKey());
    }

    /**
     * Get the distance from the start of this Lane to the first
     * MergeManager that this Lane, or any Lane it leads into intersects.
     * Recursively searches through all subsequent Lanes. Returns
     * <code>Double.MAX_VALUE</code> if no such MergeManager exists.
     *
     * @return the distance from the start of this Lane to the first
     *         MergeManager this Lane, or any lane it leads into
     *         intersects, or <code>Double.MAX_VALUE</code> if no such
     *         MergeManager exists
     */
    public double distanceToFirstMerge() {
        if(mergeManagers.isEmpty()) {
            if(lane.hasNextLane()) {
                return lane.getLength() +
                        lane.getNextLane().getLaneMM().distanceToFirstMerge();
            }
            return Double.MAX_VALUE;
        }
        // Otherwise, it's the distance from the start of the Lane to the entry
        // point of the first MergeManager
        MergeManager firstMM = mergeManagers.get(mergeManagers.firstKey());
        Point2D entry = firstMM.getMergeConnection().getEntryPoint(lane);
        if(entry == null) {
            return 0;
        }
        return lane.getStartPoint().distance(entry);
    }

    /**
     * Find the next Lane, including this one, that enters a merge at
     * any point.
     *
     * @return  the next Lane, following the chain of Lanes in which this Lane
     *          is, that enters a merge, at any point
     */
    public Lane laneToFirstMerge() {
        //If there aren't any more in this lane
        if(mergeManagers.isEmpty()) {
            //Check the next Lane
            if(lane.hasNextLane()) {
                //Pass to the next lane after this one
                return lane.getNextLane().getLaneMM().laneToFirstMerge();
            }
            //Otherwise there are none.
            return null;
        }
        //Otherwise it's this one.
        return lane;
    }

    /**
     * Get the last MergeManager that this Lane, or any Lane that leads
     * into it enters.  Recursively searches through all previous Lanes.
     *
     * @return the last MergeManager this Lane, or any Lane that leads
     *         into it enters.
     */
    public MergeManager lastMergeManager() {
        if(mergeManagers.isEmpty()) {
            if(lane.hasPrevLane()) {
                return lane.getPrevLane().getLaneMM().lastMergeManager();
            }
            return null;
        }
        return mergeManagers.get(mergeManagers.lastKey());
    }

    /**
     * Get the distance from the end of this Lane to the last
     * MergeManager that this Lane, or any Lane that leads into it
     * entered.  Recursively searches through all previous Lanes.  Returns
     * <code>Double.MAX_VALUE</code> if no such MergeManager exists.
     *
     * @return the distance from the end of this Lane to the last
     *         MergeManager this Lane, or any lane that leads into it
     *         entered, or <code>Double.MAX_VALUE</code> if no such
     *         MergeManager exists
     */
    public double remainingDistanceFromLastMerge() {
        if(mergeManagers.isEmpty()) {
            if(lane.hasPrevLane()) {
                return lane.getLength() +
                        lane.getPrevLane().getLaneMM().remainingDistanceFromLastMerge();
            } else {
                return Double.MAX_VALUE;
            }
        } else {
            return (1 - mergeManagers.lastKey()) * lane.getLength();
        }
    }

    //POINT -> MM
    /**
     * Find the next MergeManager a vehicle at the given position will
     * encounter. These are indexed based on how far along the lane the vehicle
     * is, from 0 (at the start) to 1 (at the end).
     *
     * @param p the location of the hypothetical vehicle
     * @return  the next MergeManager the vehicle will encounter, or
     *          <code>null</code> if none
     */
    public MergeManager nextMergeManager(Point2D p) {
        // First find how far along the point is.
        double index = lane.normalizedDistanceAlongLane(p);
        SortedMap<Double, MergeManager> remaining =
                mergeManagers.tailMap(index);
        // If nothing left, then no more MergeManagers
        if (remaining.isEmpty()) {
            if (lane.hasNextLane()) {
                return lane.getNextLane().getLaneMM().firstMergeManager();
            } else {
                return null;
            }
        } else {
            return remaining.get(remaining.firstKey());
        }
    }

    /**
     * Find the distance to the next MergeManager a vehicle at the given
     * position will encounter.  First projects the point onto the Lane.
     *
     * @param p the current location of the vehicle
     * @return  the distance along the Lane from the point on the Lane nearest
     *          to the given point to the next MergeManager a vehicle
     *          at the given point will encounter; if there is no next
     *          merge, return Double.MAX_VALUE
     */
    public double distanceToNextMerge(Point2D p) {
        // First determine how far along the Lane we are
        double index = lane.normalizedDistanceAlongLane(p);
        // Now find all MergeManagers that are after this point (remember
        // they are indexed by exit point)
        SortedMap<Double, MergeManager> remaining =
                mergeManagers.tailMap(index);
        // If there aren't any more in this lane
        if (remaining.isEmpty()) {
            // Check the next Lane
            if (lane.hasNextLane()) {
                return ((1 - index) * lane.getLength()) +
                        lane.getNextLane().getLaneMM().distanceToFirstMerge();
            } else {
                // Otherwise, just say it is really really far away
                return Double.MAX_VALUE;
            }
        } else {
            // Otherwise, we need to figure out where we are and where the current
            // Lane intersects the next merge.
            MergeManager nextMM = remaining.get(remaining.firstKey());
            Point2D entry = nextMM.getMergeConnection().getEntryPoint(lane);
            // Where does this Lane enter?
            if (entry == null) { // It doesn't! It just exits! That means we're in it!
                return 0.0;
            } else {
                // Otherwise, there is an entry point.  Find out how far along it is in
                // the Lane
                double entryFraction = lane.normalizedDistanceAlongLane(entry);
                // Now, we want to return 0 if we are past the entry point, or the
                // distance to the entry point otherwise
                return Math.max(0.0, (entryFraction - index) * lane.getLength());
            }
        }
    }

    /**
     * Find the next Lane, including this one, that will enter an merge,
     * starting at the point in this Lane nearest the provided point.
     *
     * @param p the current location of the vehicle
     * @return  the next Lane, following the chain of Lanes in which this Lane
     *          is, that will enter an merge, starting at the point in
     *          this Lane nearest the provided point
     */
    public Lane laneToNextMerge(Point2D p) {
        // First determine how far along the Lane we are
        double index = lane.normalizedDistanceAlongLane(p);
        // Now find all MergeManagers that are after this point (remember
        // they are indexed by exit point)
        SortedMap<Double, MergeManager> remaining =
                mergeManagers.tailMap(index);
        // If there aren't any more in this lane
        if(remaining.isEmpty()) {
            // Check the next Lane
            if(lane.hasNextLane()) {
                // Pass the buck to the next Lane after this one
                return lane.getNextLane().getLaneMM().laneToFirstMerge();
            }
            // Otherwise, there are none.
            return null;
        }
        // Otherwise, it is this one.
        return lane;
    }

    /**
     * Find the distance from a point, projected onto the Lane, to the previous
     * merge that a vehicle at that position on the Lane would have
     * encountered.
     *
     * @param p the current location of the vehicle
     * @return  the distance from a point, projected onto the Lane, to the
     *          previous merge that a vehicle at that position on the
     *          Lane would have encountered
     */
    public double distanceFromPrevMerge(Point2D p) {
        // First determine how far along the Lane we are
        double index = lane.normalizedDistanceAlongLane(p);
        // Now find all MergeManagers that are before this point (remember
        // they are indexed by exit point)
        SortedMap<Double, MergeManager> preceding =
                mergeManagers.headMap(index);
        // If there aren't any in this lane
        if(preceding.isEmpty()) {
            // Check the previous Lane
            if(lane.hasPrevLane()) {
                return (index * lane.getLength()) +
                        lane.getNextLane().getLaneMM().remainingDistanceFromLastMerge();
            }
            // Otherwise, just say it is really really far away
            return Double.MAX_VALUE;
        }
        // preceding.lastKey() is the relative distance to the exit point of the
        // last Merge in the Lane before our position, so we subtract that
        // from our current relative position (index) to get the total relative
        // distance. Then, multiply that by length to get an absolute distance.
        // This can't be negative because the last key must be before index
        // since we did a headMap.
        return (index - preceding.lastKey()) * lane.getLength();
    }

    //MM -> MM

    /**
     * Get the MergeManager that this Lane, or any Lane it leads into
     * enters, after the given MergeManager.
     *
     * @param mm the MergeManager to which we would like the successor
     * @return   the MergeManager that this Lane, or any Lane it leads
     *           into enters, after the given MergeManager
     */
    public MergeManager nextMergeManager(MergeManager mm) {
        // Build the cache if it doesn't exist
        if(memoGetSubsequentMergeManager == null) {
            memoGetSubsequentMergeManager =
                    new HashMap<MergeManager, MergeManager>();
            MergeManager lastMM = null;
            // Now run through the MergeManagers in order and set up
            // the cache
            for(MergeManager currMM : mergeManagers.values()) {
                // Don't include the first one as a value, since it isn't subsequent
                // to anything
                if(lastMM != null) {
                    memoGetSubsequentMergeManager.put(lastMM, currMM);
                }
                lastMM = currMM;
            }
            // Link up to the next Lane
            if(lastMM != null && lane.hasNextLane()) {
                memoGetSubsequentMergeManager.put(lastMM,lane.getNextLane().getLaneMM().firstMergeManager());
            }
        }
        return memoGetSubsequentMergeManager.get(mm);
    }

    /**
     * Get the distance from the given MergeManager to the next
     * one that that this Lane, or any Lane it leads into enters.
     *
     * @param mm          the MergeManager at which to start
     * @return            the distance, in meters, departing the given
     *                    MergeManager, to reach the next
     *                    MergeManager
     */
    public double distanceToNextMergeManager(MergeManager mm) {
        // Two cases: either the next merge is in this Lane, or it is
        // in a Lane connected to this one
        MergeManager nextMM = nextMergeManager(mm);
        if(nextMM == null) {
            // If there's no next merge, we just return 0 since the
            // behavior isn't well defined
            return 0;
        }
        if(nextMM.getMergeConnection().isEnteredBy(lane)) {
            // This is the easy case: just find the distance to the next
            // merge and divide by the speed limit
            return mm.getMergeConnection().getExitPoint(lane).distance(
                    nextMM.getMergeConnection().getEntryPoint(lane));
        } else {
            // This is more challenging.  We need to keep adding it up the Lanes
            // in between until we find it
            // Start with the distance to the end of this Lane
            double totalDist = remainingDistanceFromLastMerge();
            Lane currLane = lane.getNextLane();
            // Okay, add up all the lanes until the MM
            while(!nextMM.getMergeConnection().isEnteredBy(currLane)) {
                totalDist += currLane.getLength();
                currLane = currLane.getNextLane();
            }
            // Now we're at the Lane that actually enters the next MM
            totalDist += currLane.getLaneMM().distanceToFirstMerge();
            return totalDist;
        }
    }

    /**
     * Get the approximate time from the given MergeManager to the next
     * one that that this Lane, or any Lane it leads into enters, based on
     * distances and speed limits.
     *
     * @param mm          the MergeManager at which to start
     * @param maxVelocity the maximum velocity of the vehicle
     * @return            the time, in seconds, that it should take once
     *                    departing the given MergeManager, to reach the
     *                    next MergeManager
     */
    public double timeToNextMergeManager(MergeManager mm,
                                         double maxVelocity) {
        // Two cases: either the next merge is in this Lane, or it is
        // in a Lane connected to this one
        MergeManager nextMM = nextMergeManager(mm);
        if(nextMM == null) {
            // If there's no next merge, we just return 0 since the
            // behavior isn't well defined
            return 0;
        }
        if(nextMM.getMergeConnection().isEnteredBy(lane)) {
            // This is the easy case: just find the distance to the next
            // merge and divide by the speed limit
            return mm.getMergeConnection().getExitPoint(lane).distance(
                    nextMM.getMergeConnection().getEntryPoint(lane)) /
                    Math.min(lane.getSpeedLimit(), maxVelocity);
        } else {
            // This is more challenging.  We need to keep adding it up the Lanes
            // in between until we find it
            // Start with the distance to the end of this Lane
            double totalTime = remainingDistanceFromLastMerge() /
                    lane.getSpeedLimit();
            Lane currLane = lane.getNextLane();
            // Okay, add up all the lanes until the MM
            while(!nextMM.getMergeConnection().isEnteredBy(currLane)) {
                totalTime += currLane.getLength() /
                        Math.min(currLane.getSpeedLimit(), maxVelocity);
                currLane = currLane.getNextLane();
            }
            // Now we're at the Lane that actually enters the next MM
            totalTime += currLane.getLaneMM().distanceToFirstMerge() /
                    Math.min(currLane.getSpeedLimit(), maxVelocity);
            return totalTime;
        }
    }

}
