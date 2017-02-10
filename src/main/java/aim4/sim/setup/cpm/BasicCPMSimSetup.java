package aim4.sim.setup.cpm;

import aim4.map.BasicMap;
import aim4.sim.Simulator;
import aim4.vehicle.VehicleSimModel;

/**
 * The basic simulation setup for CPM. Common for all CPM simulation types.
 */
public class BasicCPMSimSetup implements CPMSimSetup {

    /** The speed limit of the roads */
    protected double speedLimit;

    /**
     * Create a copy of a given basic simulator setup.
     *
     * @param basicSimSetup  a basic simulator setup
     */
    public BasicCPMSimSetup(BasicCPMSimSetup basicSimSetup) {
        this.speedLimit = basicSimSetup.speedLimit;
    }

    /**
     * Create a basic simulator setup.
     *
     * @param speedLimit                  the speed limit in the car park
     */
    public BasicCPMSimSetup(double speedLimit) {
        this.speedLimit = speedLimit;
    }

    @Override
    public Simulator getSimulator() {
        return new Simulator() {
            @Override
            public SimStepResult step(double timeStep) {
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
        };
    }
}
