package aim4.vehicle.merge;

import aim4.driver.merge.MergeAutoDriver;
import aim4.vehicle.AutoVehicleDriverModel;

/**
 * Created by Callum on 14/03/2017.
 */
public interface MergeAutoVehicleDriverModel extends AutoVehicleDriverModel {
    @Override
    MergeAutoDriver getDriver();

    void setPrecedingVehicleVIN(int vin);
    int getPrecedingVehicleVIN();
}
