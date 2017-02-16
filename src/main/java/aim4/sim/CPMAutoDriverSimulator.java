package aim4.sim;

import aim4.config.Debug;
import aim4.config.DebugPoint;
import aim4.driver.AutoDriver;
import aim4.driver.cpm.CPMBasicV2VDriver;
import aim4.map.BasicMap;
import aim4.map.Road;
import aim4.map.SpawnPoint;
import aim4.map.cpm.VerySimpleMap;
import aim4.map.lane.Lane;
import aim4.vehicle.VehicleSimModel;
import aim4.vehicle.VehicleSpec;
import aim4.vehicle.VinRegistry;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

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
        provideSensorInput();
        // letDriversAct();
        // communication();
        // moveVehicles(timeStep);
        // List<Integer> completedVINs = cleanUpCompletedVehicles();
        currentTime += timeStep;
        // return new CPMAutoDriverSimStepResult(completedVINs);
        return null;
    }

    /////////////////////////////////
    // STEP 1
    /////////////////////////////////

    /**
     * Spawn vehicles.
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
        CPMBasicV2VDriver driver = new CPMBasicV2VDriver(vehicle, simpleMap);
        driver.setCurrentLane(lane);
        driver.setSpawnPoint(spawnPoint);
        vehicle.setDriver(driver);

        return vehicle;
    }

    /////////////////////////////////
    // STEP 2
    /////////////////////////////////

    /**
     * Provide each vehicle with sensor information to allow it to make
     * decisions.  This works first by making an ordered list for each Lane of
     * all the vehicles in that Lane, in order from the start of the Lane to
     * the end of the Lane.  We must make sure to leave out all vehicles that
     * are in the intersection.  We must also concatenate the lists for lanes
     * that feed into one another.  Then, for each vehicle, depending on the
     * state of its sensors, we provide it with the appropriate sensor input.
     */
    private void provideSensorInput() {
        Map<Lane,SortedMap<Double,CPMBasicAutoVehicle>> vehicleLists =
                computeVehicleLists();
        Map<CPMBasicAutoVehicle, CPMBasicAutoVehicle> nextVehicle =
                computeNextVehicle(vehicleLists);

        provideIntervalInfo(nextVehicle);
        provideVehicleTrackingInfo(vehicleLists);
    }

    /**
     * Compute the lists of vehicles of all lanes.
     *
     * @return a mapping from lanes to lists of vehicles sorted by their
     *         distance on their lanes
     */
    private Map<Lane,SortedMap<Double,CPMBasicAutoVehicle>> computeVehicleLists() {
        // Set up the structure that will hold all the Vehicles as they are
        // currently ordered in the Lanes
        Map<Lane,SortedMap<Double,CPMBasicAutoVehicle>> vehicleLists =
                new HashMap<Lane,SortedMap<Double,CPMBasicAutoVehicle>>();
        for(Road road : simpleMap.getRoads()) {
            for(Lane lane : road.getLanes()) {
                vehicleLists.put(lane, new TreeMap<Double,CPMBasicAutoVehicle>());
            }
        }
        // Now add each of the Vehicles, but make sure to exclude those that are
        // already inside (partially or entirely) the intersection
        for(CPMBasicAutoVehicle vehicle : vinToVehicles.values()) {
            // Find out what lanes it is in.
            Set<Lane> lanes = vehicle.getDriver().getCurrentlyOccupiedLanes();
            for(Lane lane : lanes) {
                // Now find how far along the lane it is.
                double dst = lane.distanceAlongLane(vehicle.getPosition());
                // Now add it to the map.
                vehicleLists.get(lane).put(dst, vehicle);
            }
        }
        // Now consolidate the lists based on lanes
        for(Road road : simpleMap.getRoads()) {
            for(Lane lane : road.getLanes()) {
                // We may have already removed this Lane from the map
                if(vehicleLists.containsKey(lane)) {
                    Lane currLane = lane;
                    // Now run through the lanes
                    while(currLane.hasNextLane()) {
                        currLane = currLane.getNextLane();
                        // Put everything from the next lane into the original lane
                        // and remove the mapping for the next lane
                        vehicleLists.get(lane).putAll(vehicleLists.remove(currLane));
                    }
                }
            }
        }

        return vehicleLists;
    }

    /**
     * Compute the next vehicles of all vehicles.
     *
     * @param vehicleLists  a mapping from lanes to lists of vehicles sorted by
     *                      their distance on their lanes
     * @return a mapping from vehicles to next vehicles
     */
    private Map<CPMBasicAutoVehicle, CPMBasicAutoVehicle> computeNextVehicle(
            Map<Lane,SortedMap<Double,CPMBasicAutoVehicle>> vehicleLists) {
        // At this point we should only have mappings for start Lanes, and they
        // should include all the Lanes they run into.  Now we need to turn this
        // into a hash map that maps Vehicles to the next vehicle in the Lane
        // or any Lane the Lane runs into
        Map<CPMBasicAutoVehicle, CPMBasicAutoVehicle> nextVehicle =
                new HashMap<CPMBasicAutoVehicle,CPMBasicAutoVehicle>();
        // For each of the ordered lists of vehicles
        for(SortedMap<Double,CPMBasicAutoVehicle> vehicleList : vehicleLists.values()) {
            CPMBasicAutoVehicle lastVehicle = null;
            // Go through the Vehicles in order of their position in the Lane
            for(CPMBasicAutoVehicle currVehicle : vehicleList.values()) {
                if(lastVehicle != null) {
                    // Create the mapping from the previous Vehicle to the current one
                    nextVehicle.put(lastVehicle,currVehicle);
                }
                lastVehicle = currVehicle;
            }
        }

        return nextVehicle;
    }

    /**
     * Provide sensing information to the intervalometers of all vehicles.
     *
     * @param nextVehicle  a mapping from vehicles to next vehicles
     */
    private void provideIntervalInfo(
            Map<CPMBasicAutoVehicle, CPMBasicAutoVehicle> nextVehicle) {

        // Now that we have this list set up, let's provide input to all the
        // Vehicles.
        for(CPMBasicAutoVehicle vehicle: vinToVehicles.values()) {
            // If the vehicle is autonomous
            if (vehicle instanceof CPMBasicAutoVehicle) {


                switch(vehicle.getLRFMode()) {
                    case DISABLED:
                        // Find the interval to the next vehicle
                        double interval;
                        // If there is a next vehicle, then calculate it
                        if(nextVehicle.containsKey(vehicle)) {
                            // It's the distance from the front of this Vehicle to the point
                            // at the rear of the Vehicle in front of it
                            interval = calcInterval(vehicle, nextVehicle.get(vehicle));
                        } else { // Otherwise, just set it to the maximum possible value
                            interval = Double.MAX_VALUE;
                        }
                        // Now actually record it in the vehicle
                        vehicle.getIntervalometer().record(interval);
                        vehicle.setLRFSensing(false); // Vehicle is not using
                        // the LRF sensor
                        break;
                    case LIMITED:
                        // FIXME
                        vehicle.setLRFSensing(true); // Vehicle is using the LRF sensor
                        break;
                    case ENABLED:
                        // FIXME
                        vehicle.setLRFSensing(true); // Vehicle is using the LRF sensor
                        break;
                    default:
                        throw new RuntimeException("Unknown LRF Mode: " +
                                vehicle.getLRFMode().toString());
                }
            }
        }
    }

    /**
     * Calculate the distance between vehicle and the next vehicle on a lane.
     *
     * @param vehicle      the vehicle
     * @param nextVehicle  the next vehicle
     * @return the distance between vehicle and the next vehicle on a lane
     */
    private double calcInterval(CPMBasicAutoVehicle vehicle,
                                CPMBasicAutoVehicle nextVehicle) {
        // From Chiu: Kurt, if you think this function is not okay, probably
        // we should talk to see what to do.
        Point2D pos = vehicle.getPosition();
        if(nextVehicle.getShape().contains(pos)) {
            return 0.0;
        } else {
            // TODO: make it more efficient
            double interval = Double.MAX_VALUE ;
            for(Line2D edge : nextVehicle.getEdges()) {
                double dst = edge.ptSegDist(pos);
                if(dst < interval){
                    interval = dst;
                }
            }
            return interval;
        }
    }

    /**
     * Provide tracking information to vehicles.
     *
     * @param vehicleLists  a mapping from lanes to lists of vehicles sorted by
     *                      their distance on their lanes
     */
    private void provideVehicleTrackingInfo(
            Map<Lane, SortedMap<Double, CPMBasicAutoVehicle>> vehicleLists) {
        // Vehicle Tracking
        for(CPMBasicAutoVehicle vehicle: vinToVehicles.values()) {
            // If the vehicle is autonomous
            if (vehicle instanceof CPMBasicAutoVehicle) {
                CPMBasicAutoVehicle autoVehicle = (CPMBasicAutoVehicle)vehicle;

                if (autoVehicle.isVehicleTracking()) {
                    AutoDriver driver = autoVehicle.getDriver();
                    Lane targetLane = autoVehicle.getTargetLaneForVehicleTracking();
                    Point2D pos = autoVehicle.getPosition();
                    double dst = targetLane.distanceAlongLane(pos);

                    // initialize the distances to infinity
                    double frontDst = Double.MAX_VALUE;
                    double rearDst = Double.MAX_VALUE;
                    CPMBasicAutoVehicle frontVehicle = null ;
                    CPMBasicAutoVehicle rearVehicle = null ;

                    // only consider the vehicles on the target lane
                    SortedMap<Double,CPMBasicAutoVehicle> vehiclesOnTargetLane =
                            vehicleLists.get(targetLane);

                    // compute the distances and the corresponding vehicles
                    try {
                        double d = vehiclesOnTargetLane.tailMap(dst).firstKey();
                        frontVehicle = vehiclesOnTargetLane.get(d);
                        frontDst = (d-dst)-frontVehicle.getSpec().getLength();
                    } catch(NoSuchElementException e) {
                        frontDst = Double.MAX_VALUE;
                        frontVehicle = null;
                    }
                    try {
                        double d = vehiclesOnTargetLane.headMap(dst).lastKey();
                        rearVehicle = vehiclesOnTargetLane.get(d);
                        rearDst = dst-d;
                    } catch(NoSuchElementException e) {
                        rearDst = Double.MAX_VALUE;
                        rearVehicle = null;
                    }

                    // assign the sensor readings

                    autoVehicle.getFrontVehicleDistanceSensor().record(frontDst);
                    autoVehicle.getRearVehicleDistanceSensor().record(rearDst);

                    // assign the vehicles' velocities

                    if(frontVehicle!=null) {
                        autoVehicle.getFrontVehicleSpeedSensor().record(
                                frontVehicle.getVelocity());
                    } else {
                        autoVehicle.getFrontVehicleSpeedSensor().record(Double.MAX_VALUE);
                    }
                    if(rearVehicle!=null) {
                        autoVehicle.getRearVehicleSpeedSensor().record(
                                rearVehicle.getVelocity());
                    } else {
                        autoVehicle.getRearVehicleSpeedSensor().record(Double.MAX_VALUE);
                    }

                    // show the section on the viewer
                    if (Debug.isTargetVIN(driver.getVehicle().getVIN())) {
                        Point2D p1 = targetLane.getPointAtNormalizedDistance(
                                Math.max((dst-rearDst)/targetLane.getLength(),0.0));
                        Point2D p2 = targetLane.getPointAtNormalizedDistance(
                                Math.min((frontDst+dst)/targetLane.getLength(),1.0));
                        Debug.addLongTermDebugPoint(
                                new DebugPoint(p2, p1, "cl", Color.RED.brighter()));
                    }
                }
            }
        }

    }



    @Override
    public BasicMap getMap() {
        return simpleMap;
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
