package aim4.map.cpm.parking.statusmonitor;

import aim4.driver.cpm.CPMV2VDriver;
import aim4.map.cpm.parking.ParkingLane;
import aim4.map.cpm.parking.parkingarea.SingleLaneWidthParkingArea;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import aim4.vehicle.VehicleSpec;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;

import java.util.Map;

/**
 * Created by Becci on 24-Apr-17.
 */
public class SingleWidthStatusMonitor extends BasicStatusMonitor {



    /**
     * Create a SingleWidthStatusMonitor to record the status of the car park.
     *
     * @param parkingArea The parking area to record the status of.
     */
    public SingleWidthStatusMonitor(SingleLaneWidthParkingArea parkingArea) {
        super();
        this.parkingArea = parkingArea;
        initialiseParkingLanesSpace(parkingArea);
    }

    @Override
    public void vehicleOnEntry(CPMBasicAutoVehicle vehicle) {
        // TODO CPM Think about what to do if the vehicle has a targetParkingLane already
        /** ^ This might happen if say seem to be on the sensored line for a while
         // Like letting another car go through the intersection first.*/

        // check that the vehicle has not already entered the car park
        if (vehicle.hasEnteredCarPark()) {
            throw new RuntimeException("The vehicle has already entered, should not be entering again.");
        }

        // Find the lane with the most room available
        Map.Entry<ParkingLane, Double> parkingLaneEntry = findLeastFullParkingLane();

        // Update the space available on that lane
        decreaseCapacity(vehicle, parkingLaneEntry);

        // Allocate this parking lane to the vehicle by sending message
        System.out.println("Status monitor sending parking lane to vehicle.");
        sendParkingLaneMessage(vehicle, parkingLaneEntry.getKey());

        // Register the vehicle with the StatusMonitor, along with the
        // parking lane it has been allocated
        vehicles.put(vehicle, parkingLaneEntry.getKey());
    }

    @Override
    public void vehicleOnReEntry(CPMBasicAutoVehicle vehicle) {
        // first update the capacity - the vehicle has just left a parking lane
        increaseCapacity(vehicle);

        // Find the lane with the most room available
        Map.Entry<ParkingLane, Double> parkingLaneEntry = findLeastFullParkingLane();

        // Update the space available on that lane
        decreaseCapacity(vehicle, parkingLaneEntry);

        // Allocate this parking lane to the vehicle by sending message
        System.out.println("Status monitor sending parking lane to vehicle.");
        sendParkingLaneMessage(vehicle, parkingLaneEntry.getKey());

        // Update the vehicles parking lane in BasicStatusMonitor records, along with the
        // parking lane it has been allocated
        // TODO CPM rename vehicles to vehiclesToLane
        vehicles.put(vehicle, parkingLaneEntry.getKey());
    }

    @Override
    public void vehicleOnExit(CPMBasicAutoVehicle vehicle) {
        // Update capacity
        increaseCapacity(vehicle);

        // Remove the vehicle from the status monitor's records
        vehicles.remove(vehicle);
    }

    @Override
    public boolean roomForVehicle(VehicleSpec vehicleSpec) {
        double vehicleLength = vehicleSpec.getLength();

        // Find the lane with the most room available
        Map.Entry<ParkingLane, Double> parkingLaneEntry = findLeastFullParkingLane();

        // Check there is room for this vehicle
        double distanceBetweenVehicles = CPMAutoDriverSimulator.MIN_DISTANCE_BETWEEN_PARKED_VEHICLES;
        double spaceNeeded = vehicleLength + distanceBetweenVehicles;

        if (willVehicleLengthFit(parkingLaneEntry, spaceNeeded)) {
            numberOfAllowedEntries++;
            return true;
        }
        numberOfDeniedEntries++;
        return false;
    }

    private Map.Entry<ParkingLane, Double> findLeastFullParkingLane() {
        Map.Entry<ParkingLane, Double> maxEntry = null;
        String name = null;

        for (Map.Entry<ParkingLane, Double> entry : parkingLanesSpace.entrySet()) {
            boolean hasLowerId = false;
            if (maxEntry != null
                    && entry.getKey().getId() < maxEntry.getKey().getId()) {
                hasLowerId = true;
            }
            if (maxEntry == null
                    || entry.getValue().compareTo(maxEntry.getValue()) > 0
                    || (entry.getValue().equals(maxEntry.getValue()) && hasLowerId)) {
                maxEntry = entry;
                name = entry.getKey().getRoadName();
            }
        }
        System.out.println("Lane with most room is " + name);
        return maxEntry;
    }
}
