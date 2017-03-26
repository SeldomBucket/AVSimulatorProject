package aim4.sim.simulator.merge;

import aim4.config.SimConfig;
import aim4.map.lane.Lane;
import aim4.map.merge.MergeMapUtil;
import aim4.map.merge.MergeSpawnPoint;
import aim4.sim.Simulator;
import aim4.vehicle.VehicleSpec;
import aim4.vehicle.VehicleSpecDatabase;
import aim4.vehicle.merge.MergeVehicleSimModel;
import org.junit.Before;
import org.junit.Test;
import util.map.SingleLaneOnlyMap;

import java.util.List;

import static org.junit.Assert.*;
/**
 * Created by Callum on 17/03/2017.
 */
public class CoreMergeSimulatorIntegrationTest {
    private final static double SPEED_LIMIT = 60.0;
    private final static double SINGLE_LANE_MAP_LANE_LENGTH = 250.0;
    private final static double TRAFFIC_LEVEL = 0.28;
    private final static double TIME_STEP = SimConfig.TIME_STEP;

    //Maps

    //Sims

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testSingleLaneSingleSpawnMap() {
        //Create Sim
        SingleLaneOnlyMap map = new SingleLaneOnlyMap(0, SPEED_LIMIT, SINGLE_LANE_MAP_LANE_LENGTH);
        MergeMapUtil.setSingleSpawnPoints(map, VehicleSpecDatabase.getVehicleSpecByName("COUPE"));
        CoreMergeSimulator sim = new CoreMergeSimulator(map);

        //Create useful variables
        Lane lane = map.getLane();
        MergeSpawnPoint spawnPoint = map.getSpawnPoints().get(0);

        //Before
        assertEquals(sim.getNumCompletedVehicles(), 0);
        assertEquals(sim.getSimulationTime(), 0, 0);

        //After One Step
        int stepsTaken = 0;
        sim.step(TIME_STEP);
        stepsTaken++;

        assertEquals(sim.getNumCompletedVehicles(), 0);
        assertEquals(sim.getSimulationTime(), stepsTaken * TIME_STEP, 0);
        assertEquals(sim.getVinToVehicles().size(), 1);

        int vin = sim.getVinToVehicles().keySet().iterator().next();
        MergeVehicleSimModel vehicle = sim.getActiveVehicle(vin);

        double startXPosition = vehicle.gaugePosition().getX();
        double startYPosition = vehicle.gaugePosition().getY();
        assertEquals(map.getEntranceDCLine().getTimes(vin).get(0), stepsTaken * TIME_STEP - TIME_STEP, 0);
        assertEquals(startXPosition, vehicle.gaugeVelocity() * (stepsTaken * TIME_STEP), 0);
        assertEquals(startYPosition, vehicle.getShape().getBounds2D().getCenterY(), 0);

        //After one step
        sim.step(TIME_STEP);
        stepsTaken++;
        double currentXPosition = vehicle.gaugePosition().getX();
        double currentYPosition = vehicle.gaugePosition().getY();
        assertEquals(currentXPosition, vehicle.gaugeVelocity() * (stepsTaken * TIME_STEP), 0);
        assertEquals(currentYPosition, vehicle.getShape().getBounds2D().getCenterY(), 0);
        assertEquals(vehicle.gaugeHeading(), Double.MIN_VALUE, 0);

        //After Many Steps
        for(int i = 0; i < 200; i++) {
            sim.step(TIME_STEP);
            stepsTaken++;
        }

        double newXPosition = vehicle.getPosition().getX();
        double newYPosition = vehicle.getPosition().getY();
        assertEquals(newXPosition, (vehicle.gaugeVelocity() * (stepsTaken * TIME_STEP)), 0.0001);
        assertEquals(newYPosition, vehicle.getShape().getBounds2D().getCenterY(),  0.0001);
        assertEquals(vehicle.gaugeHeading(), Double.MIN_VALUE,  0.0001);

        //After reaching the end
        CoreMergeSimulator.CoreMergeSimStepResult result = null;
        while(vehicle.getShape().intersects(map.getDimensions())) {
            result = sim.step(TIME_STEP);
            stepsTaken++;
        }

        assertNotNull(result);
        assertTrue(result.getCompletedVINs().contains(vin));
        assertEquals(sim.getNumCompletedVehicles(), 1);
        assertEquals(sim.getVinToVehicles().size(), 0);
        assertEquals(map.getExitDCLine().getTimes(vin).size(), 1);
    }

    @Test
    public void testSingleLaneUniformSpawnMap() {
        //Create Sim
        SingleLaneOnlyMap map = new SingleLaneOnlyMap(0, SPEED_LIMIT, SINGLE_LANE_MAP_LANE_LENGTH);
        MergeMapUtil.setUniformSpawnSpecGenerator(map, TRAFFIC_LEVEL);
    }

}
