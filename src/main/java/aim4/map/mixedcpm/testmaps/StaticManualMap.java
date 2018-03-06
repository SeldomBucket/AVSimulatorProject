package aim4.map.mixedcpm.testmaps;

import aim4.map.DataCollectionLine;
import aim4.map.Road;
import aim4.map.connections.Corner;
import aim4.map.connections.Junction;
import aim4.map.connections.SimpleIntersection;
import aim4.map.cpm.CPMSpawnPoint;
import aim4.map.cpm.parking.ParkingArea;
import aim4.map.cpm.parking.StatusMonitor;
import aim4.map.lane.Lane;
import aim4.map.mixedcpm.MixedCPMBasicMap;
import aim4.map.mixedcpm.parking.ManualParkingArea;
import aim4.util.Registry;
import aim4.vehicle.mixedcpm.MixedCPMBasicAutoVehicle;

import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * Test map which is just a standard manual car park (for testing the spaces)
 */
public class StaticManualMap extends MixedCPMBasicMap {
    StaticManualMap(){
        super(3,5,0);
    }

    @Override
    public List<Lane> getExitLanes() {
        return null;
    }

    @Override
    public double getLaneWidth() {
        return 0;
    }

    @Override
    public List<Corner> getCorners() {
        return null;
    }

    @Override
    public List<Junction> getJunctions() {
        return null;
    }

    @Override
    public List<SimpleIntersection> getIntersections() {
        return null;
    }

    @Override
    public List<CPMSpawnPoint> getSpawnPoints() {
        return null;
    }

    @Override
    public StatusMonitor getStatusMonitor() {
        return null;
    }

    @Override
    public void addVehicleToMap(MixedCPMBasicAutoVehicle vehicle) {

    }

    @Override
    public List<MixedCPMBasicAutoVehicle> getVehicles() {
        return null;
    }

    @Override
    public ParkingArea getParkingArea() {
        return null;
    }

    @Override
    public List<Road> getRoads() {
        return null;
    }

    @Override
    public Rectangle2D getDimensions() {
        return null;
    }

    @Override
    public double getMaximumSpeedLimit() {
        return 0;
    }

    @Override
    public Registry<Lane> getLaneRegistry() {
        return null;
    }

    @Override
    public Road getRoad(Lane lane) {
        return null;
    }

    @Override
    public Road getRoad(int laneID) {
        return null;
    }

    @Override
    public List<DataCollectionLine> getDataCollectionLines() {
        return null;
    }

    @Override
    public void printDataCollectionLinesData(String outFileName) {

    }
}
