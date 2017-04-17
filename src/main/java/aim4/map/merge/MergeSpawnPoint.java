package aim4.map.merge;

import aim4.map.SpawnPoint;
import aim4.map.lane.Lane;
import aim4.vehicle.VehicleSpec;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * Created by Callum on 13/03/2017.
 */
public class MergeSpawnPoint extends SpawnPoint {
    private MergeSpawnSpecGenerator vehicleSpecChooser;

    /**
     * Create a spawn point.
     *
     * @param currentTime   the current time
     * @param pos           the initial position
     * @param heading       the initial heading
     * @param steeringAngle the initial steering angle
     * @param acceleration  the initial acceleration
     * @param lane          the lane
     * @param noVehicleZone the no vehicle zone
     */
    public MergeSpawnPoint(double currentTime, Point2D pos, double heading, double steeringAngle, double acceleration, Lane lane, Path2D noVehicleZone) {
        super(currentTime, pos, heading, steeringAngle, acceleration, lane, noVehicleZone);
    }

    public static class MergeSpawnSpec extends SpawnSpec {
        /**
         * Create a spawn specification.
         *
         * @param spawnTime   the spawn time
         * @param vehicleSpec the vehicle specification
         */
        public MergeSpawnSpec(double spawnTime, VehicleSpec vehicleSpec) {
            super(spawnTime, vehicleSpec);
        }
    }

    public static interface MergeSpawnSpecGenerator {
        List<MergeSpawnSpec> act(MergeSpawnPoint spawnPoint, double timestep);
    }

    @Override
    public List<MergeSpawnSpec> act(double timeStep) {
        assert vehicleSpecChooser != null;
        List<MergeSpawnSpec> spawnSpecs = vehicleSpecChooser.act(this, timeStep);
        currentTime += timeStep;
        return spawnSpecs;
    }

    public void setVehicleSpecChooser(MergeSpawnSpecGenerator vehicleSpecChooser) {
        this.vehicleSpecChooser = vehicleSpecChooser;
    }
}
