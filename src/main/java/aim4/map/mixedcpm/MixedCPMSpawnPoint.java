package aim4.map.mixedcpm;

import aim4.map.SpawnPoint;
import aim4.map.lane.Lane;
import aim4.vehicle.VehicleSpec;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * A SpawnPoint for MixedCPM simulations.
 */
public class MixedCPMSpawnPoint extends SpawnPoint {

    /////////////////////////////////
    // NESTED CLASSES
    /////////////////////////////////

    /** The specification of a spawn */
    public static class MixedCPMSpawnSpec extends SpawnSpec {

        double parkingTime;
        boolean disabledVehicle;
        boolean automatedVehicle;

        /**
         * Create a spawn specification.
         *
         * @param spawnTime       the spawn time
         * @param vehicleSpec     the vehicle specification
         */
        public MixedCPMSpawnSpec(double spawnTime, VehicleSpec vehicleSpec, double parkingTime) {
            super(spawnTime, vehicleSpec);
            this.parkingTime = parkingTime;
            this.disabledVehicle = false;
            this.automatedVehicle = false;
        }

        /**
         * Create a spawn specification.
         *
         * @param spawnTime       the spawn time
         * @param vehicleSpec     the vehicle specification
         * @param disabledVehicle whether this vehicle is a disabled vehicle
         */
        public MixedCPMSpawnSpec(double spawnTime, VehicleSpec vehicleSpec, double parkingTime, boolean disabledVehicle) {
            super(spawnTime, vehicleSpec);
            this.parkingTime = parkingTime;
            this.disabledVehicle = disabledVehicle;
        }

        /**
         * Create a spawn specification.
         *
         * @param spawnTime       the spawn time
         * @param vehicleSpec     the vehicle specification
         * @param disabledVehicle whether this vehicle is a disabled vehicle
         */
        public MixedCPMSpawnSpec(double spawnTime,
                                 VehicleSpec vehicleSpec,
                                 double parkingTime,
                                 boolean disabledVehicle,
                                 boolean automatedVehicle) {
            super(spawnTime, vehicleSpec);
            this.parkingTime = parkingTime;
            this.disabledVehicle = disabledVehicle;
            this.automatedVehicle = automatedVehicle;
        }

        public double getParkingTime() { return parkingTime; }

        public boolean isDisabledVehicle() {
            return disabledVehicle;
        }

        public boolean isAutomatedVehicle(){
            return automatedVehicle;
        }
    }

    /**
     * The interface of the spawn specification generator.
     */
    public static interface MixedCPMSpawnSpecGenerator {
        List<MixedCPMSpawnSpec> act(MixedCPMSpawnPoint spawnPoint, double timestep);
        double generateParkingTime();
        boolean isDone();
    }

    /////////////////////////////////
    // PRIVATE FIELDS
    /////////////////////////////////

    private MixedCPMSpawnSpecGenerator vehicleSpecChooser;

    /////////////////////////////////
    // CONSTRUCTORS
    /////////////////////////////////

    /**
     * Create a spawn point with a vehicleSpecChooser.
     *
     * @param currentTime   the current time
     * @param pos           the initial position
     * @param heading       the initial heading
     * @param steeringAngle the initial steering angle
     * @param acceleration  the initial acceleration
     * @param lane          the lane
     * @param noVehicleZone the no vehicle zone
     * @param vehicleSpecChooser  the vehicle spec chooser
     */
    public MixedCPMSpawnPoint(double currentTime, Point2D pos,
                         double heading, double steeringAngle,
                         double acceleration, Lane lane,
                         Rectangle2D noVehicleZone,
                         MixedCPMSpawnSpecGenerator vehicleSpecChooser) {
        super(currentTime, pos, heading, steeringAngle,
                acceleration, lane, noVehicleZone);
        this.vehicleSpecChooser = vehicleSpecChooser;
    }

    /**
     * Create a spawn point without a vehicleSpecChooser.
     *
     * @param currentTime         the current time
     * @param pos                 the initial position
     * @param heading             the initial heading
     * @param steeringAngle       the initial steering angle
     * @param acceleration        the initial acceleration
     * @param lane                the lane
     * @param noVehicleZone       the no vehicle zone
     */
    public MixedCPMSpawnPoint(double currentTime,
                         Point2D pos,
                         double heading,
                         double steeringAngle,
                         double acceleration,
                         Lane lane,
                         Rectangle2D noVehicleZone) {
        super(currentTime, pos, heading, steeringAngle,
                acceleration, lane, noVehicleZone);
        this.vehicleSpecChooser = null;
    }

    /////////////////////////////////
    // PUBLIC METHODS
    /////////////////////////////////

    /**
     * Advance the time step.
     *
     * @param timeStep  the time step
     * @return The list of spawn spec generated in this time step
     */
    public List<MixedCPMSpawnSpec> act(double timeStep) {
        assert vehicleSpecChooser != null;
        List<MixedCPMSpawnSpec> spawnSpecs = vehicleSpecChooser.act(this, timeStep);
        currentTime += timeStep;
        return spawnSpecs;
    }

    /**
     * Set the vehicle spec chooser.
     *
     * @param vehicleSpecChooser the vehicle spec chooser
     */
    public void setVehicleSpecChooser(MixedCPMSpawnSpecGenerator vehicleSpecChooser) {
        this.vehicleSpecChooser = vehicleSpecChooser;
    }

    public MixedCPMSpawnSpecGenerator getVehicleSpecChooser() {
        return vehicleSpecChooser;
    }
}
