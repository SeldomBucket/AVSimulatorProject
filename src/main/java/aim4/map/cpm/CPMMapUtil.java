package aim4.map.cpm;

import aim4.config.SimConfig;
import aim4.driver.AutoDriver;
import aim4.driver.cpm.CPMV2VDriver;
import aim4.map.cpm.CPMSpawnPoint.*;
import aim4.map.lane.Lane;
import aim4.util.Util;
import aim4.vehicle.VehicleSpec;
import aim4.vehicle.VehicleSpecDatabase;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for CPM maps.
 */
public class CPMMapUtil {

    /////////////////////////////////
    // NESTED CLASSES
    /////////////////////////////////
    /**
     * The spec generator that generates just one vehicle in the entire
     * simulation.
     */
    public static class OnlyOneSpawnSpecGenerator implements CPMSpawnSpecGenerator {
        /** The vehicle specification */
        private VehicleSpec vehicleSpec;
        /** Whether the spec has been generated */
        private boolean isDone;

        /**
         * Create a spec generator that generates just one vehicle in the entire
         * simulation.
         */
        public OnlyOneSpawnSpecGenerator() {
            vehicleSpec = VehicleSpecDatabase.getVehicleSpecByName("COUPE");
            isDone = false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<CPMSpawnSpec> act(CPMSpawnPoint spawnPoint, double timeStep) {
            List<CPMSpawnSpec> result = new ArrayList<CPMSpawnSpec>(1);
            if (!isDone) {
                isDone = true;
                // TODO CPM need a way of generating this to represent the data by ferreira
                double parkingTime = 20000.0;
                result.add(new CPMSpawnSpec(spawnPoint.getCurrentTime(),vehicleSpec, parkingTime));
                System.out.println("Vehicle spawned!");
            }
            return result;
        }
    }

    /**
     * The spec generator that generates a finite number of vehicles of the same spec.
     */
    public static class FiniteSpawnSpecGenerator implements CPMSpawnSpecGenerator {
        /** The vehicle specification */
        private VehicleSpec vehicleSpec;
        /** Number of vehicles to be spawned. */
        private int numberOfVehiclesToSpawn;
        /** Number vehicles that have been spawned so far. */
        private int numberOfSpawnedVehicles;
        /** Whether the spec has been generated */
        private boolean isDone;
        /** The probability of generating a vehicle in each spawn time step */
        private double spawnProbability;

        /**
         * Create a spec generator that generates the specified number
         * of vehicles. This only spawns one VehicleSpec at the moment.
         */
        public FiniteSpawnSpecGenerator(int numberOfVehiclesToSpawn) {
            vehicleSpec = VehicleSpecDatabase.getVehicleSpecByName("COUPE");
            this.numberOfVehiclesToSpawn = numberOfVehiclesToSpawn;
            this.numberOfSpawnedVehicles = 0;
            isDone = false;
            spawnProbability = 0.28 * SimConfig.SPAWN_TIME_STEP; // TODO CPM should get trafficLevel from somewhere
            // Cannot generate more than one vehicle in each spawn time step
            assert spawnProbability <= 1.0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<CPMSpawnSpec> act(CPMSpawnPoint spawnPoint, double timeStep) {
            List<CPMSpawnSpec> result = new ArrayList<CPMSpawnSpec>(1);
            if (numberOfSpawnedVehicles == numberOfVehiclesToSpawn) {
                isDone = true;
            }
            if (!isDone) {
                double initTime = spawnPoint.getCurrentTime();
                for(double time = initTime; time < initTime + timeStep;
                    time += SimConfig.SPAWN_TIME_STEP) {
                    if (Util.random.nextDouble() < spawnProbability) {
                        // TODO CPM need a way of generating this to represent the data by ferreira
                        double parkingTime = 20000.0;
                        result.add(new CPMSpawnSpec(spawnPoint.getCurrentTime(),vehicleSpec, parkingTime));
                        numberOfSpawnedVehicles += 1;
                        System.out.println("Vehicle spawned!");
                    }
                }
            }
            return result;
        }
    }

    /**
     * The spec generator that continuously generates vehicles of the same spec.
     */
    public static class InfiniteSpawnSpecGenerator implements CPMSpawnSpecGenerator {
        /** The vehicle specification */
        private VehicleSpec vehicleSpec;
        /** The probability of generating a vehicle in each spawn time step */
        private double spawnProbability;

        /**
         * Create a spec generator that generates the specified number
         * of vehicles. This only spawns one VehicleSpec at the moment.
         */
        public InfiniteSpawnSpecGenerator() {
            vehicleSpec = VehicleSpecDatabase.getVehicleSpecByName("COUPE");
            spawnProbability = 0.28 * SimConfig.SPAWN_TIME_STEP; // TODO CPM should get trafficLevel from somewhere
            // Cannot generate more than one vehicle in each spawn time step
            assert spawnProbability <= 1.0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<CPMSpawnSpec> act(CPMSpawnPoint spawnPoint, double timeStep) {
            List<CPMSpawnSpec> result = new ArrayList<CPMSpawnSpec>(1);
            double initTime = spawnPoint.getCurrentTime();
            for(double time = initTime; time < initTime + timeStep;
                time += SimConfig.SPAWN_TIME_STEP) {
                if (Util.random.nextDouble() < spawnProbability) {
                    // TODO CPM need a way of generating this to represent the data by ferreira
                    double parkingTime = 20000.0;
                    result.add(new CPMSpawnSpec(spawnPoint.getCurrentTime(),vehicleSpec, parkingTime));
                    System.out.println("Vehicle spawned!");
                }
            }
            return result;
        }
    }

    /**
     * The spec generator that creates scenario where relocation is required.
     * 2 vehicles of the same spec are spawned, where the 2nd will need to
     * exit the car park before the first. This will require the first vehicle
     * to relocate.
     */
    public static class SimpleRelocateSpawnSpecGenerator implements CPMSpawnSpecGenerator {
        /** The vehicle specification */
        private VehicleSpec vehicleSpec;
        /** The probability of generating a vehicle in each spawn time step */
        private double spawnProbability;
        /** Number of vehicles to be spawned. */
        private int numberOfVehiclesToSpawn;
        /** Number vehicles that have been spawned so far. */
        private int numberOfSpawnedVehicles;
        /** Whether the spec has been generated */
        private boolean isDone;

        /**
         * Create a spec generator that creates a simple relocate scenario.
         */
        public SimpleRelocateSpawnSpecGenerator() {
            this.numberOfVehiclesToSpawn = 2;
            this.numberOfSpawnedVehicles = 0;
            vehicleSpec = VehicleSpecDatabase.getVehicleSpecByName("COUPE");
            spawnProbability = 0.28 * SimConfig.SPAWN_TIME_STEP; // TODO CPM should get trafficLevel from somewhere
            // Cannot generate more than one vehicle in each spawn time step
            assert spawnProbability <= 1.0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<CPMSpawnSpec> act(CPMSpawnPoint spawnPoint, double timeStep) {
            List<CPMSpawnSpec> result = new ArrayList<CPMSpawnSpec>(1);
            if (numberOfSpawnedVehicles == numberOfVehiclesToSpawn) {
                isDone = true;
            }
            if (!isDone) {
                double initTime = spawnPoint.getCurrentTime();
                for(double time = initTime; time < initTime + timeStep;
                    time += SimConfig.SPAWN_TIME_STEP) {
                    if (Util.random.nextDouble() < spawnProbability) {
                        double parkingTime;
                        if (numberOfSpawnedVehicles == 0) {
                            parkingTime = 20000.0;
                        } else {
                            parkingTime = 10000.0;
                        }
                        result.add(new CPMSpawnSpec(spawnPoint.getCurrentTime(),vehicleSpec, parkingTime));
                        System.out.println("Vehicle spawned!");
                        numberOfSpawnedVehicles += 1;
                    }
                }
            }
            return result;
        }
    }


    public static void setUpOneVehicleSpawnPoint(CPMMap simpleMap){
        // The spawn point will only spawn one vehicle in the whole simulation
        for(CPMSpawnPoint sp : simpleMap.getSpawnPoints()) {
            sp.setVehicleSpecChooser(
                    new OnlyOneSpawnSpecGenerator());
        }
    }

    public static void setUpFiniteVehicleSpawnPoint(CPMMap simpleMap, int numberOfVehiclesToSpawn){
        // The spawn point will only spawn numberOfVehiclesToSpawn, all of the same spec.
        for(CPMSpawnPoint sp : simpleMap.getSpawnPoints()) {
            sp.setVehicleSpecChooser(
                    new FiniteSpawnSpecGenerator(numberOfVehiclesToSpawn));
        }
    }

    public static void setUpInfiniteVehicleSpawnPoint(CPMMap simpleMap){
        // The spawn point will continuously spawn vehicles of the same spec.
        for(CPMSpawnPoint sp : simpleMap.getSpawnPoints()) {
            sp.setVehicleSpecChooser(
                    new InfiniteSpawnSpecGenerator());
        }
    }

    public static void setUpSimpleRelocateSpawnPoint(CPMMap simpleMap){
        // The spawn point will continuously spawn vehicles of the same spec.
        for(CPMSpawnPoint sp : simpleMap.getSpawnPoints()) {
            sp.setVehicleSpecChooser(
                    new SimpleRelocateSpawnSpecGenerator());
        }
    }

    /**
     * Check that the vehicle is still on the map when it should be.
     * @param map   the map we are check the vehicle is still on
     * @param vehiclePosition the current x coordinate of the vehicle
     * @param currentLane the lane the vehicle is currently driving on
     * */
    public static void checkVehicleStillOnMap(CPMMap map,
                                              CPMBasicAutoVehicle vehicle,
                                              Point2D vehiclePosition,
                                              Lane currentLane){
        // For this map, should only drive off the map when it has
        // finished following the exit lane
        double x = vehiclePosition.getX();
        double y = vehiclePosition.getY();

        AutoDriver driver = vehicle.getDriver();

        // If the vehicle is off the map
        if (!map.getDimensions().contains(new Point2D.Double(x, y))){
            // And the vehicle is not on the exit lane
            if (!map.getExitLanes().contains(currentLane)) {
                throw new RuntimeException("Vehicle has driven off the map! Vehicle position " + vehiclePosition
                + ", map dimensions " + map.getDimensions().getMaxX() + "," + map.getDimensions().getMaxY());
            }
        }
    }

}
