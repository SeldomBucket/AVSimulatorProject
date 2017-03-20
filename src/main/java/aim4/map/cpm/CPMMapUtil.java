package aim4.map.cpm;

import aim4.config.SimConfig;
import aim4.map.Road;
import aim4.map.aim.AIMSpawnPoint;
import aim4.map.cpm.CPMSpawnPoint.*;
import aim4.map.lane.Lane;
import aim4.util.Util;
import aim4.vehicle.VehicleSpec;
import aim4.vehicle.VehicleSpecDatabase;

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
                System.out.println("Vehicle spawned!");
                result.add(new CPMSpawnSpec(spawnPoint.getCurrentTime(),vehicleSpec));
            }
            return result;
        }
    }

    /**
     * The spec generator that generates a finite number of vehicles.
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


                        System.out.println("Vehicle spawned!");
                        result.add(new CPMSpawnSpec(spawnPoint.getCurrentTime(),vehicleSpec));
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
        // The spawn point will only spawn one vehicle in the whole simulation
        for(CPMSpawnPoint sp : simpleMap.getSpawnPoints()) {
            sp.setVehicleSpecChooser(
                    new FiniteSpawnSpecGenerator(numberOfVehiclesToSpawn));
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

        /*// check if it is within the dimensions of the map
        if (0 < x & x < map.getDimensions().getMaxX() &
                0 < y & y < map.getDimensions().getMaxY()){
            return;
        }
        // Allow it to drive off the map once it's followed the exit lane
        if (map.getExitLanes().contains(currentLane)){
            return;
        }*/

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
