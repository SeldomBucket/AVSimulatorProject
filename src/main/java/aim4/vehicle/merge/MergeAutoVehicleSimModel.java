package aim4.vehicle.merge;

import aim4.driver.merge.MergeAutoDriver;
import aim4.vehicle.AutoVehicleSimModel;

/**
 * Created by Callum on 14/03/2017.
 */
public interface MergeAutoVehicleSimModel extends MergeAutoVehicleDriverModel,
        MergeVehicleSimModel, AutoVehicleSimModel {
    MergeAutoDriver getDriver();
}
