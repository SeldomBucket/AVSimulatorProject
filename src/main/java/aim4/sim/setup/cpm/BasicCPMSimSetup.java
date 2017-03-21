package aim4.sim.setup.cpm;

import aim4.sim.Simulator;

/**
 * The basic simulation setup for CPM. Common for all CPM simulation types.
 */
public class BasicCPMSimSetup implements CPMSimSetup {

    /** The speed limit of the roads */
    protected double speedLimit;
    /** The traffic level */
    protected double trafficLevel;
    /** The width of all lanes */
    // TODO CPM can separate this into lanes and parking lanes so can have different for both
    protected double laneWidth;
    /** The number of parking rows in the car park. */
    protected int numberOfParkingLanes;
    /** The length of the parking lanes used for parking.*/
    protected double parkingLength;
    /** The length of the parking lanes used for accessing the parking.*/
    protected double accessLength;

    /**
     * Create a copy of a given basic simulator setup.
     *
     * @param basicSimSetup  a basic simulator setup
     */
    public BasicCPMSimSetup(BasicCPMSimSetup basicSimSetup) {
        this.speedLimit = basicSimSetup.speedLimit;
        this.trafficLevel = basicSimSetup.trafficLevel;
        this.laneWidth = basicSimSetup.laneWidth;
        this.numberOfParkingLanes = basicSimSetup.numberOfParkingLanes;
        this.parkingLength = basicSimSetup.parkingLength;
        this.accessLength = basicSimSetup.accessLength;
    }

    /**
     * Create a basic simulator setup.
     *
     * @param speedLimit                  the speed limit in the car park
     */
    public BasicCPMSimSetup(double speedLimit, double trafficLevel,
                            double laneWidth, int numberOfParkingLanes,
                            double parkingLength, double accessLength) {
        this.speedLimit = speedLimit;
        this.trafficLevel = trafficLevel;
        this.laneWidth = laneWidth;
        this.numberOfParkingLanes = numberOfParkingLanes;
        this.parkingLength = parkingLength;
        this.accessLength = accessLength;
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

    public double getLaneWidth() {
        return laneWidth;
    }

    public void setLaneWidth(double laneWidth) {
        this.laneWidth = laneWidth;
    }

    public int getNumberOfParkingLanes() {
        return numberOfParkingLanes;
    }

    public void setNumberOfParkingLanes(int numberOfParkingLanes) {
        this.numberOfParkingLanes = numberOfParkingLanes;
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
}