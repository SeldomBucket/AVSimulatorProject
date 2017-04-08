package aim4.cpm;

import aim4.map.cpm.CPMCarParkWithStatus;
import aim4.map.cpm.CPMMapUtil;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import util.TestSimThread;

import static org.junit.Assert.assertTrue;

/**
 * Tests to check that vehicles are spawned at the right time.
 */
public class SpawnTests {

    CPMCarParkWithStatus map;
    TestSimThread simThread;
    CPMAutoDriverSimulator sim;

    @Before
    public void setUp() {

    }

    @Test
    public void testNoRoomNoSpawn() throws Exception {
        /**
         * Set up a map where there is no room to park.
         * */

        this.map = new CPMCarParkWithStatus(4, // laneWidth
                10.0, // speedLimit
                0.0, // initTime
                1, // numberOfParkingLanes
                1, // parkingLength
                5); // access length
        CPMMapUtil.setUpInfiniteSingleSpecVehicleSpawnPoint(map, 0.28);
        this.sim = new CPMAutoDriverSimulator(map);
        this.simThread = new TestSimThread(sim);

        // Run the simulation for a period of time.
        try {
            int count = 1000000000;
            simThread.start();
            while (count != 0) {
                simThread.run();
                count--;
            }
        } catch(RuntimeException e) {
            throw new RuntimeException("RuntimeException thrown: " + ". Message was: " + e.getMessage());
        }

        assertTrue(sim.getMap() instanceof CPMCarParkWithStatus);

        // There should be 0 vehicles registered with the status monitor.
        assertTrue(sim.getMap().getStatusMonitor().getVehicles().size() == 0);

        // There should be 0 vehicles registered as parked with the map.
        assertTrue(sim.getParkedVehicles().size() == 0);

        // There should be no vehicle on the map
        assertTrue(map.getVehicles().size() == 0);
    }

    @Test
    public void testNotWideEnoughNoSpawn() throws Exception {
        /**
         * Set up a map where the lanes are not wide enough for any vehicle.
         * */

        this.map = new CPMCarParkWithStatus(1, // laneWidth
                10.0, // speedLimit
                0.0, // initTime
                1, // numberOfParkingLanes
                1, // parkingLength
                5); // access length
        CPMMapUtil.setUpInfiniteSingleSpecVehicleSpawnPoint(map, 0.28);
        this.sim = new CPMAutoDriverSimulator(map);
        this.simThread = new TestSimThread(sim);

        // Run the simulation for a period of time.
        try {
            int count = 1000000000;
            simThread.start();
            while (count != 0) {
                simThread.run();
                count--;
            }
        } catch(RuntimeException e) {
            throw new RuntimeException("RuntimeException thrown: " + ". Message was: " + e.getMessage());
        }

        assertTrue(sim.getMap() instanceof CPMCarParkWithStatus);

        // There should be 0 vehicles registered with the status monitor.
        assertTrue(sim.getMap().getStatusMonitor().getVehicles().size() == 0);

        // There should be 0 vehicles registered as parked with the map.
        assertTrue(sim.getParkedVehicles().size() == 0);

        // There should be no vehicle on the map
        assertTrue(map.getVehicles().size() == 0);
    }

    @Test
    public void testOneSpawn() throws Exception {

        /**
         * Set up a map where there room for one vehicle to park.
         * */

        this.map = new CPMCarParkWithStatus(4, // laneWidth
                10.0, // speedLimit
                0.0, // initTime
                1, // numberOfParkingLanes
                5, // parkingLength
                5); // access length
        CPMMapUtil.setUpInfiniteSingleSpecVehicleSpawnPoint(map, 0.28);
        this.sim = new CPMAutoDriverSimulator(map);
        this.simThread = new TestSimThread(sim);

        // Run the simulation for a period of time.
        try {
            int count = 1000000000;
            simThread.start();
            while (count != 0) {
                simThread.run();
                count--;
            }
        } catch(RuntimeException e) {
            throw new RuntimeException("RuntimeException thrown: " + ". Message was: " + e.getMessage());
        }

        assertTrue(sim.getMap() instanceof CPMCarParkWithStatus);

        // There should be 1 vehicle registered with the status monitor.
        assertTrue(sim.getMap().getStatusMonitor().getVehicles().size() == 1);

        // There should be 1 vehicle on the map
        assertTrue(map.getVehicles().size() == 1);
    }

    @After
    public void tearDown() throws Exception {
        this.simThread.terminate();
    }
}
