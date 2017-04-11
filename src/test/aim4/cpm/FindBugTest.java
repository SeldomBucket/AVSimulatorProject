package aim4.cpm;

import aim4.map.cpm.CPMExitDataCollectionLine;
import aim4.map.cpm.CPMMapUtil;
import aim4.map.cpm.CPMCarParkWithStatus;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import util.TestSimThread;

import java.awt.geom.Point2D;

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
                10.0, // speedLimit
                0.0, // initTime
                1, // numberOfParkingLanes
                20, // parkingLength
                5); // access length
        CPMMapUtil.setUpSimpleRelocateSpawnPoint(map, 0.28);
        this.sim = new CPMAutoDriverSimulator(map);
        this.simThread = new TestSimThread(sim);
    }





    @After
    public void tearDown() throws Exception {
        this.simThread.terminate();
    }

}
