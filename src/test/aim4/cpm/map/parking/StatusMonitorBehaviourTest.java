package aim4.cpm.map.parking;

import aim4.map.cpm.CPMMapUtil;
import aim4.map.cpm.parking.ParkingLane;
import aim4.map.cpm.CPMCarParkWithStatus;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import util.TestSimThread;

import java.awt.geom.Point2D;

import static org.junit.Assert.*;

/**
 * TEST SUITE PURPOSE: Ensure that the behaviour of the Status Monitor is correct.
 */
public class StatusMonitorBehaviourTest {
    CPMCarParkWithStatus mapNoParkingSpace, mapTwoLanes;
    TestSimThread simThreadForNoParkingSpace, simThreadForTwoLanes;
    CPMAutoDriverSimulator simForNoParkingSpace, simForTwoLanes;

    @Before
    public void setUp() {
        /**
         * Set up a map where there is no room for vehicles
         * to park. No vehicle should be spawned.
         * */
        this.mapNoParkingSpace = new CPMCarParkWithStatus(4, // laneWidth
                10.0, // speedLimit
                0.0, // initTime
                1, // numberOfParkingLanes
                0, // parkingLength
                5); // access length
        CPMMapUtil.setUpInfiniteSingleSpecVehicleSpawnPoint(mapNoParkingSpace, 0.28);
        this.simForNoParkingSpace = new CPMAutoDriverSimulator(mapNoParkingSpace);
        this.simThreadForNoParkingSpace = new TestSimThread(simForNoParkingSpace);

        /**
         * Set up a map where there are 2 parking lanes and
         * 2 vehicles are spawned.
         * */
        this.mapTwoLanes = new CPMCarParkWithStatus(4, // laneWidth
                10.0, // speedLimit
                0.0, // initTime
                2, // numberOfParkingLanes
                20, // parkingLength
                5); // access length
        CPMMapUtil.setUpFiniteSingleSpecSpawnPoint(mapTwoLanes, 2, 0.28);
        this.simForTwoLanes = new CPMAutoDriverSimulator(mapTwoLanes);
        this.simThreadForTwoLanes = new TestSimThread(simForTwoLanes);
    }

    @Test
    public void testNoSpawnWhenNoSpace() throws Exception {

        // Run the simulation for a period of time.
        try {
            int count = 10000;
            simThreadForNoParkingSpace.start();
            while (count != 0) {
                simThreadForNoParkingSpace.run();
                count--;
            }
        } catch(RuntimeException e) {
            throw new RuntimeException("RuntimeException thrown: " + ". Message was: " + e.getMessage());
        }

        assertTrue(simForNoParkingSpace.getMap() instanceof CPMCarParkWithStatus);
        // There should be no vehicles on the map.
        assertTrue(mapNoParkingSpace.getVehicles().size() == 0);
        // There should be no vehicles registered with the status monitor
        assertTrue(mapNoParkingSpace.getStatusMonitor().getVehicles().size() == 0);
    }

    @Test
    public void testCorrectLaneAllocated() throws Exception {
        // TODO CPM This test is temperamental, if the first vehicle exits before the second has parked, condition never met
        // Run the simulation until 2 vehicles are parked.
        try {
            simThreadForTwoLanes.start();
            while (simForTwoLanes.getParkedVehicles().size() != 2) {
                simThreadForTwoLanes.run();
            }
        } catch(RuntimeException e) {
            throw new RuntimeException("RuntimeException thrown: " + ". Message was: " + e.getMessage());
        }

        assertTrue(simForTwoLanes.getMap() instanceof CPMCarParkWithStatus);

        // There should be 2 vehicles registered with the status monitor.
        assertTrue(simForTwoLanes.getMap().getStatusMonitor().getVehicles().size() == 2);

        // There should be 2 different vehicles registered as parked with the map.
        assertTrue(simForTwoLanes.getParkedVehicles().get(0) != simForTwoLanes.getParkedVehicles().get(1));

        // There should be 1 vehicle in each lane
        ParkingLane parkingLane1 = mapTwoLanes.getParkingArea().getParkingLanes().get(0);
        ParkingLane parkingLane2 = mapTwoLanes.getParkingArea().getParkingLanes().get(1);
        Point2D positionOfVehicle1 = simForTwoLanes.getParkedVehicles().get(0).getPosition();
        Point2D positionOfVehicle2 = simForTwoLanes.getParkedVehicles().get(1).getPosition();
        boolean oneInEachLane = false;
        if (parkingLane1.getShape().contains(positionOfVehicle1)
                && parkingLane2.getShape().contains(positionOfVehicle2)
                || parkingLane1.getShape().contains(positionOfVehicle2)
                && parkingLane2.getShape().contains(positionOfVehicle1)) {
            oneInEachLane = true;
        }
        assertTrue(oneInEachLane);
    }


    @After
    public void tearDown() throws Exception {
        this.simThreadForNoParkingSpace.terminate();
        this.simThreadForTwoLanes.terminate();
    }
}