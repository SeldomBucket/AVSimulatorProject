package aim4.cpm.vehicle;

import aim4.map.cpm.CPMCarParkWithStatus;
import aim4.map.cpm.CPMMapUtil;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import util.TestSimThread;

import java.awt.geom.Point2D;

import static org.junit.Assert.assertTrue;

/**
 * Created by Becci on 04-Apr-17.
 */
public class DistanceTravelledTests {
    CPMCarParkWithStatus map;
    TestSimThread simThread;
    CPMAutoDriverSimulator sim;

    @Before
    public void setUp() {
        /**
         * Set up a map where 1 vehicle is spawned in a car
         * park with one parking lane.
         * */

        this.map = new CPMCarParkWithStatus(4, // laneWidth
                10.0, // speedLimit
                0.0, // initTime
                1, // numberOfParkingLanes
                20, // parkingLength
                5); // access length
        CPMMapUtil.setUpFiniteVehicleSpawnPoint(map, 1, 0.28);
        this.sim = new CPMAutoDriverSimulator(map);
        this.simThread = new TestSimThread(sim);
    }

    @Test
    public void testEstimatedDistanceTravelled() throws Exception {

        // Run the simulation until 1 vehicle has completed.
        try {
            simThread.start();
            while (sim.getNumCompletedVehicles() != 1) {
                simThread.run();
            }
            simThread.pause();
        } catch(RuntimeException e) {
            throw new RuntimeException("RuntimeException thrown: " + ". Message was: " + e.getMessage());
        }

        assertTrue(sim.getMap() instanceof CPMCarParkWithStatus);


    }

    @After
    public void tearDown() throws Exception {
        this.simThread.terminate();
    }
}
