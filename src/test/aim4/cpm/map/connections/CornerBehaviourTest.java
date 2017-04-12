package aim4.cpm.map.connections;

import aim4.driver.cpm.CPMCoordinator.*;
import aim4.map.connections.Corner;
import aim4.map.cpm.CPMMapUtil;
import aim4.map.cpm.testmaps.CPMMapOneCorner;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;
import javafx.util.Pair;
import org.junit.After;
import org.junit.Test;
import util.cpm.MockCPMDriver;
import util.cpm.simulators.SimulatorForMapOneCorner;
import util.sim.TestSimThread;

import java.awt.geom.Point2D;

import static org.junit.Assert.assertTrue;

/**
 * Validate the behaviour of vehicles around a corner.
 */
public class CornerBehaviourTest {
    CPMMapOneCorner map;
    TestSimThread simThread;
    SimulatorForMapOneCorner sim;

    @Test
    public void testVehiclePositionThroughCornerWithStatusWaiting() throws Exception {

        // Set up a map with one corner.
        this.map = new CPMMapOneCorner(2, // laneWidth
                10.0, // speedLimit
                0.0); // initTime
        Pair<Boolean, String> useCsvPair =
                new Pair<Boolean, String>(true, "src\\test\\aim4\\cpm\\testfiles\\oneVehicleParks10Seconds.csv");
        CPMMapUtil.setUpSpecificSingleSpecVehicleSpawnPoint(map, useCsvPair);
        this.sim = new SimulatorForMapOneCorner(map, ParkingStatus.WAITING, DrivingState.DEFAULT_DRIVING_BEHAVIOUR);
        this.simThread = new TestSimThread(sim);

        try {
            simThread.start();

            // Run until vehicle crosses entry DCL
            while (map.getDataCollectionLines().get(0).getAllVIN().size() != 1) {
                simThread.run();
            }

            // Get the vehicle
            CPMBasicAutoVehicle vehicle = map.getVehicles().get(0);
            MockCPMDriver driver = (MockCPMDriver)vehicle.getDriver();

            // Run until vehicle is traversing corner
            while (driver.getDrivingState() != DrivingState.TRAVERSING_CORNER){
                simThread.run();
            }

            // While traversing corner, check position of vehicle in relation to the corner
            Corner corner = map.getCorners().get(0);
            Point2D cornerCentroid = corner.getCentroid();
            while (driver.getDrivingState() == DrivingState.TRAVERSING_CORNER) {
                double xPos = vehicle.getPosition().getX();
                double yPos = vehicle.getPosition().getY();
                assertTrue(xPos < cornerCentroid.getX());
                // assertTrue(yPos < cornerCentroid.getY()); // TODO CPM This jumps up the tiniest amount, why?
                simThread.run();
            }

        } catch(RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException("RuntimeException thrown: " + ". Message was: " + e.getMessage());
        }
    }

    @Test
    public void testVehiclePositionThroughCornerWithStatusParking() throws Exception {

        // Set up a map with one corner.
        this.map = new CPMMapOneCorner(2, // laneWidth
                10.0, // speedLimit
                0.0); // initTime
        Pair<Boolean, String> useCsvPair =
                new Pair<Boolean, String>(true, "src\\test\\aim4\\cpm\\testfiles\\oneVehicleParks10Seconds.csv");
        CPMMapUtil.setUpSpecificSingleSpecVehicleSpawnPoint(map, useCsvPair);
        this.sim = new SimulatorForMapOneCorner(map, ParkingStatus.PARKING, DrivingState.DEFAULT_DRIVING_BEHAVIOUR);
        this.simThread = new TestSimThread(sim);

        try {
            simThread.start();

            // Run until vehicle crosses entry DCL
            while (map.getDataCollectionLines().get(0).getAllVIN().size() != 1) {
                simThread.run();
            }

            // Get the vehicle
            CPMBasicAutoVehicle vehicle = map.getVehicles().get(0);
            MockCPMDriver driver = (MockCPMDriver)vehicle.getDriver();

            // Run until vehicle is traversing corner
            while (driver.getDrivingState() != DrivingState.TRAVERSING_CORNER){
                simThread.run();
            }

            // While traversing corner, check position of vehicle in relation to the corner
            Corner corner = map.getCorners().get(0);
            Point2D cornerCentroid = corner.getCentroid();
            while (driver.getDrivingState() == DrivingState.TRAVERSING_CORNER) {
                double xPos = vehicle.getPosition().getX();
                double yPos = vehicle.getPosition().getY();
                assertTrue(xPos < cornerCentroid.getX());
                // assertTrue(yPos < cornerCentroid.getY()); // TODO CPM This jumps up the tiniest amount, why?
                simThread.run();
            }

        } catch(RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException("RuntimeException thrown: " + ". Message was: " + e.getMessage());
        }
    }

    @Test
    public void testVehiclePositionThroughCornerWithStatusRelocating() throws Exception {

        // Set up a map with one corner.
        this.map = new CPMMapOneCorner(2, // laneWidth
                10.0, // speedLimit
                0.0); // initTime
        Pair<Boolean, String> useCsvPair =
                new Pair<Boolean, String>(true, "src\\test\\aim4\\cpm\\testfiles\\oneVehicleParks10Seconds.csv");
        CPMMapUtil.setUpSpecificSingleSpecVehicleSpawnPoint(map, useCsvPair);
        this.sim = new SimulatorForMapOneCorner(map, ParkingStatus.RELOCATING, DrivingState.DEFAULT_DRIVING_BEHAVIOUR);
        this.simThread = new TestSimThread(sim);

        try {
            simThread.start();

            // Run until vehicle crosses entry DCL
            while (map.getDataCollectionLines().get(0).getAllVIN().size() != 1) {
                simThread.run();
            }

            // Get the vehicle
            CPMBasicAutoVehicle vehicle = map.getVehicles().get(0);
            MockCPMDriver driver = (MockCPMDriver)vehicle.getDriver();

            // Run until vehicle is traversing corner
            while (driver.getDrivingState() != DrivingState.TRAVERSING_CORNER){
                simThread.run();
            }

            // While traversing corner, check position of vehicle in relation to the corner
            Corner corner = map.getCorners().get(0);
            Point2D cornerCentroid = corner.getCentroid();
            while (driver.getDrivingState() == DrivingState.TRAVERSING_CORNER) {
                double xPos = vehicle.getPosition().getX();
                double yPos = vehicle.getPosition().getY();
                assertTrue(xPos < cornerCentroid.getX());
                // assertTrue(yPos < cornerCentroid.getY()); // TODO CPM This jumps up the tiniest amount, why?
                simThread.run();
            }

        } catch(RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException("RuntimeException thrown: " + ". Message was: " + e.getMessage());
        }
    }

    @Test
    public void testVehiclePositionThroughCornerWithStatusExiting() throws Exception {

        // Set up a map with one corner.
        this.map = new CPMMapOneCorner(2, // laneWidth
                10.0, // speedLimit
                0.0); // initTime
        Pair<Boolean, String> useCsvPair =
                new Pair<Boolean, String>(true, "src\\test\\aim4\\cpm\\testfiles\\oneVehicleParks10Seconds.csv");
        CPMMapUtil.setUpSpecificSingleSpecVehicleSpawnPoint(map, useCsvPair);
        this.sim = new SimulatorForMapOneCorner(map, ParkingStatus.EXIT, DrivingState.DEFAULT_DRIVING_BEHAVIOUR);
        this.simThread = new TestSimThread(sim);

        try {
            simThread.start();

            // Run until vehicle crosses entry DCL
            while (map.getDataCollectionLines().get(0).getAllVIN().size() != 1) {
                simThread.run();
            }

            // Get the vehicle
            CPMBasicAutoVehicle vehicle = map.getVehicles().get(0);
            MockCPMDriver driver = (MockCPMDriver)vehicle.getDriver();

            // Run until vehicle is traversing corner
            while (driver.getDrivingState() != DrivingState.TRAVERSING_CORNER){
                simThread.run();
            }

            // While traversing corner, check position of vehicle in relation to the corner
            Corner corner = map.getCorners().get(0);
            Point2D cornerCentroid = corner.getCentroid();
            while (driver.getDrivingState() == DrivingState.TRAVERSING_CORNER) {
                double xPos = vehicle.getPosition().getX();
                double yPos = vehicle.getPosition().getY();
                assertTrue(xPos < cornerCentroid.getX());
                // assertTrue(yPos < cornerCentroid.getY()); // TODO CPM This jumps up the tiniest amount, why?
                simThread.run();
            }

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
