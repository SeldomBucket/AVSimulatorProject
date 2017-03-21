package aim4.cpm;

import aim4.map.cpm.CPMMapUtil;
import aim4.map.cpm.testmaps.CPMCarParkWithStatus;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import org.junit.Before;
import org.junit.Test;
import util.TestSimThread;

import static org.junit.Assert.assertTrue;

/**
 * Created by Becci on 21-Mar-17.
 */
public class FindBugTest {
    CPMCarParkWithStatus map;
    TestSimThread simThread;
    CPMAutoDriverSimulator sim;

    @Before
    public void setUp() {
        /**
         * Set up a map where 2 vehicles are spawned in a car
         * park with one parking lane. They will both be sent
         * to the same parking lane.
         * */

        this.map = new CPMCarParkWithStatus(4, // laneWidth
                55.0, // speedLimit
                0.0, // initTime
                1, // numberOfParkingLanes
                20, // parkingLength
                10); // access length
        CPMMapUtil.setUpFiniteVehicleSpawnPoint(map, 2);
        this.sim = new CPMAutoDriverSimulator(map);
        this.simThread = new TestSimThread(sim);
    }

    @Test
    public void testToFindBug() throws Exception {

        // Run the simulation until there are 2 vehicles parked.
        try {
            simThread.start();
            while (sim.getNumCompletedVehicles() != 2) {
                simThread.run();
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("RuntimeException thrown: " + ". Message was: " + e.getMessage());
        }

        assertTrue(sim.getMap() instanceof CPMCarParkWithStatus);
    }

}
