package aim4.map.mixedcpm;

import aim4.map.DataCollectionLine;
import aim4.map.Road;
import aim4.map.lane.Lane;
import aim4.map.mixedcpm.parking.ManualParkingArea;
import aim4.vehicle.VinRegistry;
import aim4.vehicle.mixedcpm.MixedCPMBasicAutoVehicle;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Stream;

/**
 * The base class for all Mixed CPM Maps.
 */
public abstract class MixedCPMBasicMap extends MixedCPMRoadMap implements MixedCPMMap {

    /////////////////////////////////
    // CONSTANTS
    /////////////////////////////////

    /** The length of the no vehicle zone */
    protected static final double NO_VEHICLE_ZONE_LENGTH = 28.0;

    /** The length of the map border, used for
     * space between map edge and elements, distance
     * of DCL from edge etc.
     * */
    protected static final double BORDER = 28.0;

    /** The Manual Parking Area */
    protected ManualParkingArea manualParkingArea;
    /**The initial time*/
    protected double initTime;
    /** The data collection lines */
    protected List<DataCollectionLine> dataCollectionLines;
    /** The vehicles currently on this map. */
    private List<MixedCPMBasicAutoVehicle> vehicles = new ArrayList<MixedCPMBasicAutoVehicle>();


    // spawn points
    /** The spawn points */
    protected List<MixedCPMSpawnPoint> spawnPoints;
    /** The horizontal spawn points */
    protected List<MixedCPMSpawnPoint> horizontalSpawnPoints;
    /** The vertical spawn points */
    protected List<MixedCPMSpawnPoint> verticalSpawnPoints;

    /**
     * Constructor for MixedCPMBasicMap
     * @param laneWidth Standard width of the lanes
     * @param speedLimit Speed limit for all lanes
     * @param initTime Time the map was initialised at
     */
    public MixedCPMBasicMap(double laneWidth, double speedLimit, double initTime){
        super(laneWidth, speedLimit);
        this.initTime = initTime;
    }


    /**
     * Make the spawn point.
     *
     * @param initTime  the initial time
     * @param lane      the lane
     * @return the spawn point
     */
    protected MixedCPMSpawnPoint makeSpawnPoint(double initTime, Lane lane) {
        double startDistance = 0.0;
        double normalizedStartDistance = lane.normalizedDistance(startDistance);
        Point2D pos = lane.getPointAtNormalizedDistance(normalizedStartDistance);
        double heading = lane.getInitialHeading();
        double steeringAngle = 0.0;
        double acceleration = 0.0;
        double d = lane.normalizedDistance(startDistance + NO_VEHICLE_ZONE_LENGTH);
        Rectangle2D noVehicleZone =
                lane.getShape(normalizedStartDistance, d).getBounds2D();

        return new MixedCPMSpawnPoint(initTime, pos, heading, steeringAngle, acceleration,
                lane, noVehicleZone);
    }

    @Override
    public List<Road> getRoads() {
        HashSet<Road> set = new HashSet<>();
        set.addAll(this.roads);
        set.addAll(this.manualParkingArea.roads);
        // TODO ED When have automated section of car park, add here
        return new ArrayList<>(set);
    }

    @Override
    public void update(){
        // TODO ED maybe...?
    }

    /**
     * Initialize spawn points.
     *
     * @param initTime  the initial time
     */
    protected void initializeSpawnPoints(double initTime) {
        spawnPoints = new ArrayList<MixedCPMSpawnPoint>(1);
        horizontalSpawnPoints = new ArrayList<MixedCPMSpawnPoint>(1);
        horizontalSpawnPoints.add(makeSpawnPoint(initTime, entranceLane));

        spawnPoints.addAll(horizontalSpawnPoints);
    }

    /**
     * Add a vehicle to this map
     * @param vehicle the vehicle to add
     */
    public void addVehicleToMap(MixedCPMBasicAutoVehicle vehicle) {
        vehicles.add(vehicle);
    }

    /**
     * Remove a vehicle from the map
     * @param vehicle the vehicle to remove
     */
    public void removeCompletedVehicle(MixedCPMBasicAutoVehicle vehicle) {
        vehicles.remove(vehicle);
    }

    /**
     * Returns the spawn points of this map
     * @return the spawn points
     */
    public List<MixedCPMSpawnPoint> getSpawnPoints() {
        return spawnPoints;
    }

    /**
     * Returns the data collection lines of this map
     * @return the data collection lines
     */
    public List<DataCollectionLine> getDataCollectionLines() { return dataCollectionLines; }

    /**
     * Returns the vehicles in this map
     * @return the vehicles
     */
    public List<MixedCPMBasicAutoVehicle> getVehicles() { return vehicles; }

    /**
     * prints the data from the data collection lines to a file
     * @param outFileName  the name of the file to which the data is outputted.
     */
    public void printDataCollectionLinesData(String outFileName) {
        PrintStream outfile = null;
        try {
            outfile = new PrintStream(outFileName);
        } catch (FileNotFoundException e) {
            System.err.printf("Cannot open file %s%n", outFileName);
            return;
        }
        // TODO: sort by time and LineId and VIN
        outfile.printf("Printing file for CPM simulation%n");
        outfile.printf("VIN,Time,DCLname,vType,startLaneId%n");
        for (DataCollectionLine line : dataCollectionLines) {
            for (int vin : line.getAllVIN()) {
                for(double time : line.getTimes(vin)) {
                    outfile.printf("%d,%.4f,%s,%s,%d%n",
                            vin, time, line.getName(),
                            VinRegistry.getVehicleSpecFromVIN(vin).getName(),
                            VinRegistry.getSpawnPointFromVIN(vin).getLane().getId());
                }
            }
        }

        outfile.close();
    }
}
