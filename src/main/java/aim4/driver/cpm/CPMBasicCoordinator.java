package aim4.driver.cpm;

import aim4.vehicle.cpm.CPMBasicAutoVehicle;

/**
 * This class has a similar role to the V2ICoodinator for AIM.
 * It should handle the messages between vehicles, and with
 * the paypoint (if we have one). This includes processing messages
 * and sending messages.
 */
public class CPMBasicCoordinator {

    /** The Vehicle being coordinated by this coordinator. */
    private CPMBasicAutoVehicle vehicle;

    /** The driver of which this coordinator is a part. */
    private CPMBasicV2VDriver driver;

    // Does it make sense to have this here? Should it be in Driver or Vehicle?
    /** The sub-agent that controls physical manipulation of the vehicle */
    private CPMV2VPilot pilot;

    /////////////////////////////////
    // CONSTRUCTORS
    /////////////////////////////////

    /**
     * Create an basic V2V Coordinator to coordinate a Vehicle in CPM.
     *
     * @param vehicle  the Vehicle to coordinate
     * @param driver   the driver
     */
    public CPMBasicCoordinator (CPMBasicAutoVehicle vehicle,
                                CPMBasicV2VDriver driver){
        this.vehicle = vehicle;
        this.driver = driver;
        this.pilot = new CPMV2VPilot(vehicle, driver);
    }
}
