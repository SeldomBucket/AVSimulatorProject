package aim4.cpm;

import aim4.config.SimConfig;
import aim4.map.cpm.CPMCarParkWithStatus;
import aim4.map.cpm.CPMMapUtil;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;
import javafx.util.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import util.TestSimThread;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.csvreader.CsvWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Tests to check that vehicles are spawned at the right time.
 */
public class SpawnTests {

    CPMCarParkWithStatus map;
    TestSimThread simThread;
    CPMAutoDriverSimulator sim;

    @Before
    public void setUp() {

    }

    @Test
    public void testNoRoomNoSpawn() throws Exception {
        /**
         * Set up a map where there is no room to park.
         * */

        this.map = new CPMCarParkWithStatus(4, // laneWidth
                10.0, // speedLimit
                0.0, // initTime
                1, // numberOfParkingLanes
                1, // parkingLength
                5); // access length
        CPMMapUtil.setUpInfiniteSingleSpecVehicleSpawnPoint(map, 0.28);
        this.sim = new CPMAutoDriverSimulator(map);
        this.simThread = new TestSimThread(sim);

        // Run the simulation for a period of time.
        try {
            int count = 1000000000;
            simThread.start();
            while (count != 0) {
                simThread.run();
                count--;
            }
        } catch(RuntimeException e) {
            throw new RuntimeException("RuntimeException thrown: " + ". Message was: " + e.getMessage());
        }

        assertTrue(sim.getMap() instanceof CPMCarParkWithStatus);

        // There should be 0 vehicles registered with the status monitor.
        assertTrue(sim.getMap().getStatusMonitor().getVehicles().size() == 0);

        // There should be 0 vehicles registered as parked with the map.
        assertTrue(sim.getParkedVehicles().size() == 0);

        // There should be no vehicle on the map
        assertTrue(map.getVehicles().size() == 0);
    }

    @Test
    public void testNotWideEnoughNoSpawn() throws Exception {
        /**
         * Set up a map where the lanes are not wide enough for any vehicle.
         * */

        this.map = new CPMCarParkWithStatus(1, // laneWidth
                10.0, // speedLimit
                0.0, // initTime
                1, // numberOfParkingLanes
                1, // parkingLength
                5); // access length
        CPMMapUtil.setUpInfiniteSingleSpecVehicleSpawnPoint(map, 0.28);
        this.sim = new CPMAutoDriverSimulator(map);
        this.simThread = new TestSimThread(sim);

        // Run the simulation for a period of time.
        try {
            int count = 1000000000;
            simThread.start();
            while (count != 0) {
                simThread.run();
                count--;
            }
        } catch(RuntimeException e) {
            throw new RuntimeException("RuntimeException thrown: " + ". Message was: " + e.getMessage());
        }

        assertTrue(sim.getMap() instanceof CPMCarParkWithStatus);

        // There should be 0 vehicles registered with the status monitor.
        assertTrue(sim.getMap().getStatusMonitor().getVehicles().size() == 0);

        // There should be 0 vehicles registered as parked with the map.
        assertTrue(sim.getParkedVehicles().size() == 0);

        // There should be no vehicle on the map
        assertTrue(map.getVehicles().size() == 0);
    }

    @Test
    public void testOneSpawn() throws Exception {

        /**
         * Set up a map where there room for one vehicle to park.
         * */

        this.map = new CPMCarParkWithStatus(4, // laneWidth
                10.0, // speedLimit
                0.0, // initTime
                1, // numberOfParkingLanes
                5, // parkingLength
                5); // access length
        Pair<Boolean, String> useCsvPair = new Pair<Boolean, String>(true, "src\\test\\aim4\\cpm\\testfiles\\2vehicles1spawn.csv");
        CPMMapUtil.setUpSpecificSingleSpecVehicleSpawnPoint(map, useCsvPair);
        this.sim = new CPMAutoDriverSimulator(map);
        this.simThread = new TestSimThread(sim);

        // Run the simulation for a period of time.
        try {
            int count = 700000000;
            simThread.start();
            while (count != 0) {
                simThread.run();
                count--;
            }
        } catch(RuntimeException e) {
            throw new RuntimeException("RuntimeException thrown: " + ". Message was: " + e.getMessage());
        }

        assertTrue(sim.getMap() instanceof CPMCarParkWithStatus);

        // There should be 1 vehicle registered with the status monitor.
        assertTrue(sim.getMap().getStatusMonitor().getVehicles().size() == 1);

        // There should be 1 vehicle on the map
        assertTrue(map.getVehicles().size() == 1);
    }

    @Test
    public void testSpawnWithValidSecondsStringCsvSingleSpec() throws Exception {
        // Set up a map with space for 2 vehicles and with spawn points to use the csv file
        this.map = new CPMCarParkWithStatus(4, // laneWidth
                5.0, // speedLimit
                0.0, // initTime
                1, // numberOfParkingLanes
                20, // parkingLength
                5); // access length
        Pair<Boolean, String> useCsvPair =
                new Pair<Boolean, String>(true, "src\\test\\aim4\\cpm\\testfiles\\validSeconds.csv");
        CPMMapUtil.setUpSpecificSingleSpecVehicleSpawnPoint(map, useCsvPair);
        this.sim = new CPMAutoDriverSimulator(map);
        this.simThread = new TestSimThread(sim);

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
        assertTrue(map.getVehicles().size() == 2);

        for (CPMBasicAutoVehicle vehicle : map.getVehicles()) {
            // Check that the vehicles have the correct parking time
            assertTrue(vehicle.getParkingTime() == 20000);

            // Check that the vehicles have the entry time (within 30 seconds of spawning)
            if (vehicle.getVIN() == 1000) {
                List<Double> entryTimes = map.getEntryDataCollectionLine().getTimes(1000);
                assertTrue(entryTimes.size() == 1);
                double expectedEntryTime = 10.0;
                assertEquals(expectedEntryTime, entryTimes.get(0), 30.0);
            }

            if (vehicle.getVIN() == 1001) {
                List<Double> entryTimes = map.getEntryDataCollectionLine().getTimes(1001);
                assertTrue(entryTimes.size() == 1);
                double expectedEntryTime = 20.0;
                assertEquals(expectedEntryTime, entryTimes.get(0), 30.0);
            }
        }
    }

    @Test
    public void testSpawnWithValidTimesStringCsvSingleSpec() throws Exception {
        // Set up a map with space for 3 vehicles and with spawn points to use the csv file
        this.map = new CPMCarParkWithStatus(4, // laneWidth
                5.0, // speedLimit
                0.0, // initTime
                1, // numberOfParkingLanes
                25, // parkingLength
                5); // access length
        Pair<Boolean, String> useCsvPair =
                new Pair<Boolean, String>(true, "src\\test\\aim4\\cpm\\testfiles\\validTimes.csv");
        CPMMapUtil.setUpSpecificSingleSpecVehicleSpawnPoint(map, useCsvPair);
        this.sim = new CPMAutoDriverSimulator(map);
        this.simThread = new TestSimThread(sim);

        // Run the simulation until there are 3 vehicles parked.
        try {
            simThread.start();
            while (sim.getParkedVehicles().size() != 3) {
                simThread.run();
            }
        } catch(RuntimeException e) {
            throw new RuntimeException("RuntimeException thrown: " + ". Message was: " + e.getMessage());
        }

        assertTrue(sim.getMap() instanceof CPMCarParkWithStatus);
        assertTrue(map.getVehicles().size() == 3);

        for (CPMBasicAutoVehicle vehicle : map.getVehicles()) {
            // Check that the vehicles have the entry time (within 30 seconds of spawning) and correct parking time
            if (vehicle.getVIN() == 1000) {
                double expectedParkingTime = 23961.0;
                assertEquals(expectedParkingTime, vehicle.getParkingTime(), 1.0);

                List<Double> entryTimes = map.getEntryDataCollectionLine().getTimes(1000);
                assertTrue(entryTimes.size() == 1);
                double expectedEntryTime = 533.0;
                assertEquals(expectedEntryTime, entryTimes.get(0), 30.0);
            }

            if (vehicle.getVIN() == 1001) {
                double expectedParkingTime = 25280.0;
                assertEquals(expectedParkingTime, vehicle.getParkingTime(), 1.0);

                List<Double> entryTimes = map.getEntryDataCollectionLine().getTimes(1001);
                assertTrue(entryTimes.size() == 1);
                double expectedEntryTime = 3259.0;
                assertEquals(expectedEntryTime, entryTimes.get(0), 30.0);
            }

            if (vehicle.getVIN() == 1002) {
                double expectedParkingTime = 80.0;
                assertEquals(expectedParkingTime, vehicle.getParkingTime(), 1.0);

                List<Double> entryTimes = map.getEntryDataCollectionLine().getTimes(1002);
                assertTrue(entryTimes.size() == 1);
                double expectedEntryTime = 3619.0;
                assertEquals(expectedEntryTime, entryTimes.get(0), 30.0);
            }
        }
    }

    @Test
    public void testSpawnWithValidSecondsStringCsvRandomSpec() throws Exception {
        // Set up a map with space for 2 vehicles and with spawn points to use the csv file
        this.map = new CPMCarParkWithStatus(4, // laneWidth
                5.0, // speedLimit
                0.0, // initTime
                1, // numberOfParkingLanes
                20, // parkingLength
                5); // access length
        Pair<Boolean, String> useCsvPair =
                new Pair<Boolean, String>(true, "src\\test\\aim4\\cpm\\testfiles\\validSeconds.csv");
        CPMMapUtil.setUpSpecificRandomSpecVehicleSpawnPoint(map, useCsvPair);
        this.sim = new CPMAutoDriverSimulator(map);
        this.simThread = new TestSimThread(sim);

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
        assertTrue(map.getVehicles().size() == 2);

        for (CPMBasicAutoVehicle vehicle : map.getVehicles()) {
            // Check that the vehicles have the correct parking time
            assertTrue(vehicle.getParkingTime() == 20000);

            // Check that the vehicles have the entry time (within 30 seconds of spawning)
            if (vehicle.getVIN() == 1000) {
                List<Double> entryTimes = map.getEntryDataCollectionLine().getTimes(1000);
                assertTrue(entryTimes.size() == 1);
                double expectedEntryTime = 10.0;
                assertEquals(expectedEntryTime, entryTimes.get(0), 30.0);
            }

            if (vehicle.getVIN() == 1001) {
                List<Double> entryTimes = map.getEntryDataCollectionLine().getTimes(1001);
                assertTrue(entryTimes.size() == 1);
                double expectedEntryTime = 20.0;
                assertEquals(expectedEntryTime, entryTimes.get(0), 30.0);
            }
        }
    }

    @Test
    public void testSpawnWithValidTimesStringCsvRandomSpec() throws Exception {
        // Set up a map with space for 3 vehicles and with spawn points to use the csv file
        this.map = new CPMCarParkWithStatus(4, // laneWidth
                5.0, // speedLimit
                0.0, // initTime
                1, // numberOfParkingLanes
                25, // parkingLength
                5); // access length
        Pair<Boolean, String> useCsvPair =
                new Pair<Boolean, String>(true, "src\\test\\aim4\\cpm\\testfiles\\validTimes.csv");
        CPMMapUtil.setUpSpecificRandomSpecVehicleSpawnPoint(map, useCsvPair);
        this.sim = new CPMAutoDriverSimulator(map);
        this.simThread = new TestSimThread(sim);

        // Run the simulation until there are 3 vehicles parked.
        try {
            simThread.start();
            while (sim.getParkedVehicles().size() != 3) {
                simThread.run();
            }
        } catch(RuntimeException e) {
            throw new RuntimeException("RuntimeException thrown: " + ". Message was: " + e.getMessage());
        }

        assertTrue(sim.getMap() instanceof CPMCarParkWithStatus);
        assertTrue(map.getVehicles().size() == 3);

        for (CPMBasicAutoVehicle vehicle : map.getVehicles()) {
            // Check that the vehicles have the entry time (within 30 seconds of spawning) and correct parking time
            if (vehicle.getVIN() == 1000) {
                double expectedParkingTime = 23961.0;
                assertEquals(expectedParkingTime, vehicle.getParkingTime(), 1.0);

                List<Double> entryTimes = map.getEntryDataCollectionLine().getTimes(1000);
                assertTrue(entryTimes.size() == 1);
                double expectedEntryTime = 533.0;
                assertEquals(expectedEntryTime, entryTimes.get(0), 30.0);
            }

            if (vehicle.getVIN() == 1001) {
                double expectedParkingTime = 25280.0;
                assertEquals(expectedParkingTime, vehicle.getParkingTime(), 1.0);

                List<Double> entryTimes = map.getEntryDataCollectionLine().getTimes(1001);
                assertTrue(entryTimes.size() == 1);
                double expectedEntryTime = 3259.0;
                assertEquals(expectedEntryTime, entryTimes.get(0), 30.0);
            }

            if (vehicle.getVIN() == 1002) {
                double expectedParkingTime = 80.0;
                assertEquals(expectedParkingTime, vehicle.getParkingTime(), 1.0);

                List<Double> entryTimes = map.getEntryDataCollectionLine().getTimes(1002);
                assertTrue(entryTimes.size() == 1);
                double expectedEntryTime = 3619.0;
                assertEquals(expectedEntryTime, entryTimes.get(0), 30.0);
            }
        }
    }

    @After
    public void tearDown() throws Exception {
        this.simThread.terminate();
    }
}
