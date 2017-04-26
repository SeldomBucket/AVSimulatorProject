package aim4.map.cpm;

import aim4.config.SimConfig;
import aim4.map.cpm.components.CPMSpawnPoint;
import aim4.map.cpm.components.CPMSpawnPoint.*;
import aim4.map.lane.Lane;
import aim4.util.Util;
import aim4.vehicle.VehicleSpec;
import aim4.vehicle.VehicleSpecDatabase;
import javafx.util.Pair;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.csvreader.CsvReader;

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
        /** Mixed - vehicles will be spawned according to a given distribution for the available specs */
        MIXED,
        /** Random - vehicles will be spawned with a randomly selected spec */
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
        public FiniteSpawnSingleSpecGenerator(int numberOfVehiclesToSpawn, double trafficLevel, String vehicleSpecName) {
            vehicleSpec = VehicleSpecDatabase.getVehicleSpecByName(vehicleSpecName);
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
                        // System.out.println("Vehicle spawned!");
                    }
                }
            }
            return result;
        }

        @Override
        public int getNumberOfVehiclesLeftToSpawn() {
            return numberOfVehiclesToSpawn - numberOfSpawnedVehicles;
        }

        public double generateParkingTime(){
            // Returns a random double between 00:01:30 and 15:07:14
            double rangeMin = 90.0;
            double rangeMax = 54434.0;
            Random r = new Random();
            return rangeMin + (rangeMax - rangeMin) * r.nextDouble();
        }
    }

    /**
     * The spec generator that infinitely generates vehicles of the same spec.
     */
    public static class InfiniteSpawnSingleSpecGenerator implements CPMSpawnSpecGenerator {
        /** The vehicle specification */
        private VehicleSpec vehicleSpec;
        /** The probability of generating a vehicle in each spawn time step */
        private double spawnProbability;

        /**
         * Create a spec generator that infinitely generates vehicles of the same spec.
         */
        public InfiniteSpawnSingleSpecGenerator(double trafficLevel, String vehicleSpecName) {
            vehicleSpec = VehicleSpecDatabase.getVehicleSpecByName(vehicleSpecName);
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
                    // System.out.println("Vehicle " + vehicleSpec.getName() + " spawned!");
                }
            }
            return result;
        }

        @Override
        public int getNumberOfVehiclesLeftToSpawn() {
            return -1;
        }

        @Override
        public double generateParkingTime() {
            // Returns a random double between 00:01:30 and 15:07:14
            double rangeMin = 90.0;
            double rangeMax = 54434.0;
            Random r = new Random();
            return rangeMin + (rangeMax - rangeMin) * r.nextDouble();
        }
    }

    /**
     * The spec generator that generates vehicles of the same spec, using a CSV file to
     * specify the spawn times and parking times.
     */
    public static class SpecificSpawnSingleSpecGenerator implements CPMSpawnSpecGenerator {
        /** The vehicle specification */
        private VehicleSpec vehicleSpec;
        /** A list of entry time and parking time pairs */
        private List<Pair<Double, Double>> spawnTimes = new ArrayList<Pair<Double, Double>>();
        /** Whether the spawn point has finished spawning vehicles */
        private boolean isDone = false;


        /**
         * Create a spec generator that generates vehicles according to the CSV file, all of the same spec.
         */
        public SpecificSpawnSingleSpecGenerator(Pair<Boolean, String> useCSVFilePair, String vehicleSpecName) {
            vehicleSpec = VehicleSpecDatabase.getVehicleSpecByName(vehicleSpecName);
            processCSV(useCSVFilePair);
        }

        private void processCSV(Pair<Boolean, String> useCSVFilePair){
            // Ensure we are meant to be using a file
            boolean useCSV = useCSVFilePair.getKey();
            assert useCSV;

            // Ensure the given location is valid
            // TODO CPM this check should be done way before now so user can try again.
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
         * Read the CSV file, adding the entry time and parking time as a pair to spawnTimes.
         * The CSV file should either give the times in seconds, or in the format hh:mm:ss.
         * The CSV file should be ordered by increasing entry time.
         * @param filepath for the CSV file
         * @throws FileNotFoundException
         */
        // TODO Should add some validation for this somewhere
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
                    String entryString = csvFile.get("Entry");
                    String parkingString = csvFile.get("Parking");

                    Double entry;
                    Double parking;
                    try {
                        entry = Double.parseDouble(entryString);
                    } catch (NumberFormatException e) {
                        entry = Util.convertTimeStringToSeconds(entryString);
                    }
                    try {
                        parking = Double.parseDouble(parkingString);
                    } catch (NumberFormatException e) {
                        parking = Util.convertTimeStringToSeconds(parkingString);
                    }

                    Pair<Double, Double> pair = new Pair<Double, Double>(entry, parking);
                    spawnTimes.add(pair);
                }

                csvFile.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<CPMSpawnSpec> act(CPMSpawnPoint spawnPoint, double timeStep) {
            List<CPMSpawnSpec> result = new ArrayList<CPMSpawnSpec>(1);

            if (spawnTimes.isEmpty()) {
                isDone = true;
            }

            double initTime = spawnPoint.getCurrentTime();
            if (!isDone) {
                if (spawnTimes.get(0).getKey() < initTime) {
                    double parkingTime = spawnTimes.get(0).getValue();
                    result.add(new CPMSpawnSpec(spawnPoint.getCurrentTime(), vehicleSpec, parkingTime));
                    spawnTimes.remove(0);
                    // System.out.println("Vehicle spawned at time: " + initTime);
                }
            }

            return result;
        }

        @Override
        public int getNumberOfVehiclesLeftToSpawn() {
            return spawnTimes.size();
        }

        @Override
        public double generateParkingTime() {
            // Returns a random double between 00:01:30 and 15:07:14
            double rangeMin = 90.0;
            double rangeMax = 54434.0;
            Random r = new Random();
            return rangeMin + (rangeMax - rangeMin) * r.nextDouble();
        }
    }

    /**
     * The spec generator that generates a finite number of vehicles,
     * randomly and uniformly selecting the spec of each one.
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
         * of vehicles. The vehicle spec is chosen with a uniform random probability.
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
                        // System.out.println("Vehicle " + vehicleSpec.getName() + " spawned!");
                    }
                }
            }
            return result;
        }

        @Override
        public int getNumberOfVehiclesLeftToSpawn() {
            return numberOfVehiclesToSpawn - numberOfSpawnedVehicles;
        }

        public double generateParkingTime(){
            // Returns a random double between 00:01:30 and 15:07:14
            double rangeMin = 90.0;
            double rangeMax = 54434.0;
            Random r = new Random();
            return rangeMin + (rangeMax - rangeMin) * r.nextDouble();
        }
    }

    /**
     * The spec generator that infinitely generates vehicles with a uniform random spec.
     */
    public static class InfiniteSpawnRandomSpecGenerator implements CPMSpawnSpecGenerator {
        /** The proportion of each spec */
        private List<Double> proportion;
        /** The probability of generating a vehicle in each spawn time step */
        private double spawnProbability;
        /** The vehicle specifications that should be randomly spawned */
        private List<String> specsToSpawn;

        /**
         * Create a spec generator that infinitely generates vehicles.
         * The vehicle spec is chosen with a uniform random probability.
         */
        public InfiniteSpawnRandomSpecGenerator(double trafficLevel, List<String> specsToSpawn) {
            this.specsToSpawn = specsToSpawn;

            int n = specsToSpawn.size();
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
                    String specName = specsToSpawn.get(i);
                    VehicleSpec vehicleSpec = VehicleSpecDatabase.getVehicleSpecByName(specName);
                    double parkingTime = generateParkingTime();
                    result.add(new CPMSpawnSpec(spawnPoint.getCurrentTime(),
                                                vehicleSpec,
                                                parkingTime));
                    // System.out.println("Vehicle " + vehicleSpec.getName() + " spawned!");
                }
            }

            return result;
        }

        @Override
        public int getNumberOfVehiclesLeftToSpawn() {
            return -1;
        }

        @Override
        public double generateParkingTime() {
            // Returns a random double between 00:01:30 and 15:07:14
            double rangeMin = 90.0;
            double rangeMax = 54434.0;
            Random r = new Random();
            return rangeMin + (rangeMax - rangeMin) * r.nextDouble();
        }
    }

    /**
     * The spec generator that generates vehicles with a uniform random spec, using a CSV file to
     * specify the spawn times and parking times.
     */
    public static class SpecificSpawnRandomSpecGenerator implements CPMSpawnSpecGenerator {
        /** A list of entry time and parking time pairs */
        private List<Pair<Double, Double>> spawnTimes = new ArrayList<Pair<Double, Double>>();
        /** Whether the spawn point has finished spawning vehicles */
        private boolean isDone = false;
        /** The proportion of each spec */
        private List<Double> proportion;
        /** The vehicle specifications that should be randomly spawned */
        private List<String> specsToSpawn;


        /**
         * Create a spec generator that spawns vehicles with a uniform random spec, at times according to
         * the given CSV file.
         */
        public SpecificSpawnRandomSpecGenerator(Pair<Boolean, String> useCSVFilePair,
                                                List<String> specsToSpawn) {
            processCSV(useCSVFilePair);
            this.specsToSpawn = specsToSpawn;

            int n = specsToSpawn.size();
            proportion = new ArrayList<Double>(n);
            double p = 1.0 / n;
            for(int i=0; i<n; i++) {
                proportion.add(p);
            }
        }

        private void processCSV(Pair<Boolean, String> useCSVFilePair){
            // Ensure we are meant to be using a file
            boolean useCSV = useCSVFilePair.getKey();
            assert useCSV;

            // Ensure the given location is valid
            // TODO CPM this check should be done way before now so user can try again.
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
         * Read the CSV file, adding the entry time and parking time as a pair to spawnTimes.
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
                    String entryString = csvFile.get("Entry");
                    String parkingString = csvFile.get("Parking");

                    Double entry;
                    Double parking;
                    try {
                        entry = Double.parseDouble(entryString);
                        parking = Double.parseDouble(parkingString);
                    } catch (NumberFormatException e) {
                        entry = Util.convertTimeStringToSeconds(entryString);
                        parking = Util.convertTimeStringToSeconds(parkingString);

                    }

                    Pair<Double, Double> pair = new Pair<Double, Double>(entry, parking);
                    spawnTimes.add(pair);
                }

                csvFile.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<CPMSpawnSpec> act(CPMSpawnPoint spawnPoint, double timeStep) {
            List<CPMSpawnSpec> result = new ArrayList<CPMSpawnSpec>(1);

            if (spawnTimes.isEmpty()) {
                isDone = true;
            }

            double initTime = spawnPoint.getCurrentTime();
            if (!isDone) {
                if (spawnTimes.get(0).getKey() < initTime) {
                    int i = Util.randomIndex(proportion);
                    String specName = specsToSpawn.get(i);
                    VehicleSpec vehicleSpec = VehicleSpecDatabase.getVehicleSpecByName(specName);
                    double parkingTime = spawnTimes.get(0).getValue();
                    result.add(new CPMSpawnSpec(spawnPoint.getCurrentTime(), vehicleSpec, parkingTime));
                    spawnTimes.remove(0);
                    // System.out.println("Vehicle " + vehicleSpec.getName() +" spawned at time: " + initTime);
                }
            }

            return result;
        }

        @Override
        public int getNumberOfVehiclesLeftToSpawn() {
            return spawnTimes.size();
        }

        @Override
        public double generateParkingTime() {
            // Returns a random double between 00:01:30 and 15:07:14
            double rangeMin = 90.0;
            double rangeMax = 54434.0;
            Random r = new Random();
            return rangeMin + (rangeMax - rangeMin) * r.nextDouble();
        }
    }

    /**
     * The spec generator that infinitely generates vehicles with specs according to the given distribution.
     */
    public static class InfiniteSpawnMixedSpecGenerator implements CPMSpawnSpecGenerator {
        /** The proportion of each spec */
        private List<Double> proportion;
        /** The probability of generating a vehicle in each spawn time step */
        private double spawnProbability;

        /**
         * Create a spec generator that infinitely generates vehicles.
         * The vehicle spec is chosen according to the given distribution.
         */
        public InfiniteSpawnMixedSpecGenerator(double trafficLevel, List<Double> distribution) {
            proportion = distribution;
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
                    // System.out.println("Vehicle " + vehicleSpec.getName() + " spawned!");
                }
            }

            return result;
        }

        @Override
        public int getNumberOfVehiclesLeftToSpawn() {
            return -1;
        }

        @Override
        public double generateParkingTime() {
            // Returns a random double between 00:01:30 and 15:07:14
            double rangeMin = 90.0;
            double rangeMax = 54434.0;
            Random r = new Random();
            return rangeMin + (rangeMax - rangeMin) * r.nextDouble();
        }
    }

    /**
     * The spec generator that infinitely generates vehicles with specs according to the given distribution,
     * using a CSV file to specify the spawn times and parking times.
     */
    public static class SpecificSpawnMixedSpecGenerator implements CPMSpawnSpecGenerator {
        /** A list of entry time and parking time pairs */
        private List<Pair<Double, Double>> spawnTimes = new ArrayList<Pair<Double, Double>>();
        /** Whether the spawn point has finished spawning vehicles */
        private boolean isDone = false;
        /** The proportion of each spec */
        private List<Double> proportion;


        /**
         * Create a spec generator that infinitely generates vehicles.
         * The vehicle spec is chosen according to the given distribution.
         * The spawn time and parking time is specified by the CSV file.
         */
        public SpecificSpawnMixedSpecGenerator(Pair<Boolean, String> useCSVFilePair, List<Double> distribution) {
            processCSV(useCSVFilePair);
            proportion = distribution;
        }

        private void processCSV(Pair<Boolean, String> useCSVFilePair){
            // Ensure we are meant to be using a file
            boolean useCSV = useCSVFilePair.getKey();
            assert useCSV;

            // Ensure the given location is valid
            // TODO CPM this check should be done way before now so user can try again.
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
         * Read the CSV file, adding the entry time and parking time as a pair to spawnTimes.
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
                    String entryString = csvFile.get("Entry");
                    String parkingString = csvFile.get("Parking");

                    Double entry;
                    Double parking;
                    try {
                        entry = Double.parseDouble(entryString);
                        parking = Double.parseDouble(parkingString);
                    } catch (NumberFormatException e) {
                        entry = Util.convertTimeStringToSeconds(entryString);
                        parking = Util.convertTimeStringToSeconds(parkingString);

                    }

                    Pair<Double, Double> pair = new Pair<Double, Double>(entry, parking);
                    spawnTimes.add(pair);
                }

                csvFile.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<CPMSpawnSpec> act(CPMSpawnPoint spawnPoint, double timeStep) {
            List<CPMSpawnSpec> result = new ArrayList<CPMSpawnSpec>(1);

            if (spawnTimes.isEmpty()) {
                isDone = true;
            }

            double initTime = spawnPoint.getCurrentTime();
            if (!isDone) {
                if (spawnTimes.get(0).getKey() < initTime) {
                    int i = Util.randomIndex(proportion);
                    VehicleSpec vehicleSpec = VehicleSpecDatabase.getVehicleSpecById(i);
                    double parkingTime = spawnTimes.get(0).getValue();
                    result.add(new CPMSpawnSpec(spawnPoint.getCurrentTime(), vehicleSpec, parkingTime));
                    spawnTimes.remove(0);
                    // System.out.println("Vehicle " + vehicleSpec.getName() +" spawned at time: " + initTime);
                }
            }

            return result;
        }

        @Override
        public int getNumberOfVehiclesLeftToSpawn() {
            return spawnTimes.size();
        }

        @Override
        public double generateParkingTime() {
            // Returns a random double between 00:01:30 and 15:07:14
            double rangeMin = 90.0;
            double rangeMax = 54434.0;
            Random r = new Random();
            return rangeMin + (rangeMax - rangeMin) * r.nextDouble();
        }
    }

    public static void setUpFiniteSingleSpecSpawnPoint(CPMMap map,
                                                       int numberOfVehiclesToSpawn,
                                                       double trafficLevel,
                                                       String vehicleSpecName){
        // The spawn point will only spawn numberOfVehiclesToSpawn, all of the same spec.
        for(CPMSpawnPoint sp : map.getSpawnPoints()) {
            sp.setVehicleSpecChooser(
                    new FiniteSpawnSingleSpecGenerator(numberOfVehiclesToSpawn, trafficLevel, vehicleSpecName));
        }
    }

    public static void setUpInfiniteSingleSpecVehicleSpawnPoint(CPMMap map,
                                                                double trafficLevel,
                                                                String vehicleSpecName){
        // The spawn point will infinitely spawn vehicles of the same spec.
        for(CPMSpawnPoint sp : map.getSpawnPoints()) {
            sp.setVehicleSpecChooser(
                    new InfiniteSpawnSingleSpecGenerator(trafficLevel, vehicleSpecName));
        }
    }

    public static void setUpSpecificSingleSpecVehicleSpawnPoint(CPMMap map,
                                                                Pair<Boolean, String> useCSVPair,
                                                                String vehicleSpecName){
        // The spawn point will infinitely spawn vehicles of the same spec.
        for(CPMSpawnPoint sp : map.getSpawnPoints()) {
            sp.setVehicleSpecChooser(
                    new SpecificSpawnSingleSpecGenerator(useCSVPair, vehicleSpecName));
        }
    }

    public static void setUpSpecificRandomSpecVehicleSpawnPoint(CPMMap map,
                                                                Pair<Boolean, String> useCSVPair,
                                                                List<String> specsToSpawn){
        // The spawn point will infinitely spawn vehicles of the same spec.
        for(CPMSpawnPoint sp : map.getSpawnPoints()) {
            sp.setVehicleSpecChooser(
                    new SpecificSpawnRandomSpecGenerator(useCSVPair, specsToSpawn));
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

    public static void setUpInfiniteRandomSpecVehicleSpawnPoint(CPMMap map,
                                                                double trafficLevel,
                                                                List<String> specsToSpawn){
        // The spawn point will infinitely spawn vehicles of the same spec.
        for(CPMSpawnPoint sp : map.getSpawnPoints()) {
            sp.setVehicleSpecChooser(
                    new InfiniteSpawnRandomSpecGenerator(trafficLevel, specsToSpawn));
        }
    }

    public static void setUpSpecificMixedSpecVehicleSpawnPoint(CPMMap map,
                                                                Pair<Boolean, String> useCSVPair,
                                                                List<Double> distribution){
        // The spawn point will infinitely spawn vehicles of the same spec.
        for(CPMSpawnPoint sp : map.getSpawnPoints()) {
            sp.setVehicleSpecChooser(
                    new SpecificSpawnMixedSpecGenerator(useCSVPair, distribution));
        }
    }

    public static void setUpInfiniteMixedSpecVehicleSpawnPoint(CPMMap map,
                                                                double trafficLevel,
                                                                List<Double> distribution){
        // The spawn point will infinitely spawn vehicles of the same spec.
        for(CPMSpawnPoint sp : map.getSpawnPoints()) {
            sp.setVehicleSpecChooser(
                    new InfiniteSpawnMixedSpecGenerator(trafficLevel, distribution));
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
