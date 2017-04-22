package aim4.sim.results;

import java.util.List;

/**
 * Created by Callum on 21/04/2017.
 */
public class AIMResult {
    private List<AIMVehicleResult> vehicleResults;
    private double throughput;
    private double maxDelay;
    private double minDelay;

    public AIMResult(List<AIMVehicleResult> vehicleResults, double throughput) {
        this.vehicleResults = vehicleResults;
        this.throughput = throughput;
        this.maxDelay = Double.MIN_VALUE;
        this.minDelay = Double.MAX_VALUE;
        for(AIMVehicleResult result : vehicleResults) {
            if(maxDelay < result.getDelayTime())
                maxDelay = result.getDelayTime();
            if(minDelay > result.getDelayTime())
                minDelay = result.getDelayTime();
        }
    }

    public List<AIMVehicleResult> getVehicleResults() {
        return vehicleResults;
    }

    public double getThroughput() {
        return throughput;
    }

    public double getMaxDelay() {
        return maxDelay;
    }

    public double getMinDelay() {
        return minDelay;
    }
}
