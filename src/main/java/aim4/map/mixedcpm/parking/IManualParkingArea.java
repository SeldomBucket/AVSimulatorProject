package aim4.map.mixedcpm.parking;

import aim4.map.RoadMap;
import aim4.vehicle.mixedcpm.MixedCPMBasicManualVehicle;

import java.util.ArrayList;

public interface IManualParkingArea extends RoadMap{
    public void addVehicleToMap(MixedCPMBasicManualVehicle vehicle);
    public ManualStall findSpace(StallSpec stallSpec);
    public ManualParkingRoad getParkingRoadByName(String parkingRoadName);
    public void update();
    public ManualParkingRoad addNewParkingRoad(String roadName, double initialStackWidth);
    public void removeParkingRoad(ManualParkingRoad road);
    public ArrayList<ManualParkingRoad> getParkingRoads();
}
