package aim4.map.mixedcpm.statusmonitor;

import aim4.driver.mixedcpm.MixedCPMAutoDriver;
import aim4.driver.mixedcpm.MixedCPMManualDriver;
import aim4.map.Road;
import aim4.map.lane.Lane;
import aim4.map.mixedcpm.MixedCPMBasicMap;
import aim4.map.mixedcpm.parking.AutomatedParkingRoad;
import aim4.map.mixedcpm.parking.IAutomatedParkingArea;
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
public class MixedStatusMonitor implements IStatusMonitor {

    private MixedCPMBasicMap map;
    /** The automated parking area that we are recording the status of. */
    private IAutomatedParkingArea automatedParkingArea;
    /** The manual parking area that we are recording the status of. */
    private IManualParkingArea manualParkingArea;
    /** A list of manualVehicles which are currently in the car park,
     * and the lane they are parked in. */
    private Map<MixedCPMBasicManualVehicle, ManualStall> manualVehicles = new HashMap<>();
    /** A list of manualVehicles which are currently in the car park,
     * and the lane they are parked in. */
    private Map<MixedCPMBasicAutoVehicle, AutomatedParkingRoad> autoVehicles = new HashMap<>();
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

    /** The number of manualVehicles denied entry due to not enough room.*/
    private int numberOfDeniedManualEntries;
    /** The number of manualVehicles allowed entry as there is enough room.*/
    private int numberOfAllowedManualEntries;
    /** The number of manualVehicles exited the car park*/
    private int numberOfCompletedManualVehicles;
    /** The most number of manualVehicles that have been in the car park at any one time during simulation.*/
    private int mostNumberOfManualVehicles;
    /** The most number of manualVehicles that have been parked in the car park at any one time during simulation.*/
    private int mostNumberOfParkedManualVehicles;
    
    /** The number of manualVehicles denied entry due to not enough room.*/
    private int numberOfDeniedAutoEntries;
    /** The number of manualVehicles allowed entry as there is enough room.*/
    private int numberOfAllowedAutoEntries;
    /** The number of manualVehicles exited the car park*/
    private int numberOfCompletedAutoVehicles;
    /** The most number of manualVehicles that have been in the car park at any one time during simulation.*/
    private int mostNumberOfAutoVehicles;
    /** The most number of manualVehicles that have been parked in the car park at any one time during simulation.*/
    private int mostNumberOfParkedAutoVehicles;
    
    /** The max space efficiency of the parking area */
    private double maxEfficiency;
    /** The current space efficiency of the parking area */
    private double currentEfficiency;
    /** The current area per vehicle the parking area */
    private double areaPerVehicle;
    /** The minimum area per vehicle the parking area */
    private double minAreaPerVehicle;


    /** The max space efficiency of the parking area */
    private double maxManualEfficiency;
    /** The current space efficiency of the parking area */
    private double currentManualEfficiency;
    /** The current area per vehicle the parking area */
    private double areaPerManualVehicle;
    /** The minimum area per vehicle the parking area */
    private double minAreaPerManualVehicle;

    /** The max space efficiency of the parking area */
    private double maxAutoEfficiency;
    /** The current space efficiency of the parking area */
    private double currentAutoEfficiency;
    /** The current area per vehicle the parking area */
    private double areaPerAutoVehicle;
    /** The minimum area per vehicle the parking area */
    private double minAreaPerAutoVehicle;

    /**
     * Create a AdjustableManualStatusMonitor to record the status of the parking area.
     * @param map The map to record the status of.
     */
    public MixedStatusMonitor(MixedCPMBasicMap map) {
        this.map = map;
        this.manualParkingArea = map.getManualParkingArea();
        this.automatedParkingArea = map.getAutomatedParkingArea();
        numberOfDeniedEntries = 0;
        numberOfAllowedEntries = 0;
        mostNumberOfVehicles = 0;
        numberOfCompletedVehicles = 0;
        
        numberOfDeniedManualEntries = 0;
        numberOfAllowedManualEntries = 0;
        mostNumberOfManualVehicles = 0;
        numberOfCompletedManualVehicles = 0;

        maxEfficiency = 0;
        currentEfficiency = 0;
        areaPerVehicle = Double.MAX_VALUE;
        minAreaPerVehicle = Double.MAX_VALUE;

        maxManualEfficiency = 0;
        currentManualEfficiency = 0;
        areaPerManualVehicle = Double.MAX_VALUE;
        minAreaPerManualVehicle = Double.MAX_VALUE;

        maxAutoEfficiency = 0;
        currentAutoEfficiency = 0;
        areaPerAutoVehicle = Double.MAX_VALUE;
        minAreaPerAutoVehicle = Double.MAX_VALUE;
    }


    public boolean addNewVehicle(MixedCPMBasicVehicle vehicle) {
        if (vehicle instanceof MixedCPMBasicManualVehicle){
            return addNewVehicle((MixedCPMBasicManualVehicle)vehicle);
        }
        if (vehicle instanceof MixedCPMBasicAutoVehicle){
            return addNewVehicle((MixedCPMBasicAutoVehicle)vehicle);
        }
        return false;
    }

    public void vehicleOnExit(MixedCPMBasicVehicle vehicle) {
        if (vehicle instanceof MixedCPMBasicManualVehicle){
            vehicleOnExit((MixedCPMBasicManualVehicle)vehicle);
        }
        if (vehicle instanceof MixedCPMBasicAutoVehicle){
            vehicleOnExit((MixedCPMBasicAutoVehicle)vehicle);
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
        ManualStall allocatedStall = manualParkingArea.findSpace(vehicle.getStallSpec());

        if (allocatedStall == null){
            numberOfDeniedEntries++;
            numberOfDeniedManualEntries++;
            return false;
        }


        ArrayList<Lane> pathToTargetStall = new ArrayList<>();
        for (Road road : allocatedStall.getJunction().getRoads()){
            if (road.getOnlyLane() != allocatedStall.getLane()){
                pathToTargetStall.add(0, manualParkingArea.getRoadByName("topRoad").getOnlyLane()); // Top Road
                pathToTargetStall.add(1, road.getOnlyLane());                                       // Parking Road Lane
                pathToTargetStall.add(2, allocatedStall.getLane());                                 // Manual Stall Lane
                break;
            }
        }

        // Allocate this parking lane to the vehicle by sending message
        sendManualStallMessage(vehicle, allocatedStall, pathToTargetStall);

        // Register the vehicle with the AdjustableManualStatusMonitor, along with the
        // parking lane it has been allocated
        manualVehicles.put(vehicle, allocatedStall);
        numberOfAllowedEntries++;
        numberOfAllowedManualEntries++;
        return true;
    }


    /**
     * Update capacity and allocate a parking lane to a vehicle on entry to the car park.
     * @param vehicle The vehicle entering the car park.
     */
    private boolean addNewVehicle(MixedCPMBasicAutoVehicle vehicle) {
        AutomatedParkingRoad allocatedLane = automatedParkingArea.findTargetLane(vehicle.getSpec());

        if (allocatedLane == null) {
            numberOfDeniedEntries++;
            numberOfDeniedAutoEntries++;
            return false;
        }

        sendParkingLaneMessage(vehicle, allocatedLane);

        autoVehicles.put(vehicle, allocatedLane);
        numberOfAllowedEntries++;
        numberOfAllowedAutoEntries++;
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
        numberOfCompletedManualVehicles++;
    }

    /**
     * Update capacity when a vehicle exits the car park.
     * @param vehicle The vehicle exiting the car park.
     */
    private void vehicleOnExit(MixedCPMBasicAutoVehicle vehicle) {
        // Remove the vehicle from the status monitor's records
        vehicle.getTargetLane().removeVehicle(vehicle.getSpec());
        autoVehicles.remove(vehicle);
        numberOfCompletedVehicles++;
        numberOfCompletedAutoVehicles++;
    }


    private void sendManualStallMessage(MixedCPMBasicManualVehicle vehicle, ManualStall manualStall, List<Lane> path) {
        vehicle.sendMessageToI2VInbox(new Pair<>(manualStall, path));
    }

    private void sendParkingLaneMessage(MixedCPMBasicAutoVehicle vehicle, AutomatedParkingRoad message) {
        vehicle.sendMessageToI2VInbox(message);
    }

    @Override
    public List<MixedCPMBasicVehicle> getVehicles() {
        ArrayList<MixedCPMBasicVehicle> returnList = new ArrayList<>(manualVehicles.keySet());
        returnList.addAll(autoVehicles.keySet());
        return returnList;
    }

    public List<MixedCPMBasicManualVehicle> getManualVehicles() {
        return new ArrayList<>(manualVehicles.keySet());
    }

    public List<MixedCPMBasicAutoVehicle> getAutoVehicles() {
        return new ArrayList<>(autoVehicles.keySet());
    }

    private double getTotalAreaOfParkedVehicles(){
        return getTotalAreaOfParkedAutoVehicles() + getTotalAreaOfParkedManualVehicles();
    }

    private double getTotalAreaOfParkedAutoVehicles(){
        double totalVehicleArea = 0;
        for (MixedCPMBasicAutoVehicle vehicle : autoVehicles.keySet()){
            if(((MixedCPMAutoDriver)vehicle.getDriver()).isParked()){
                totalVehicleArea = totalVehicleArea +
                        (vehicle.getSpec().getWidth() *
                                vehicle.getSpec().getWidth());
            }
        }
        return totalVehicleArea;
    }

    private double getTotalAreaOfParkedManualVehicles(){
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
        return getNoOfParkedAutoVehicles() + getNoOfParkedManualVehicles();
    }

    public int getNoOfParkedManualVehicles(){
        int noOfParkedVehicles = 0;
        for (MixedCPMBasicManualVehicle vehicle : manualVehicles.keySet()) {
            if (((MixedCPMManualDriver) vehicle.getDriver()).isParked()) {
                noOfParkedVehicles++;
            }
        }
        return noOfParkedVehicles;
    }

    public int getNoOfParkedAutoVehicles(){
        int noOfParkedVehicles = 0;
        for (MixedCPMBasicAutoVehicle vehicle : autoVehicles.keySet()) {
            if (((MixedCPMAutoDriver) vehicle.getDriver()).isParked()) {
                noOfParkedVehicles++;
            }
        }
        return noOfParkedVehicles;
    }

    public void updateMostNumberOfVehicles(){
        int currentNumberOfVehicles = manualVehicles.size() + autoVehicles.size();
        if (currentNumberOfVehicles > mostNumberOfVehicles) {
            mostNumberOfVehicles = currentNumberOfVehicles;
        }

        int currentNumberOfParkedVehicles = getNoOfParkedVehicles();
        if (currentNumberOfParkedVehicles > mostNumberOfParkedVehicles) {
            mostNumberOfParkedVehicles = currentNumberOfParkedVehicles;
        }

        int currentNumberOfParkedManualVehicles = getNoOfParkedManualVehicles();
        if (currentNumberOfParkedManualVehicles > mostNumberOfParkedManualVehicles) {
            mostNumberOfParkedManualVehicles = currentNumberOfParkedManualVehicles;
        }

        int currentNumberOfParkedAutoVehicles = getNoOfParkedAutoVehicles();
        if (currentNumberOfParkedAutoVehicles > mostNumberOfParkedAutoVehicles) {
            mostNumberOfParkedAutoVehicles = currentNumberOfParkedAutoVehicles;
        }
    }

    public void updateEfficiencyMeasurements(){
        double areaOfCarPark = map.getAreaOfCarPark();
        double areaOfManualParkingArea = manualParkingArea.getDimensions().getWidth()*manualParkingArea.getDimensions().getHeight();
        double areaOfAutomatedParkingArea = automatedParkingArea.getDimensions().getWidth()*automatedParkingArea.getDimensions().getHeight();


        double areaOfVehicles = getTotalAreaOfParkedVehicles();
        double areaOfManualVehicles = getTotalAreaOfParkedManualVehicles();
        double areaOfAutoVehicles = getTotalAreaOfParkedAutoVehicles();

        areaPerVehicle = areaOfCarPark / getNoOfParkedVehicles();
        currentEfficiency =  areaOfVehicles / areaOfCarPark;

        areaPerManualVehicle = areaOfManualParkingArea / getNoOfParkedManualVehicles();
        currentManualEfficiency =  areaOfManualVehicles / areaOfManualParkingArea;

        areaPerAutoVehicle = areaOfAutomatedParkingArea / getNoOfParkedAutoVehicles();
        currentAutoEfficiency =  areaOfAutoVehicles / areaOfAutomatedParkingArea;

        if (currentEfficiency > maxEfficiency){
            maxEfficiency = currentEfficiency;
        }
        if (currentManualEfficiency > maxManualEfficiency){
            maxManualEfficiency = currentManualEfficiency;
        }
        if (currentAutoEfficiency > maxAutoEfficiency){
            maxAutoEfficiency = currentAutoEfficiency;
        }

        if (areaPerVehicle < minAreaPerVehicle){
            minAreaPerVehicle = areaPerVehicle;
        }
        if (areaPerManualVehicle < minAreaPerManualVehicle){
            minAreaPerManualVehicle = areaPerManualVehicle;
        }
        if (areaPerAutoVehicle < minAreaPerAutoVehicle){
            minAreaPerAutoVehicle = areaPerAutoVehicle;
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
    public int getNumberOfDeniedManualEntries() {
        return numberOfDeniedManualEntries;
    }

    @Override
    public int getNumberOfAllowedManualEntries() {
        return numberOfAllowedManualEntries;
    }

    @Override
    public int getNumberOfCompletedManualVehicles() {
        return numberOfCompletedManualVehicles;
    }

    @Override
    public int getMostNumberOfManualVehicles() {
        return mostNumberOfManualVehicles;
    }

    @Override
    public int getMostNumberOfParkedManualVehicles() {
        return mostNumberOfParkedManualVehicles;
    }

    @Override
    public int getNumberOfDeniedAutoEntries() {
        return numberOfDeniedAutoEntries;
    }

    @Override
    public int getNumberOfAllowedAutoEntries() {
        return numberOfAllowedAutoEntries;
    }

    @Override
    public int getNumberOfCompletedAutoVehicles() {
        return numberOfCompletedAutoVehicles;
    }

    @Override
    public int getMostNumberOfAutoVehicles() {
        return mostNumberOfAutoVehicles;
    }

    @Override
    public int getMostNumberOfParkedAutoVehicles() {
        return mostNumberOfParkedAutoVehicles;
    }

    @Override
    public double getMaxManualEfficiency() {
        return maxManualEfficiency;
    }

    @Override
    public double getCurrentManualEfficiency() {
        return currentManualEfficiency;
    }

    @Override
    public double getAreaPerManualVehicle() {
        return areaPerManualVehicle;
    }

    @Override
    public double getMinAreaPerManualVehicle() {
        return minAreaPerManualVehicle;
    }

    @Override
    public double getMaxAutoEfficiency() {
        return maxAutoEfficiency;
    }

    @Override
    public double getCurrentAutoEfficiency() {
        return currentAutoEfficiency;
    }

    @Override
    public double getAreaPerAutoVehicle() {
        return areaPerAutoVehicle;
    }

    @Override
    public double getMinAreaPerAutoVehicle() {
        return minAreaPerAutoVehicle;
    }
}
