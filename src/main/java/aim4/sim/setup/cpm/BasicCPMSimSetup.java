package aim4.sim.setup.cpm;

import aim4.map.cpm.CPMMapUtil.SpawnSpecType;
import aim4.sim.Simulator;
import javafx.util.Pair;

import java.util.List;

/**
 * The basic simulation setup for CPM. Common for all CPM simulation types.
 */
public class BasicCPMSimSetup implements CPMSimSetup {

    /**
     * The speed limit of the roads
     */
    protected double speedLimit;
    /**
     * The traffic level
     */
    protected double trafficLevel;
    /**
     * The length of the parking lanes used for parking.
     */
    protected double parkingLength;
    /**
     * The length of the parking lanes used for accessing the parking.
     */
    protected double accessLength;
    /**
     * The type of spawn specification.
     */
    protected SpawnSpecType spawnSpecType;
    /**
     * The distribution for a mixed spawn spec.
     */
    protected List<Double> mixedSpawnDistribution;
    /**
     * The name of vehicle spec for single spec spawn, e.g. COUPE
     */
    protected String singleSpawnSpecName;
    /**
     * Whether to use a CSV file for the spawn times and parking times, and the location of the file
     */
    protected Pair<Boolean, String> useCSVFile;
    /**
     * Whether to run the simulation for a specific period of time
     */
    protected Pair<Boolean, Double> useSpecificSimTime;
    /**
     * The number of times the simulation should be run.
     */
    protected Integer numberOfSimulations;
    /**
     * The location where the csv files are output to
     */
    protected String fileLocation;

    /**
     * Create a copy of a given basic simulator setup.
     *
     * @param basicSimSetup a basic simulator setup
     */
    public BasicCPMSimSetup(BasicCPMSimSetup basicSimSetup) {
        this.speedLimit = basicSimSetup.speedLimit;
        this.trafficLevel = basicSimSetup.trafficLevel;
        this.parkingLength = basicSimSetup.parkingLength;
        this.accessLength = basicSimSetup.accessLength;
        this.spawnSpecType = basicSimSetup.spawnSpecType;
        this.mixedSpawnDistribution = basicSimSetup.mixedSpawnDistribution;
        this.singleSpawnSpecName = basicSimSetup.singleSpawnSpecName;
        this.useCSVFile = basicSimSetup.useCSVFile;
        this.useSpecificSimTime = basicSimSetup.useSpecificSimTime;
        this.numberOfSimulations = basicSimSetup.numberOfSimulations;
        this.fileLocation = basicSimSetup.fileLocation;
    }

    /**
     * Create a basic simulator setup.
     *
     * @param speedLimit the speed limit in the car park
     */
    public BasicCPMSimSetup(double speedLimit, double trafficLevel,
                            double parkingLength, double accessLength,
                            SpawnSpecType spawnSpecType,
                            Pair<Boolean, String> useCSVFile,
                            Pair<Boolean, Double> useSpecificSimTime,
                            String singleSpawnSpecName,
                            List<Double> mixedSpawnDistribution,
                            Integer numberOfSimulations,
                            String fileLocation) {
        this.speedLimit = speedLimit;
        this.trafficLevel = trafficLevel;
        this.parkingLength = parkingLength;
        this.accessLength = accessLength;
        this.spawnSpecType = spawnSpecType;
        this.useCSVFile = useCSVFile;
        this.useSpecificSimTime = useSpecificSimTime;
        this.singleSpawnSpecName = singleSpawnSpecName;
        this.mixedSpawnDistribution = mixedSpawnDistribution;
        this.numberOfSimulations = numberOfSimulations;
        this.fileLocation = fileLocation;
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

    public double getTrafficLevel() {
        return trafficLevel;
    }

    public void setTrafficLevel(double trafficLevel) {
        this.trafficLevel = trafficLevel;
    }

    public double getParkingLength() {
        return parkingLength;
    }

    public void setParkingLength(double parkingLength) {
        this.parkingLength = parkingLength;
    }

    public double getAccessLength() {
        return accessLength;
    }

    public void setAccessLength(double accessLength) {
        this.accessLength = accessLength;
    }

    public SpawnSpecType getSpawnSpecType() {
        return spawnSpecType;
    }

    public void setSpawnSpecType(SpawnSpecType spawnSpecType) {
        this.spawnSpecType = spawnSpecType;
    }

    public String getSingleSpawnSpecName() {
        return singleSpawnSpecName;
    }

    public List<Double> getMixedSpawnDistribution() {
        return mixedSpawnDistribution;
    }

    public void setMixedSpawnDistribution(List<Double> mixedSpawnDistribution) {
        this.mixedSpawnDistribution = mixedSpawnDistribution;
    }

    public void setSingleSpawnSpecName(String singleSpawnSpecName) {
        this.singleSpawnSpecName = singleSpawnSpecName;
    }

    public Pair<Boolean, String> getUseCSVFile() {
        return useCSVFile;
    }

    public void setUseCSVFile(Pair<Boolean, String> useCSVFile) {
        this.useCSVFile = useCSVFile;
    }

    public Pair<Boolean, Double> getUseSpecificSimTime() {
        return useSpecificSimTime;
    }

    public void setUseSpecificSimTime(Pair<Boolean, Double> useSpecificSimTime) {
        this.useSpecificSimTime = useSpecificSimTime;
    }

    public Integer getNumberOfSimulations() {
        return numberOfSimulations;
    }

    public void setNumberOfSimulations(Integer numberOfSimulations) {
        this.numberOfSimulations = numberOfSimulations;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }
}