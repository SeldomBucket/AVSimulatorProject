package aim4.sim.results;

import java.util.List;

/**
 * Created by Callum on 21/04/2017.
 */
public class CoreMergeResult {
    //Nested Class
    public static class CoreMergeVehicleResult {
        private int vin;
        private String specType;
        private double finishTime;
        private double delayTime;
        private double finalVelocity;
        private double maxVelocity;
        private double minVelocity;

        public CoreMergeVehicleResult(int vin, String specType, double finishTime, double delayTime, double finalVelocity, double maxVelocity, double minVelocity) {
            this.vin = vin;
            this.specType = specType;
            this.finishTime = finishTime;
            this.delayTime = delayTime;
            this.finalVelocity = finalVelocity;
            this.maxVelocity = maxVelocity;
            this.minVelocity = minVelocity;
        }

        public int getVin() {
            return vin;
        }

        public String specType() {
            return specType;
        }

        public double getFinishTime() {
            return finishTime;
        }

        public double getDelayTime() {
            return delayTime;
        }

        public double getFinalVelocity() {
            return finalVelocity;
        }

        public double getMaxVelocity() {
            return maxVelocity;
        }

        public double getMinVelocity() {
            return minVelocity;
        }
    }

    private List<CoreMergeVehicleResult> vehicleResults;
    private double throughput;

    public CoreMergeResult(List<CoreMergeVehicleResult> vehicleResults, double throughput) {
        this.vehicleResults = vehicleResults;
        this.throughput = throughput;
    }

    public List<CoreMergeVehicleResult> getVehicleResults() {
        return vehicleResults;
    }

    public double getThroughput() {
        return throughput;
    }
}
