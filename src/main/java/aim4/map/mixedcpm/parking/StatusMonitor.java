package aim4.map.mixedcpm.parking;

import aim4.driver.cpm.CPMV2VDriver;
import aim4.driver.mixedcpm.MixedCPMManualDriver;
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
    /** The most number of vehicles that have been parked in the car park at any one time during simulation.*/
    private int mostNumberOfParkedVehicles;

    /**
     * Create a StatusMonitor to record the status of the parking area.
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
        //System.out.println("Status monitor sending parking lane to vehicle.");
//        ManualStall allocatedStall2 = parkingArea.findSpace(vehicle.getStallInfo());
//        allocatedStall2.delete();
        sendParkingLaneMessage(vehicle, allocatedStall);

        // Register the vehicle with the StatusMonitor, along with the
        // parking lane it has been allocated
        vehicles.put(vehicle, allocatedStall);
        numberOfAllowedEntries++;
        return true;
    }


    /**
     * Update capacity when a vehicle exits the car park.
     * @param vehicle The vehicle exiting the car park.
     */
    public void vehicleOnExit(MixedCPMBasicManualVehicle vehicle) {
        // Remove the vehicle from the status monitor's records
        vehicles.remove(vehicle);
    }


    private void sendParkingLaneMessage(MixedCPMBasicManualVehicle vehicle, ManualStall manualStall) {
        vehicle.sendMessageToI2VInbox(manualStall);
    }


    public Map<MixedCPMBasicManualVehicle, ManualStall> getVehicles() {
        return vehicles;
    }

    public int getNoOfParkedVehicles(){
        int noOfParkedVehicles = 0;
        for (MixedCPMBasicManualVehicle vehicle : vehicles.keySet()){
            if(((MixedCPMManualDriver)vehicle.getDriver()).isParked()){
                noOfParkedVehicles++;
            }
        }
        return noOfParkedVehicles;
    }

    public void updateMostNumberOfVehicles(){
        int currentNumberOfVehicles = vehicles.size();
        if (currentNumberOfVehicles > mostNumberOfVehicles) {
            mostNumberOfVehicles = currentNumberOfVehicles;
        }

        int currentNumberOfParkedVehicles = getNoOfParkedVehicles();
        if (currentNumberOfParkedVehicles > mostNumberOfParkedVehicles) {
            mostNumberOfParkedVehicles = currentNumberOfParkedVehicles;
        }
    }

    public int getMostNumberOfParkedVehicles() { return mostNumberOfParkedVehicles; }

    public int getNumberOfDeniedEntries() { return numberOfDeniedEntries; }

    public int getNumberOfAllowedEntries() { return numberOfAllowedEntries; }

    public int getMostNumberOfVehicles() { return mostNumberOfVehicles; }
}
