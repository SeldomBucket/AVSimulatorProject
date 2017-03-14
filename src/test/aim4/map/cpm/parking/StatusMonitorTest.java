package aim4.map.cpm.parking;

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

        @Test
        public void testGetRemainingCapacity() throws Exception {
                double expectedRemainingCapacity = 20;
                double actualRemainingCapacity = statusMonitor1Lane.getRemainingCapacity();
                assert(expectedRemainingCapacity == actualRemainingCapacity);

                expectedRemainingCapacity = 60;
                actualRemainingCapacity = statusMonitor3Lane.getRemainingCapacity();
                assert(expectedRemainingCapacity == actualRemainingCapacity);
        }


}