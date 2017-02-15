package aim4.map.cpm;

import aim4.map.Road;
import aim4.map.SpawnPoint;
import aim4.vehicle.VehicleSpec;
import aim4.vehicle.VehicleSpecDatabase;

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


    public static void setUpSimpleSpawnPoints(VerySimpleMap simpleMap){
        // The spawn point will only spawn one vehicle in the whole simulation
        for(SpawnPoint sp : simpleMap.getSpawnPoints()) {
            sp.setVehicleSpecChooser(
                    new OnlyOneSpawnSpecGenerator());
        }
    }

}
