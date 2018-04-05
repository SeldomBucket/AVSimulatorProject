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
        RANDOM
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
     * The spec generator that generates vehicles of the same spec, using a CSV file to
     * specify the spawn times and parking times.
     */
    public static class SpecificSpawnSingleSpecGenerator implements MixedCPMSpawnSpecGenerator {
        /** The vehicle specification */
        private VehicleSpec vehicleSpec;
        /** A list of entry time and parking time pairs */
        private List<Pair<Double, Double>> spawnTimes = new ArrayList<Pair<Double, Double>>();
        /** Whether the spawn point has finished spawning vehicles */
        private boolean isDone = false;


        /**
         * Create a spec generator that infinitely generates vehicles of the same spec.
         */
        public SpecificSpawnSingleSpecGenerator(Pair<Boolean, String> useCSVFilePair) {
            vehicleSpec = VehicleSpecDatabase.getVehicleSpecByName("COUPE");
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
                    } catch (NumberFormatException e) {
                        entry = convertTimeToSeconds(entryString);
                    }
                    try {
                        parking = Double.parseDouble(parkingString);
                    } catch (NumberFormatException e) {
                        parking = convertTimeToSeconds(parkingString);
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

            if (spawnTimes.isEmpty()) {
                isDone = true;
            }

            double initTime = spawnPoint.getCurrentTime();
            if (!isDone) {
                if (spawnTimes.get(0).getKey() < initTime) {
                    double parkingTime = spawnTimes.get(0).getValue();
                    result.add(new MixedCPMSpawnSpec(spawnPoint.getCurrentTime(), vehicleSpec, parkingTime));
                    spawnTimes.remove(0);
                    System.out.println("Vehicle spawned at time: " + initTime);
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
     * The spec generator that generates vehicles of the a random spec, using a CSV file to
     * specify the spawn times and parking times.
     */
    public static class SpecificSpawnRandomSpecGenerator implements MixedCPMSpawnSpecGenerator {
        /** A list of entry time and parking time pairs */
        private List<Pair<Double, Double>> spawnTimes = new ArrayList<Pair<Double, Double>>();
        /** Whether the spawn point has finished spawning vehicles */
        private boolean isDone = false;
        /** The proportion of each spec */
        private List<Double> proportion;


        /**
         * Create a spec generator that infinitely generates vehicles of the same spec.
         */
        public SpecificSpawnRandomSpecGenerator(Pair<Boolean, String> useCSVFilePair) {
            processCSV(useCSVFilePair);

            int n = VehicleSpecDatabase.getNumOfSpec();
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
                        entry = convertTimeToSeconds(entryString);
                        parking = convertTimeToSeconds(parkingString);

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

            if (spawnTimes.isEmpty()) {
                isDone = true;
            }

            double initTime = spawnPoint.getCurrentTime();
            if (!isDone) {
                if (spawnTimes.get(0).getKey() < initTime) {
                    int i = Util.randomIndex(proportion);
                    VehicleSpec vehicleSpec = VehicleSpecDatabase.getVehicleSpecById(i);
                    double parkingTime = spawnTimes.get(0).getValue();
                    result.add(new MixedCPMSpawnSpec(spawnPoint.getCurrentTime(), vehicleSpec, parkingTime));
                    spawnTimes.remove(0);
                    System.out.println("Vehicle " + vehicleSpec.getName() +" spawned at time: " + initTime);
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
    public static class SimpleRelocateSpawnSpecGenerator implements MixedCPMSpawnSpecGenerator {
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
            spawnProbability = trafficLevel * SimConfig.SPAWN_TIME_STEP; // TODO MixedCPM should get trafficLevel from somewhere
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

    public static void setUpSpecificSingleSpecVehicleSpawnPoint(MixedCPMMap map, Pair<Boolean, String> useCSVPair){
        // The spawn point will infinitely spawn vehicles of the same spec.
        for(MixedCPMSpawnPoint sp : map.getSpawnPoints()) {
            sp.setVehicleSpecChooser(
                    new SpecificSpawnSingleSpecGenerator(useCSVPair));
        }
    }

    public static void setUpSpecificRandomSpecVehicleSpawnPoint(MixedCPMMap map, Pair<Boolean, String> useCSVPair){
        // The spawn point will infinitely spawn vehicles of the same spec.
        for(MixedCPMSpawnPoint sp : map.getSpawnPoints()) {
            sp.setVehicleSpecChooser(
                    new SpecificSpawnRandomSpecGenerator(useCSVPair));
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

    public static void setUpSimpleRelocateSpawnPoint(MixedCPMMap simpleMap, double trafficLevel){
        // The spawn point will spawn 2 vehicles which will trigger a relocation scenario.
        for(MixedCPMSpawnPoint sp : simpleMap.getSpawnPoints()) {
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
