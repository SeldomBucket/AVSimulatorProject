package aim4.map.cpm;

import aim4.map.SpawnPoint;
import aim4.map.lane.Lane;
import aim4.vehicle.VehicleSpec;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * Created by Callum on 14/03/2017.
 */
public class CPMSpawnPoint extends SpawnPoint {
    private CPMSpawnSpecGenerator vehicleSpecChooser;

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
    public CPMSpawnPoint(double currentTime, Point2D pos, double heading, double steeringAngle, double acceleration,
                         Lane lane, Rectangle2D noVehicleZone) {
        super(currentTime, pos, heading, steeringAngle, acceleration, lane, noVehicleZone);
    }

    public void setVehicleSpecChooser(CPMSpawnSpecGenerator vehicleSpecChooser) {
        this.vehicleSpecChooser = vehicleSpecChooser;
    }

    public static class CPMSpawnSpec extends SpawnSpec {
        /**
         * Create a spawn specification.
         *
         * @param spawnTime   the spawn time
         * @param vehicleSpec the vehicle specification
         */
        public CPMSpawnSpec(double spawnTime, VehicleSpec vehicleSpec) {
            super(spawnTime, vehicleSpec);
        }
    }

    public static interface CPMSpawnSpecGenerator {
        List<CPMSpawnSpec> act(CPMSpawnPoint spawnPoint, double timestep);
    }

    @Override
    public List<CPMSpawnSpec> act(double timeStep) {
        return null;
    }
}
