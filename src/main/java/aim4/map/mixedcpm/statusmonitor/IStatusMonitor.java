package aim4.map.mixedcpm.statusmonitor;

import aim4.driver.mixedcpm.MixedCPMManualDriver;
import aim4.map.mixedcpm.parking.ManualStall;
import aim4.vehicle.mixedcpm.MixedCPMBasicAutoVehicle;
import aim4.vehicle.mixedcpm.MixedCPMBasicManualVehicle;
import aim4.vehicle.mixedcpm.MixedCPMBasicVehicle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IStatusMonitor {

        /**
         * Update capacity and allocate a parking lane to a vehicle on entry to the car park.
         * @param vehicle The vehicle entering the car park.
         */
        public boolean addNewVehicle(MixedCPMBasicVehicle vehicle) ;

        /**
         * Update capacity when a vehicle exits the car park.
         * @param vehicle The vehicle exiting the car park.
         */
        public void vehicleOnExit(MixedCPMBasicVehicle vehicle);

        public List<MixedCPMBasicVehicle> getVehicles();

        public List<MixedCPMBasicManualVehicle> getManualVehicles();

        public List<MixedCPMBasicAutoVehicle> getAutoVehicles();

        public void updateMostNumberOfVehicles();

        public void updateEfficiencyMeasurements();

        // ALL VEHICLES STATS

        public int getNoOfParkedVehicles();

        public double getCurrentEfficiency();

        public double getMaxEfficiency();

        public double getAreaPerVehicle();

        public double getMinAreaPerVehicle() ;

        public int getNumberOfCompletedVehicles() ;

        public int getMostNumberOfParkedVehicles() ;

        public int getNumberOfDeniedEntries() ;

        public int getNumberOfAllowedEntries() ;

        public int getMostNumberOfVehicles() ;

        // MANUAL VEHICLES STATS

        public double getCurrentManualEfficiency();

        public double getMaxManualEfficiency();

        public double getAreaPerManualVehicle();

        public double getMinAreaPerManualVehicle() ;

        public int getNoOfParkedDisabledVehicles();

        public int getMostNumberOfParkedDisabledVehicles();

        public int getNoOfParkedManualVehicles();

        public int getNumberOfCompletedManualVehicles() ;

        public int getMostNumberOfParkedManualVehicles() ;

        public int getNumberOfDeniedManualEntries() ;

        public int getNumberOfAllowedManualEntries() ;

        public int getMostNumberOfManualVehicles() ;

        // AUTO VEHICLES STATS

        public double getCurrentAutoEfficiency();

        public double getMaxAutoEfficiency();

        public double getAreaPerAutoVehicle();

        public double getMinAreaPerAutoVehicle() ;
        
        public int getNoOfParkedAutoVehicles();

        public int getNumberOfCompletedAutoVehicles() ;

        public int getMostNumberOfParkedAutoVehicles() ;

        public int getNumberOfDeniedAutoEntries() ;

        public int getNumberOfAllowedAutoEntries() ;

        public int getMostNumberOfAutoVehicles() ;

}
