package aim4.sim.simulator.merge.helper;

import aim4.config.Debug;
import aim4.driver.merge.MergeAutoDriver;
import aim4.map.Road;
import aim4.map.lane.Lane;
import aim4.map.merge.MergeMap;
import aim4.vehicle.AutoVehicleSimModel;
import aim4.vehicle.merge.MergeAutoVehicleSimModel;
import aim4.vehicle.merge.MergeVehicleSimModel;
import com.sun.scenario.effect.Merge;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.*;

/**
 * Created by Callum on 15/03/2017.
 */
public class SensorInputHelper {
    MergeMap map;
    Map<Integer, MergeVehicleSimModel> vinToVehicles;

    /**
     * Provides sensor input for the
     *
     * @param map
     * @param vinToVehicles
     */
    public SensorInputHelper(MergeMap map, Map<Integer, MergeVehicleSimModel> vinToVehicles) {
        this.map = map;
        this.vinToVehicles = vinToVehicles;
    }

    /**
     * Provides sensor input to all of the vehicles on all of the lanes.
     */
    public void provideSensorInput() {
        Map<Lane, SortedMap<Double, MergeVehicleSimModel>> vehicleLists = computeVehicleLists();
        Map<MergeVehicleSimModel, MergeVehicleSimModel> nextVehicle = computeNextVehicle(vehicleLists);

        provideIntervalInfo(nextVehicle);
        provideVehicleTrackingInfo(vehicleLists);
    }

    /**
     * Computes a list of all of the vehicles on the lanes.
     *
     * @return a mapping from lanes to lists of vehicles sorted by their distance on their lanes.
     */
    private Map<Lane, SortedMap<Double, MergeVehicleSimModel>> computeVehicleLists() {
        //Creating the structure for the vehicle mapping
        Map<Lane, SortedMap<Double, MergeVehicleSimModel>> vehicleLists =
                new HashMap<Lane, SortedMap<Double, MergeVehicleSimModel>>();
        for (Road road : map.getRoads()) {
            for (Lane lane : road.getLanes()) {
                vehicleLists.put(lane, new TreeMap<Double, MergeVehicleSimModel>());
            }
        }

        //Adding all of the vehicles
        for (MergeVehicleSimModel vehicle : vinToVehicles.values()) {
            Set<Lane> lanes = vehicle.getDriver().getCurrentlyOccupiedLanes();
            for (Lane lane : lanes) {
                double distanceAlongLane = lane.distanceAlongLane(vehicle.getPosition());
                vehicleLists.get(lane).put(distanceAlongLane, vehicle);
            }
        }

        return vehicleLists;
    }

    /**
     * Finds the preceding vehicle for all of the vehicles. This is the preceding vehicle for the lane the vehicle is
     * currently on.
     *
     * @param vehicleLists
     * @return
     */
    private Map<MergeVehicleSimModel, MergeVehicleSimModel> computeNextVehicle(
            Map<Lane, SortedMap<Double, MergeVehicleSimModel>> vehicleLists) {
        Map<MergeVehicleSimModel, MergeVehicleSimModel> nextVehicle =
                new HashMap<MergeVehicleSimModel, MergeVehicleSimModel>();
        for (SortedMap<Double, MergeVehicleSimModel> vehicleList : vehicleLists.values()) {
            MergeVehicleSimModel lastVehicle = null;
            for (MergeVehicleSimModel currVehicle : vehicleList.values()) {
                if (lastVehicle != null) {
                    nextVehicle.put(lastVehicle, currVehicle);
                }
                lastVehicle = currVehicle;
            }
        }

        return nextVehicle;
    }

    private void provideIntervalInfo(Map<MergeVehicleSimModel, MergeVehicleSimModel> nextVehicle) {
        for (MergeVehicleSimModel mergeVehicle : vinToVehicles.values()) {
            if (mergeVehicle instanceof MergeAutoVehicleSimModel) {
                MergeAutoVehicleSimModel autoVehicle = (MergeAutoVehicleSimModel) mergeVehicle;

                double interval;
                if (nextVehicle.containsKey(autoVehicle)) {
                    interval = calcInterval(autoVehicle, nextVehicle.get(autoVehicle));
                } else {
                    interval = Double.MAX_VALUE;
                }

                autoVehicle.getIntervalometer().record(interval);
            }
        }

    }

    private void provideVehicleTrackingInfo(Map<Lane, SortedMap<Double, MergeVehicleSimModel>> vehicleLists) {
        for(MergeVehicleSimModel vehicle : vinToVehicles.values()) {
            if(vehicle instanceof MergeVehicleSimModel) {
                MergeAutoVehicleSimModel autoVehicle = (MergeAutoVehicleSimModel) vehicle;

                if(autoVehicle.isVehicleTracking()) {
                    MergeAutoDriver driver = autoVehicle.getDriver();
                    Lane targetLaneForTracking = autoVehicle.getTargetLaneForVehicleTracking();
                    Point2D pos = autoVehicle.getPosition();
                    double dst = targetLaneForTracking.distanceAlongLane(pos);

                    //initialise distances to infinity
                    double frontDst = Double.MAX_VALUE;
                    double rearDst = Double.MAX_VALUE;
                    MergeVehicleSimModel frontVehicle = null;
                    MergeVehicleSimModel rearVehicle = null;

                    //only consider the vehicles on the target tracking lane
                    SortedMap<Double, MergeVehicleSimModel> vehiclesOnTargetLane =
                            vehicleLists.get(targetLaneForTracking);

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

                    //assign the sensor readings

                    autoVehicle.getFrontVehicleDistanceSensor().record(frontDst);
                    autoVehicle.getRearVehicleDistanceSensor().record(rearDst);

                    //assign the vehicles' velocities
                    if(frontVehicle!=null){
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
                }
            }
        }
    }

    private double calcInterval(MergeVehicleSimModel vehicle, MergeVehicleSimModel nextVehicle) {
        Point2D pos = vehicle.getPosition();
        if(nextVehicle.getShape().contains(pos)) {
            return 0.0;
        } else {
            double interval = Double.MAX_VALUE;
            for(Line2D edge : nextVehicle.getEdges()) {
                double dst = edge.ptSegDist(pos);
                if(dst < interval) {
                    interval = dst;
                }
            }
            return interval;
        }
    }
}
