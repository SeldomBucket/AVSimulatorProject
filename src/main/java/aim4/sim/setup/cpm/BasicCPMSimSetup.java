package aim4.sim.setup.cpm;

import aim4.map.BasicMap;
import aim4.sim.Simulator;
import aim4.vehicle.VehicleSimModel;

/**
 * The basic simulation setup for CPM. Common for all CPM simulation types.
 */
public class BasicCPMSimSetup implements CPMSimSetup {

    /** The speed limit of the roads */
    protected double speedLimit;
    /** The traffic level */
    protected double trafficLevel;

    /**
     * Create a copy of a given basic simulator setup.
     *
     * @param basicSimSetup  a basic simulator setup
     */
    public BasicCPMSimSetup(BasicCPMSimSetup basicSimSetup) {
        this.speedLimit = basicSimSetup.speedLimit;
        this.trafficLevel = basicSimSetup.trafficLevel;
    }

    /**
     * Create a basic simulator setup.
     *
     * @param speedLimit                  the speed limit in the car park
     */
    public BasicCPMSimSetup(double speedLimit, double trafficLevel) {
        this.speedLimit = speedLimit;
        this.trafficLevel = trafficLevel;
    }

    @Override
    public Simulator getSimulator() {
        // TODO: think how to avoid using the following assertation.
        assert false : ("Cannot instantiate BasicCPMSimSetup");
        return null;
    }
}
