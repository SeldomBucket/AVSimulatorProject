package aim4.map.mixedcpm.parking;

import aim4.driver.mixedcpm.MixedCPMManualDriver;
import aim4.vehicle.mixedcpm.MixedCPMBasicManualVehicle;

import java.util.HashMap;
import java.util.Map;

public interface IStatusMonitor {

        /**
         * Update capacity and allocate a parking lane to a vehicle on entry to the car park.
         * @param vehicle The vehicle entering the car park.
         */
        public boolean addNewVehicle(MixedCPMBasicManualVehicle vehicle) ;

        /**
         * Update capacity when a vehicle exits the car park.
         * @param vehicle The vehicle exiting the car park.
         */
        public void vehicleOnExit(MixedCPMBasicManualVehicle vehicle);

        public Map<MixedCPMBasicManualVehicle, ManualStall> getVehicles();

        public double getTotalAreaOfParkedVehicles();

        public int getNoOfParkedVehicles();

        public void updateMostNumberOfVehicles();

        public void updateEfficiencyMeasurements();

        public double getCurrentEfficiency();

        public double getMaxEfficiency();

        public double getAreaPerVehicle();

        public double getMinAreaPerVehicle() ;

        public int getNumberOfCompletedVehicles() ;

        public int getMostNumberOfParkedVehicles() ;

        public int getNumberOfDeniedEntries() ;

        public int getNumberOfAllowedEntries() ;

        public int getMostNumberOfVehicles() ;

}
