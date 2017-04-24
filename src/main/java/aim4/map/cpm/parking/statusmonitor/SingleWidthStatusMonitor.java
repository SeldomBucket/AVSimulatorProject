package aim4.map.cpm.parking.statusmonitor;

import aim4.driver.cpm.CPMV2VDriver;
import aim4.map.cpm.parking.ParkingLane;
import aim4.map.cpm.parking.parkingarea.SingleLaneWidthParkingArea;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;

import java.util.Map;

/**
 * Created by Becci on 24-Apr-17.
 */
public class SingleWidthStatusMonitor extends BasicStatusMonitor {

    /**
     * The parking area that we are recording the status of.
     */
    private SingleLaneWidthParkingArea parkingArea;

    /**
     * Create a BasicStatusMonitor to record the status of the car park.
     *
     * @param parkingArea The parking area to record the status of.
     */
    public SingleWidthStatusMonitor(SingleLaneWidthParkingArea parkingArea) {
        super();
        this.parkingArea = parkingArea;
        initialiseParkingLanesSpace(parkingArea);
    }

    /**
     * Create a mapping from each parking lane to the length of
     * the parking space available in that lane.
     *
     * @param parkingArea The parking area to extract the parking
     *                    lanes from.
     */
    private void initialiseParkingLanesSpace(SingleLaneWidthParkingArea parkingArea) {
        for (ParkingLane lane : parkingArea.getParkingLanes()) {
            parkingLanesSpace.put(lane, lane.getTotalParkingLength());
        }
    }

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

        // Register the vehicle with the BasicStatusMonitor, along with the
        // parking lane it has been allocated
        vehicles.put(vehicle, parkingLaneEntry.getKey());
    }

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

    public void vehicleOnExit(CPMBasicAutoVehicle vehicle) {
        // Update capacity
        increaseCapacity(vehicle);

        // Remove the vehicle from the status monitor's records
        vehicles.remove(vehicle);
    }

    public boolean roomForVehicle(double vehicleLength) {
        // Find the lane with the most room available
        Map.Entry<ParkingLane, Double> parkingLaneEntry = findLeastFullParkingLane();


        // Check there is room for this vehicle
        double distanceBetweenVehicles = CPMAutoDriverSimulator.MIN_DISTANCE_BETWEEN_PARKED_VEHICLES;
        ;
        double spaceNeeded = vehicleLength + distanceBetweenVehicles;

        if (willVehicleFit(parkingLaneEntry, spaceNeeded)) {
            numberOfAllowedEntries++;
            return true;
        }
        numberOfDeniedEntries++;
        return false;
    }

    private boolean willVehicleFit(Map.Entry<ParkingLane, Double> parkingLaneEntry,
                                   Double spaceNeeded) {

        double spaceOnParkingLane = parkingLaneEntry.getValue();
        if (spaceOnParkingLane > (spaceNeeded)) {
            return true;
        }
        return false;
    }

    /**
     * Increase the capacity when a vehicle has left the parking area.
     * This is on EXIT and RELOCATING.
     *
     * @param vehicle the vehicle that has left the parking area
     */
    private void increaseCapacity(CPMBasicAutoVehicle vehicle) {
        ParkingLane laneToUpdate = vehicles.get(vehicle);
        Map.Entry<ParkingLane, Double> entryToUpdate = findParkingLaneSpace(laneToUpdate);
        double spaceFreed = calculateTotalVehicleSpace(vehicle);
        parkingLanesSpace.put(entryToUpdate.getKey(), entryToUpdate.getValue() + spaceFreed);
    }

    /**
     * Decrease the capacity when a vehicle is entering the parking area.
     * This is on ENTRERING and RELOCATING
     *
     * @param vehicle The vehicle entering the parking area.
     */
    private void decreaseCapacity(CPMBasicAutoVehicle vehicle, Map.Entry<ParkingLane, Double> parkingLaneEntry) {
        double spaceTaken = calculateTotalVehicleSpace(vehicle);
        if (!willVehicleFit(parkingLaneEntry, spaceTaken)) {
            assert vehicle.getDriver() instanceof CPMV2VDriver;
            throw new RuntimeException("There's not enough room in the car " +
                    "park for this vehicle to park! Vehicle is " +
                    ((CPMV2VDriver) vehicle.getDriver()).getParkingStatus());
        }
        parkingLanesSpace.put(parkingLaneEntry.getKey(), parkingLaneEntry.getValue() - spaceTaken);
    }

    private Map.Entry<ParkingLane, Double> findParkingLaneSpace(ParkingLane parkingLane) {
        for (Map.Entry<ParkingLane, Double> entry : parkingLanesSpace.entrySet()) {
            if (entry.getKey() == parkingLane) {
                return entry;
            }
        }
        throw new RuntimeException("Parking lane could not be found.");
    }

    private Map.Entry<ParkingLane, Double> findLeastFullParkingLane() {
        Map.Entry<ParkingLane, Double> maxEntry = null;
        String name = null;

        for (Map.Entry<ParkingLane, Double> entry : parkingLanesSpace.entrySet()) {
            boolean hasLowerId = false;
            if (maxEntry != null && entry.getKey().getId() < maxEntry.getKey().getId()) {
                hasLowerId = true;
            }
            if (maxEntry == null ||
                    entry.getValue().compareTo(maxEntry.getValue()) > 0 ||
                    (entry.getValue().equals(maxEntry.getValue()) && hasLowerId)) {
                maxEntry = entry;
                name = entry.getKey().getRoadName();
            }
        }
        System.out.println("Lane with most room is " + name);
        return maxEntry;
    }

    private void sendParkingLaneMessage(CPMBasicAutoVehicle vehicle, ParkingLane parkingLane) {
        vehicle.sendMessageToI2VInbox(parkingLane);
    }

}
