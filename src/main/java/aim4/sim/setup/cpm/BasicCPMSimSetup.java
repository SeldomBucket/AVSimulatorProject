package aim4.sim.setup.cpm;

import aim4.map.BasicMap;
import aim4.sim.Simulator;
import aim4.vehicle.VehicleSimModel;

/**
 * The basic simulation setup for CPM.
 */
public class BasicCPMSimSetup implements CPMSimSetup {
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
