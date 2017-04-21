package aim4.sim.simulator.merge.helper;

import aim4.driver.merge.MergeAutoDriver;
import aim4.driver.merge.MergeV2IAutoDriver;
import aim4.map.lane.Lane;
import aim4.map.merge.MergeMap;
import aim4.map.merge.MergeSpawnPoint;
import aim4.sim.setup.merge.enums.ProtocolType;
import aim4.vehicle.VehicleSpec;
import aim4.vehicle.VinRegistry;
import aim4.vehicle.merge.*;

import java.awt.geom.Path2D;
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
    public void spawnVehicles(double timeStep, ProtocolType protocolType) {
        for(MergeSpawnPoint spawnPoint : map.getSpawnPoints()) {
            List<MergeSpawnPoint.MergeSpawnSpec> spawnSpecs = spawnPoint.act(timeStep);
            if(!spawnSpecs.isEmpty()){
                if(canSpawnVehicle(spawnPoint)) {
                    for(MergeSpawnPoint.MergeSpawnSpec spawnSpec : spawnSpecs) {
                        MergeVehicleSimModel vehicle = setupVehicle(spawnPoint, spawnSpec, protocolType);
                        VinRegistry.registerVehicle(vehicle);
                        vinToVehicles.put(vehicle.getVIN(), vehicle);
                        if(!canSpawnVehicle(spawnPoint))
                            break;
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
        assert spawnPoint.getNoVehicleZone() instanceof Path2D;
        Path2D noVehicleZone = (Path2D) spawnPoint.getNoVehicleZone();
        for(MergeVehicleSimModel vehicle : vinToVehicles.values()) {
            if (noVehicleZone.intersects(vehicle.getShape().getBounds2D())) {
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
    private MergeVehicleSimModel setupVehicle(
            MergeSpawnPoint spawnPoint,
            MergeSpawnPoint.MergeSpawnSpec spawnSpec,
            ProtocolType protocolType) {
        VehicleSpec spec = spawnSpec.getVehicleSpec();
        Lane lane = spawnPoint.getLane();
        double initVelocity = Math.min(spec.getMaxVelocity(), lane.getSpeedLimit());

        switch(protocolType){
            case AIM_GRID:
                return makeV2IVehicle(spawnPoint,initVelocity,lane,spec,spawnSpec,protocolType);
            case AIM_NO_GRID:
                return makeV2IVehicle(spawnPoint,initVelocity,lane,spec,spawnSpec,protocolType);
            case QUEUE:
                return makeV2IVehicle(spawnPoint,initVelocity,lane,spec,spawnSpec,protocolType);
            case TEST_MERGE:
                return makeAutoVehicle(spawnPoint,initVelocity,lane,spec,spawnSpec);
            case TEST_TARGET:
                return makeAutoVehicle(spawnPoint,initVelocity,lane,spec,spawnSpec);
            case NONE:
                return makeAutoVehicle(spawnPoint,initVelocity,lane,spec,spawnSpec);
            default:
                throw new UnsupportedOperationException(String.format(
                        "ProtocolType %s not supported",
                        protocolType.toString())
                );
        }
    }

    private MergeVehicleSimModel  makeAutoVehicle(
            MergeSpawnPoint spawnPoint,
            double initVelocity, Lane lane,
            VehicleSpec spec,
            MergeSpawnPoint.MergeSpawnSpec spawnSpec) {
        MergeAutoVehicleSimModel vehicle =
                new MergeBasicAutoVehicle(spec,
                        spawnPoint.getPosition(),
                        spawnPoint.getHeading(),
                        initVelocity,
                        spawnPoint.getSteeringAngle(),
                        spawnPoint.getAcceleration(),
                        lane.getSpeedLimit(),
                        spawnSpec.getSpawnTime());
        vehicle.setStartTime(spawnPoint.getCurrentTime());

        MergeAutoDriver driver = new MergeAutoDriver(vehicle, map);
        driver.setCurrentLane(lane);
        driver.setSpawnPoint(spawnPoint);
        vehicle.setDriver(driver);

        return vehicle;
    }

    private MergeVehicleSimModel makeV2IVehicle(
            MergeSpawnPoint spawnPoint,
            double initVelocity, Lane lane,
            VehicleSpec spec,
            MergeSpawnPoint.MergeSpawnSpec spawnSpec,
            ProtocolType protocolType) {
        MergeV2IAutoVehicleSimModel vehicle =
                new MergeV2IAutoVehicle(spec,
                        spawnPoint.getPosition(),
                        spawnPoint.getHeading(),
                        initVelocity,
                        spawnPoint.getSteeringAngle(),
                        spawnPoint.getAcceleration(),
                        lane.getSpeedLimit(),
                        spawnSpec.getSpawnTime());
        vehicle.setStartTime(spawnPoint.getCurrentTime());

        MergeV2IAutoDriver driver = new MergeV2IAutoDriver(vehicle, map, protocolType);
        driver.setCurrentLane(lane);
        driver.setSpawnPoint(spawnPoint);
        vehicle.setDriver(driver);

        return vehicle;
    }
}
