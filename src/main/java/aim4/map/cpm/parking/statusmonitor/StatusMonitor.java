package aim4.map.cpm.parking.statusmonitor;

import aim4.vehicle.cpm.CPMBasicAutoVehicle;

/**
 * Created by Becci on 24-Apr-17.
 */
public interface StatusMonitor {
    /**
     * Calculate if there is enough room for the vehicle in the car park.
     * @param vehicleLength The length of the vehicle that wishes to enter the car park
     * @return true if there is space for the vehicle to park.
     */
    public boolean roomForVehicle(double vehicleLength);

    /**
     * Update capacity and allocate a parking lane to a vehicle on entry to the car park.
     * @param vehicle The vehicle entering the car park.
     */
    public void vehicleOnEntry(CPMBasicAutoVehicle vehicle);

    /**
     * Update capacity and allocate a parking lane to a vehicle when
     * re-entering the car park.
     * @param vehicle The vehicle re-entering the car park.
     */
    public void vehicleOnReEntry(CPMBasicAutoVehicle vehicle);

    /**
     * Update capacity when a vehicle exits the car park.
     * @param vehicle The vehicle exiting the car park.
     */
    public void vehicleOnExit(CPMBasicAutoVehicle vehicle);
}
