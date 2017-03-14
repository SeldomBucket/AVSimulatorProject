package aim4.sim.simulator.aim;

import aim4.map.aim.BasicIntersectionMap;
import aim4.sim.Simulator;
import aim4.vehicle.VehicleSimModel;
import aim4.vehicle.aim.AIMVehicleSimModel;
import aim4.vehicle.aim.ProxyVehicleSimModel;

import java.util.Set;

/**
 * Created by Callum on 28/11/2016.
 */
public interface AIMSimulator extends Simulator {
    /**
     * Get the set of all active vehicles in the simulation.
     *
     * @return the set of all active vehicles in the simulation
     */
    Set<AIMVehicleSimModel> getActiveVehicles();

    @Override
    BasicIntersectionMap getMap();

    /**
     * Add the proxy vehicle to the simulator for the mixed reality experiments.
     *
     * @param vehicle  the proxy vehicle
     */
    void addProxyVehicle(ProxyVehicleSimModel vehicle);

    /**
     * Get average number of bits transmitted by completed vehicles.
     *
     * @return the average number of bits transmitted by completed vehicles
     */
    double getAvgBitsTransmittedByCompletedVehicles();

    /**
     * Get average number of bits received by completed vehicles.
     *
     * @return the average number of bits received by completed vehicles
     */
    double getAvgBitsReceivedByCompletedVehicles();

    /**
     * Get a particular active vehicle via a given VIN.
     *
     * @param vin  the VIN number of the vehicle
     * @return the active vehicle
     */
    AIMVehicleSimModel getActiveVehicle(int vin);
}
