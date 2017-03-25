package aim4.cpm.vehicle;

import aim4.map.cpm.CPMMapUtil;
import aim4.map.cpm.testmaps.CPMCarParkWithStatus;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import util.sim.TestSimThread;

import static org.junit.Assert.*;

public class CPMBasicAutoVehicleTest {
    CPMCarParkWithStatus map;
    TestSimThread simThread;
    CPMAutoDriverSimulator sim;

    @Before
    public void setUp() {
        this.map = new CPMCarParkWithStatus(4, // laneWidth
                10.0, // speedLimit
                0.0, // initTime
                2, // numberOfParkingLanes
                20, // parkingLength
                5); // access length
        CPMMapUtil.setUpOneVehicleSpawnPoint(map);
        this.sim = new CPMAutoDriverSimulator(map);
        this.simThread = new TestSimThread(sim);
    }


    @Test
    public void testRunSimulator() throws Exception {
        int stepCount = 4000;
        for(int i = 0; i < stepCount; i++){
            try {
                sim.step(i);
            } catch(RuntimeException e) {
                throw new RuntimeException("RuntimeException thrown at simStep: " + i + ". Message was: " + e.getMessage());
            }
        }
        assertTrue(sim.getMap() instanceof CPMCarParkWithStatus);
        assertTrue(((CPMCarParkWithStatus) sim.getMap()).getStatusMonitor().getVehicles().size() > 0);
    }

    @After
    public void tearDown() throws Exception {
        this.simThread.terminate();
    }



}