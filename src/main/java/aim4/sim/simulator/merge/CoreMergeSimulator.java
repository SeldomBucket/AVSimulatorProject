package aim4.sim.simulator.merge;

import aim4.map.DataCollectionLine;
import aim4.map.merge.MergeMap;
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
    /* The number of vehicles that passed through the merge zone */
    private int numberOfCompletedVehicles;
    /* The protocol type */
    protected ProtocolType protocolType;

    //HELPERS//
    SpawnHelper spawnHelper;
    SensorInputHelper sensorInputHelper;


    //PUBLIC METHODS//
    public CoreMergeSimulator(MergeMap map, ProtocolType protocolType){
        this.map = map;
        this.protocolType = protocolType;
        this.vinToVehicles = new HashMap<Integer, MergeVehicleSimModel>();

        currentTime = 0.0;
        numberOfCompletedVehicles = 0;

        this.spawnHelper = new SpawnHelper(map, vinToVehicles);
        this.sensorInputHelper = new SensorInputHelper(map, vinToVehicles);
    }

    @Override
    public synchronized CoreMergeSimStepResult step(double timeStep) {
        spawnHelper.spawnVehicles(timeStep, protocolType);
        sensorInputHelper.provideSensorInput();
        letDriversAct();
        moveVehicles(timeStep);
        checkForCollisions();

        Map<Integer, MergeVehicleSimModel> completedVehicles = cleanUpCompletedVehicles();
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

    @Override
    public double calculateDelay(MergeVehicleSimModel vehicle) {
        return 0;
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

}
