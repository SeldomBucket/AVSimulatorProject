package util.mixedcpm;

import aim4.map.mixedcpm.MixedCPMRoadMap;
import aim4.map.mixedcpm.parking.IManualParkingArea;
import aim4.map.mixedcpm.parking.ManualParkingRoad;
import aim4.map.mixedcpm.parking.ManualStall;
import aim4.map.mixedcpm.parking.StallInfo;
import aim4.vehicle.mixedcpm.MixedCPMBasicVehicleModel;

import java.util.ArrayList;

public class MockManualParkingArea extends MixedCPMRoadMap implements IManualParkingArea{

    public MockManualParkingArea(double laneWidth, double speedLimit){
        super(laneWidth,speedLimit);
    }

    @Override
    public void addVehicleToMap(MixedCPMBasicVehicleModel vehicle) {

    }

    @Override
    public ManualStall findSpace(StallInfo stallInfo) {
        return null;
    }

    @Override
    public ManualParkingRoad getParkingRoadByName(String parkingRoadName) {
        return null;
    }

    @Override
    public void update() {

    }

    @Override
    public ManualParkingRoad addNewParkingRoad(String roadName, double initialStackWidth) {
        return null;
    }

    @Override
    public void removeParkingRoad(ManualParkingRoad road) {

    }

    @Override
    public ArrayList<ManualParkingRoad> getParkingRoads() {
        return null;
    }

}
