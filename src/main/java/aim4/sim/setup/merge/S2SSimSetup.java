package aim4.sim.setup.merge;

import aim4.config.SimConfig;
import aim4.im.merge.reservation.grid.ReservationMergeGridManager;
import aim4.im.merge.reservation.nogrid.ReservationMergeManager;
import aim4.map.merge.MergeMapUtil;
import aim4.map.merge.S2SMergeMap;
import aim4.sim.setup.merge.enums.ProtocolType;
import aim4.sim.simulator.merge.CoreMergeSimulator;
import aim4.sim.simulator.merge.MergeSimulator;
import aim4.sim.simulator.merge.V2IMergeSimulator;

import java.io.File;

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
    /**The json file dictating target spawn times and types**/
    File targetSpawnSchedule;
    /**The json file dictating merge spawn times and types**/
    File mergeSpawnSchedule;

    public S2SSimSetup(ProtocolType protocol, double trafficLevel,
                            double targetLaneSpeedLimit, double mergingLaneSpeedLimit,
                            double targetLeadInDistance, double targetLeadOutDistance,
                            double mergeLeadInDistance, double mergingAngle,
                            File targetSpawnSchedule, File mergeSpawnSchedule) {
        this.mergingProtocol = protocol;
        this.trafficLevel = trafficLevel;
        this.targetLaneSpeedLimit = targetLaneSpeedLimit;
        this.mergingLaneSpeedLimit = mergingLaneSpeedLimit;
        this.targetLeadInDistance = targetLeadInDistance;
        this.targetLeadOutDistance = targetLeadOutDistance;
        this.mergeLeadInDistance = mergeLeadInDistance;
        this.mergingAngle = mergingAngle;
        this.targetSpawnSchedule = targetSpawnSchedule;
        this.mergeSpawnSchedule = mergeSpawnSchedule;
    }

    public S2SSimSetup(ProtocolType protocol, double trafficLevel,
                       double targetLaneSpeedLimit, double mergingLaneSpeedLimit,
                       double targetLeadInDistance, double targetLeadOutDistance,
                       double mergeLeadInDistance, double mergingAngle) {
        this(
                protocol, trafficLevel,
                targetLaneSpeedLimit, mergingLaneSpeedLimit,
                targetLeadInDistance, targetLeadOutDistance,
                mergeLeadInDistance, mergingAngle,
                null, null
        );
    }

    @Override
    public MergeSimulator getSimulator() {
        double currentTime = 0.0;
        S2SMergeMap layout = new S2SMergeMap(currentTime,
                targetLaneSpeedLimit, mergingLaneSpeedLimit,
                targetLeadInDistance, targetLeadOutDistance,
                mergeLeadInDistance, mergingAngle);

        switch(mergingProtocol){
            case AIM_GRID:
                ReservationMergeGridManager.Config mergeGridReservationConfig =
                        new ReservationMergeGridManager.Config(
                                SimConfig.TIME_STEP,
                                SimConfig.GRID_TIME_STEP,
                                0.1,
                                0.15,
                                0.15,
                                true,
                                1.0);
                MergeMapUtil.setFCFSGridMergeManagers(layout, currentTime, mergeGridReservationConfig);
                setSpawnSpecs(layout);
                return new V2IMergeSimulator(layout, mergingProtocol);
            case AIM_NO_GRID:
                ReservationMergeManager.Config mergeReservationConfig =
                        new ReservationMergeManager.Config(SimConfig.TIME_STEP, SimConfig.MERGE_TIME_STEP);
                MergeMapUtil.setFCFSMergeManagers(layout, currentTime, mergeReservationConfig);
                setSpawnSpecs(layout);
                return new V2IMergeSimulator(layout, mergingProtocol);
            case QUEUE:
                MergeMapUtil.setQueueMergeManagers(layout, currentTime);
                setSpawnSpecs(layout);
                return new V2IMergeSimulator(layout, mergingProtocol);
            case TEST_MERGE:
                MergeMapUtil.setUniformSpawnSpecGeneratorMergeLaneOnly(layout, trafficLevel);
                return new CoreMergeSimulator(layout, mergingProtocol);
            case TEST_TARGET:
                MergeMapUtil.setUniformSpawnSpecGeneratorTargetLaneOnly(layout, trafficLevel);
                return new CoreMergeSimulator(layout, mergingProtocol);
            default: throw new IllegalArgumentException("Unexpected Protocol Type: " + mergingProtocol.toString());
        }
    }

    private void setSpawnSpecs(S2SMergeMap layout) {
        if(targetSpawnSchedule == null && mergeSpawnSchedule == null)
            MergeMapUtil.setUniformSpawnSpecGenerator(layout, trafficLevel);
        else if(targetSpawnSchedule != null && mergeSpawnSchedule != null)
            MergeMapUtil.setJSONScheduleSpawnSpecGenerator(layout, mergeSpawnSchedule, targetSpawnSchedule);
        else
            throw new IllegalArgumentException("Both target and merge spawn schedules must be set");
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
