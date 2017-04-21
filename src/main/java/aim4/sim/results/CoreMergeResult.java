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
        private double startTime;
        private double finishTime;
        private double delayTime;
        private double finalVelocity;
        private double maxVelocity;
        private double minVelocity;
        private double finalXPos;
        private double finalYPos;

        public CoreMergeVehicleResult(int vin, String specType, double startTime, double finishTime, double delayTime, double finalVelocity, double maxVelocity, double minVelocity, double finalXPos, double finalYPos) {
            this.vin = vin;
            this.specType = specType;
            this.startTime = startTime;
            this.finishTime = finishTime;
            this.delayTime = delayTime;
            this.finalVelocity = finalVelocity;
            this.maxVelocity = maxVelocity;
            this.minVelocity = minVelocity;
            this.finalXPos = finalXPos;
            this.finalYPos = finalYPos;
        }

        public int getVin() {
            return vin;
        }

        public String specType() {
            return specType;
        }

        public double getStartTime() {
            return startTime;
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

        public double getFinalXPos() {
            return finalXPos;
        }

        public double getFinalYPos() {
            return finalYPos;
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
