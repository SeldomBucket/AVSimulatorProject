package aim4.sim;

import aim4.map.BasicMap;
import aim4.map.cpm.VerySimpleMap;
import aim4.vehicle.VehicleSimModel;

import java.util.List;

/**
 * The simulator of AVs in an AV specific car park which are self-organising.
 */
public class CPMAutoDriverSimulator implements Simulator {

    /////////////////////////////////
    // NESTED CLASSES
    /////////////////////////////////

    /**
     * The result of a simulation step.
     */
    public static class CPMAutoDriverSimStepResult implements SimStepResult {

        /** The VIN of the completed vehicles in this time step */
        List<Integer> completedVINs;

        /**
         * Create a result of a simulation step
         *
         * @param completedVINs  the VINs of completed vehicles.
         */
        public CPMAutoDriverSimStepResult(List<Integer> completedVINs) {
            this.completedVINs = completedVINs;
        }

        /**
         * Get the list of VINs of completed vehicles.
         *
         * @return the list of VINs of completed vehicles.
         */
        public List<Integer> getCompletedVINs() {
            return completedVINs;
        }
    }

    /////////////////////////////////
    // PRIVATE FIELDS
    /////////////////////////////////

    /** The map */
    private VerySimpleMap simpleMap;
    /** The current time */
    private double currentTime;
    /** The number of completed vehicles */
    private int numOfCompletedVehicles;
    /** The total number of bits transmitted by the completed vehicles */
    private int totalBitsTransmittedByCompletedVehicles;
    /** The total number of bits received by the completed vehicles */
    private int totalBitsReceivedByCompletedVehicles;

    public CPMAutoDriverSimulator(VerySimpleMap simpleMap){
        this.simpleMap = simpleMap;

        currentTime = 0.0;
        numOfCompletedVehicles = 0;
        totalBitsTransmittedByCompletedVehicles = 0;
        totalBitsReceivedByCompletedVehicles = 0;
    }

    @Override
    public SimStepResult step(double timeStep) {
        System.out.println("CPM Simulator speaking!");

        // spawnVehicles(timeStep);
        // provideSensorInput();
        // letDriversAct();
        // communication();
        // moveVehicles(timeStep);
        // List<Integer> completedVINs = cleanUpCompletedVehicles();
        currentTime += timeStep;
        // return new CPMAutoDriverSimStepResult(completedVINs);
        return null;
    }

    @Override
    public BasicMap getMap() {
        return null;
    }

    @Override
    public double getSimulationTime() {
        return 0;
    }

    @Override
    public int getNumCompletedVehicles() {
        return 0;
    }

    @Override
    public double getAvgBitsTransmittedByCompletedVehicles() {
        return 0;
    }

    @Override
    public double getAvgBitsReceivedByCompletedVehicles() {
        return 0;
    }

    @Override
    public VehicleSimModel getActiveVehicle(int vin) {
        return null;
    }
}
