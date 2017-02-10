package aim4.sim;

import aim4.map.BasicMap;
import aim4.vehicle.VehicleSimModel;

/**
 * Created by Becci on 10-Feb-17.
 */
public class CPMAutoDriverSimulator implements Simulator {
    @Override
    public SimStepResult step(double timeStep) {
        System.out.println("CPM Simulator speaking!");
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
