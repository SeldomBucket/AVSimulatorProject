package aim4.map.cpm.parking.statusmonitor;

import aim4.driver.cpm.CPMV2VDriver;
import aim4.map.cpm.parking.ParkingLane;
import aim4.map.cpm.parking.parkingarea.BasicParkingArea;
import aim4.map.cpm.parking.parkingarea.ParkingArea;
import aim4.map.cpm.parking.parkingarea.SingleLaneWidthParkingArea;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * An object which holds an updated status of the car park,
 * including the space left in each parking lane and the
 * remaining capacity of the car park.
 */
public abstract class BasicStatusMonitor implements StatusMonitor {

    /**
     * The parking area that we are recording the status of.
     */
    protected ParkingArea parkingArea;

    /**
     * A mapping from parking lanes to the amount of
     * space (length) left for parking on that lane.
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

    /**
     * Create a mapping from each parking lane to the length of
     * the parking space available in that lane.
     *
     * @param parkingArea The parking area to extract the parking
     *                    lanes from.
     */
    protected void initialiseParkingLanesSpace(BasicParkingArea parkingArea) {
        for (ParkingLane lane : parkingArea.getParkingLanes()) {
            parkingLanesSpace.put(lane, lane.getTotalParkingLength());
        }
    }

    protected boolean willVehicleLengthFit(Map.Entry<ParkingLane, Double> parkingLaneEntry,
                                         Double spaceNeeded) {

        double spaceOnParkingLane = parkingLaneEntry.getValue();
        if (spaceOnParkingLane > (spaceNeeded)) {
            return true;
        }
        return false;
    }

    /**
     * Decrease the capacity when a vehicle is entering the parking area.
     * This is on ENTERING and RELOCATING
     *
     * @param vehicle The vehicle entering the parking area.
     */
    protected void decreaseCapacity(CPMBasicAutoVehicle vehicle, Map.Entry<ParkingLane, Double> parkingLaneEntry) {
        double spaceTaken = calculateTotalVehicleSpace(vehicle);
        if (!willVehicleLengthFit(parkingLaneEntry, spaceTaken)) {
            assert vehicle.getDriver() instanceof CPMV2VDriver;
            throw new RuntimeException("There's not enough room in the car " +
                    "park for this vehicle to park! Vehicle is " +
                    ((CPMV2VDriver) vehicle.getDriver()).getParkingStatus());
        }
        parkingLanesSpace.put(parkingLaneEntry.getKey(), parkingLaneEntry.getValue() - spaceTaken);
    }

    /**
     * Increase the capacity when a vehicle has left the parking area.
     * This is on EXIT and RELOCATING.
     *
     * @param vehicle the vehicle that has left the parking area
     */
    protected void increaseCapacity(CPMBasicAutoVehicle vehicle) {
        ParkingLane laneToUpdate = vehicles.get(vehicle);
        Map.Entry<ParkingLane, Double> entryToUpdate = findParkingLaneSpace(laneToUpdate);
        double spaceFreed = calculateTotalVehicleSpace(vehicle);
        parkingLanesSpace.put(entryToUpdate.getKey(), entryToUpdate.getValue() + spaceFreed);
    }

    private Map.Entry<ParkingLane, Double> findParkingLaneSpace(ParkingLane parkingLane) {
        for (Map.Entry<ParkingLane, Double> entry : parkingLanesSpace.entrySet()) {
            if (entry.getKey() == parkingLane) {
                return entry;
            }
        }
        throw new RuntimeException("Parking lane could not be found.");
    }

    protected void sendParkingLaneMessage(CPMBasicAutoVehicle vehicle, ParkingLane parkingLane) {
        vehicle.sendMessageToI2VInbox(parkingLane);
    }

    protected double calculateTotalVehicleSpace(CPMBasicAutoVehicle vehicle) {
        double vehicleLength = vehicle.getSpec().getLength();
        double distanceBetweenVehicles = CPMAutoDriverSimulator.MIN_DISTANCE_BETWEEN_PARKED_VEHICLES;
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

    /**
     * Get the area of the car park that is available for vehicles to park in.
     * @return the available parking area.
     */
    public double getAvailableParkingArea(){
        double freeSpace = 0.0;
        List<ParkingLane> parkingLanes = parkingArea.getParkingLanes();

        for (ParkingLane lane : parkingLanes) {
            freeSpace += parkingLanesSpace.get(lane) * lane.getWidth();
        }

        return freeSpace;
    }
}
