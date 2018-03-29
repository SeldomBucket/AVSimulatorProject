package aim4.driver.mixedcpm.navigator;

import aim4.driver.mixedcpm.MixedCPMManualDriver;
import aim4.map.connections.BasicConnection;
import aim4.map.connections.Junction;
import aim4.map.connections.SimpleIntersection;
import aim4.map.lane.Lane;
import aim4.map.lane.LineSegmentLane;
import aim4.map.mixedcpm.parking.ManualStall;
import aim4.vehicle.mixedcpm.MixedCPMBasicManualVehicle;
import aim4.driver.mixedcpm.coordinator.MixedCPMManualCoordinator.*;

import java.util.ArrayList;
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
                //System.out.println("Vehicle " + vehicle.getVIN() + " Junction One Exit Lane");
                return connection.getExitLanes().get(0);
            } else {
                // If there are 2 exit lanes, we are either looking for the next lane
                // or we are exiting/relocating
                //System.out.println("Vehicle " + vehicle.getVIN() + " Junction Multiple Exit Lane " + ((Junction)connection).getRoads().toString());
                if (parkingStatus == ParkingStatus.PARKING) {
                    // Check the next lane to drive on

                    Lane nextLane = driver.getNextLane();
                    /*if (nextLane == null) {
                        throw new RuntimeException("Search for next lane failed unexpectedly");
                    }*/
                    if (connection.getExitLanes().contains(nextLane)) {
                        // Want to move to next lane if it's connected to this junction
                        System.out.println("Vehicle " + vehicle.getVIN() + " exiting junction on next lane in list");
                        return nextLane;
                    } else {
                        // Otherwise we want to stay on the same road we're on
                        return driver.getCurrentLane();
                    }

                } else if (parkingStatus == ParkingStatus.EXIT) {
                    // Then we want to exit by staying on the same lane
                    // TODO ED exit from parking space
                    return driver.getCurrentLane();
                } else {
                    throw new RuntimeException("No behaviour defined for WAITING in junction.");
                }
            }
        } else {
            throw new RuntimeException("Behaviour of navigator is not defined for this connection.");
        }
        // return null;
    }

}
