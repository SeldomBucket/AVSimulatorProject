package aim4.cpm.map.parking;

import aim4.map.cpm.parking.StatusMonitor;
import aim4.map.cpm.testmaps.CPMCarParkWithStatus;
import org.junit.Test;

import static org.junit.Assert.*;

public class StatusMonitorTest {

        CPMCarParkWithStatus map1Lane = new CPMCarParkWithStatus(4, // laneWidth
            10.0, // speedLimit
            0.0, // initTime
            1, // numberOfParkingLanes
            20, // parkingLength
            5); // access length

        StatusMonitor statusMonitor1Lane = map1Lane.getStatusMonitor();

        CPMCarParkWithStatus map3Lane = new CPMCarParkWithStatus(4, // laneWidth
                10.0, // speedLimit
                0.0, // initTime
                3, // numberOfParkingLanes
                20, // parkingLength
                5); // access length

        StatusMonitor statusMonitor3Lane = map3Lane.getStatusMonitor();

}