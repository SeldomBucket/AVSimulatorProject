package aim4.map.cpm.parking;

import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;

import java.util.*;

/**
 * An object which holds an updated status of the car park,
 * including the space left in each parking lane and the
 * remaining capacity of the car park.
 */
public class StatusMonitor {

    /** The parking area that we are recording the status of. */
    private ParkingArea parkingArea;
    /** A mapping from parking lanes to the amount of
     * space left for parking on that lane. */
    private Map<ParkingLane, Double> parkingLanesSpace = new HashMap<ParkingLane, Double>();
    /** A list of vehicles which are currently in the car park,
     * and the lane they are parked in. */
    private Map<CPMBasicAutoVehicle, ParkingLane> vehicles = new HashMap<CPMBasicAutoVehicle, ParkingLane>();
    /** The number of vehicles denied entry due to not enough room.*/
    private int numberOfDeniedEntries;
    /** The number of vehicles allowed entry as there is enough room.*/
    private int numberOfAllowedEntries;
    /** The most number of vehicles that have been in the car park at any one time during simulation.*/
    private int mostNumberOfVehicles;

    /**
     * Create a StatusMonitor to record the status of the car park.
     * @param parkingArea The parking area to record the status of.
     */
    public StatusMonitor(ParkingArea parkingArea) {
        this.parkingArea = parkingArea;
        numberOfDeniedEntries = 0;
        numberOfAllowedEntries = 0;
        mostNumberOfVehicles = 0;
        initialiseParkingLanesSpace(parkingArea);
    }

    /**
     * Create a mapping from each parking lane to the length of
     * the parking space available in that lane.
     * @param parkingArea The parking area to extract the parking
     *                    lanes from.
     */
    private void initialiseParkingLanesSpace(ParkingArea parkingArea){
        for (ParkingLane lane : parkingArea.getParkingLanes()) {
            parkingLanesSpace.put(lane, lane.getTotalParkingLength());
        }
    }

    /**
     * Calculate if there is enough room for the vehicle in the car park.
     * @param vehicleLength The length of the vehicle that wishes to enter the car park
     * @return true if there is space for the vehicle to park.
     */
    public boolean roomForVehicle(double vehicleLength) {
        // Find the lane with the most room available
        Map.Entry<ParkingLane, Double>  parkingLaneEntry = findLeastFullParkingLane();


        // Check there is room for this vehicle
        double distanceBetweenVehicles = CPMAutoDriverSimulator.MIN_DISTANCE_BETWEEN_PARKED_VEHICLES;;
        double spaceNeeded = vehicleLength + distanceBetweenVehicles;

        if (willVehicleFit(parkingLaneEntry, spaceNeeded)) {
            numberOfAllowedEntries++;
            return true;
        }
        numberOfDeniedEntries++;
        return false;
    }

    /**
     * Update capacity and allocate a parking lane to a vehicle on entry to the car park.
     * @param vehicle The vehicle entering the car park.
     */
    public void vehicleOnEntry(CPMBasicAutoVehicle vehicle) {
        // TODO CPM Think about what to do if the vehicle has a targetParkingLane already
        /** ^ This might happen if say seem to be on the sensored line for a while
        // Like letting another car go through the intersection first.*/

        // Find the lane with the most room available
        Map.Entry<ParkingLane, Double>  parkingLaneEntry = findLeastFullParkingLane();

        // Update the space available on that lane
        decreaseCapacity(vehicle, parkingLaneEntry);

        // Allocate this parking lane to the vehicle by sending message
        System.out.println("Status monitor sending parking lane to vehicle.");
        sendParkingLaneMessage(vehicle, parkingLaneEntry.getKey());

        // Register the vehicle with the StatusMonitor, along with the
        // parking lane it has been allocated
        vehicles.put(vehicle, parkingLaneEntry.getKey());
    }

    /**
     * Update capacity and allocate a parking lane to a vehicle when
     * re-entering the car park.
     * @param vehicle The vehicle re-entering the car park.
     */
    public void vehicleOnReEntry(CPMBasicAutoVehicle vehicle) {
        // first update the capacity - the vehicle has just left a parking lane
        increaseCapacity(vehicle);

        // Find the lane with the most room available
        Map.Entry<ParkingLane, Double>  parkingLaneEntry = findLeastFullParkingLane();

        // Update the space available on that lane
        decreaseCapacity(vehicle, parkingLaneEntry);

        // Allocate this parking lane to the vehicle by sending message
        System.out.println("Status monitor sending parking lane to vehicle.");
        sendParkingLaneMessage(vehicle, parkingLaneEntry.getKey());

        // Update the vehicles parking lane in StatusMonitor records, along with the
        // parking lane it has been allocated
        // TODO CPM rename vehicles to vehiclesToLane
        vehicles.put(vehicle, parkingLaneEntry.getKey());
    }

    /**
     * Update capacity when a vehicle exits the car park.
     * @param vehicle The vehicle exiting the car park.
     */
    public void vehicleOnExit(CPMBasicAutoVehicle vehicle) {
        // Update capacity
        increaseCapacity(vehicle);

        // Remove the vehicle from the status monitor's records
        vehicles.remove(vehicle);
    }

    /**
     * Increase the capacity when a vehicle has left the parking area.
     * This is on EXIT and RELOCATING.
     * @param vehicle the vehicle that has left the parking area
     */
    private void increaseCapacity(CPMBasicAutoVehicle vehicle){
        ParkingLane laneToUpdate = vehicles.get(vehicle);
        Map.Entry<ParkingLane, Double>  entryToUpdate = findParkingLaneSpace(laneToUpdate);
        double spaceFreed = calculateTotalVehicleSpace(vehicle);
        parkingLanesSpace.put(entryToUpdate.getKey(), entryToUpdate.getValue() + spaceFreed);
    }

    /**
     * Decrease the capacity when a vehicle is entering the parking area.
     * This is on ENTRERING and RELOCATING
     * @param vehicle The vehicle entering the parking area.
     */
    private void decreaseCapacity(CPMBasicAutoVehicle vehicle, Map.Entry<ParkingLane, Double>  parkingLaneEntry){
        double spaceTaken = calculateTotalVehicleSpace(vehicle);
        if (!willVehicleFit(parkingLaneEntry, spaceTaken)){
            throw new RuntimeException("There's not enough room in the car " +
                    "park for this vehicle to park!");
        }
        parkingLanesSpace.put(parkingLaneEntry.getKey(), parkingLaneEntry.getValue() - spaceTaken);
    }

    private double calculateTotalVehicleSpace(CPMBasicAutoVehicle vehicle) {
        double vehicleLength = vehicle.getSpec().getLength();
        double distanceBetweenVehicles = CPMAutoDriverSimulator.MIN_DISTANCE_BETWEEN_PARKED_VEHICLES; // TODO CPM find this value from AIM
        return vehicleLength + distanceBetweenVehicles;
    }

    private Map.Entry<ParkingLane, Double> findParkingLaneSpace(ParkingLane parkingLane) {
        for (Map.Entry<ParkingLane, Double> entry : parkingLanesSpace.entrySet())
        {
            if (entry.getKey() == parkingLane) {
                return entry;
            }
        }
        throw new RuntimeException("Parking lane could not be found.");
    }

    private Map.Entry<ParkingLane, Double> findLeastFullParkingLane() {
        Map.Entry<ParkingLane, Double> maxEntry = null;
        String name = null;

        for (Map.Entry<ParkingLane, Double> entry : parkingLanesSpace.entrySet())
        {
            boolean hasLowerId = false;
            if (maxEntry != null && entry.getKey().getId() < maxEntry.getKey().getId()){ hasLowerId = true; }
            if (maxEntry == null ||
                    entry.getValue().compareTo(maxEntry.getValue()) > 0 ||
                    (entry.getValue().equals(maxEntry.getValue()) && hasLowerId))
            {
                maxEntry = entry;
                name = entry.getKey().getRoadName();
            }
        }
        System.out.println("Lane with most room is " + name);
        return maxEntry;
    }

    private boolean willVehicleFit(Map.Entry<ParkingLane, Double> parkingLaneEntry,
                                   Double spaceNeeded) {

        double spaceOnParkingLane = parkingLaneEntry.getValue();
        if (spaceOnParkingLane > (spaceNeeded)) {
            return true;
        }
        return false;
    }

    private void sendParkingLaneMessage(CPMBasicAutoVehicle vehicle, ParkingLane parkingLane) {
        vehicle.sendMessageToI2VInbox(parkingLane);
    }

    public void updateMostNumberOfVehicles(){
        int currentNumberOfVehicles = vehicles.size();
        if (currentNumberOfVehicles > mostNumberOfVehicles) {
            mostNumberOfVehicles = currentNumberOfVehicles;
        }
    }

    public Map<CPMBasicAutoVehicle, ParkingLane> getVehicles() {
        return vehicles;
    }

    public int getNumberOfDeniedEntries() { return numberOfDeniedEntries; }

    public int getNumberOfAllowedEntries() { return numberOfAllowedEntries; }

    public int getMostNumberOfVehicles() { return mostNumberOfVehicles; }
}
