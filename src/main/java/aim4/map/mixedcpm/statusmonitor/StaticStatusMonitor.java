package aim4.map.mixedcpm.statusmonitor;

import aim4.driver.mixedcpm.MixedCPMManualDriver;
import aim4.map.Road;
import aim4.map.lane.Lane;
import aim4.map.mixedcpm.MixedCPMBasicMap;
import aim4.map.mixedcpm.parking.*;
import aim4.vehicle.mixedcpm.MixedCPMBasicManualVehicle;
import aim4.vehicle.mixedcpm.MixedCPMBasicVehicle;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticStatusMonitor implements IStatusMonitor {

    private MixedCPMBasicMap map;
    /** The parking area that we are recording the status of. */
    private IManualParkingArea parkingArea;
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

    private List<ManualStall> unoccupiedStalls = new ArrayList<>();
    private List<ManualStall> occupiedStalls = new ArrayList<>();

    private StallSpec standardStallSpec = new StallSpec(1.9, 4.5, StallType.Standard);
    private StallSpec disabledStallSpec = new StallSpec(1.9,4.5, StallType.Disabled);

    /**
     * Create a StaticStatusMonitor to record the status of the parking area.
     * @param map The map to record the status of.
     */
    public StaticStatusMonitor(MixedCPMBasicMap map) {
        this.map = map;
        this.parkingArea = map.getManualParkingArea();
        numberOfDeniedEntries = 0;
        numberOfAllowedEntries = 0;
        mostNumberOfVehicles = 0;
        numberOfCompletedVehicles = 0;
        maxEfficiency = 0;
        currentEfficiency = 0;
        areaPerVehicle = Double.MAX_VALUE;
        minAreaPerVehicle = Double.MAX_VALUE;

        for (ManualParkingRoad road : parkingArea.getParkingRoads()){
            unoccupiedStalls.addAll(road.getManualStalls());
        }
    }

    public boolean addNewVehicle(MixedCPMBasicVehicle vehicle) {
        if (vehicle instanceof MixedCPMBasicManualVehicle){
            return addNewVehicle((MixedCPMBasicManualVehicle)vehicle);
        }
        return false;
    }

    public void vehicleOnExit(MixedCPMBasicVehicle vehicle) {
        if (vehicle instanceof MixedCPMBasicManualVehicle){
            vehicleOnExit((MixedCPMBasicManualVehicle)vehicle);
        }
    }

    private boolean addNewVehicle(MixedCPMBasicManualVehicle vehicle) {
        // check that the vehicle has not already entered the car park
        if (vehicle.hasEnteredCarPark()) {
            throw new RuntimeException("The vehicle has already entered, should not be entering again.");
        }

        ManualStall allocatedStall = null;

        for (ManualStall stall : unoccupiedStalls){
            if (vehicle.isDisabledVehicle() && stall.getType() == StallType.Disabled){
                allocatedStall = stall;
                break;
            }else if (!vehicle.isDisabledVehicle() && stall.getType() == StallType.Standard){
                allocatedStall = stall;
                break;
            }
        }

        if (allocatedStall == null
                || allocatedStall.getWidth() < vehicle.getSpec().getWidth()
                || allocatedStall.getLength() < vehicle.getSpec().getLength()) {
            numberOfDeniedEntries++;
            return false;
        }

        unoccupiedStalls.remove(allocatedStall);
        occupiedStalls.add(allocatedStall);


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
        sendManualStallMessage(vehicle, allocatedStall, pathToTargetStall);

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
    private void vehicleOnExit(MixedCPMBasicManualVehicle vehicle) {
        // Remove the vehicle from the status monitor's records
        vehicles.remove(vehicle);
        numberOfCompletedVehicles++;
        occupiedStalls.remove(vehicle.getTargetStall());
        // Replace the space deleted by this vehicle when it left
        if (vehicle.isDisabledVehicle()){
            unoccupiedStalls.add(vehicle.getTargetStall());
        }else{
            unoccupiedStalls.add(vehicle.getTargetStall());
        }
    }


    private void sendManualStallMessage(MixedCPMBasicManualVehicle vehicle, ManualStall manualStall, List<Lane> path) {
        vehicle.sendMessageToI2VInbox(new Pair<>(manualStall, path));
    }

    public List<MixedCPMBasicVehicle> getVehicles() {
        return new ArrayList<>(vehicles.keySet());
    }

    public Map<MixedCPMBasicManualVehicle, ManualStall> getManualVehicles() {
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
        double areaOfCarPark = map.getAreaOfCarPark();
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
