package aim4.sim.simulator.merge.helper;

import aim4.driver.merge.MergeAutoDriver;
import aim4.map.lane.Lane;
import aim4.map.merge.MergeMap;
import aim4.map.merge.MergeSpawnPoint;
import aim4.vehicle.VehicleSpec;
import aim4.vehicle.VinRegistry;
import aim4.vehicle.merge.MergeAutoVehicleSimModel;
import aim4.vehicle.merge.MergeBasicAutoVehicle;
import aim4.vehicle.merge.MergeVehicleSimModel;

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;

/**
 * Created by Callum on 15/03/2017.
 */
public class SpawnHelper {
    private MergeMap map;
    private Map<Integer, MergeVehicleSimModel> vinToVehicles;

    public SpawnHelper(MergeMap map, Map<Integer, MergeVehicleSimModel> vinToVehicles){
        this.map = map;
        this.vinToVehicles = vinToVehicles;
    }

    /**
     * Spawn vehicles
     *
     * @param timeStep
     */
    public void spawnVehicles(double timeStep) {
        for(MergeSpawnPoint spawnPoint : map.getSpawnPoints()) {
            List<MergeSpawnPoint.MergeSpawnSpec> spawnSpecs = spawnPoint.act(timeStep);
            if(!spawnSpecs.isEmpty()){
                if(canSpawnVehicle(spawnPoint)) {
                    for(MergeSpawnPoint.MergeSpawnSpec spawnSpec : spawnSpecs) {
                        MergeVehicleSimModel vehicle = makeVehicle(spawnPoint, spawnSpec);
                        VinRegistry.registerVehicle(vehicle);
                        vinToVehicles.put(vehicle.getVIN(), vehicle);
                    }
                }
            }
        }
    }

    /**
     * Checks if the spawn point can spawn a vehicle, based on the size of it's no spawn zone.
     * @param spawnPoint
     * @return
     */
    private boolean canSpawnVehicle(MergeSpawnPoint spawnPoint) {
        Rectangle2D noVehicleZone = spawnPoint.getNoVehicleZone();
        for(MergeVehicleSimModel vehicle : vinToVehicles.values()) {
            if (vehicle.getShape().intersects(noVehicleZone)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a vehicle at the spawn point.
     * @param spawnPoint
     * @param spawnSpec
     * @return
     */
    private MergeVehicleSimModel makeVehicle(MergeSpawnPoint spawnPoint, MergeSpawnPoint.MergeSpawnSpec spawnSpec) {
        VehicleSpec spec = spawnSpec.getVehicleSpec();
        Lane lane = spawnPoint.getLane();
        double initVelocity = Math.min(spec.getMaxVelocity(), lane.getSpeedLimit());

        MergeAutoVehicleSimModel vehicle =
                new MergeBasicAutoVehicle(spec,
                        spawnPoint.getPosition(),
                        spawnPoint.getHeading(),
                        spawnPoint.getSteeringAngle(),
                        initVelocity,
                        initVelocity,
                        spawnPoint.getAcceleration(),
                        spawnSpec.getSpawnTime());

        MergeAutoDriver driver = new MergeAutoDriver(vehicle, map);
        driver.setCurrentLane(lane);
        driver.setSpawnPoint(spawnPoint);
        vehicle.setDriver(driver);



        return vehicle;
    }
}
