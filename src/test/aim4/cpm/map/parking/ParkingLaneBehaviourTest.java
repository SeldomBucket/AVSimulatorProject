package aim4.cpm.map.parking;

import aim4.driver.AutoDriver;
import aim4.driver.cpm.CPMCoordinator;
import aim4.driver.cpm.CPMV2VDriver;
import aim4.map.cpm.CPMMapUtil;
import aim4.map.cpm.testmaps.CPMMapParkingLane;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;
import javafx.util.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import util.cpm.MockCPMBasicAutoVehicle;
import util.cpm.MockCPMDriver;
import util.cpm.SimulatorForMapParkingLane;
import util.sim.TestSimThread;

import java.awt.geom.Point2D;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Becci on 12-Apr-17.
 */
public class ParkingLaneBehaviourTest {
    CPMMapParkingLane map;
    TestSimThread simThread;
    SimulatorForMapParkingLane sim;

    @Before
    public void setUp() {

    }

    @Test
    public void testVehicleSpawnedCorrectlyForTests() throws Exception {

        // Set up a map with one parking lane.
        this.map = new CPMMapParkingLane(2, // laneWidth
                10.0, // speedLimit
                0.0, // initTime
                5, // accessLength
                2, // overlappingRoadWidth
                2); // parkingLaneWidth
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
        }
    }

    @Test
    public void testVehicleStopsBeforeParkingEndPoint() throws Exception {

        // Set up a map with one parking lane.
        this.map = new CPMMapParkingLane(2, // laneWidth
                10.0, // speedLimit
                0.0, // initTime
                5, // accessLength
                2, // overlappingRoadWidth
                2); // parkingLaneWidth
        Pair<Boolean, String> useCsvPair =
                new Pair<Boolean, String>(true, "src\\test\\aim4\\cpm\\testfiles\\oneVehicleParks30Seconds.csv");
        CPMMapUtil.setUpSpecificSingleSpecVehicleSpawnPoint(map, useCsvPair);
        this.sim = new SimulatorForMapParkingLane(map);
        this.simThread = new TestSimThread(sim);

        try {
            simThread.start();

            // Run until the vehicle is parked
            while (sim.getParkedVehicles().size() != 1) {
                simThread.run();
            }

            // Ensure the vehicle is parked behind parking end point
            CPMBasicAutoVehicle vehicle = map.getVehicles().get(0);
            double xPosition = vehicle.getPosition().getX();
            double yPosition = vehicle.getPosition().getY();
            Point2D parkingEndPoint = map.getOnlyParkingLane().getParkingEndPoint();
            assertTrue(xPosition < parkingEndPoint.getX());
            assertTrue(yPosition == parkingEndPoint.getY());

            while (sim.getNumCompletedVehicles() != 1) {
                simThread.run();
            }

            assert(map.getVehicles().size() == 0);


        } catch(RuntimeException e) {
            throw new RuntimeException("RuntimeException thrown: " + ". Message was: " + e.getMessage());
        }
    }

    @Test
    public void testVehicleMovesUpToParkingEndPoint() throws Exception {

        // Set up a map with one parking lane.
        this.map = new CPMMapParkingLane(2, // laneWidth
                10.0, // speedLimit
                0.0, // initTime
                5, // accessLength
                2, // overlappingRoadWidth
                2); // parkingLaneWidth
        Pair<Boolean, String> useCsvPair =
                new Pair<Boolean, String>(true, "src\\test\\aim4\\cpm\\testfiles\\secondParksLongerThanFirst.csv");
        CPMMapUtil.setUpSpecificSingleSpecVehicleSpawnPoint(map, useCsvPair);
        this.sim = new SimulatorForMapParkingLane(map);
        this.simThread = new TestSimThread(sim);

        try {
            simThread.start();

            // Run until both vehicles are parked
            while (sim.getParkedVehicles().size() != 2) {
                simThread.run();
            }

            Map<Integer, CPMBasicAutoVehicle> vinToVehicles = sim.getVinToVehicles();
            CPMBasicAutoVehicle firstVehicle = vinToVehicles.get(1000);
            CPMBasicAutoVehicle secondVehicle = vinToVehicles.get(1001);

            // Ensure the vehicles are not touching
            assertFalse(firstVehicle.getShape().contains(secondVehicle.getPointAtMiddleFront(0.01)));
            // Ensure the second vehicle is parked behind the first vehicle
            assertTrue(firstVehicle.getPosition().getX() > secondVehicle.getPosition().getX());
            assertTrue(firstVehicle.getPosition().getY() == secondVehicle.getPosition().getY());

            // Get the position of the first vehicle for the next part of the test
            double stoppingPointX = firstVehicle.getPosition().getX();
            double stoppingPointY = firstVehicle.getPosition().getY();

            // Run until first vehicle completes
            while (sim.getNumCompletedVehicles() != 1) {
                simThread.run();
            }

            assert(map.getVehicles().size() == 1);
            Point2D secondVehiclePosition = secondVehicle.getPosition();
            // Ensure the second vehicle has moved up to the parking end point (i.e. where the first vehicle was parked)
            assertEquals(stoppingPointX, secondVehiclePosition.getX(), 0.1);
            assertTrue(stoppingPointY == secondVehiclePosition.getY());


        } catch(RuntimeException e) {
            throw new RuntimeException("RuntimeException thrown: " + ". Message was: " + e.getMessage());
        }
    }

    @After
    public void tearDown() throws Exception {
        this.simThread.terminate();
    }
}
