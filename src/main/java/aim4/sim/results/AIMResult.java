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
    private double averageDelay;

    public AIMResult(List<AIMVehicleResult> vehicleResults, double throughput) {
        this.vehicleResults = vehicleResults;
        this.throughput = throughput;
        this.maxDelay = Double.MIN_VALUE;
        this.minDelay = Double.MAX_VALUE;
        double totalDelay = 0;
        for(AIMVehicleResult result : vehicleResults) {
            if(maxDelay < result.getDelayTime())
                maxDelay = result.getDelayTime();
            if(minDelay > result.getDelayTime())
                minDelay = result.getDelayTime();
            totalDelay += result.getDelayTime();
        }
        this.averageDelay = totalDelay / vehicleResults.size();
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

    public double getAverageDelay() {
        return minDelay;
    }
}
