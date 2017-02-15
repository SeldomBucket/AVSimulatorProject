package aim4.sim;

import aim4.map.BasicMap;
import aim4.map.SpawnPoint;
import aim4.map.cpm.VerySimpleMap;
import aim4.map.lane.Lane;
import aim4.vehicle.VehicleSimModel;
import aim4.vehicle.VehicleSpec;
import aim4.vehicle.VinRegistry;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The simulator of AVs in an AV specific car park which are self-organising.
 */
public class CPMAutoDriverSimulator implements Simulator {

    /////////////////////////////////
    // NESTED CLASSES
    /////////////////////////////////

    /**
     * The result of a simulation step.
     */
    public static class CPMAutoDriverSimStepResult implements SimStepResult {

        /** The VIN of the completed vehicles in this time step */
        List<Integer> completedVINs;

        /**
         * Create a result of a simulation step
         *
         * @param completedVINs  the VINs of completed vehicles.
         */
        public CPMAutoDriverSimStepResult(List<Integer> completedVINs) {
            this.completedVINs = completedVINs;
        }

        /**
         * Get the list of VINs of completed vehicles.
         *
         * @return the list of VINs of completed vehicles.
         */
        public List<Integer> getCompletedVINs() {
            return completedVINs;
        }
    }

    /////////////////////////////////
    // PRIVATE FIELDS
    /////////////////////////////////

    /** The map */
    private VerySimpleMap simpleMap;
    /** All active vehicles, in form of a map from VINs to vehicle objects. */
    private Map<Integer,CPMBasicAutoVehicle> vinToVehicles;
    /** The current time */
    private double currentTime;
    /** The number of completed vehicles */
    private int numOfCompletedVehicles;
    /** The total number of bits transmitted by the completed vehicles */
    private int totalBitsTransmittedByCompletedVehicles;
    /** The total number of bits received by the completed vehicles */
    private int totalBitsReceivedByCompletedVehicles;

    public CPMAutoDriverSimulator(VerySimpleMap simpleMap){
        this.simpleMap = simpleMap;
        this.vinToVehicles = new HashMap<Integer,CPMBasicAutoVehicle>();

        currentTime = 0.0;
        numOfCompletedVehicles = 0;
        totalBitsTransmittedByCompletedVehicles = 0;
        totalBitsReceivedByCompletedVehicles = 0;

        System.out.println("CPM Simulator created!");
    }

    @Override
    public SimStepResult step(double timeStep) {
        spawnVehicles(timeStep);
        // provideSensorInput();
        // letDriversAct();
        // communication();
        // moveVehicles(timeStep);
        // List<Integer> completedVINs = cleanUpCompletedVehicles();
        currentTime += timeStep;
        // return new CPMAutoDriverSimStepResult(completedVINs);
        return null;
    }

    /**
     * Step 1: Spawn vehicles.
     *
     * @param timeStep  the time step
     */
    private void spawnVehicles(double timeStep) {
        for(SpawnPoint spawnPoint : simpleMap.getSpawnPoints()) {
            List<SpawnPoint.SpawnSpec> spawnSpecs = spawnPoint.act(timeStep);
            if (!spawnSpecs.isEmpty()) {
                if (canSpawnVehicle(spawnPoint)) {
                    for(SpawnPoint.SpawnSpec spawnSpec : spawnSpecs) {
                        CPMBasicAutoVehicle vehicle = makeVehicle(spawnPoint, spawnSpec);
                        VinRegistry.registerVehicle(vehicle); // Get vehicle a VIN number
                        vinToVehicles.put(vehicle.getVIN(), vehicle);
                        break; // only handle the first spawn vehicle
                        // TODO: need to fix this
                    }
                } // else ignore the spawnSpecs and do nothing
            }
        }
    }

    /**
     * Whether a spawn point can spawn any vehicle
     *
     * @param spawnPoint  the spawn point
     * @return Whether the spawn point can spawn any vehicle
     */
    private boolean canSpawnVehicle(SpawnPoint spawnPoint) {
        // return true for the moment.
        return true;
    }

    /**
     * Create a vehicle at a spawn point.
     *
     * @param spawnPoint  the spawn point
     * @param spawnSpec   the spawn specification
     * @return the vehicle
     */
    private CPMBasicAutoVehicle makeVehicle(SpawnPoint spawnPoint,
                                           SpawnPoint.SpawnSpec spawnSpec) {
        VehicleSpec spec = spawnSpec.getVehicleSpec();
        Lane lane = spawnPoint.getLane();
        // Now just take the minimum of the max velocity of the vehicle, and
        // the speed limit in the lane
        double initVelocity = Math.min(spec.getMaxVelocity(), lane.getSpeedLimit());
        // Obtain a Vehicle
        CPMBasicAutoVehicle vehicle =
                new CPMBasicAutoVehicle(spec,
                        spawnPoint.getPosition(),
                        spawnPoint.getHeading(),
                        spawnPoint.getSteeringAngle(),
                        initVelocity, // velocity
                        initVelocity,  // target velocity
                        spawnPoint.getAcceleration(),
                        spawnSpec.getSpawnTime());
        // Set the driver
        // AIMAutoDriver driver = new AIMAutoDriver(vehicle, basicIntersectionMap);
        // driver.setCurrentLane(lane);
        // driver.setSpawnPoint(spawnPoint);
        // driver.setDestination(spawnSpec.getDestinationRoad());
        // vehicle.setDriver(driver);

        return vehicle;
    }

    @Override
    public BasicMap getMap() {
        return null;
    }

    @Override
    public double getSimulationTime() {
        return 0;
    }

    @Override
    public int getNumCompletedVehicles() {
        return 0;
    }

    @Override
    public double getAvgBitsTransmittedByCompletedVehicles() {
        return 0;
    }

    @Override
    public double getAvgBitsReceivedByCompletedVehicles() {
        return 0;
    }

    @Override
    public VehicleSimModel getActiveVehicle(int vin) {
        return null;
    }
}
