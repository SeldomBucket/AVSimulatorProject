package aim4.vehicle;

/**
 * Created by Callum on 17/11/2016.
 */
public interface AutoVehicleSimModel extends VehicleSimModel, AutoVehicleDriverModel {
    /**
     * Set whether or not the laser range finder is sensing anything. This
     * should only be called by the actual physical simulator when it is
     * providing sensing information to the Vehicle.
     *
     * @param sensing whether or not the laser range finder is sensing anything
     */
    void setLRFSensing(boolean sensing);
}
