package aim4.sim.setup.merge;

import aim4.map.merge.S2SMergeMap;
import aim4.sim.Simulator;
import aim4.sim.simulator.merge.MergingProtocol;
import com.sun.scenario.effect.Merge;

/**
 * Created by Callum on 02/03/2017.
 */
public class S2SSimSetup implements MergeSimSetup {
    /**The rate of traffic flow**/
    double trafficRate;
    /**The speed limit of the target lane**/
    double targetLaneSpeedLimit;
    /**The speed limit of the merging lane**/
    double mergingLaneSpeedLimit;
    /**The distance between the target lane start and the merge point**/
    double targetLeadInDistance;
    /**The distance between the end of the target lane and the merge point**/
    double targetLeadOutDistance;
    /**The length of the merging road as it leads into the merge point**/
    double mergeLeadInDistance;
    /**The angle of aproach for the merging road**/
    double mergingAngle;

    public S2SSimSetup(double trafficRate, MergingProtocol protocol,
                       double targetLaneSpeedLimit, double mergingLaneSpeedLimit,
                       double targetLeadInDistance, double targetLeadOutDistance,
                       double mergeLeadInDistance, double mergingAngle) {
        this.trafficRate = trafficRate;
        this.targetLaneSpeedLimit = targetLaneSpeedLimit;
        this.mergingLaneSpeedLimit = mergingLaneSpeedLimit;
    }

    @Override
    public Simulator getSimulator() {
        double currentTime = 0.0;
        S2SMergeMap layout = new S2SMergeMap(currentTime,
                targetLaneSpeedLimit, mergingLaneSpeedLimit,
                targetLeadInDistance, targetLeadOutDistance,
                mergeLeadInDistance, mergingAngle);

        return null;
    }
}
