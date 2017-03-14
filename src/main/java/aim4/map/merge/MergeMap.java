package aim4.map.merge;

import aim4.map.BasicMap;
import aim4.map.DataCollectionLine;
import aim4.map.Road;
import aim4.map.merge.MergeSpawnPoint;
import aim4.map.lane.Lane;
import aim4.util.ArrayListRegistry;
import aim4.util.Registry;
import aim4.vehicle.VinRegistry;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Callum on 08/03/2017.
 */
public class MergeMap implements BasicMap {
    //BASIC MAP PROPERTIES//
    /** The dimensions of the map */
    private Rectangle2D dimensions;
    /**The set of roads*/
    private List<Road> roads = new ArrayList<Road>();
    /** The data collection lines. */
    private List<DataCollectionLine> dataCollectionLines = new ArrayList<DataCollectionLine>();
    /** The spawn points */
    private List<MergeSpawnPoint> spawnPoints = new ArrayList<MergeSpawnPoint>();
    /** The lane registry */
    private Registry<Lane> laneRegistry = new ArrayListRegistry<Lane>();
    /** A mapping form lanes to roads they belong */
    private Map<Lane,Road> laneToRoad = new HashMap<Lane,Road>();
    /** The maximum speed limit  */
    private double memoMaximumSpeedLimit = -1;

    //MAP CONSTANTS//
    protected static final double LANE_WIDTH = 4;
    protected static final double MEDIAN_SIZE = 1;
    protected static final double LOWER_BUFFER = 40;
    protected static final double NO_VEHICLE_ZONE_LENGTH = 28.0;

    //BASIC MAP INHERITORS//
    @Override
    public List<Road> getRoads() {
        return roads;
    }

    @Override
    public Rectangle2D getDimensions() {
        return dimensions;
    }

    @Override
    public double getMaximumSpeedLimit() {
        if(memoMaximumSpeedLimit < 0) {
            for(Road r : getRoads()) {
                for(Lane l : r.getLanes()) {
                    if(l.getSpeedLimit() > memoMaximumSpeedLimit) {
                        memoMaximumSpeedLimit = l.getSpeedLimit();
                    }
                }
            }
        }
        return memoMaximumSpeedLimit;
    }

    @Override
    public Registry<Lane> getLaneRegistry() {
        return laneRegistry;
    }

    @Override
    public Road getRoad(Lane lane) {
        return laneToRoad.get(lane);
    }

    @Override
    public Road getRoad(int laneID) {
        return laneToRoad.get(laneRegistry.get(laneID));
    }

    @Override
    public List<DataCollectionLine> getDataCollectionLines() {
        return dataCollectionLines;
    }

    @Override
    public List<MergeSpawnPoint> getSpawnPoints() {
        return spawnPoints;
    }

    @Override
    public void printDataCollectionLinesData(String outFileName) {
        PrintStream outfile = null;
        try {
            outfile = new PrintStream(outFileName);
        } catch (FileNotFoundException e) {
            System.err.printf("Cannot open file %s\n", outFileName);
            return;
        }
        // TODO: sort by time and LineId and VIN
        outfile.printf("VIN,Time,DCLname,vType,startLaneId,destRoad\n");
        for (DataCollectionLine line : dataCollectionLines) {
            for (int vin : line.getAllVIN()) {
                for(double time : line.getTimes(vin)) {
                    outfile.printf("%d,%.4f,%s,%s,%d,%s\n",
                            vin, time, line.getName(),
                            VinRegistry.getVehicleSpecFromVIN(vin).getName(),
                            VinRegistry.getSpawnPointFromVIN(vin).getLane().getId(),
                            VinRegistry.getDestRoadFromVIN(vin).getName());
                }
            }
        }

        outfile.close();
    }

    //PROTECTED ACCESSORS//
    protected void addLaneToRoad(Lane lane, Road road){
        laneToRoad.put(lane, road);
    }

    protected void setDimensions(Rectangle2D dimensions){
        this.dimensions = dimensions;
    }

    protected void addDataCollectionLine(String name, Point2D p1, Point2D p2, boolean isNoRepeat) {
        dataCollectionLines.add(
                new DataCollectionLine(
                        name,
                        dataCollectionLines.size(),
                        p1,
                        p2,
                        isNoRepeat
                )
        );
    }

    protected void addRoad(Road road){
        roads.add(road);
    }

    protected void addSpawnPoint(MergeSpawnPoint spawn){
        spawnPoints.add(spawn);
    }

    //PROTECTED UTILITIES//
    protected MergeSpawnPoint makeSpawnPoint(Lane lane, double initTime, double startDistance) {
        double normalisedStartDistance = lane.normalizedDistance(startDistance);

        Point2D pos = lane.getPointAtNormalizedDistance(startDistance);
        double heading = lane.getInitialHeading();
        double steeringAngle = 0.0;
        double acceleration = 0.0;
        double d = lane.normalizedDistance(startDistance + NO_VEHICLE_ZONE_LENGTH);
        Rectangle2D noVehicleZone = lane.getShape(normalisedStartDistance, d).getBounds2D();

        return new MergeSpawnPoint(initTime, pos, heading, steeringAngle, acceleration, lane, noVehicleZone);
    }
}
