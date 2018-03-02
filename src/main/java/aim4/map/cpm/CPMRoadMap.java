package aim4.map.cpm;

import aim4.map.Road;
import aim4.map.connections.Corner;
import aim4.map.connections.Junction;
import aim4.map.connections.SimpleIntersection;
import aim4.vehicle.mixedcpm.MixedCPMBasicVehicleModel;

import java.util.List;

public abstract class CPMRoadMap {

    protected List<Road> roads;
    protected List<Corner> corners;
    protected List<Junction> junctions;
    protected List<SimpleIntersection> intersections;
    protected List<MixedCPMBasicVehicleModel> vehicles;

    public List<Road> getRoads(){return roads;}
    public List<Corner> getCorners(){return corners;}
    public List<Junction> getJunctions(){return junctions;}
    public List<SimpleIntersection> getIntersections() {return intersections;}
    public List<MixedCPMBasicVehicleModel> getVehicles() {return vehicles;}

    public abstract void addVehicleToMap(MixedCPMBasicVehicleModel vehicle);
}
