package aim4.map.merge;

import aim4.map.SpawnPoint;
import aim4.map.lane.Lane;
import aim4.vehicle.VehicleSpec;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * Created by Callum on 13/03/2017.
 */
public class MergeSpawnPoint extends SpawnPoint {

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
    public MergeSpawnPoint(double currentTime, Point2D pos, double heading, double steeringAngle, double acceleration, Lane lane, Rectangle2D noVehicleZone) {
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

    @Override
    public List<MergeSpawnSpec> act(double timeStep) {
        return null;
    }
}
