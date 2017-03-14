package aim4.sim.simulator.merge;

import aim4.map.BasicMap;
import aim4.map.merge.MergeMap;
import aim4.sim.Simulator;
import aim4.vehicle.VehicleSimModel;
import aim4.vehicle.merge.MergeVehicleSimModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Callum on 08/03/2017.
 */
public class S2SIMMergeSimulator implements MergeSimulator {

    //NESTED CLASSES//
    public static class S2SIMMergeSimStepResult implements SimStepResult {
        List<Integer> completedVINs;

        public S2SIMMergeSimStepResult(List<Integer> completedVINs) {
            this.completedVINs = completedVINs;
        }

        public List<Integer> getCompletedVINs(){
            return completedVINs;
        }
    }


    /*The map for the simulation*/
    private MergeMap map;
    /* All active vehicles, in form of a map from VINs to vehicle objects. */
    private Map<Integer, MergeVehicleSimModel> vinToVehicles;
    /* The current time */
    private double currentTime;
    /* The number of vehicles that passed through the merge zone */
    private int numberOfCompletedVehicles;

    public S2SIMMergeSimulator(MergeMap map){
        this.map = map;
        this.vinToVehicles = new HashMap<Integer, MergeVehicleSimModel>();

        currentTime = 0.0;
        numberOfCompletedVehicles = 0;
    }

    @Override
    public synchronized S2SIMMergeSimStepResult step(double timeStep) {
        List<Integer> completedVINs = new ArrayList<Integer>();//cleanUpCompletedVehicles();

        return new S2SIMMergeSimStepResult(completedVINs);
    }

    @Override
    public BasicMap getMap() {
        return map;
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
