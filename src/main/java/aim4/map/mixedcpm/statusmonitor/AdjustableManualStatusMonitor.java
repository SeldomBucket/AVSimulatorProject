package aim4.map.mixedcpm.statusmonitor;

import aim4.driver.mixedcpm.MixedCPMManualDriver;
import aim4.map.Road;
import aim4.map.lane.Lane;
import aim4.map.mixedcpm.MixedCPMBasicMap;
import aim4.map.mixedcpm.parking.IManualParkingArea;
import aim4.map.mixedcpm.parking.ManualStall;
import aim4.vehicle.mixedcpm.MixedCPMBasicAutoVehicle;
import aim4.vehicle.mixedcpm.MixedCPMBasicManualVehicle;
import aim4.vehicle.mixedcpm.MixedCPMBasicVehicle;
import javafx.util.Pair;

import java.util.*;

/**
 * An object which holds an updated status of the car park,
 * including the space left in each parking lane and the
 * remaining capacity of the car park.
 */
public class AdjustableManualStatusMonitor implements IStatusMonitor {

    private MixedCPMBasicMap map;
    /** The parking area that we are recording the status of. */
    private IManualParkingArea parkingArea;
    /** A list of manualVehicles which are currently in the car park,
     * and the lane they are parked in. */
    private Map<MixedCPMBasicManualVehicle, ManualStall> manualVehicles = new HashMap<>();
    /** The number of manualVehicles denied entry due to not enough room.*/
    private int numberOfDeniedEntries;
    /** The number of manualVehicles allowed entry as there is enough room.*/
    private int numberOfAllowedEntries;
    /** The number of manualVehicles exited the car park*/
    private int numberOfCompletedVehicles;
    /** The most number of manualVehicles that have been in the car park at any one time during simulation.*/
    private int mostNumberOfVehicles;
    /** The most number of manualVehicles that have been parked in the car park at any one time during simulation.*/
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
     * @param map The map to monitor.
     */
    public AdjustableManualStatusMonitor(MixedCPMBasicMap map) {
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

    /**
     * Update capacity and allocate a parking lane to a vehicle on entry to the car park.
     * @param vehicle The vehicle entering the car park.
     */
    private boolean addNewVehicle(MixedCPMBasicManualVehicle vehicle) {

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
        manualVehicles.put(vehicle, allocatedStall);
        numberOfAllowedEntries++;
        return true;
    }


    /**
     * Update capacity when a vehicle exits the car park.
     * @param vehicle The vehicle exiting the car park.
     */
    private void vehicleOnExit(MixedCPMBasicManualVehicle vehicle) {
        // Remove the vehicle from the status monitor's records
        vehicle.getTargetStall().delete();
        manualVehicles.remove(vehicle);
        numberOfCompletedVehicles++;
    }


    private void sendParkingLaneMessage(MixedCPMBasicManualVehicle vehicle, ManualStall manualStall, List<Lane> path) {
        vehicle.sendMessageToI2VInbox(new Pair<>(manualStall, path));
    }

    @Override
    public List<MixedCPMBasicVehicle> getVehicles() {
        return new ArrayList<>(manualVehicles.keySet());
    }

    public List<MixedCPMBasicManualVehicle> getManualVehicles() {
        return new ArrayList<>(manualVehicles.keySet());
    }

    public double getTotalAreaOfParkedVehicles(){
        double totalVehicleArea = 0;
        for (MixedCPMBasicManualVehicle vehicle : manualVehicles.keySet()){
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
        for (MixedCPMBasicManualVehicle vehicle : manualVehicles.keySet()){
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
        int currentNumberOfVehicles = manualVehicles.size();
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


    @Override
    public List<MixedCPMBasicAutoVehicle> getAutoVehicles() {
        return new ArrayList<>();
    }

    @Override
    public int getNoOfParkedManualVehicles() {
        return getNoOfParkedVehicles();
    }

    @Override
    public int getNumberOfCompletedManualVehicles() {
        return getNumberOfCompletedVehicles();
    }

    @Override
    public int getMostNumberOfParkedManualVehicles() {
        return getMostNumberOfParkedVehicles();
    }

    @Override
    public int getNumberOfDeniedManualEntries() {
        return getNumberOfDeniedEntries();
    }

    @Override
    public int getNumberOfAllowedManualEntries() {
        return getNumberOfAllowedEntries();
    }

    @Override
    public int getMostNumberOfManualVehicles() {
        return getMostNumberOfVehicles();
    }

    @Override
    public int getNoOfParkedAutoVehicles() {
        return 0;
    }

    @Override
    public int getNumberOfCompletedAutoVehicles() {
        return 0;
    }

    @Override
    public int getMostNumberOfParkedAutoVehicles() {
        return 0;
    }

    @Override
    public int getNumberOfDeniedAutoEntries() {
        return 0;
    }

    @Override
    public int getNumberOfAllowedAutoEntries() {
        return 0;
    }

    @Override
    public int getMostNumberOfAutoVehicles() {
        return 0;
    }

    @Override
    public double getCurrentManualEfficiency() {
        return getCurrentEfficiency();
    }

    @Override
    public double getMaxManualEfficiency() {
        return getMaxEfficiency();
    }

    @Override
    public double getAreaPerManualVehicle() {
        return getAreaPerVehicle();
    }

    @Override
    public double getMinAreaPerManualVehicle() {
        return getMinAreaPerVehicle();
    }

    @Override
    public double getCurrentAutoEfficiency() {
        return 0;
    }

    @Override
    public double getMaxAutoEfficiency() {
        return 0;
    }

    @Override
    public double getAreaPerAutoVehicle() {
        return 0;
    }

    @Override
    public double getMinAreaPerAutoVehicle() {
        return 0;
    }
}
