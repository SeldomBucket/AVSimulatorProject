package aim4.map.mixedcpm;

import aim4.config.SimConfig;
import aim4.map.mixedcpm.MixedCPMSpawnPoint.*;
import aim4.map.lane.Lane;
import aim4.util.Util;
import aim4.vehicle.VehicleSpec;
import aim4.vehicle.VehicleSpecDatabase;
import aim4.vehicle.mixedcpm.MixedCPMBasicManualVehicle;
import javafx.util.Pair;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.csvreader.CsvReader;

/**
 * Utility class for MixedCPM maps.
 */
public class MixedCPMMapUtil {

    /**
     * The different spawn specification types.
     */
    public enum SpawnSpecType {
        /** Single - all vehicles spawned will have the same specification */
        SINGLE,
        /** Random - all vehicles spawned will have a randomly selected specification */
        RANDOM,
        /** CSV - vehicles are spawned in a specific order, described by a csv file */
        CSV
    }

    public enum MapType{
        /** Static - like an existing car park, the spaces don't change */
        STATIC,
        /** Adjustable Manual - ManualParkingArea only, adjusts to new vehicles arriving */
        ADJUSTABLE_MANUAL
    }

    /////////////////////////////////
    // NESTED CLASSES
    /////////////////////////////////

    /**
     * The spec generator that generates a finite number of vehicles of the same spec.
     */
    public static class FiniteSpawnSingleSpecGenerator implements MixedCPMSpawnSpecGenerator {
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
        public List<MixedCPMSpawnSpec> act(MixedCPMSpawnPoint spawnPoint, double timeStep) {
            List<MixedCPMSpawnSpec> result = new ArrayList<MixedCPMSpawnSpec>(1);
            if (numberOfSpawnedVehicles == numberOfVehiclesToSpawn) {
                isDone = true;
            }
            if (!isDone) {
                double initTime = spawnPoint.getCurrentTime();
                for(double time = initTime; time < initTime + timeStep;
                    time += SimConfig.SPAWN_TIME_STEP) {
                    if (Util.random.nextDouble() < spawnProbability) {
                        double parkingTime = generateParkingTime();
                        result.add(new MixedCPMSpawnSpec(spawnPoint.getCurrentTime(),vehicleSpec, parkingTime));
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
    public static class InfiniteSpawnSingleSpecGenerator implements MixedCPMSpawnSpecGenerator {
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
        public List<MixedCPMSpawnSpec> act(MixedCPMSpawnPoint spawnPoint, double timeStep) {
            List<MixedCPMSpawnSpec> result = new ArrayList<MixedCPMSpawnSpec>(1);
            double initTime = spawnPoint.getCurrentTime();
            for(double time = initTime; time < initTime + timeStep;
                time += SimConfig.SPAWN_TIME_STEP) {
                if (Util.random.nextDouble() < spawnProbability) {
                    double parkingTime = generateParkingTime();// TODO ED HERE IS WHERE TO CHANGE GENERATE PARKING TIME
                    result.add(new MixedCPMSpawnSpec(spawnPoint.getCurrentTime(),vehicleSpec, parkingTime));
                    //System.out.println("Vehicle spawned!");
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
    public static class FiniteSpawnRandomSpecGenerator implements MixedCPMSpawnSpecGenerator {
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
        public List<MixedCPMSpawnSpec> act(MixedCPMSpawnPoint spawnPoint, double timeStep) {
            List<MixedCPMSpawnSpec> result = new ArrayList<MixedCPMSpawnSpec>(1);

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
                        result.add(new MixedCPMSpawnSpec(spawnPoint.getCurrentTime(),
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
    public static class InfiniteSpawnRandomSpecGenerator implements MixedCPMSpawnSpecGenerator {
        /** The proportion of each spec */
        private List<Double> proportion;
        /** The probability of a vehicle being a disabled vehicle */
        private double probabilityOfVehicleBeingDisabled = 0.048;
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
        public List<MixedCPMSpawnSpec> act(MixedCPMSpawnPoint spawnPoint, double timeStep) {
            List<MixedCPMSpawnSpec> result = new ArrayList<MixedCPMSpawnSpec>(1);

            double initTime = spawnPoint.getCurrentTime();
            for(double time = initTime; time < initTime + timeStep;
                time += SimConfig.SPAWN_TIME_STEP) {
                if (Util.random.nextDouble() < spawnProbability) {
                    int i = Util.randomIndex(proportion);
                    VehicleSpec vehicleSpec = VehicleSpecDatabase.getVehicleSpecById(i);

                    double parkingTime = generateParkingTime();

                    if(Util.random.nextDouble() < probabilityOfVehicleBeingDisabled){
                        result.add(new MixedCPMSpawnSpec(spawnPoint.getCurrentTime(),
                                vehicleSpec,
                                parkingTime,
                                true));
                    }else{
                        result.add(new MixedCPMSpawnSpec(spawnPoint.getCurrentTime(),
                                vehicleSpec,
                                parkingTime));
                    }


                    // TODO ED Re-add this, maybe?
                    //System.out.println("Vehicle " + vehicleSpec.getName() + " spawned!");
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
     * The spec generator that generates vehicles using a CSV file to
     * specify the spawn times, parking times, and spec.
     */
    public static class SpecificSpawnSpecGenerator implements MixedCPMSpawnSpecGenerator {
        /** A map of entry times to spawn specifications for the vehicles */
        private ArrayList<SpawnSpecification> spawnSpecificationMap = new ArrayList<>();
        /** Whether the spawn point has finished spawning vehicles */
        private boolean isDone = false;

        public class SpawnSpecification{
            double spawnTime;
            double parkingTime;
            VehicleSpec vehicleSpec;
            boolean isDisabled;

            public SpawnSpecification(double spawnTime, double parkingTime, String specName, boolean isDisabled){
                this.spawnTime = spawnTime;
                this.parkingTime = parkingTime;
                this.vehicleSpec = VehicleSpecDatabase.getVehicleSpecByName(specName);
                this.isDisabled = isDisabled;
            }

            public double getSpawnTime() {
                return spawnTime;
            }

            public double getParkingTime() {
                return parkingTime;
            }

            public VehicleSpec getVehicleSpec() {
                return vehicleSpec;
            }

            public boolean isDisabled() {
                return isDisabled;
            }
        }


        /**
         * Create a spec generator that generates vehicles based on a csv file.
         */
        public SpecificSpawnSpecGenerator(Pair<Boolean, String> useCSVFilePair) {
            processCSV(useCSVFilePair);
        }

        private void processCSV(Pair<Boolean, String> useCSVFilePair){
            // Ensure we are meant to be using a file
            boolean useCSV = useCSVFilePair.getKey();
            assert useCSV;

            // Ensure the given location is valid
            // TODO MixedCPM this check should be done way before now so user can try again.
            String filepath = useCSVFilePair.getValue();
            File file = new File(filepath);
            if (!file.exists()){
                throw new RuntimeException("This file doesn't exist.");
            }
            if (!file.canRead()) {
                throw new RuntimeException("This file cannot be read.");
            }

            try {
                readCSV(filepath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        /**
         * Read the CSV file, adding the entry time and parking time as a pair to spawnSpecificationMap.
         * The CSV file should either give the times in seconds, or in the format hh:mm:ss.
         * The CSV file should be ordered by increasing entry time.
         * @param filepath for the CSV file
         * @throws FileNotFoundException
         */
        private void readCSV(String filepath) throws FileNotFoundException {
            try {

                CsvReader csvFile = new CsvReader(filepath);

                try {
                    csvFile.readHeaders();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                while (csvFile.readRecord())
                {
                    String specString = csvFile.get("Spec");
                    boolean isDisabled = csvFile.get("Disabled").equals("Y");
                    String entryString = csvFile.get("Entry");
                    String parkingString = csvFile.get("Parking");

                    // Parse spawn times
                    Double entry;
                    Double parking;
                    try {
                        entry = Double.parseDouble(entryString);
                        parking = Double.parseDouble(parkingString);
                    } catch (NumberFormatException e) {
                        entry = convertTimeToSeconds(entryString);
                        parking = convertTimeToSeconds(parkingString);

                    }

                    SpawnSpecification spec = new SpawnSpecification(entry, parking, specString, isDisabled);
                    spawnSpecificationMap.add(spec);

                }

                csvFile.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private Double convertTimeToSeconds(String timeString){
            String[] data = timeString.split(":");
            double hours = Double.parseDouble(data[0]);
            double minutes = Double.parseDouble(data[1]);
            double seconds = Double.parseDouble(data[2]);
            double totalSeconds = (3600*hours) + (60*minutes) + seconds;
            return totalSeconds;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<MixedCPMSpawnSpec> act(MixedCPMSpawnPoint spawnPoint, double timeStep) {
            List<MixedCPMSpawnSpec> result = new ArrayList<MixedCPMSpawnSpec>(1);

            if (spawnSpecificationMap.isEmpty()) {
                isDone = true;
            }

            double initTime = spawnPoint.getCurrentTime();
            if (!isDone) {
                if (spawnSpecificationMap.get(0).getSpawnTime() < initTime) {
                    VehicleSpec vehicleSpec = spawnSpecificationMap.get(0).getVehicleSpec();
                    double parkingTime = spawnSpecificationMap.get(0).getParkingTime();
                    result.add(new MixedCPMSpawnSpec(spawnPoint.getCurrentTime(), vehicleSpec, parkingTime));
                    spawnSpecificationMap.remove(0);
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

    public static void setUpFiniteSingleSpecSpawnPoint(MixedCPMMap map,
                                                       int numberOfVehiclesToSpawn,
                                                       double trafficLevel){
        // The spawn point will only spawn numberOfVehiclesToSpawn, all of the same spec.
        for(MixedCPMSpawnPoint sp : map.getSpawnPoints()) {
            sp.setVehicleSpecChooser(
                    new FiniteSpawnSingleSpecGenerator(numberOfVehiclesToSpawn, trafficLevel));
        }
    }

    public static void setUpInfiniteSingleSpecVehicleSpawnPoint(MixedCPMMap map, double trafficLevel){
        // The spawn point will infinitely spawn vehicles of the same spec.
        for(MixedCPMSpawnPoint sp : map.getSpawnPoints()) {
            sp.setVehicleSpecChooser(
                    new InfiniteSpawnSingleSpecGenerator(trafficLevel));
        }
    }

    public static void setUpSpecificSpecVehicleSpawnPoint(MixedCPMMap map, Pair<Boolean, String> useCSVPair){
        // The spawn point will infinitely spawn vehicles of the same spec.
        for(MixedCPMSpawnPoint sp : map.getSpawnPoints()) {
            sp.setVehicleSpecChooser(
                    new SpecificSpawnSpecGenerator(useCSVPair));
        }
    }

    public static void setUpFiniteRandomSpecSpawnPoint(MixedCPMMap map,
                                                       int numberOfVehiclesToSpawn,
                                                       double trafficLevel){
        // The spawn point will only spawn numberOfVehiclesToSpawn, all of the same spec.
        for(MixedCPMSpawnPoint sp : map.getSpawnPoints()) {
            sp.setVehicleSpecChooser(
                    new FiniteSpawnRandomSpecGenerator(numberOfVehiclesToSpawn, trafficLevel));
        }
    }

    public static void setUpInfiniteRandomSpecVehicleSpawnPoint(MixedCPMMap map, double trafficLevel){
        // The spawn point will infinitely spawn vehicles of the same spec.
        for(MixedCPMSpawnPoint sp : map.getSpawnPoints()) {
            sp.setVehicleSpecChooser(
                    new InfiniteSpawnRandomSpecGenerator(trafficLevel));
        }
    }

    /**
     * Check that the vehicle is still on the map when it should be.
     * @param map   the map we are check the vehicle is still on
     * @param vehiclePosition the current x coordinate of the vehicle
     * @param currentLane the lane the vehicle is currently driving on
     * */
    public static void checkVehicleStillOnMap(MixedCPMMap map,
                                              Point2D vehiclePosition,
                                              Lane currentLane,
                                              MixedCPMBasicManualVehicle vehicle){
        // For this map, should only drive off the map when it has
        // finished following the exit lane
        double x = vehiclePosition.getX();
        double y = vehiclePosition.getY();

        // If the vehicle is off the map
        if (!map.getDimensions().contains(new Point2D.Double(x, y))){
            // And the vehicle is not on the exit lane
            if (!map.getExitLanes().contains(currentLane)) {
                throw new RuntimeException("Vehicle " + vehicle.getVIN() + " has driven off the map! Vehicle position " + vehiclePosition
                        + ", map dimensions " + map.getDimensions().getMaxX() + "," + map.getDimensions().getMaxY());
            }
        }
    }

}
