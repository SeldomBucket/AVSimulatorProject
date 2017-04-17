package aim4.map.merge;

import aim4.im.merge.MergeManager;
import aim4.map.BasicMap;
import aim4.map.DataCollectionLine;
import aim4.map.Road;
import aim4.map.connections.MergeConnection;
import aim4.map.lane.Lane;
import aim4.util.ArrayListRegistry;
import aim4.util.Registry;
import aim4.vehicle.VinRegistry;

import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
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
    /** The merge connections */
    private List<MergeConnection> mergeConnections = new ArrayList<MergeConnection>();
    /** The merge managers */
    private List<MergeManager> mergeManagers = new ArrayList<MergeManager>();
    /** The lane registry */
    private Registry<Lane> laneRegistry = new ArrayListRegistry<Lane>();
    /** The merge manager registry */
    private Registry<MergeManager> mergeManagerRegistry = new ArrayListRegistry<MergeManager>();
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

    //PUBLIC ACCESSORS//
    public List<MergeConnection> getMergeConnections() {
        return this.mergeConnections;
    }

    public List<MergeManager> getMergeManagers() {
        return this.mergeManagers;
    }

    public Registry<MergeManager> getMMRegistry() {
        return mergeManagerRegistry;
    }

    public void removeAllMergeManagers() {
        mergeManagers.clear();
    }

    public void addMergeManager(MergeManager mergeManager) {
        mergeManagers.add(mergeManager);
        mergeManagerRegistry.register(mergeManager);
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

    protected void addMergeConnection(MergeConnection connection) { mergeConnections.add(connection); }

    //PROTECTED UTILITIES//
    protected MergeSpawnPoint makeSpawnPoint(Lane lane, double initTime, double startDistance) {
        Point2D pos = calculateLowerPointAlongMergeLane(lane, startDistance);
        double heading = lane.getInitialHeading();
        double steeringAngle = 0.0;
        double acceleration = 0.0;
        //double d = lane.normalizedDistance(startDistance + NO_VEHICLE_ZONE_LENGTH);
        //Rectangle2D noVehicleZone = lane.getShape(normalisedStartDistance, d).getBounds2D();
        Path2D noVehicleZone = calculateNoVehicleZone(lane, pos, startDistance);

        return new MergeSpawnPoint(initTime, pos, heading, steeringAngle, acceleration, lane, noVehicleZone);
    }

    private Path2D calculateNoVehicleZone(Lane lane, Point2D startPoint, double startDistance) {
        Point2D noVehicleZoneEnd = calculateLowerPointAlongMergeLane(lane, startDistance + NO_VEHICLE_ZONE_LENGTH);
        double boundIncrease = 0.5; //Grows the zone slightly larger than required.
        double laneXAdjustment;
        double laneYAdjustment;
        if(lane.getInitialHeading() == 0 || lane.getInitialHeading() == 2*Math.PI){
            laneXAdjustment = 0;
            laneYAdjustment = LANE_WIDTH/2;
        } else if(lane.getInitialHeading() == 2*Math.PI - Math.PI/2) {
            laneXAdjustment = LANE_WIDTH/2;
            laneYAdjustment = 0;
        } else {
            double laneAngle = (2*Math.PI) - lane.getInitialHeading();
            double interiorAngle = (Math.PI/2) - laneAngle;
            laneXAdjustment = (LANE_WIDTH/2) / Math.cos(interiorAngle);
            laneYAdjustment = (LANE_WIDTH/2) / Math.sin(interiorAngle);
        }
        double xPoints[] = {
                startPoint.getX() - laneXAdjustment - boundIncrease,
                startPoint.getX() + laneXAdjustment + boundIncrease,
                noVehicleZoneEnd.getX() + laneXAdjustment + boundIncrease,
                noVehicleZoneEnd.getX() - laneXAdjustment - boundIncrease
        };
        double yPoints[] = {
                startPoint.getY() - laneYAdjustment - boundIncrease,
                startPoint.getY() + laneYAdjustment + boundIncrease,
                noVehicleZoneEnd.getY() + laneYAdjustment + boundIncrease,
                noVehicleZoneEnd.getY() - laneYAdjustment - boundIncrease
        };
        GeneralPath noVehicleZonePath = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xPoints.length);
        noVehicleZonePath.moveTo(xPoints[0], yPoints[0]);

        for(int i = 1; i < xPoints.length; i++) {
            noVehicleZonePath.lineTo(xPoints[i], yPoints[i]);
        };

        noVehicleZonePath.closePath();
        Path2D noVehicleZone = new Path2D.Double(noVehicleZonePath);
        return noVehicleZone;
    }

    private Point2D calculateLowerPointAlongMergeLane(Lane lane, double distance) {
        Point2D startPoint = lane.getStartPoint();
        if(lane.getInitialHeading() == 0 || lane.getInitialHeading() == 2*Math.PI){ //Lane goes horizontally L->R
            return new Point2D.Double(
                    lane.getStartPoint().getX() + distance,
                    lane.getStartPoint().getY()
                    );
        } else if (lane.getInitialHeading() == (2*Math.PI - Math.PI/2)) { //Lane goes down vertically T->B
            return new Point2D.Double(
                    lane.getStartPoint().getX(),
                    lane.getStartPoint().getY() - distance
            );
        } else {
            double laneAngle = (2 * Math.PI) - lane.getInitialHeading();
            double interiorAngle = (Math.PI / 2) - laneAngle;
            double otherAngle = Math.PI / 2 - laneAngle;

            double laneYAdjustment = (distance * Math.sin(interiorAngle)) / Math.sin(Math.PI / 2);
            double laneXAdjustment = (distance * Math.sin(otherAngle)) / Math.sin(Math.PI / 2);

            return new Point2D.Double(
                    startPoint.getX() + laneXAdjustment,
                    startPoint.getY() - laneYAdjustment
            );
        }
    }
}
