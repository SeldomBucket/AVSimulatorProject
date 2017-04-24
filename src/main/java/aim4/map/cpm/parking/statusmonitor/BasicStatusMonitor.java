package aim4.map.cpm.parking.statusmonitor;

import aim4.map.cpm.parking.ParkingLane;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;

import java.util.HashMap;
import java.util.Map;

/**
 * An object which holds an updated status of the car park,
 * including the space left in each parking lane and the
 * remaining capacity of the car park.
 */
public abstract class BasicStatusMonitor implements StatusMonitor {

    /**
     * A mapping from parking lanes to the amount of
     * space left for parking on that lane.
     */
    protected Map<ParkingLane, Double> parkingLanesSpace = new HashMap<ParkingLane, Double>();
    /**
     * A list of vehicles which are currently in the car park,
     * and the lane they are parked in.
     */
    protected Map<CPMBasicAutoVehicle, ParkingLane> vehicles = new HashMap<CPMBasicAutoVehicle, ParkingLane>();
    /**
     * The number of vehicles denied entry due to not enough room.
     */
    protected int numberOfDeniedEntries;
    /**
     * The number of vehicles allowed entry as there is enough room.
     */
    protected int numberOfAllowedEntries;
    /**
     * The most number of vehicles that have been in the car park at any one time during simulation.
     */
    protected int mostNumberOfVehicles;

    /**
     * Create a BasicStatusMonitor to record the status of the car park.
     */
    public BasicStatusMonitor() {
        numberOfDeniedEntries = 0;
        numberOfAllowedEntries = 0;
        mostNumberOfVehicles = 0;
    }

    protected double calculateTotalVehicleSpace(CPMBasicAutoVehicle vehicle) {
        double vehicleLength = vehicle.getSpec().getLength();
        double distanceBetweenVehicles = CPMAutoDriverSimulator.MIN_DISTANCE_BETWEEN_PARKED_VEHICLES; // TODO CPM find this value from AIM
        return vehicleLength + distanceBetweenVehicles;
    }

    public void updateMostNumberOfVehicles() {
        int currentNumberOfVehicles = vehicles.size();
        if (currentNumberOfVehicles > mostNumberOfVehicles) {
            mostNumberOfVehicles = currentNumberOfVehicles;
        }
    }

    public Map<CPMBasicAutoVehicle, ParkingLane> getVehicles() {
        return vehicles;
    }

    public int getNumberOfDeniedEntries() {
        return numberOfDeniedEntries;
    }

    public int getNumberOfAllowedEntries() {
        return numberOfAllowedEntries;
    }

    public int getMostNumberOfVehicles() {
        return mostNumberOfVehicles;
    }
}
