package aim4.sim.setup.merge;

import aim4.config.SimConfig;
import aim4.im.merge.reservation.ReservationMergeManager;
import aim4.map.merge.MergeMapUtil;
import aim4.map.merge.S2SMergeMap;
import aim4.sim.setup.merge.enums.ProtocolType;
import aim4.sim.simulator.merge.CentralManagementMergeSimulator;
import aim4.sim.simulator.merge.CoreMergeSimulator;
import aim4.sim.simulator.merge.MergeSimulator;

/**
 * Created by Callum on 02/03/2017.
 */
public class S2SSimSetup implements MergeSimSetup {
    /**The default traffic level **/
    public final static double DEFAULT_TRAFFIC_LEVEL = 0.28;
    /**The default speed limit of the target lane**/
    public final static double DEFAULT_TARGET_LANE_SPEED_LIMIT = 40.0;
    /**The default speed limit of the merging lane**/
    public final static double DEFAULT_MERGING_LANE_SPEED_LIMIT = 40.0;
    /**The default distance between the target lane start and the merge point**/
    public final static double DEFAULT_TARGET_LEAD_IN_DISTANCE = 150.0;
    /**The default distance between the end of the target lane and the merge point**/
    public final static double DEFAULT_TARGET_LEAD_OUT_DISTANCE = 150.0;
    /**The default length of the merging road as it leads into the merge point**/
    public final static double DEFAULT_MERGE_LEAD_IN_DISTANCE = 150.0;
    /**The default angle of aproach for the merging road**/
    public final static double DEFAULT_MERGING_ANGLE = 45.0;

    /**The merging protocol for the simulation**/
    ProtocolType mergingProtocol;
    /**The traffic level **/
    double trafficLevel;
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

    public S2SSimSetup(ProtocolType protocol, double trafficLevel,
                       double targetLaneSpeedLimit, double mergingLaneSpeedLimit,
                       double targetLeadInDistance, double targetLeadOutDistance,
                       double mergeLeadInDistance, double mergingAngle) {
        this.mergingProtocol = protocol;
        this.trafficLevel = trafficLevel;
        this.targetLaneSpeedLimit = targetLaneSpeedLimit;
        this.mergingLaneSpeedLimit = mergingLaneSpeedLimit;
        this.targetLeadInDistance = targetLeadInDistance;
        this.targetLeadOutDistance = targetLeadOutDistance;
        this.mergeLeadInDistance = mergeLeadInDistance;
        this.mergingAngle = mergingAngle;
    }

    @Override
    public MergeSimulator getSimulator() {
        double currentTime = 0.0;
        S2SMergeMap layout = new S2SMergeMap(currentTime,
                targetLaneSpeedLimit, mergingLaneSpeedLimit,
                targetLeadInDistance, targetLeadOutDistance,
                mergeLeadInDistance, mergingAngle);

        switch(mergingProtocol){
            case AIM:
                ReservationMergeManager.Config mergeReservationConfig =
                        new ReservationMergeManager.Config(SimConfig.TIME_STEP, SimConfig.MERGE_TIME_STEP);
                MergeMapUtil.setFCFSMergeManagers(layout, currentTime, mergeReservationConfig);
                MergeMapUtil.setUniformSpawnSpecGenerator(layout, trafficLevel);
                return new CentralManagementMergeSimulator(layout);
            case DECENTRALISED:
                MergeMapUtil.setUniformSpawnSpecGenerator(layout, trafficLevel);
                return null;
            case TEST_MERGE:
                MergeMapUtil.setUniformSpawnSpecGeneratorMergeLaneOnly(layout, trafficLevel);
                return new CoreMergeSimulator(layout);
            case TEST_TARGET:
                MergeMapUtil.setUniformSpawnSpecGeneratorTargetLaneOnly(layout, trafficLevel);
                return new CoreMergeSimulator(layout);
            default: throw new IllegalArgumentException("Unexpected Protocol Type: " + mergingProtocol.toString());
        }
    }

    public double getTrafficLevel() {
        return trafficLevel;
    }

    public double getTargetLaneSpeedLimit() {
        return targetLaneSpeedLimit;
    }

    public double getMergingLaneSpeedLimit() {
        return mergingLaneSpeedLimit;
    }

    public double getTargetLeadInDistance() {
        return targetLeadInDistance;
    }

    public double getTargetLeadOutDistance() {
        return targetLeadOutDistance;
    }

    public double getMergeLeadInDistance() {
        return mergeLeadInDistance;
    }

    public double getMergingAngle() {
        return mergingAngle;
    }
}
