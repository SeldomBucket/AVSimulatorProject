package aim4.sim.simulator.merge;

import aim4.map.DataCollectionLine;
import aim4.map.merge.MergeMap;
import aim4.map.merge.RoadNames;
import aim4.sim.results.CoreMergeResult;
import aim4.sim.results.CoreMergeVehicleResult;
import aim4.sim.setup.merge.enums.ProtocolType;
import aim4.sim.simulator.merge.helper.SensorInputHelper;
import aim4.sim.simulator.merge.helper.SpawnHelper;
import aim4.vehicle.VehicleUtil;
import aim4.vehicle.merge.MergeVehicleSimModel;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

/**
 * Created by Callum on 08/03/2017.
 */
public class CoreMergeSimulator implements MergeSimulator {
    //NESTED CLASSES//
    public static class CoreMergeSimStepResult implements SimStepResult {
        private Map<Integer, MergeVehicleSimModel> completedVehicles;

        public CoreMergeSimStepResult(Map<Integer, MergeVehicleSimModel> completedVehicles) {
            this.completedVehicles = completedVehicles;
        }

        public Map<Integer, MergeVehicleSimModel> getCompletedVehicles(){ return completedVehicles; }
    }

    //PROPERTIES//
    /*The map for the simulation*/
    private MergeMap map;
    /* All active vehicles, in form of a map from VINs to vehicle objects. */
    private Map<Integer, MergeVehicleSimModel> vinToVehicles;
    /* The current time */
    private double currentTime;
    /* The last time a vehicle completed */
    private double lastVehicleCompleteTime;
    /* The number of vehicles that passed through the merge zone */
    private int numberOfCompletedVehicles;
    /* The protocol type */
    protected ProtocolType protocolType;
    /* The time for which the simulation should run */
    protected Double timeLimit;

    //RESULTS//
    protected List<CoreMergeVehicleResult> vehiclesRecord;
    protected Map<String, Double> specToExpectedTimeMergeLane;
    protected Map<String, Double> specToExpectedTimeTargetLane;

    //HELPERS//
    SpawnHelper spawnHelper;
    SensorInputHelper sensorInputHelper;

    //PUBLIC METHODS//
    public CoreMergeSimulator(MergeMap map, ProtocolType protocolType){
        this(map, protocolType, null, null);
    }

    public CoreMergeSimulator(MergeMap map,
                              ProtocolType protocolType,
                              Map<String, Double> specToExpectedTimeMergeLane,
                              Map<String, Double> specToExpectedTimeTargetLane){
        this.map = map;
        this.protocolType = protocolType;
        this.vinToVehicles = new HashMap<Integer, MergeVehicleSimModel>();
        this.vehiclesRecord = new ArrayList<CoreMergeVehicleResult>();

        currentTime = 0.0;
        numberOfCompletedVehicles = 0;

        this.spawnHelper = new SpawnHelper(map, vinToVehicles);
        this.sensorInputHelper = new SensorInputHelper(map, vinToVehicles);
        this.specToExpectedTimeMergeLane = specToExpectedTimeMergeLane;
        this.specToExpectedTimeTargetLane = specToExpectedTimeTargetLane;
    }

    @Override
    public synchronized CoreMergeSimStepResult step(double timeStep) {
        spawnHelper.spawnVehicles(timeStep, protocolType);
        sensorInputHelper.provideSensorInput();
        letDriversAct();
        moveVehicles(timeStep);
        //checkForCollisions(); TODO: Fix collision prevention so that this can be run.

        Map<Integer, MergeVehicleSimModel> completedVehicles = cleanUpCompletedVehicles();
        provideCompletedVehiclesWithResultsInfo(completedVehicles);
        recordCompletedVehicles(completedVehicles);
        updateMaxMinVelocities();
        incrementCurrentTime(timeStep);

        return new CoreMergeSimStepResult(completedVehicles);
    }

    @Override
    public MergeMap getMap() {
        return map;
    }

    @Override
    public double getSimulationTime() {
        return currentTime;
    }

    @Override
    public int getNumCompletedVehicles() {
        return numberOfCompletedVehicles;
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
    public MergeVehicleSimModel getActiveVehicle(int vin) {
        return vinToVehicles.get(vin);
    }

    @Override
    public Map<Integer, MergeVehicleSimModel> getVinToVehicles() {
        return this.vinToVehicles;
    }

    protected void incrementCurrentTime(double timeStep) {
        currentTime += timeStep;
    }

    //STEP DRIVERS//
    protected void letDriversAct() {
        for(MergeVehicleSimModel vehicle : vinToVehicles.values()) {
            vehicle.getDriver().act();
        }
    }

    protected void moveVehicles(double timestep) {
        for(MergeVehicleSimModel vehicle : vinToVehicles.values()) {
            Point2D p1 = vehicle.getPosition();
            vehicle.move(timestep);
            Point2D p2 = vehicle.getPosition();
            for(DataCollectionLine line : map.getDataCollectionLines()){
                line.intersect(vehicle, currentTime, p1, p2);
            }
        }
    }

    //CLEAN UP//
    protected Map<Integer, MergeVehicleSimModel> cleanUpCompletedVehicles() {
        Map<Integer, MergeVehicleSimModel> completedVehicles = new HashMap<Integer, MergeVehicleSimModel>();

        Rectangle2D mapBoundary = map.getDimensions();

        List<MergeVehicleSimModel> removedVehicles = new ArrayList<MergeVehicleSimModel>(vinToVehicles.size());
        for(int vin : vinToVehicles.keySet()) {
            MergeVehicleSimModel v = vinToVehicles.get(vin);
            if(!v.getShape().intersects(mapBoundary)){
                removedVehicles.add(v);
            }
        }
        for(MergeVehicleSimModel vehicle : removedVehicles) {
            vinToVehicles.remove(vehicle.getVIN());
            completedVehicles.put(vehicle.getVIN(), vehicle);
            numberOfCompletedVehicles++;
        }

        return completedVehicles;
    }

    //CHECKS//
    /**
     * Detects collisions. Currently not used because vehicles collide - Go figure.
     */
    protected void checkForCollisions() {
        Integer[] keys = vinToVehicles.keySet().toArray(new Integer[]{});
        for(int i = 0; i < keys.length - 1; i++) { //-1 because we won't compare the last element with anything.
            Integer[] keysToCompare = Arrays.copyOfRange(keys, i + 1, keys.length);
            MergeVehicleSimModel vehicle1 = vinToVehicles.get(keys[i]);
            for(int j = 0; j < keysToCompare.length; j++) {
                MergeVehicleSimModel vehicle2 = vinToVehicles.get(keysToCompare[j]);
                if(VehicleUtil.collision(vehicle1, vehicle2)) {
                    throw new RuntimeException(String.format("There was a collision between vehicles %d and %d",
                            vehicle1.getVIN(),
                            vehicle2.getVIN()));
                }
            }
        }
    }

    // RESULTS PRODUCTION //
    public String produceResultsCSV(){
        return resultsToCSV(produceResult());
    }

    protected CoreMergeResult produceResult() {
        CoreMergeResult result = new CoreMergeResult(vehiclesRecord, numberOfCompletedVehicles/lastVehicleCompleteTime);
        return result;
    }

    protected String resultsToCSV(CoreMergeResult result) {
        StringBuilder sb = new StringBuilder();
        //Global Stats
        sb.append("Maximum Delay");
        sb.append(',');
        sb.append("Average Delay");
        sb.append(',');
        sb.append("Minimum Delay");
        sb.append(',');
        sb.append("No. Completed Vehicles");
        sb.append(',');
        sb.append("Throughput");
        sb.append('\n');
        sb.append(result.getMaxDelay());
        sb.append(',');
        sb.append(result.getAverageDelay());
        sb.append(',');
        sb.append(result.getMinDelay());
        sb.append(',');
        sb.append(result.getCompletedVehicles());
        sb.append(',');
        sb.append(result.getThroughput());
        sb.append('\n');
        sb.append('\n');
        //Headings
        sb.append("VIN");
        sb.append(',');
        sb.append("Starting Road");
        sb.append(',');
        sb.append("Vehicle Spec");
        sb.append(',');
        sb.append("Start Time");
        sb.append(',');
        sb.append("Finish Time");
        sb.append(',');
        sb.append("Delay");
        sb.append(',');
        sb.append("Final Velocity");
        sb.append(',');
        sb.append("Max Velocity");
        sb.append(',');
        sb.append("Min Velocity");
        sb.append(',');
        sb.append("Final X Position");
        sb.append(',');
        sb.append("Final Y Position");
        sb.append('\n');
        //Vehicle Data
        for(CoreMergeVehicleResult vr : result.getVehicleResults()){
            sb.append(vr.getVin());
            sb.append(',');
            sb.append(vr.getStartingRoad());
            sb.append(',');
            sb.append(vr.getSpecType());
            sb.append(',');
            sb.append(vr.getStartTime());
            sb.append(',');
            sb.append(vr.getFinishTime());
            sb.append(',');
            sb.append(vr.getDelayTime());
            sb.append(',');
            sb.append(vr.getFinalVelocity());
            sb.append(',');
            sb.append(vr.getMaxVelocity());
            sb.append(',');
            sb.append(vr.getMinVelocity());
            sb.append(',');
            sb.append(vr.getFinalXPos());
            sb.append(',');
            sb.append(vr.getFinalYPos());
            sb.append('\n');
        }
        return sb.toString();
    }

    protected void provideCompletedVehiclesWithResultsInfo(Map<Integer, MergeVehicleSimModel> completedVehicles) {
        for(int vin : completedVehicles.keySet()) {
            MergeVehicleSimModel vehicle = completedVehicles.get(vin);
            vehicle.setFinishTime(currentTime);
            vehicle.setDelay(calculateDelay(vehicle));
            vehicle.setFinalVelocity(vehicle.getVelocity());
            vehicle.setFinalXPos(vehicle.getPosition().getX());
            vehicle.setFinalYPos(vehicle.getPosition().getY());
        }
    }

    protected double calculateDelay(MergeVehicleSimModel vehicle) {
        if(vehicle.getStartingRoad() == RoadNames.TARGET_ROAD) {
            if (specToExpectedTimeTargetLane != null) {
                double delay = vehicle.getFinishTime() -
                        vehicle.getStartTime() -
                        specToExpectedTimeTargetLane.get(vehicle.getSpec().getName()).doubleValue();
                if (delay < 0)
                    delay = 0;
                return delay;
            }
        }
        else if(vehicle.getStartingRoad() == RoadNames.MERGING_ROAD) {
            if (specToExpectedTimeMergeLane != null) {
                double delay = vehicle.getFinishTime() -
                        vehicle.getStartTime() -
                        specToExpectedTimeMergeLane.get(vehicle.getSpec().getName()).doubleValue();
                if (delay < 0)
                    delay = 0;
                return delay;
            }
        }
        return Double.MAX_VALUE;
    }

    protected void recordCompletedVehicles(Map<Integer, MergeVehicleSimModel> completedVehicles) {
        for(int vin : completedVehicles.keySet()) {
            MergeVehicleSimModel vehicle = completedVehicles.get(vin);
            vehiclesRecord.add(new CoreMergeVehicleResult(
                    vin,
                    vehicle.getStartingRoad().toString(),
                    vehicle.getSpec().getName(),
                    vehicle.getStartTime(),
                    vehicle.getFinishTime(),
                    vehicle.getDelay(),
                    vehicle.getFinalVelocity(),
                    vehicle.getMaxVelocity(),
                    vehicle.getMinVelocity(),
                    vehicle.getFinalXPos(),
                    vehicle.getFinalYPos()
            ));
            lastVehicleCompleteTime = currentTime;
        }
    }

    protected void updateMaxMinVelocities() {
        for(int vin : vinToVehicles.keySet()) {
            MergeVehicleSimModel vehicle = vinToVehicles.get(vin);
            if(vehicle.getVelocity() > vehicle.getMaxVelocity())
                vehicle.setMaxVelocity(vehicle.getVelocity());
            else if(vehicle.getVelocity() < vehicle.getMinVelocity()) {
                vehicle.setMinVelocity(vehicle.getVelocity());
            }
        }
    }

}
