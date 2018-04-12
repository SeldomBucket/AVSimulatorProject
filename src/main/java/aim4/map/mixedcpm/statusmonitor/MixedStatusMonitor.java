package aim4.map.mixedcpm.statusmonitor;

import aim4.driver.mixedcpm.MixedCPMManualDriver;
import aim4.map.Road;
import aim4.map.lane.Lane;
import aim4.map.mixedcpm.parking.ManualParkingArea;
import aim4.map.mixedcpm.parking.ManualStall;
import aim4.vehicle.mixedcpm.MixedCPMBasicManualVehicle;
import javafx.util.Pair;

import java.util.*;

/**
 * An object which holds an updated status of the car park,
 * including the space left in each parking lane and the
 * remaining capacity of the car park.
 */
public class MixedStatusMonitor implements IStatusMonitor {

    /** The parking area that we are recording the status of. */
    private ManualParkingArea parkingArea;
    /** A list of vehicles which are currently in the car park,
     * and the lane they are parked in. */
    private Map<MixedCPMBasicManualVehicle, ManualStall> vehicles = new HashMap<>();
    /** The number of vehicles denied entry due to not enough room.*/
    private int numberOfDeniedEntries;
    /** The number of vehicles allowed entry as there is enough room.*/
    private int numberOfAllowedEntries;
    /** The number of vehicles exited the car park*/
    private int numberOfCompletedVehicles;
    /** The most number of vehicles that have been in the car park at any one time during simulation.*/
    private int mostNumberOfVehicles;
    /** The most number of vehicles that have been parked in the car park at any one time during simulation.*/
    private int mostNumberOfParkedVehicles;
    /** The max space efficiency of the parking area */
    private double maxEfficiency;
    /** The current space efficiency of the parking area */
    private double currentEfficiency;
    /** The current area per vehicle the parking area */
    private double areaPerVehicle;
    /** The minimum area per vehicle the parking area */
    private double minAreaPerVehicle;

    /**
     * Create a AdjustableManualStatusMonitor to record the status of the parking area.
     * @param parkingArea The parking area to record the status of.
     */
    public MixedStatusMonitor(ManualParkingArea parkingArea) {
        this.parkingArea = parkingArea;
        numberOfDeniedEntries = 0;
        numberOfAllowedEntries = 0;
        mostNumberOfVehicles = 0;
        numberOfCompletedVehicles = 0;
        maxEfficiency = 0;
        currentEfficiency = 0;
        areaPerVehicle = Double.MAX_VALUE;
        minAreaPerVehicle = Double.MAX_VALUE;
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

        // Find a stall for the vehicle
        ManualStall allocatedStall = parkingArea.findSpace(vehicle.getStallSpec());

        if (allocatedStall == null){
            numberOfDeniedEntries++;
            return false;
        }


        ArrayList<Lane> pathToTargetStall = new ArrayList<>();
        for (Road road : allocatedStall.getJunction().getRoads()){
            if (road.getOnlyLane() != allocatedStall.getLane()){
                pathToTargetStall.add(0, parkingArea.getRoadByName("topRoad").getOnlyLane()); // Top Road
                pathToTargetStall.add(1, road.getOnlyLane());                                           // Parking Road Lane
                pathToTargetStall.add(2, allocatedStall.getLane());                                     // Manual Stall Lane
                break;
            }
        }

        // Allocate this parking lane to the vehicle by sending message
        sendParkingLaneMessage(vehicle, allocatedStall, pathToTargetStall);

        // Register the vehicle with the AdjustableManualStatusMonitor, along with the
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
        vehicle.getTargetStall().delete();
        vehicles.remove(vehicle);
        numberOfCompletedVehicles++;
    }


    private void sendParkingLaneMessage(MixedCPMBasicManualVehicle vehicle, ManualStall manualStall, List<Lane> path) {
        vehicle.sendMessageToI2VInbox(new Pair<>(manualStall, path));
    }


    public Map<MixedCPMBasicManualVehicle, ManualStall> getVehicles() {
        return vehicles;
    }

    public double getTotalAreaOfParkedVehicles(){
        double totalVehicleArea = 0;
        for (MixedCPMBasicManualVehicle vehicle : vehicles.keySet()){
            if(((MixedCPMManualDriver)vehicle.getDriver()).isParked()){
                totalVehicleArea = totalVehicleArea +
                        (vehicle.getSpec().getWidth() *
                                vehicle.getSpec().getWidth());
            }
        }
        return totalVehicleArea;
    }

    public int getNoOfParkedVehicles(){
        int noOfParkedVehicles = 0;
        int noOfVansNotParked = 0;
        int noOfNonVansNotParked = 0;
        int noOfVansParked = 0;
        int noOfNonVansParked = 0;
        for (MixedCPMBasicManualVehicle vehicle : vehicles.keySet()){
            if(((MixedCPMManualDriver)vehicle.getDriver()).isParked()){
                noOfParkedVehicles++;
                if (vehicle.getSpec().getName() == "VAN"){
                    noOfVansParked++;
                }else{
                    noOfNonVansParked++;
                }
            }else{
                //here
                if (vehicle.getSpec().getName() == "VAN"){
                    noOfVansNotParked++;
                }else{
                    noOfNonVansNotParked++;
                }
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

    public void updateEfficiencyMeasurements(){
        double areaOfCarPark = (parkingArea.getDimensions().getWidth() *
                parkingArea.getDimensions().getHeight());
        double areaOfVehicles = getTotalAreaOfParkedVehicles();

        areaPerVehicle = areaOfCarPark/getNoOfParkedVehicles();
        currentEfficiency =  areaOfVehicles / areaOfCarPark;

        if (currentEfficiency > maxEfficiency){
            maxEfficiency = currentEfficiency;
        }

        if (areaPerVehicle < minAreaPerVehicle){
            minAreaPerVehicle = areaPerVehicle;
        }
    }

    public double getCurrentEfficiency(){ return currentEfficiency; }

    public double getMaxEfficiency(){ return maxEfficiency; }

    public double getAreaPerVehicle() { return areaPerVehicle; }

    public double getMinAreaPerVehicle() { return minAreaPerVehicle; }

    public int getNumberOfCompletedVehicles() { return numberOfCompletedVehicles; }

    public int getMostNumberOfParkedVehicles() { return mostNumberOfParkedVehicles; }

    public int getNumberOfDeniedEntries() { return numberOfDeniedEntries; }

    public int getNumberOfAllowedEntries() { return numberOfAllowedEntries; }

    public int getMostNumberOfVehicles() { return mostNumberOfVehicles; }
}
