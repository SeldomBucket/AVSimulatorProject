package util.mixedcpm;

import aim4.map.mixedcpm.MixedCPMRoadMap;
import aim4.map.mixedcpm.parking.IManualParkingArea;
import aim4.map.mixedcpm.parking.ManualParkingRoad;
import aim4.map.mixedcpm.parking.ManualStall;
import aim4.map.mixedcpm.parking.StallSpec;
import aim4.vehicle.mixedcpm.MixedCPMBasicManualVehicle;

import java.util.ArrayList;

public class MockManualParkingArea extends MixedCPMRoadMap implements IManualParkingArea{

    public MockManualParkingArea(double laneWidth, double speedLimit){
        super(laneWidth,speedLimit);
    }

    @Override
    public void addVehicleToMap(MixedCPMBasicManualVehicle vehicle) {

    }

    @Override
    public ManualStall findSpace(StallSpec stallSpec) {
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
