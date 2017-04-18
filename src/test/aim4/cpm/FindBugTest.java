package aim4.cpm;

import aim4.driver.cpm.CPMCoordinator.*;
import aim4.map.connections.Corner;
import aim4.map.cpm.CPMExitDataCollectionLine;
import aim4.map.cpm.CPMMapUtil;
import aim4.map.cpm.CPMCarParkWithStatus;
import aim4.map.cpm.testmaps.CPMMapOneCorner;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;
import javafx.util.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import util.cpm.MockCPMDriver;
import util.cpm.simulators.SimulatorForCarParkWithStatus;
import util.cpm.simulators.SimulatorForMapOneCorner;
import util.sim.TestSimThread;

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

    }

    @Test
    public void testFindBugWith2Vehicles() throws Exception {

        this.map = new CPMCarParkWithStatus(2, // laneWidth
                10.0, // speedLimit
                0.0, // initTime
                1, // numberOfParkingLanes
                20, // parkingLength
                5); // access length
        Pair<Boolean, String> useCsvPair =
                new Pair<Boolean, String>(true, "src\\test\\aim4\\cpm\\testfiles\\firstParksLongerThanSecond.csv");
        CPMMapUtil.setUpSpecificSingleSpecVehicleSpawnPoint(map, useCsvPair);
        this.sim = new SimulatorForCarParkWithStatus(map);
        this.simThread = new TestSimThread(sim);

        try {
            simThread.start();

            // Run until vehicles cross entry DCL
            while (map.getDataCollectionLines().get(0).getAllVIN().size() != 2) {
                simThread.run();
            }

            // Get the vehicles and drivers
            CPMBasicAutoVehicle firstVehicle = sim.getVinToVehicles().get(1000);
            MockCPMDriver firstDriver = (MockCPMDriver)firstVehicle.getDriver();
            CPMBasicAutoVehicle secondVehicle = sim.getVinToVehicles().get(1001);
            MockCPMDriver secondDriver = (MockCPMDriver)secondVehicle.getDriver();

            // Run until first vehicle is traversing the junction
            while (((MockCPMDriver) firstVehicle.getDriver()).getDrivingState() != DrivingState.TRAVERSING_JUNCTION){
                simThread.run();
            }

            while (sim.getNumCompletedVehicles() != 2) {
                simThread.run();
            }

        } catch(RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException("RuntimeException thrown: " + ". Message was: " + e.getMessage());
        }
    }

    @Test
    public void testFindBugWithDatasetFile() throws Exception {

        this.map = new CPMCarParkWithStatus(2, // laneWidth
                5.0, // speedLimit
                0.0, // initTime
                1, // numberOfParkingLanes
                20, // parkingLength
                1); // access length
        Pair<Boolean, String> useCsvPair =
                new Pair<Boolean, String>(true, "C:\\Users\\Becci\\Google Drive\\Documents\\York\\Year 3\\Project\\Design & Imp\\ferreiraDataset.csv");
        CPMMapUtil.setUpSpecificSingleSpecVehicleSpawnPoint(map, useCsvPair);
        this.sim = new SimulatorForCarParkWithStatus(map);
        this.simThread = new TestSimThread(sim);

        try {
            simThread.start();

            while (sim.getNumCompletedVehicles() != 1) {
                simThread.run();
            }

            assert(sim.getParkedVehicles().size() == 2);

        } catch(RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException("RuntimeException thrown: " + ". Message was: " + e.getMessage());
        }
    }



    @After
    public void tearDown() throws Exception {
        this.simThread.terminate();
    }

}
