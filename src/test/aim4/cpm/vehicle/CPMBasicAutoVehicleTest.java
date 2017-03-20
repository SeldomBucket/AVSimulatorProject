package aim4.cpm.vehicle;

import aim4.map.cpm.CPMMapUtil;
import aim4.map.cpm.testmaps.CPMCarParkWithStatus;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import util.TestSimThread;
import java.awt.geom.Point2D;
import static org.junit.Assert.*;

public class CPMBasicAutoVehicleTest {
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
        CPMMapUtil.setUpFiniteVehicleSpawnPoint(map, 2);
        this.sim = new CPMAutoDriverSimulator(map);
        this.simThread = new TestSimThread(sim);
    }

    @Test
    public void testNoTwoVehiclesInSameSpace() throws Exception {

        // Run the simulation until there are 2 vehicles parked.
        try {
            simThread.start();
            while (sim.getParkedVehicles().size() != 2) {
                simThread.run();
            }
        } catch(RuntimeException e) {
            throw new RuntimeException("RuntimeException thrown: " + ". Message was: " + e.getMessage());
        }

        assertTrue(sim.getMap() instanceof CPMCarParkWithStatus);

        // There should be 2 vehicles registered with the status monitor.
        assertTrue(sim.getMap().getStatusMonitor().getVehicles().size() == 2);

        // There should be 2 different vehicles registered as parked with the map.
        assertTrue(sim.getParkedVehicles().get(0) != sim.getParkedVehicles().get(1));

        // The vehicles should not be parked in the same place.
        Point2D p1 = sim.getParkedVehicles().get(0).getPosition();
        Point2D p2 = sim.getParkedVehicles().get(1).getPosition();
        assert(p1 != p2);

        // Find which car is parked at the front to ensure cars not touching
        CPMBasicAutoVehicle firstParkedCar, secondParkedCar;
        if (p1.getX() < p2.getX()) {
            secondParkedCar = sim.getParkedVehicles().get(0);
            firstParkedCar = sim.getParkedVehicles().get(1);
        } else {
            secondParkedCar = sim.getParkedVehicles().get(1);
            firstParkedCar = sim.getParkedVehicles().get(0);
        }
        Point2D pointAtBack = firstParkedCar.getPointAtRear();
        Point2D pointAtFront = secondParkedCar.getPointAtMiddleFront(0.001);
        assertTrue(pointAtBack.getX() - pointAtFront.getX() > 0 );
    }

    @After
    public void tearDown() throws Exception {
        this.simThread.terminate();
    }
}