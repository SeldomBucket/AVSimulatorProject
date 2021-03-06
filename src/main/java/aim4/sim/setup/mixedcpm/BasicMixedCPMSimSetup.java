package aim4.sim.setup.mixedcpm;

import aim4.map.mixedcpm.MixedCPMMapUtil.*;
import aim4.sim.Simulator;
import javafx.util.Pair;

/**
 * The basic simulation setup for CPM. Common for all CPM simulation types.
 */
public class BasicMixedCPMSimSetup implements MixedCPMSimSetup {

    /** The width of the car park */
    protected double carParkWidth;
    /** The width of the car park */
    protected double carParkHeight;
    /** The speed limit of the roads */
    protected double speedLimit;
    /** The traffic level */
    protected double trafficLevel;
    /** The rate at which automated vehicles spawn */
    protected double automatedVehiclesRate;
    /** The width of all lanes */
    // TODO CPM can separate this into lanes and parking lanes so can have different for both
    protected double laneWidth;
    /** The type of spawn specification. */
    protected SpawnSpecType spawnSpecType;
    /** The type of spawn specification. */
    protected MapType mapType;
    /** Whether to use a CSV file for the spawn times and parking times, and the location of the file */
    protected Pair<Boolean, String> useCSVFile;
    /** Whether to use a CSV file for the spawn times and parking times, and the location of the file */
    protected Pair<Boolean, String> useMultipleCSVFile;
    protected boolean logToFile;
    protected int noOfVehiclesToSpawn;

    /**
     * Create a copy of a given basic simulator setup.
     *
     * @param basicSimSetup  a basic simulator setup
     */
    public BasicMixedCPMSimSetup(BasicMixedCPMSimSetup basicSimSetup) {
        this.speedLimit = basicSimSetup.speedLimit;
        this.automatedVehiclesRate = 0.5;
        this.trafficLevel = basicSimSetup.trafficLevel;
        this.laneWidth = basicSimSetup.laneWidth;
        this.carParkWidth = basicSimSetup.carParkWidth;
        this.carParkHeight = basicSimSetup.carParkHeight;
        this.spawnSpecType = basicSimSetup.spawnSpecType;
        this.mapType = basicSimSetup.mapType;
        this.useCSVFile = basicSimSetup.useCSVFile;
        this.noOfVehiclesToSpawn = basicSimSetup.noOfVehiclesToSpawn;
    }

    /**
     * Create a basic simulator setup.
     *
     * @param speedLimit                  the speed limit in the car park
     */
    public BasicMixedCPMSimSetup(double speedLimit, double trafficLevel,
                            double laneWidth, double carParkWidth, double carParkHeight,
                            SpawnSpecType spawnSpecType, MapType mapType, Pair<Boolean, String> useCSVFile) {
        this.speedLimit = speedLimit;
        this.automatedVehiclesRate = 0.5;
        this.trafficLevel = trafficLevel;
        this.laneWidth = laneWidth;
        this.carParkWidth = carParkWidth;
        this.carParkHeight = carParkHeight;
        this.spawnSpecType = spawnSpecType;
        this.mapType = mapType;
        this.useCSVFile = useCSVFile;
        this.noOfVehiclesToSpawn = 100;
    }

    @Override
    public Simulator getSimulator() {
        throw new RuntimeException("Cannot instantiate BasicCPMSimSetup");
    }

    public double getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(double speedLimit) {
        this.speedLimit = speedLimit;
    }

    public double getAutomatedVehiclesRate() {
        return automatedVehiclesRate;
    }

    public void setAutomatedVehiclesRate(double automatedVehiclesRate) {
        this.automatedVehiclesRate = automatedVehiclesRate;
    }

    public double getTrafficLevel() {
        return trafficLevel;
    }

    public void setTrafficLevel(double trafficLevel) {
        this.trafficLevel = trafficLevel;
    }

    public double getCarParkWidth() {
        return carParkWidth;
    }

    public void setCarParkWidth(double carParkWidth) {
        this.carParkWidth = carParkWidth;
    }

    public double getCarParkHeight() {
        return carParkHeight;
    }

    public void setCarParkHeight(double carParkHeight) {
        this.carParkHeight = carParkHeight;
    }

    public double getLaneWidth() {
        return laneWidth;
    }

    public void setLaneWidth(double laneWidth) {
        this.laneWidth = laneWidth;
    }
    public SpawnSpecType getSpawnSpecType() { return spawnSpecType; }

    public void setSpawnSpecType(SpawnSpecType spawnSpecType) {
        this.spawnSpecType = spawnSpecType;
    }

    public void setMapType(MapType mapType) {
        this.mapType = mapType;
    }
    public MapType getMapType() { return mapType; }

    public Pair<Boolean, String> getUseCSVFile() {
        return useCSVFile;
    }

    public void setUseCSVFile(Pair<Boolean, String> useCSVFile) {
        this.useCSVFile = useCSVFile;
    }

    public Pair<Boolean, String> getMultipleCSVFile() {
        return useMultipleCSVFile;
    }

    public void setMultipleCSVFile(Pair<Boolean, String> useMultipleCSVFile) {
        this.useMultipleCSVFile = useMultipleCSVFile;
    }

    public boolean isLogToFile() {
        return logToFile;
    }

    public void setLogToFile(boolean logToFile){
        this.logToFile = logToFile;
    }

    public int getNoOfVehiclesToSpawn() {
        return noOfVehiclesToSpawn;
    }

    public void setNoOfVehiclesToSpawn(int noOfVehiclesToSpawn) {
        this.noOfVehiclesToSpawn = noOfVehiclesToSpawn;
    }
}