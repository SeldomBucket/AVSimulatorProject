package aim4.driver.mixedcpm.navigator;

import aim4.driver.mixedcpm.MixedCPMManualDriver;
import aim4.map.connections.BasicConnection;
import aim4.map.connections.Junction;
import aim4.map.connections.SimpleIntersection;
import aim4.map.lane.Lane;
import aim4.map.mixedcpm.parking.ManualStall;
import aim4.vehicle.mixedcpm.MixedCPMBasicManualVehicle;
import aim4.driver.mixedcpm.coordinator.MixedCPMManualCoordinator.*;

import java.util.List;

/**
 * A navigator to decide where to go.
 */
public class MixedCPMManualNavigator {

    private MixedCPMBasicManualVehicle vehicle;

    private MixedCPMManualDriver driver;

    public MixedCPMManualNavigator(MixedCPMBasicManualVehicle vehicle, MixedCPMManualDriver driver){
        this.driver = driver;
        this.vehicle = vehicle;
    }

    public Lane navigateConnection(BasicConnection connection,
                                   ParkingStatus parkingStatus){

        if (connection instanceof Junction) {
            // Could have 1 or 2 exits
            if (connection.getExitLanes().size() == 1) {
                return connection.getExitLanes().get(0);
            } else {
                // If there are 2 exit lanes, we are either looking for our parking lane
                // or we are exiting/relocating
                if (parkingStatus == ParkingStatus.PARKING) {
                    // Check if we can turn onto the target parking lane
                    if (connection.getExitLanes().contains(vehicle.getTargetStall())){
                        return vehicle.getTargetStall().getRoad().getOnlyLane();
                    } else {
                        // Otherwise we want to stay on the same road we're on
                        return driver.getCurrentLane();
                    }
                } else if (parkingStatus == ParkingStatus.RELOCATING) {
                    // Then we want to go back into the car park
                    // This is not the current lane
                    List<Lane> exitLanes = connection.getExitLanes();
                    exitLanes.remove(driver.getCurrentLane());
                    return exitLanes.get(0);
                } else if (parkingStatus == ParkingStatus.EXIT) {
                    // Then we want to exit by staying on the same lane
                    return driver.getCurrentLane();
                } else {
                    throw new RuntimeException("No behaviour defined for WAITING in junction.");
                }
            }
        } else if (connection instanceof SimpleIntersection) {
            // There will be 2 exits, we are at the start of the parking area
            if (parkingStatus == ParkingStatus.PARKING) {
                // Check if we can turn onto the target parking lane
                if (connection.getExitLanes().contains(vehicle.getTargetStall())){
                    return vehicle.getTargetStall().getRoad().getOnlyLane();
                } else {
                    // Otherwise we want to choose the other exit lane
                    for (Lane lane : connection.getExitLanes()) {
                        if (lane instanceof ManualStall) {
                            continue;
                        } else {
                            return lane;
                        }
                    }
                }
            } else if (parkingStatus == ParkingStatus.WAITING) {
                // TODO Make cars wait
            }
        } else {
            throw new RuntimeException("Behaviour of navigator is not defined for this connection.");
        }
        return null;
    }

}
