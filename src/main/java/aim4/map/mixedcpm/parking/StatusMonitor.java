package aim4.map.mixedcpm.parking;

import aim4.driver.cpm.CPMV2VDriver;
import aim4.map.lane.Lane;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;
import aim4.vehicle.mixedcpm.MixedCPMBasicManualVehicle;

import java.util.*;

/**
 * An object which holds an updated status of the car park,
 * including the space left in each parking lane and the
 * remaining capacity of the car park.
 */
public class StatusMonitor {

    /** The parking area that we are recording the status of. */
    private ManualParkingArea parkingArea;
    /** A list of vehicles which are currently in the car park,
     * and the lane they are parked in. */
    private Map<MixedCPMBasicManualVehicle, ManualStall> vehicles = new HashMap<>();
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
    public StatusMonitor(ManualParkingArea parkingArea) {
        this.parkingArea = parkingArea;
        numberOfDeniedEntries = 0;
        numberOfAllowedEntries = 0;
        mostNumberOfVehicles = 0;
    }

    /**
     * Update capacity and allocate a parking lane to a vehicle on entry to the car park.
     * @param vehicle The vehicle entering the car park.
     */
    public boolean addNewVehicle(MixedCPMBasicManualVehicle vehicle) {

        // check that the vehicle has not already entered the car park
        if (vehicle.hasEnteredCarPark()) {
            throw new RuntimeException("The vehicle has already entered, should not be entering again.");
        }

        // Find the lane with the most room available
        ManualStall allocatedStall = parkingArea.findSpace(vehicle.getStallInfo());

        if (allocatedStall == null){
            numberOfDeniedEntries++;
            return false;
        }

        // Allocate this parking lane to the vehicle by sending message
        System.out.println("Status monitor sending parking lane to vehicle.");
        sendParkingLaneMessage(vehicle, allocatedStall);

        // Register the vehicle with the StatusMonitor, along with the
        // parking lane it has been allocated
        vehicles.put(vehicle, allocatedStall);
        numberOfAllowedEntries++;
        return true;
    }


    private void sendParkingLaneMessage(MixedCPMBasicManualVehicle vehicle, ManualStall manualStall) {
        vehicle.sendMessageToI2VInbox(manualStall);
    }


    public Map<MixedCPMBasicManualVehicle, ManualStall> getVehicles() {
        return vehicles;
    }

    public void updateMostNumberOfVehicles(){
        int currentNumberOfVehicles = vehicles.size();
        if (currentNumberOfVehicles > mostNumberOfVehicles) {
            mostNumberOfVehicles = currentNumberOfVehicles;
        }
    }

    public int getNumberOfDeniedEntries() { return numberOfDeniedEntries; }

    public int getNumberOfAllowedEntries() { return numberOfAllowedEntries; }

    public int getMostNumberOfVehicles() { return mostNumberOfVehicles; }
}
