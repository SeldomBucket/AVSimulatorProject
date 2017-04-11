package aim4.sim.simulator.merge;

import aim4.driver.merge.MergeAutoDriver;
import aim4.map.BasicMap;
import aim4.map.DataCollectionLine;
import aim4.map.lane.Lane;
import aim4.map.merge.MergeSpawnPoint;
import aim4.map.merge.MergeSpawnPoint.*;
import aim4.map.merge.MergeMap;
import aim4.sim.simulator.merge.helper.SensorInputHelper;
import aim4.sim.simulator.merge.helper.SpawnHelper;
import aim4.vehicle.VehicleSimModel;
import aim4.vehicle.VehicleSpec;
import aim4.vehicle.VinRegistry;
import aim4.vehicle.aim.AIMVehicleSimModel;
import aim4.vehicle.merge.MergeAutoVehicleSimModel;
import aim4.vehicle.merge.MergeBasicAutoVehicle;
import aim4.vehicle.merge.MergeVehicleSimModel;
import com.sun.scenario.effect.Merge;
import sun.management.Sensor;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

/**
 * Created by Callum on 08/03/2017.
 */
public class CoreMergeSimulator implements MergeSimulator {
    //NESTED CLASSES//
    public static class CoreMergeSimStepResult implements SimStepResult {
        private List<Integer> completedVINs;

        public CoreMergeSimStepResult(List<Integer> completedVINs) {
            this.completedVINs = completedVINs;
        }

        public List<Integer> getCompletedVINs(){
            return completedVINs;
        }
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

    //HELPERS//
    SpawnHelper spawnHelper;
    SensorInputHelper sensorInputHelper;


    //PUBLIC METHODS//
    public CoreMergeSimulator(MergeMap map){
        this.map = map;
        this.vinToVehicles = new HashMap<Integer, MergeVehicleSimModel>();

        currentTime = 0.0;
        numberOfCompletedVehicles = 0;

        this.spawnHelper = new SpawnHelper(map, vinToVehicles);
        this.sensorInputHelper = new SensorInputHelper(map, vinToVehicles);
    }

    @Override
    public synchronized CoreMergeSimStepResult step(double timeStep) {
        spawnHelper.spawnVehicles(timeStep);
        sensorInputHelper.provideSensorInput();
        letDriversAct();
        moveVehicles(timeStep);

        List<Integer> completedVINs = cleanUpCompletedVehicles();
        currentTime += timeStep;

        return new CoreMergeSimStepResult(completedVINs);
    }

    @Override
    public BasicMap getMap() {
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

    //STEP DRIVERS//
    private void letDriversAct() {
        for(MergeVehicleSimModel vehicle : vinToVehicles.values()) {
            vehicle.getDriver().act();
        }
    }

    private void moveVehicles(double timestep) {
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
    private List<Integer> cleanUpCompletedVehicles() {
        List<Integer> completedVINs = new LinkedList<Integer>();

        Rectangle2D mapBoundary = map.getDimensions();

        List<Integer> removedVINS = new ArrayList<Integer>(vinToVehicles.size());
        for(int vin : vinToVehicles.keySet()) {
            MergeVehicleSimModel v = vinToVehicles.get(vin);
            if(!v.getShape().intersects(mapBoundary)){
                removedVINS.add(vin);
            }
        }
        for(int vin : removedVINS) {
            vinToVehicles.remove(vin);
            completedVINs.add(vin);
            numberOfCompletedVehicles++;
        }

        return completedVINs;
    }

}
