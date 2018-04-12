package aim4.map.mixedcpm.parking;

import aim4.map.Road;
import aim4.map.RoadMap;
import aim4.map.connections.Junction;
import aim4.vehicle.mixedcpm.MixedCPMBasicManualVehicle;

import java.util.ArrayList;
import java.util.List;

public interface IManualParkingArea extends RoadMap{
    public ManualStall findSpace(StallSpec stallSpec);
    public Road getRoadByName(String name);
    public ManualParkingRoad getParkingRoadByName(String parkingRoadName);
    public void update();
    public ManualParkingRoad addNewParkingRoad(String roadName, double initialStackWidth);
    public void removeParkingRoad(ManualParkingRoad road);
    public ArrayList<ManualParkingRoad> getParkingRoads();
    public boolean tryResize(double newMaxX);
    public ManualParkingRoad getLastParkingRoad();
    public ManualStall getManualStallByName(String stallName);
    public List<Junction> getJunctions();
    public double getLaneWidth();
    public void removeManualStall(String stallName);

}
