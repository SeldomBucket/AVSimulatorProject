package aim4.cpm.map.connections;

import aim4.driver.cpm.CPMCoordinator;
import aim4.map.cpm.CPMMapUtil;
import aim4.map.cpm.testmaps.CPMMapOneCorner;
import aim4.map.cpm.testmaps.CPMMapParkingLane;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;
import javafx.util.Pair;
import org.junit.After;
import org.junit.Test;
import util.cpm.MockCPMDriver;
import util.cpm.SimulatorForMapParkingLane;
import util.sim.TestSimThread;

import static org.junit.Assert.assertTrue;

/**
 * Validate the behaviour of vehicles around a corner.
 */
public class CornerBehaviourTest {
    CPMMapOneCorner map;
    TestSimThread simThread;
    SimulatorForMapParkingLane sim;

    @Test
    public void testVehiclePositionThroughCorner() throws Exception {

        /*// Set up a map with one corner.
        this.map = new CPMMapOneCorner(2, // laneWidth
                10.0, // speedLimit
                0.0); // initTime
        Pair<Boolean, String> useCsvPair =
                new Pair<Boolean, String>(true, "src\\test\\aim4\\cpm\\testfiles\\oneVehicleParks30Seconds.csv");
        CPMMapUtil.setUpSpecificSingleSpecVehicleSpawnPoint(map, useCsvPair);
        this.sim = new SimulatorForMapParkingLane(map);
        this.simThread = new TestSimThread(sim);

        try {
            simThread.start();

            // Run until vehicle crosses entry DCL
            while (map.getDataCollectionLines().get(0).getAllVIN().size() != 1) {
                simThread.run();
            }

            // Ensure driver and vehicle have the correct attributes
            CPMBasicAutoVehicle vehicle = map.getVehicles().get(0);
            MockCPMDriver driver = (MockCPMDriver)vehicle.getDriver();
            assertTrue(driver.getParkingStatus() == CPMCoordinator.ParkingStatus.PARKING);
            assertTrue(driver.getDrivingStatus() == CPMCoordinator.DrivingState.TRAVERSING_PARKING_LANE);

        } catch(RuntimeException e) {
            throw new RuntimeException("RuntimeException thrown: " + ". Message was: " + e.getMessage());
        }*/
    }

    @After
    public void tearDown() throws Exception {
        this.simThread.terminate();
    }
}
