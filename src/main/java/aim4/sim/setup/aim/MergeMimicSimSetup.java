package aim4.sim.setup.aim;

import aim4.config.Debug;
import aim4.config.SimConfig;
import aim4.driver.aim.pilot.V2IPilot;
import aim4.im.aim.v2i.reservation.ReservationGridManager;
import aim4.map.aim.GridIntersectionMap;
import aim4.map.aim.GridMapUtil;
import aim4.sim.Simulator;
import aim4.sim.simulator.aim.AutoDriverOnlySimulator;

import java.io.File;

/**
 * Created by Callum on 21/04/2017.
 */
public class MergeMimicSimSetup implements AIMSimSetup {
    // Settings from AutoDriverOnlySimSetup //
        /** The static buffer size */
    private double staticBufferSize = 0.25;
    /** The time buffer for internal tiles */
    private double internalTileTimeBufferSize = 0.1;
    /** The time buffer for edge tiles */
    private double edgeTileTimeBufferSize = 0.25;
    /** Whether the edge time buffer is enabled */
    private boolean isEdgeTileTimeBufferEnabled = true;
    /** The granularity of the reservation grid */
    private double granularity = 1.0;
    /** The stopping distance before intersection */
    protected double stopDistBeforeIntersection;

    private static final double LANE_WIDTH = 4;

    private File mergeSchedule;
    private File targetSchedule;
    private double speedLimit;

    public MergeMimicSimSetup(File mergeSchedule, File targetSchedule, double speedLimit) {
        this.mergeSchedule = mergeSchedule;
        this.targetSchedule = targetSchedule;
        this.speedLimit = speedLimit;
    }

    @Override
    public void setTrafficLevel(double trafficLevel) {
    }

    @Override
    public void setStopDistBeforeIntersection(double stopDistBeforeIntersection) {
    }

    @Override
    public Simulator getSimulator() {
        double currentTime = 0.0;
        GridIntersectionMap layout = new GridIntersectionMap(
                currentTime, //Current time
                1, //Columns
                1, //Rows
                LANE_WIDTH, //Lane width
                speedLimit, //Speed limit
                1, //Lanes per road
                1, //Width between roads on same side
                150 //Distance between intersections
        );
        ReservationGridManager.Config gridConfig =
                new ReservationGridManager.Config(SimConfig.TIME_STEP,
                        SimConfig.GRID_TIME_STEP,
                        staticBufferSize,
                        internalTileTimeBufferSize,
                        edgeTileTimeBufferSize,
                        isEdgeTileTimeBufferEnabled,
                        granularity);  // granularity
        Debug.SHOW_VEHICLE_COLOR_BY_MSG_STATE = true;

        try {
            GridMapUtil.setJSONScheduleSpawnSpecGenerator(layout, mergeSchedule, targetSchedule);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        GridMapUtil.setFCFSManagers(layout, currentTime, gridConfig);

        V2IPilot.DEFAULT_STOP_DISTANCE_BEFORE_INTERSECTION = 1.0;
        return new AutoDriverOnlySimulator(layout);
    }
}
