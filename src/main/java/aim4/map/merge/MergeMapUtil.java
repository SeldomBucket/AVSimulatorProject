package aim4.map.merge;

import aim4.config.SimConfig;
import aim4.map.Road;
import aim4.map.SpawnPoint;
import aim4.map.aim.AIMSpawnPoint;
import aim4.map.aim.destination.DestinationSelector;
import aim4.map.merge.MergeSpawnPoint.*;
import aim4.util.Util;
import aim4.vehicle.VehicleSpec;
import aim4.vehicle.VehicleSpecDatabase;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Callum on 17/03/2017.
 */
public class MergeMapUtil {
    public static void setSingleSpawnPoints(MergeMap map) {
        for(MergeSpawnPoint sp : map.getSpawnPoints()) {
            sp.setVehicleSpecChooser(
                    new SingleSpawnSpecGenerator()
            );
        }
    }

    public static void setSingleSpawnPoints(MergeMap map, VehicleSpec spec) {
        for(MergeSpawnPoint sp : map.getSpawnPoints()) {
            sp.setVehicleSpecChooser(
                    new SingleSpawnSpecGenerator(spec)
            );
        }
    }

    public static void setUniformSpawnSpecGenerator(MergeMap map, double trafficLevel) {
        for(MergeSpawnPoint sp : map.getSpawnPoints()) {
            sp.setVehicleSpecChooser(
                    new UniformSpawnSpecGenerator(trafficLevel)
            );
        }
    }

    public static class SingleSpawnSpecGenerator implements MergeSpawnSpecGenerator {
        private List<Double> proportion;
        private VehicleSpec spec;
        private List<MergeSpawnPoint> spawnPointAlreadySpawned;

        /**
         * Call to spawn random vehicle spec during act.
         */
        public SingleSpawnSpecGenerator() {
            int n = VehicleSpecDatabase.getNumOfSpec();
            proportion = new ArrayList<Double>(n);
            double p = 1.0 / n;
            for(int i=0; i<n; i++) {
                proportion.add(p);
            }

            spawnPointAlreadySpawned = new ArrayList<MergeSpawnPoint>();
        }

        /**
         * Call to spawn specific vehicle spec during act.
         * @param spec The vehicle spec to spawn
         */
        public SingleSpawnSpecGenerator(VehicleSpec spec) {
            this.spec = spec;
            spawnPointAlreadySpawned = new ArrayList<MergeSpawnPoint>();
        }

        /**
         * Creates single SpawnSpec for a given spawnPoint. Will never Spawn more than once for a given SpawnPoint.
         * @param spawnPoint
         * @param timestep
         * @return
         */
        @Override
        public List<MergeSpawnSpec> act(MergeSpawnPoint spawnPoint, double timestep) {
            List<MergeSpawnSpec> result = new LinkedList<MergeSpawnSpec>();

            if(!spawnPointAlreadySpawned.contains(spawnPoint)) {
                spawnPointAlreadySpawned.add(spawnPoint);
                double initTime = spawnPoint.getCurrentTime();
                if (this.spec == null) {
                    int i = Util.randomIndex(proportion);
                    this.spec = VehicleSpecDatabase.getVehicleSpecById(i);
                }

                result.add(new MergeSpawnSpec(spawnPoint.getCurrentTime(), spec));
            }

            return result;
        }
    }

    public static class UniformSpawnSpecGenerator implements MergeSpawnSpecGenerator {
        /** The proportion of each spec */
        private List<Double> proportion;
        /** probability of generating a vehicle in each spawn time step */
        private double prob;

        /**
         * Create an uniform spawn specification generator.
         *
         * @param trafficLevel         the traffic level
         */
        public UniformSpawnSpecGenerator(double trafficLevel) {
            int n = VehicleSpecDatabase.getNumOfSpec();
            proportion = new ArrayList<Double>(n);
            double p = 1.0 / n;
            for(int i=0; i<n; i++) {
                proportion.add(p);
            }

            prob = trafficLevel * SimConfig.SPAWN_TIME_STEP;
            // Cannot generate more than one vehicle in each spawn time step
            assert prob <= 1.0;
        }

        @Override
        public List<MergeSpawnSpec> act(MergeSpawnPoint spawnPoint, double timestep) {
            List<MergeSpawnSpec> result = new LinkedList<MergeSpawnSpec>();

            double initTime = spawnPoint.getCurrentTime();
            for(double time = initTime; time < initTime + timestep;
                time += SimConfig.SPAWN_TIME_STEP) {
                if (Util.random.nextDouble() < prob) {
                    int i = Util.randomIndex(proportion);
                    VehicleSpec vehicleSpec = VehicleSpecDatabase.getVehicleSpecById(i);
                    result.add(new MergeSpawnSpec(spawnPoint.getCurrentTime(),
                            vehicleSpec));
                }
            }

            return result;
        }
    }
}
