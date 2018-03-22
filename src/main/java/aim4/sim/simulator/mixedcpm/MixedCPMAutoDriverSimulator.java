package aim4.sim.simulator.mixedcpm;

import aim4.config.Debug;
import aim4.config.DebugPoint;
import aim4.driver.mixedcpm.MixedCPMManualDriver;
import aim4.map.DataCollectionLine;
import aim4.map.Road;
import aim4.map.mixedcpm.*;
import aim4.map.mixedcpm.MixedCPMSpawnPoint.*;
/*
import aim4.map.mixedcpm.parking.SensoredLine;
import aim4.map.mixedcpm.parking.StatusMonitor;
*/
import aim4.map.lane.Lane;
import aim4.sim.Simulator;
import aim4.sim.results.SimulatorResult;
import aim4.vehicle.VehicleSimModel;
import aim4.vehicle.VehicleSpec;
import aim4.vehicle.VinRegistry;
import aim4.vehicle.mixedcpm.MixedCPMBasicManualVehicle;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

/**
 * The simulator of AVs in an AV specific car park which are self-organising.
 */
public class MixedCPMAutoDriverSimulator implements Simulator {
    /////////////////////////////////
    // NESTED CLASSES
    /////////////////////////////////

    /**
     * The result of a simulation step.
     */
    public static class MixedCPMAutoDriverSimStepResult implements SimStepResult {

        /** The VIN of the completed vehicles in this time step */
        List<MixedCPMBasicManualVehicle> completedVehicles;

        /**
         * Create a result of a simulation step
         *
         * @param completedVehicles  the completed vehicles.
         */
        public MixedCPMAutoDriverSimStepResult(List<MixedCPMBasicManualVehicle> completedVehicles) {
            this.completedVehicles = completedVehicles;
        }

        /**
         * Get the list of completed vehicles.
         *
         * @return the list of completed vehicles.
         */
        public List<MixedCPMBasicManualVehicle> getCompletedVehicles() {
            return completedVehicles;
        }
    }

    // TODO CPM find this value, must be defined somewhere
    public static final double MIN_DISTANCE_BETWEEN_PARKED_VEHICLES = 0.2;

    /////////////////////////////////
    // PRIVATE FIELDS
    /////////////////////////////////

    /** The map */
    protected MixedCPMBasicMap map;
    /** All active vehicles, in form of a map from VINs to vehicle objects. */
    protected Map<Integer,MixedCPMBasicManualVehicle> vinToVehicles;
    /** The current time */
    protected double currentTime;
    /** The number of completed vehicles */
    protected int numOfCompletedVehicles;
    /** A list of parked vehicles */
    protected List<MixedCPMBasicManualVehicle> parkedVehicles;
    /** The total number of bits transmitted by the completed vehicles */
    private int totalBitsTransmittedByCompletedVehicles;
    /** The total number of bits received by the completed vehicles */
    private int totalBitsReceivedByCompletedVehicles;

    public MixedCPMAutoDriverSimulator(MixedCPMBasicMap map){
        this.map = map;
        this.vinToVehicles = new HashMap<Integer,MixedCPMBasicManualVehicle>();
        this.parkedVehicles = new ArrayList<MixedCPMBasicManualVehicle>();

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
        letDriversAct();
        moveVehicles(timeStep);
        observeParkedVehicles();
        observeNumberOfVehiclesInCarPark();
        List<MixedCPMBasicManualVehicle> completedVehicles = cleanUpCompletedVehicles();
        currentTime += timeStep;
        return new MixedCPMAutoDriverSimStepResult(completedVehicles);
    }

    /////////////////////////////////
    // STEP 1
    /////////////////////////////////

    /**
     * Spawn vehicles.
     *
     * @param timeStep  the time step
     */
    protected void spawnVehicles(double timeStep) {
        for(MixedCPMSpawnPoint spawnPoint : map.getSpawnPoints()) {
            if (canSpawnVehicle(spawnPoint)) {
                List<MixedCPMSpawnPoint.MixedCPMSpawnSpec> spawnSpecs = spawnPoint.act(timeStep);
                for(MixedCPMSpawnSpec spawnSpec : spawnSpecs) {
                    // Check that the car park caters for vehicles this wide
                    double vehicleWidth = spawnSpec.getVehicleSpec().getWidth();
                    double parkingLaneWidth = map.getManualParkingArea().getLaneWidth();
                    if (parkingLaneWidth < (vehicleWidth+MIN_DISTANCE_BETWEEN_PARKED_VEHICLES)) {
                        System.out.println("Spawned vehicle discarded: car park doesn't cater for vehicles this wide.");
                    } else {
                        // Only create the vehicle if there is room in the car park
                        double vehicleLength = spawnSpec.getVehicleSpec().getLength();
                            MixedCPMBasicManualVehicle vehicle = makeVehicle(spawnPoint, spawnSpec);
                            VinRegistry.registerVehicle(vehicle); // Get vehicle a VIN number
                            vinToVehicles.put(vehicle.getVIN(), vehicle);
                            map.addVehicleToMap(vehicle);
                        if (map.getStatusMonitor().addNewVehicle(vehicle)) {
                            break; // only handle the first spawn vehicle
                        } else {
                            System.out.println("Spawned vehicle discarded: not enough room.");
                        }
                    }
                }
            } // else ignore the spawnSpecs and do nothingSystem.out.println("No vehicle spawned: canSpawn = False.");
        }
    }

    /**
     * Whether a spawn point can spawn any vehicle
     *
     * @param spawnPoint  the spawn point
     * @return Whether the spawn point can spawn a vehicle
     */
    protected boolean canSpawnVehicle(MixedCPMSpawnPoint spawnPoint) {
        // TODO: can be made much faster.
        assert spawnPoint.getNoVehicleZone() instanceof Rectangle2D;
        Rectangle2D noVehicleZone = (Rectangle2D) spawnPoint.getNoVehicleZone();
        for(MixedCPMBasicManualVehicle vehicle : vinToVehicles.values()) {
            if (vehicle.getShape().intersects(noVehicleZone)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Create a vehicle at a spawn point.
     *
     * @param spawnPoint  the spawn point
     * @param spawnSpec   the spawn specification
     * @return the vehicle
     */
    protected MixedCPMBasicManualVehicle makeVehicle(MixedCPMSpawnPoint spawnPoint,
                                              MixedCPMSpawnSpec spawnSpec) {
        VehicleSpec spec = spawnSpec.getVehicleSpec();
        Lane lane = spawnPoint.getLane();
        // Now just take the minimum of the max velocity of the vehicle, and
        // the speed limit in the lane
        double initVelocity = Math.min(spec.getMaxVelocity(), lane.getSpeedLimit());
        // Generate a length of time that this car should park for
        // This is from entering to when the EXITING state is set.


        // Obtain a Vehicle
        MixedCPMBasicManualVehicle vehicle =
                new MixedCPMBasicManualVehicle(spec,
                        spawnPoint.getPosition(),
                        spawnPoint.getHeading(),
                        spawnPoint.getSteeringAngle(),
                        initVelocity, // velocity
                        initVelocity,  // target velocity
                        spawnPoint.getAcceleration(),
                        spawnSpec.getSpawnTime(),
                        spawnSpec.getParkingTime());
        // Set the driver
        MixedCPMManualDriver driver = new MixedCPMManualDriver(vehicle, map);
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
    protected void provideSensorInput() {
        Map<Lane,SortedMap<Double,MixedCPMBasicManualVehicle>> vehicleLists =
                computeVehicleLists();
        Map<MixedCPMBasicManualVehicle, MixedCPMBasicManualVehicle> nextVehicle =
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
    private Map<Lane,SortedMap<Double,MixedCPMBasicManualVehicle>> computeVehicleLists() {
        // Set up the structure that will hold all the Vehicles as they are
        // currently ordered in the Lanes
        Map<Lane,SortedMap<Double,MixedCPMBasicManualVehicle>> vehicleLists =
                new HashMap<Lane,SortedMap<Double,MixedCPMBasicManualVehicle>>();
        for(Road road : map.getRoads()) {
            for(Lane lane : road.getLanes()) {
                vehicleLists.put(lane, new TreeMap<Double,MixedCPMBasicManualVehicle>());
            }
        }
        // Now add each of the Vehicles, but make sure to exclude those that are
        // already inside (partially or entirely) the intersection
        for(MixedCPMBasicManualVehicle vehicle : vinToVehicles.values()) {
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
        for(Road road : map.getRoads()) {
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
    private Map<MixedCPMBasicManualVehicle, MixedCPMBasicManualVehicle> computeNextVehicle(
            Map<Lane,SortedMap<Double,MixedCPMBasicManualVehicle>> vehicleLists) {
        // At this point we should only have mappings for start Lanes, and they
        // should include all the Lanes they run into.  Now we need to turn this
        // into a hash map that maps Vehicles to the next vehicle in the Lane
        // or any Lane the Lane runs into
        Map<MixedCPMBasicManualVehicle, MixedCPMBasicManualVehicle> nextVehicle =
                new HashMap<MixedCPMBasicManualVehicle,MixedCPMBasicManualVehicle>();
        // For each of the ordered lists of vehicles
        for(SortedMap<Double,MixedCPMBasicManualVehicle> vehicleList : vehicleLists.values()) {
            MixedCPMBasicManualVehicle lastVehicle = null;
            // Go through the Vehicles in order of their position in the Lane
            for(MixedCPMBasicManualVehicle currVehicle : vehicleList.values()) {
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
            Map<MixedCPMBasicManualVehicle, MixedCPMBasicManualVehicle> nextVehicle) {

        // Now that we have this list set up, let's provide input to all the
        // Vehicles.
        for(MixedCPMBasicManualVehicle vehicle: vinToVehicles.values()) {
            // If the vehicle is autonomous
            if (vehicle != null) {
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
    private double calcInterval(MixedCPMBasicManualVehicle vehicle,
                                MixedCPMBasicManualVehicle nextVehicle) {
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
            Map<Lane, SortedMap<Double, MixedCPMBasicManualVehicle>> vehicleLists) {
        // Vehicle Tracking
        for(MixedCPMBasicManualVehicle vehicle: vinToVehicles.values()) {
            // If the vehicle is autonomous
            if (vehicle != null) {
                MixedCPMBasicManualVehicle autoVehicle = vehicle;

                if (autoVehicle.isVehicleTracking()) {
                    MixedCPMManualDriver driver = (MixedCPMManualDriver)autoVehicle.getDriver();
                    Lane targetLane = autoVehicle.getTargetLaneForVehicleTracking();
                    Point2D pos = autoVehicle.getPosition();
                    double dst = targetLane.distanceAlongLane(pos);

                    // initialize the distances to infinity
                    double frontDst = Double.MAX_VALUE;
                    double rearDst = Double.MAX_VALUE;
                    MixedCPMBasicManualVehicle frontVehicle = null ;
                    MixedCPMBasicManualVehicle rearVehicle = null ;

                    // only consider the vehicles on the target lane
                    SortedMap<Double,MixedCPMBasicManualVehicle> vehiclesOnTargetLane =
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

    /////////////////////////////////
    // STEP 3
    /////////////////////////////////


    /**
     * Allow each driver to act.
     */
    protected void letDriversAct() {
        for(MixedCPMBasicManualVehicle vehicle : vinToVehicles.values()) {
            vehicle.getDriver().act();
        }
    }

    /////////////////////////////////
    // STEP 4
    /////////////////////////////////


    /**
     * Move all the vehicles.
     *
     * @param timeStep  the time step
     */
    protected void moveVehicles(double timeStep) {
        for(MixedCPMBasicManualVehicle vehicle : vinToVehicles.values()) {
            Point2D p1 = vehicle.getPosition();
            vehicle.move(timeStep);
            Point2D p2 = vehicle.getPosition();
            MixedCPMMapUtil.checkVehicleStillOnMap(map, p2, vehicle.getDriver().getCurrentLane());

            // Check if we've gone through a data collection line
            for(DataCollectionLine line : map.getDataCollectionLines()) {
                line.intersect(vehicle, currentTime, p1, p2);
            }

            // Check if we've gone through a sensored line
            // TODO CPM try remove the need for this assertion
            /*
            assert map instanceof MixedCPMCarParkWithStatus;
            for (SensoredLine line : ((CPMCarParkWithStatus) map).getSensoredLines()) {
                if (line.intersect(vehicle, currentTime, p1, p2)) {
                    StatusMonitor statusMonitor = map.getStatusMonitor();
                    if (line.getType() == SensoredLine.SensoredLineType.ENTRY) {
                        System.out.println("Vehicle is entering.");
                        vehicle.setEntryTime(currentTime);
                        statusMonitor.vehicleOnEntry(vehicle);
                    }
                    if (line.getType() == SensoredLine.SensoredLineType.REENTRY) {
                        System.out.println("Vehicle is re-entering.");
                        statusMonitor.vehicleOnReEntry(vehicle);
                    }
                    if (line.getType() == SensoredLine.SensoredLineType.EXIT) {
                        System.out.println("Vehicle is exiting.");
                        vehicle.setExitTime(currentTime);
                        statusMonitor.vehicleOnExit(vehicle);
                    }
                }
            }
            */

            // Update the time left for the vehicle to be parked.
            if (vehicle.hasEnteredCarPark()) {
                vehicle.updateTimeUntilExit(timeStep);
            }

            if (Debug.isPrintVehicleStateOfVIN(vehicle.getVIN())) {
                vehicle.printState();
            }
        }
    }

    /////////////////////////////////
    // STEP 5
    /////////////////////////////////

    protected void observeParkedVehicles() {
        parkedVehicles.clear();
        List<MixedCPMBasicManualVehicle> vehicles = map.getVehicles();
        for (MixedCPMBasicManualVehicle vehicle : vehicles) {
            // Check if the vehicle is in a parking lane.
            MixedCPMManualDriver driver = (MixedCPMManualDriver) vehicle.getDriver();
            // Check the vehicle is not moving.
            if (driver.isInStall() && vehicle.getVelocity() == 0) {
                parkedVehicles.add(vehicle);
            }
        }
    }

    /////////////////////////////////
    // STEP 6
    /////////////////////////////////

    protected void observeNumberOfVehiclesInCarPark() {
        if (map.getStatusMonitor() != null) {
            map.getStatusMonitor().updateMostNumberOfVehicles();
        }
    }

    /////////////////////////////////
    // STEP 7
    /////////////////////////////////

    /**
     * Remove all completed vehicles.
     *
     * @return the VINs of the completed vehicles
     */
    protected List<MixedCPMBasicManualVehicle> cleanUpCompletedVehicles() {
        List<MixedCPMBasicManualVehicle> completedVehicles = new LinkedList<MixedCPMBasicManualVehicle>();
        Rectangle2D mapBoundary = map.getDimensions();
        List<Integer> removedVINs = new ArrayList<Integer>(vinToVehicles.size());
        for(int vin : vinToVehicles.keySet()) {
            MixedCPMBasicManualVehicle vehicle = vinToVehicles.get(vin);
            // If the vehicle is no longer in the layout
            if(!vehicle.getShape().intersects(mapBoundary)) {
                // Process anything we need to from this vehicle
                // TODO CPM Do we need to get anything? Maybe distance travelled
                map.removeCompletedVehicle(vehicle);
                removedVINs.add(vin);
            }
        }
        // Remove the marked vehicles
        for(int vin : removedVINs) {
            completedVehicles.add(vinToVehicles.get(vin));
            vinToVehicles.remove(vin);
            numOfCompletedVehicles++;
        }
        return completedVehicles;
    }


    @Override
    public MixedCPMMap getMap() {
        return map;
    }

    @Override
    public double getSimulationTime() {
        return currentTime;
    }

    @Override
    public int getNumCompletedVehicles() {
        return numOfCompletedVehicles;
    }

    public List<MixedCPMBasicManualVehicle> getParkedVehicles() { return parkedVehicles; }

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

    public Map<Integer, MixedCPMBasicManualVehicle> getVinToVehicles() { return vinToVehicles; }

    public String produceResultsCSV(){
        StringBuilder sb = new StringBuilder();
        sb.append("Sorry, MixedCPM does not produce a results file");
        return sb.toString();
    }

    public SimulatorResult produceResult(){
        //Sorry, CPM does not produce results in this way.
        throw new NotImplementedException();
    }
}
