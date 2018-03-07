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
import java.util.ArrayList;
import java.util.List;

/**
 * The base class for all CPM Maps.
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

    public MixedCPMBasicMap(double laneWidth, double speedLimit, double initTime){
        super(laneWidth, speedLimit);
        this.initTime = initTime;
    }

    public void update(){
        for (Road road : this.manualParkingArea.getRoads())
        {
            if (!this.roads.contains(road)){
                this.roads.add(road);
            }
        }
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


    public void addVehicleToMap(MixedCPMBasicAutoVehicle vehicle) {
        vehicles.add(vehicle);
    }

    public void removeCompletedVehicle(MixedCPMBasicAutoVehicle vehicle) {
        vehicles.remove(vehicle);
    }

    public List<MixedCPMSpawnPoint> getSpawnPoints() {
        return spawnPoints;
    }

    public List<DataCollectionLine> getDataCollectionLines() { return dataCollectionLines; }

    public List<MixedCPMBasicAutoVehicle> getVehicles() { return vehicles; }

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
