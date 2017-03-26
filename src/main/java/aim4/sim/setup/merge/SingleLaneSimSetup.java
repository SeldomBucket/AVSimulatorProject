package aim4.sim.setup.merge;

import aim4.map.merge.MergeMapUtil;
import aim4.map.merge.SingleLaneOnlyMap;
import aim4.sim.simulator.merge.CoreMergeSimulator;
import aim4.sim.simulator.merge.MergeSimulator;

/**
 * Created by Callum on 26/03/2017.
 */
public class SingleLaneSimSetup implements MergeSimSetup {
    private double trafficRate;
    private double speedLimit;
    private double laneLength;

    public SingleLaneSimSetup(double trafficRate, double speedLimit, double laneLength) {
        this.trafficRate = trafficRate;
        this.speedLimit = speedLimit;
        this.laneLength = laneLength;
    }

    @Override
    public MergeSimulator getSimulator() {
        double currentTime = 0.0;
        SingleLaneOnlyMap map = new SingleLaneOnlyMap(currentTime, speedLimit, laneLength);
        MergeMapUtil.setUniformSpawnSpecGenerator(map, trafficRate);

        return new CoreMergeSimulator(map);
    }
}
