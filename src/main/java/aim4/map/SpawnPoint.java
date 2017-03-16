package aim4.map;

import aim4.map.lane.Lane;
import aim4.vehicle.VehicleSpec;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * An abstract spawn point.
 */
public abstract class SpawnPoint {

    /////////////////////////////////
    // NESTED CLASSES
    /////////////////////////////////

    // TODO: make it sortable according to the time

    /**
     * The specification of a spawn.
     */
    protected static class SpawnSpec {
        /** The spawn time */
        double spawnTime;
        /** The vehicle specification */
        VehicleSpec vehicleSpec;

        /**
         * Create a spawn specification.
         *
         * @param spawnTime        the spawn time
         * @param vehicleSpec      the vehicle specification
         */
        public SpawnSpec(double spawnTime, VehicleSpec vehicleSpec) {
            this.spawnTime = spawnTime;
            this.vehicleSpec = vehicleSpec;
        }

        /**
         * Get the spawn time.
         *
         * @return the spawn time
         */
        public double getSpawnTime() {
            return spawnTime;
        }

        /**
         * Get the vehicle specification.
         *
         * @return the vehicle specification
         */
        public VehicleSpec getVehicleSpec() {
            return vehicleSpec;
        }
    }

    /////////////////////////////////
    // PROTECTED FIELDS
    /////////////////////////////////

    /** The current time */
    protected double currentTime;
    /** The initial position of the vehicle */
    protected Point2D pos;
    /** The initial heading of the vehicle */
    protected double heading;
    /** The initial steering angle of the vehicle */
    protected double steeringAngle;
    /** The initial acceleration */
    protected double acceleration;
    /** The lane */
    protected Lane lane;
    /**
     * The area in which there should not have any other vehicle when the
     * vehicle is spawned.
     */
    protected Rectangle2D noVehicleZone;

    /////////////////////////////////
    // CONSTRUCTORS
    /////////////////////////////////

    /**
     * Create a spawn point.
     *
     * @param currentTime         the current time
     * @param pos                 the initial position
     * @param heading             the initial heading
     * @param steeringAngle       the initial steering angle
     * @param acceleration        the initial acceleration
     * @param lane                the lane
     * @param noVehicleZone       the no vehicle zone
     */
    public SpawnPoint(double currentTime,
                         Point2D pos,
                         double heading,
                         double steeringAngle,
                         double acceleration,
                         Lane lane,
                         Rectangle2D noVehicleZone) {
        this.currentTime = currentTime;
        this.pos = pos;
        this.heading = heading;
        this.steeringAngle = steeringAngle;
        this.acceleration = acceleration;
        this.lane = lane;
        this.noVehicleZone = noVehicleZone;
    }

    /**
     * Advance the time step.
     *
     * @param timeStep  the time step
     * @return The list of spawn spec generated in this time step
     */
    public abstract List<? extends SpawnSpec> act(double timeStep);

    /////////////////////////////////
    // PUBLIC METHODS
    /////////////////////////////////

    // info retrieval

    /**
     * Get the current time.
     *
     * @return the current time
     */
    public double getCurrentTime() {
        return currentTime;
    }

    /**
     * Get the initial position.
     *
     * @return the initial position
     */
    public Point2D getPosition() {
        return pos;
    }

    /**
     * Get the initial heading.
     *
     * @return the initial heading
     */
    public double getHeading() {
        return heading;
    }

    /**
     * Get the initial steering angle.
     *
     * @return the initial steering angle
     */
    public double getSteeringAngle() {
        return steeringAngle;
    }

    /**
     * Get the initial acceleration.
     *
     * @return the initial acceleration
     */
    public double getAcceleration() {
        return acceleration;
    }

    /**
     * Get the lane.
     *
     * @return the lane
     */
    public Lane getLane() {
        return lane;
    }

    /**
     * Get the no vehicle zone.
     *
     * @return the no vehicle zone
     */
    public Rectangle2D getNoVehicleZone() {
        return noVehicleZone;
    }
}
