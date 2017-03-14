package aim4.map.cpm;

import aim4.map.Road;
import aim4.map.SpawnPoint;
import aim4.map.lane.Lane;
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
    public static class OnlyOneSpawnSpecGenerator implements SpawnPoint.SpawnSpecGenerator {
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
        public List<SpawnPoint.SpawnSpec> act(SpawnPoint spawnPoint, double timeStep) {
            List<SpawnPoint.SpawnSpec> result = new ArrayList<SpawnPoint.SpawnSpec>(1);
            if (!isDone) {
                isDone = true;
                System.out.println("Vehicle spawned!");
                result.add(new SpawnPoint.SpawnSpec(spawnPoint.getCurrentTime(),
                        vehicleSpec,
                        null));
            }
            return result;
        }
    }


    public static void setUpOneVehicleSpawnPoint(CPMMap simpleMap){
        // The spawn point will only spawn one vehicle in the whole simulation
        for(SpawnPoint sp : simpleMap.getSpawnPoints()) {
            sp.setVehicleSpecChooser(
                    new OnlyOneSpawnSpecGenerator());
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

        // check if it is within the dimensions of the map
        if (0 < x & x < map.getDimensions().getMaxX() &
                0 < y & y < map.getDimensions().getMaxY()){
            return;
        }
        // Allow it to drive off the map once it's followed the exit lane
        if (map.getExitLanes().contains(currentLane)){
            return;
        }
        throw new RuntimeException("Vehicle has driven off the map!");
    }

}
