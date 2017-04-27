package aim4.sim.results;

import java.util.List;

/**
 * Created by Callum on 21/04/2017.
 */
public class CoreMergeResult {
    private List<CoreMergeVehicleResult> vehicleResults;
    private double throughput;
    private double maxDelay;
    private double minDelay;
    private double averageDelay;
    private double completedVehicles;

    public CoreMergeResult(List<CoreMergeVehicleResult> vehicleResults, double throughput) {
        this.vehicleResults = vehicleResults;
        this.throughput = throughput;
        this.maxDelay = Double.MIN_VALUE;
        this.minDelay = Double.MAX_VALUE;
        double totalDelay = 0;
        for(CoreMergeVehicleResult result : vehicleResults) {
            if(maxDelay < result.getDelayTime())
                maxDelay = result.getDelayTime();
            if(minDelay > result.getDelayTime())
                minDelay = result.getDelayTime();
            totalDelay += result.getDelayTime();
        }
        this.completedVehicles = vehicleResults.size();
        this.averageDelay = totalDelay / completedVehicles;
    }

    public List<CoreMergeVehicleResult> getVehicleResults() {
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

    public double getAverageDelay() {
        return averageDelay;
    }

    public double getCompletedVehicles() {
        return minDelay;
    }
}
