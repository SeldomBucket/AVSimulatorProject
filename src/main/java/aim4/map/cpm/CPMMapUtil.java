package aim4.map.cpm;

import aim4.config.SimConfig;
import aim4.map.cpm.CPMSpawnPoint.*;
import aim4.map.lane.Lane;
import aim4.util.Util;
import aim4.vehicle.VehicleSpec;
import aim4.vehicle.VehicleSpecDatabase;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Utility class for CPM maps.
 */
public class CPMMapUtil {

    /**
     * The different spawn specification types.
     */
    public enum SpawnSpecType {
        /** Single - all vehicles spawned will have the same specification */
        SINGLE,
        /** Random - all vehicles spawned will have a randomly selected specification */
        RANDOM
    }

    /////////////////////////////////
    // NESTED CLASSES
    /////////////////////////////////

    /**
     * The spec generator that generates a finite number of vehicles of the same spec.
     */
    public static class FiniteSpawnSingleSpecGenerator implements CPMSpawnSpecGenerator {
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
         * of vehicles. This only spawns one VehicleSpec.
         */
        public FiniteSpawnSingleSpecGenerator(int numberOfVehiclesToSpawn, double trafficLevel) {
            vehicleSpec = VehicleSpecDatabase.getVehicleSpecByName("COUPE");
            this.numberOfVehiclesToSpawn = numberOfVehiclesToSpawn;
            this.numberOfSpawnedVehicles = 0;
            isDone = false;
            spawnProbability = trafficLevel * SimConfig.SPAWN_TIME_STEP;
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
                        double parkingTime = generateParkingTime();
                        result.add(new CPMSpawnSpec(spawnPoint.getCurrentTime(),vehicleSpec, parkingTime));
                        numberOfSpawnedVehicles += 1;
                        System.out.println("Vehicle spawned!");
                    }
                }
            }
            return result;
        }

        public double generateParkingTime(){
            return 20000.0;
        }
    }

    /**
     * The spec generator that continuously generates vehicles of the same spec.
     */
    public static class InfiniteSpawnSingleSpecGenerator implements CPMSpawnSpecGenerator {
        /** The vehicle specification */
        private VehicleSpec vehicleSpec;
        /** The probability of generating a vehicle in each spawn time step */
        private double spawnProbability;

        /**
         * Create a spec generator that infinitely generates vehicles of the same spec.
         */
        public InfiniteSpawnSingleSpecGenerator(double trafficLevel) {
            vehicleSpec = VehicleSpecDatabase.getVehicleSpecByName("COUPE");
            spawnProbability = trafficLevel * SimConfig.SPAWN_TIME_STEP;
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
                    double parkingTime = generateParkingTime();
                    result.add(new CPMSpawnSpec(spawnPoint.getCurrentTime(),vehicleSpec, parkingTime));
                    System.out.println("Vehicle spawned!");
                }
            }
            return result;
        }

        @Override
        public double generateParkingTime() {
            // Returns a random double
            double rangeMin = 2000.0;
            double rangeMax = 20000.0;
            Random r = new Random();
            return rangeMin + (rangeMax - rangeMin) * r.nextDouble();
        }
    }

    /**
     * The spec generator that generates a finite number of vehicles,
     * randomly selecting the spec of each one.
     */
    public static class FiniteSpawnRandomSpecGenerator implements CPMSpawnSpecGenerator {
        /** The proportion of each spec */
        private List<Double> proportion;
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
         * of vehicles. The vehicle spec is chosen at random.
         */
        public FiniteSpawnRandomSpecGenerator(int numberOfVehiclesToSpawn, double trafficLevel) {
            this.numberOfVehiclesToSpawn = numberOfVehiclesToSpawn;
            this.numberOfSpawnedVehicles = 0;
            isDone = false;

            int n = VehicleSpecDatabase.getNumOfSpec();
            proportion = new ArrayList<Double>(n);
            double p = 1.0 / n;
            for(int i=0; i<n; i++) {
                proportion.add(p);
            }

            spawnProbability = trafficLevel * SimConfig.SPAWN_TIME_STEP;
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
                        int i = Util.randomIndex(proportion);
                        VehicleSpec vehicleSpec = VehicleSpecDatabase.getVehicleSpecById(i);
                        double parkingTime = generateParkingTime();
                        result.add(new CPMSpawnSpec(spawnPoint.getCurrentTime(),
                                vehicleSpec,
                                parkingTime));
                        numberOfSpawnedVehicles += 1;
                        System.out.println("Vehicle " + vehicleSpec.getName() + " spawned!");
                    }
                }
            }
            return result;
        }

        public double generateParkingTime(){
            return 20000.0;
        }
    }

    /**
     * The spec generator that infinitely generates vehicles with a random spec.
     */
    public static class InfiniteSpawnRandomSpecGenerator implements CPMSpawnSpecGenerator {
        /** The proportion of each spec */
        private List<Double> proportion;
        /** The probability of generating a vehicle in each spawn time step */
        private double spawnProbability;

        /**
         * Create a spec generator that infinitely generates vehicles.
         * The vehicle spec is chosen at random.
         */
        public InfiniteSpawnRandomSpecGenerator(double trafficLevel) {
            int n = VehicleSpecDatabase.getNumOfSpec();
            proportion = new ArrayList<Double>(n);
            double p = 1.0 / n;
            for(int i=0; i<n; i++) {
                proportion.add(p);
            }
            spawnProbability = trafficLevel * SimConfig.SPAWN_TIME_STEP;
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
                    int i = Util.randomIndex(proportion);
                    VehicleSpec vehicleSpec = VehicleSpecDatabase.getVehicleSpecById(i);
                    double parkingTime = generateParkingTime();
                    result.add(new CPMSpawnSpec(spawnPoint.getCurrentTime(),
                                                vehicleSpec,
                                                parkingTime));
                    System.out.println("Vehicle " + vehicleSpec.getName() + " spawned!");
                }
            }

            return result;
        }

        @Override
        public double generateParkingTime() {
            // Returns a random double
            double rangeMin = 2000.0;
            double rangeMax = 20000.0;
            Random r = new Random();
            return rangeMin + (rangeMax - rangeMin) * r.nextDouble();
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
        public SimpleRelocateSpawnSpecGenerator(double trafficLevel) {
            this.numberOfVehiclesToSpawn = 2;
            this.numberOfSpawnedVehicles = 0;
            vehicleSpec = VehicleSpecDatabase.getVehicleSpecByName("COUPE");
            spawnProbability = trafficLevel * SimConfig.SPAWN_TIME_STEP; // TODO CPM should get trafficLevel from somewhere
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
                        double parkingTime = generateParkingTime();
                        result.add(new CPMSpawnSpec(spawnPoint.getCurrentTime(),vehicleSpec, parkingTime));
                        System.out.println("Vehicle spawned!");
                        numberOfSpawnedVehicles += 1;
                    }
                }
            }
            return result;
        }

        @Override
        public double generateParkingTime() {
            if (numberOfSpawnedVehicles == 0) {
                return 20000.0;
            } else {
                return 10000.0;
            }
        }
    }

    public static void setUpFiniteSingleSpecSpawnPoint(CPMMap map,
                                                       int numberOfVehiclesToSpawn,
                                                       double trafficLevel){
        // The spawn point will only spawn numberOfVehiclesToSpawn, all of the same spec.
        for(CPMSpawnPoint sp : map.getSpawnPoints()) {
            sp.setVehicleSpecChooser(
                    new FiniteSpawnSingleSpecGenerator(numberOfVehiclesToSpawn, trafficLevel));
        }
    }

    public static void setUpInfiniteSingleSpecVehicleSpawnPoint(CPMMap map, double trafficLevel){
        // The spawn point will infinitely spawn vehicles of the same spec.
        for(CPMSpawnPoint sp : map.getSpawnPoints()) {
            sp.setVehicleSpecChooser(
                    new InfiniteSpawnSingleSpecGenerator(trafficLevel));
        }
    }

    public static void setUpFiniteRandomSpecSpawnPoint(CPMMap map,
                                                       int numberOfVehiclesToSpawn,
                                                       double trafficLevel){
        // The spawn point will only spawn numberOfVehiclesToSpawn, all of the same spec.
        for(CPMSpawnPoint sp : map.getSpawnPoints()) {
            sp.setVehicleSpecChooser(
                    new FiniteSpawnRandomSpecGenerator(numberOfVehiclesToSpawn, trafficLevel));
        }
    }

    public static void setUpInfiniteRandomSpecVehicleSpawnPoint(CPMMap map, double trafficLevel){
        // The spawn point will infinitely spawn vehicles of the same spec.
        for(CPMSpawnPoint sp : map.getSpawnPoints()) {
            sp.setVehicleSpecChooser(
                    new InfiniteSpawnRandomSpecGenerator(trafficLevel));
        }
    }

    public static void setUpSimpleRelocateSpawnPoint(CPMMap simpleMap, double trafficLevel){
        // The spawn point will spawn 2 vehicles which will trigger a relocation scenario.
        for(CPMSpawnPoint sp : simpleMap.getSpawnPoints()) {
            sp.setVehicleSpecChooser(
                    new SimpleRelocateSpawnSpecGenerator(trafficLevel));
        }
    }

    /**
     * Check that the vehicle is still on the map when it should be.
     * @param map   the map we are check the vehicle is still on
     * @param vehiclePosition the current x coordinate of the vehicle
     * @param currentLane the lane the vehicle is currently driving on
     * */
    public static void checkVehicleStillOnMap(CPMMap map,
                                              Point2D vehiclePosition,
                                              Lane currentLane){
        // For this map, should only drive off the map when it has
        // finished following the exit lane
        double x = vehiclePosition.getX();
        double y = vehiclePosition.getY();

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
