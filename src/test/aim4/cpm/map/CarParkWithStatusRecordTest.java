package aim4.cpm.map;

import aim4.map.cpm.CPMMapUtil;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import aim4.map.cpm.CPMCarParkWithStatus;
import org.junit.Before;
import org.junit.Test;
import util.TestSimThread;

import static org.junit.Assert.assertTrue;

/**
 * TEST SUITE PURPOSE: Ensure that the map records the correct data.
 */
public class CarParkWithStatusRecordTest {
    CPMCarParkWithStatus map;
    TestSimThread simThread;
    CPMAutoDriverSimulator sim;
    int numberOfVehicles = 5;

    @Before
    public void setUp() {
        /**
         * Set up a map.
         * */
        this.map = new CPMCarParkWithStatus(4, // laneWidth
                10.0, // speedLimit
                0.0, // initTime
                3, // numberOfParkingLanes
                20, // parkingLength
                5); // access length
        CPMMapUtil.setUpFiniteSingleSpecSpawnPoint(map, numberOfVehicles, 0.001);
        this.sim = new CPMAutoDriverSimulator(map);
        this.simThread = new TestSimThread(sim);
    }

    @Test
    public void testCompletedVehicles() throws Exception {
        // Run the simulation until all vehicles have completed
        try {
            simThread.start();
            while (sim.getNumCompletedVehicles() != numberOfVehicles) {
                simThread.run();
            }
            simThread.pause();
        } catch(RuntimeException e) {
            throw new RuntimeException("RuntimeException thrown: " + ". Message was: " + e.getMessage());
        }

        assertTrue(sim.getMap() instanceof CPMCarParkWithStatus);

        // There should be 0 vehicles registered with the status monitor.
        assertTrue(sim.getMap().getStatusMonitor().getVehicles().size() == 0);

        // There should be 0 vehicles registered with the map.
        assertTrue(map.getVehicles().size() == 0);
    }
}
