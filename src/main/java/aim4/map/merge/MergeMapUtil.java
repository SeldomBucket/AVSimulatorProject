package aim4.map.merge;

import aim4.config.SimConfig;
import aim4.im.merge.V2IMergeGridManager;
import aim4.im.merge.V2IMergeManager;
import aim4.im.merge.V2IQueueMergeManager;
import aim4.im.merge.policy.grid.BaseMergeGridPolicy;
import aim4.im.merge.policy.grid.FCFSMergeGridRequestHandler;
import aim4.im.merge.policy.nogrid.BaseMergePolicy;
import aim4.im.merge.policy.nogrid.FCFSMergeRequestHandler;
import aim4.im.merge.reservation.grid.ReservationMergeGridManager;
import aim4.im.merge.reservation.nogrid.ReservationMergeManager;
import aim4.map.connections.MergeConnection;
import aim4.map.merge.MergeSpawnPoint.MergeSpawnSpec;
import aim4.map.merge.MergeSpawnPoint.MergeSpawnSpecGenerator;
import aim4.sim.setup.merge.enums.ProtocolType;
import aim4.sim.simulator.merge.helper.SensorInputHelper;
import aim4.sim.simulator.merge.helper.SpawnHelper;
import aim4.util.Util;
import aim4.vehicle.VehicleSpec;
import aim4.vehicle.VehicleSpecDatabase;
import aim4.vehicle.merge.MergeVehicleSimModel;
import com.sun.scenario.effect.Merge;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by Callum on 17/03/2017.
 */
public class MergeMapUtil {
    // MERGE MANAGERS //
    public static void setFCFSMergeManagers(MergeMap layout, double currentTime, ReservationMergeManager.Config mergeReservationConfig) {
        layout.removeAllMergeManagers();
        for(MergeConnection merge : layout.getMergeConnections()) {
            V2IMergeManager mm = new V2IMergeManager(
                    merge,
                    currentTime,
                    mergeReservationConfig,
                    layout.getMMRegistry(),
                    layout
            );
            mm.setMergePolicy(new BaseMergePolicy(mm, new FCFSMergeRequestHandler()));
            layout.addMergeManager(mm);
        }
    }

    public static void setFCFSGridMergeManagers(MergeMap layout, double currentTime, ReservationMergeGridManager.Config mergeReservationConfig) {
        layout.removeAllMergeManagers();
        for(MergeConnection merge : layout.getMergeConnections()) {
            V2IMergeGridManager mm = new V2IMergeGridManager(
                    merge,
                    currentTime,
                    mergeReservationConfig,
                    layout.getMMRegistry(),
                    layout
            );
            mm.setMergePolicy(new BaseMergeGridPolicy(mm, new FCFSMergeGridRequestHandler()));
            layout.addMergeManager(mm);
        }
    }

    public static void setQueueMergeManagers(MergeMap layout, double currentTime) {
        layout.removeAllMergeManagers();
        for(MergeConnection merge : layout.getMergeConnections()) {
            V2IQueueMergeManager mm = new V2IQueueMergeManager(
                    merge,
                    currentTime,
                    layout.getMMRegistry()
            );
            layout.addMergeManager(mm);
        }
    }

    //SPAWN POINT SETTERS//
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

    public static void setSingleSpawnPointS2STargetOnly(S2SMergeMap map, VehicleSpec spec) {
        map.getTargetSpawnPoint().setVehicleSpecChooser(new SingleSpawnSpecGenerator(spec));
        map.getMergeSpawnPoint().setVehicleSpecChooser(new NoSpawnSpecGenerator());
    }

    public static void setSingleSpawnPointS2SMergeOnly(S2SMergeMap map, VehicleSpec spec) {
        map.getMergeSpawnPoint().setVehicleSpecChooser(new SingleSpawnSpecGenerator(spec));
        map.getTargetSpawnPoint().setVehicleSpecChooser(new NoSpawnSpecGenerator());
    }

    public static void setUniformSpawnSpecGenerator(MergeMap map, double trafficLevel) {
        for(MergeSpawnPoint sp : map.getSpawnPoints()) {
            sp.setVehicleSpecChooser(
                    new UniformSpawnSpecGenerator(trafficLevel)
            );
        }
    }

    public static void setUniformSpawnSpecGeneratorMergeLaneOnly(S2SMergeMap map, double trafficLevel) {
        map.getMergeSpawnPoint().setVehicleSpecChooser(new UniformSpawnSpecGenerator(trafficLevel));
        map.getTargetSpawnPoint().setVehicleSpecChooser(new NoSpawnSpecGenerator());
    }

    public static void setUniformSpawnSpecGeneratorTargetLaneOnly(S2SMergeMap map, double trafficLevel) {
        map.getTargetSpawnPoint().setVehicleSpecChooser(new UniformSpawnSpecGenerator(trafficLevel));
        map.getMergeSpawnPoint().setVehicleSpecChooser(new NoSpawnSpecGenerator());
    }

    public static void setJSONScheduleSpawnSpecGenerator(S2SMergeMap map, File mergeJson, File targetJson) {
        try {
            map.getMergeSpawnPoint().setVehicleSpecChooser(new JsonScheduleSpawnSpecGenerator(mergeJson));
            map.getTargetSpawnPoint().setVehicleSpecChooser(new JsonScheduleSpawnSpecGenerator(targetJson));
        } catch (Exception e) {
            throw new RuntimeException(
                    String.format(
                    "One of the files for the spawn schedules could not be used: %s\n",
                    e.getMessage()),
                    e
            );
        }
    }

    // SPAWN SPEC GENERATORS //
    public static class NoSpawnSpecGenerator implements MergeSpawnSpecGenerator {

        @Override
        public List<MergeSpawnSpec> act(MergeSpawnPoint spawnPoint, double timestep) {
            return new ArrayList<MergeSpawnSpec>();
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
            for(double time = initTime; time < initTime + timestep; time += SimConfig.SPAWN_TIME_STEP) {
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

    public static class JsonScheduleSpawnSpecGenerator implements MergeSpawnSpecGenerator {
        // NESTED CLASSES //
        public static class ScheduledSpawn {
            private String specName;
            private Double spawnTime;

            public ScheduledSpawn(String specName, Double spawnTime) {
                this.specName = specName;
                this.spawnTime = spawnTime;
            }

            public String getSpecName() {
                return specName;
            }

            public double getSpawnTime() {
                return spawnTime;
            }
        }

        // PRIVATE FIELDS //
        Queue<ScheduledSpawn> schedule;

        // CONSTRUCTOR //
        public JsonScheduleSpawnSpecGenerator(File jsonFile) throws IOException, ParseException {
            this.schedule = processJson(jsonFile);
        }

        private Queue<ScheduledSpawn> processJson(File jsonFile) throws IOException, ParseException {
            JSONParser parser = new JSONParser();

            Object array = parser.parse(new FileReader(jsonFile));
            JSONArray jsonSchedule = (JSONArray) array;

            Queue<ScheduledSpawn> schedule = new LinkedList<ScheduledSpawn>();
            for(Object spawnObj : jsonSchedule) {
                JSONObject jsonSpawn = (JSONObject) spawnObj;
                String specName = (String) jsonSpawn.get("specName");
                Double spawnTime = (Double) jsonSpawn.get("spawnTime");

                schedule.add(new ScheduledSpawn(specName, spawnTime));
            }
            return schedule;
        }

        // ACTION //
        @Override
        public List<MergeSpawnSpec> act(MergeSpawnPoint spawnPoint, double timestep) {
            double initTime = spawnPoint.getCurrentTime();
            List<MergeSpawnSpec> specs = new ArrayList<MergeSpawnSpec>();
            for (double time = initTime; time < initTime + timestep; time += SimConfig.SPAWN_TIME_STEP) {
                if(!schedule.isEmpty()) {
                    if (time > schedule.peek().getSpawnTime()) {
                        specs.add(new MergeSpawnSpec(
                                spawnPoint.getCurrentTime(),
                                VehicleSpecDatabase.getVehicleSpecByName(schedule.poll().getSpecName())
                        ));
                    }
                }
            }
            return specs;
        }
    }

    // SPAWN SCHEDULE GENERATOR //
    public static JSONArray createSpawnSchedule(double trafficLevel, double timeLimit, double speedLimit) {
        //Create Map to base the schedule on
        SingleLaneOnlyMap map = new SingleLaneOnlyMap(0, speedLimit, 200);
        MergeMapUtil.setUniformSpawnSpecGenerator(map, trafficLevel);

        //Create SpawnHelper
        Map<Integer, MergeVehicleSimModel> vinToVehicles = new HashMap<Integer, MergeVehicleSimModel>();
        SpawnHelper spawnHelper = new SpawnHelper(map, vinToVehicles);
        SensorInputHelper sensorInputHelper = new SensorInputHelper(map, vinToVehicles);

        //Create schedule
        JSONArray schedule = new JSONArray();
        double currentTime = 0;
        while (currentTime < timeLimit) {
            //Spawn Vehicles
            List<MergeVehicleSimModel> spawnedVehicles =
                    spawnHelper.spawnVehicles(SimConfig.MERGE_TIME_STEP, ProtocolType.NONE);
            if (spawnedVehicles != null) {
                VehicleSpec vSpec = spawnedVehicles.get(0).getSpec(); //Only expecting one.
                JSONObject scheduledSpawn = new JSONObject();
                scheduledSpawn.put("specName", vSpec.getName());
                scheduledSpawn.put("spawnTime", currentTime);
                schedule.add(scheduledSpawn);
            }

            //Provide sensor input
            sensorInputHelper.provideSensorInput();

            //Vehicle movement
            for(MergeVehicleSimModel vehicle : vinToVehicles.values()){
                vehicle.getDriver().act();
            }
            for(MergeVehicleSimModel vehicle : vinToVehicles.values()){
                vehicle.move(SimConfig.TIME_STEP);
            }
            List<MergeVehicleSimModel> removedVehicles = new ArrayList<MergeVehicleSimModel>(vinToVehicles.size());
            for(MergeVehicleSimModel vehicle : vinToVehicles.values()){
                if(!vehicle.getShape().intersects(map.getDimensions()))
                    removedVehicles.add(vehicle);
            }
            for(MergeVehicleSimModel vehicle : removedVehicles) {
                vinToVehicles.remove(vehicle.getVIN());
            }
            currentTime += SimConfig.TIME_STEP;
        }


        return schedule;
    }
}
