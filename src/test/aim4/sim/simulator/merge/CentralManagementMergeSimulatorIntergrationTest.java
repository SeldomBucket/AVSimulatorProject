package aim4.sim.simulator.merge;

import aim4.config.SimConfig;
import aim4.driver.merge.MergeCentralAutoDriver;
import aim4.driver.merge.coordinator.MergeCentralAutoCoordinator;
import aim4.driver.merge.pilot.MergeAutoPilot;
import aim4.im.merge.V2IMergeManager;
import aim4.im.merge.reservation.ReservationMerge;
import aim4.im.merge.reservation.ReservationMergeManager;
import aim4.map.connections.MergeConnection;
import aim4.map.lane.Lane;
import aim4.map.merge.MergeMapUtil;
import aim4.map.merge.S2SMergeMap;
import aim4.map.track.WayPoint;
import aim4.msg.merge.i2v.I2VMergeMessage;
import aim4.vehicle.AccelSchedule;
import aim4.vehicle.VehicleSpecDatabase;
import aim4.vehicle.merge.MergeCentralAutoVehicle;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static org.junit.Assert.*;

/**
 * Created by Callum on 15/04/2017.
 */
public class CentralManagementMergeSimulatorIntergrationTest {
    private final static double SPEED_LIMIT = 60.0;
    private final static double TIME_STEP = SimConfig.TIME_STEP;

    //SINGLE LANE PROPERTIES
    private final static double SINGLE_LANE_MAP_LANE_LENGTH = 250.0;

    //S2S PROPERTIES
    private final static double TARGET_LEAD_IN_DISTANCE = 300.0;
    private final static double TARGET_LEAD_OUT_DISTANCE = 300.0;
    private final static double MERGE_LEAD_IN_DISTANCE = 300.0;

    @Test
    public void testS2SMapMergeOnly() throws Exception {
        //Create Sim
        List<Double> testAngles = new ArrayList<Double>();
        testAngles.add(10.0);
        testAngles.add(20.0);
        testAngles.add(30.0);
        testAngles.add(40.0);
        testAngles.add(45.0);
        testAngles.add(50.0);
        testAngles.add(60.0);
        testAngles.add(70.0);
        testAngles.add(80.0);
        testAngles.add(90.0);
        for(double mergeAngle : testAngles) {
            testS2SMapMergeOnly(mergeAngle);
        }
    }

    private void testS2SMapMergeOnly(double mergeAngle) throws Exception {
        System.out.println(String.format("Testing S2SMapMergeOnly at merge angle: %f", mergeAngle));
        double mergeAngleRad = Math.toRadians(mergeAngle);
        double currentTime = 0;
        S2SMergeMap map = new S2SMergeMap(currentTime,
                SPEED_LIMIT, SPEED_LIMIT, TARGET_LEAD_IN_DISTANCE,
                TARGET_LEAD_OUT_DISTANCE, MERGE_LEAD_IN_DISTANCE,
                mergeAngle);
        ReservationMergeManager.Config mergeReservationConfig =
                new ReservationMergeManager.Config(SimConfig.TIME_STEP, SimConfig.MERGE_TIME_STEP);
        MergeMapUtil.setFCFSMergeManagers(map, currentTime, mergeReservationConfig);
        MergeMapUtil.setSingleSpawnPointS2SMergeOnly(map, VehicleSpecDatabase.getVehicleSpecByName("COUPE"));
        CentralManagementMergeSimulator sim = new CentralManagementMergeSimulator(map);

        //Create useful references
        V2IMergeManager mergeManager = (V2IMergeManager) map.getMergeManagers().get(0);
        MergeConnection merge = mergeManager.getMergeConnection();
        ReservationMergeManager reservationMergeManager = mergeManager.getReservationMergeManager();
        ReservationMerge reservationMerge = mergeManager.getReservationMerge();
        Lane targetLane = null;
        Lane mergeLane = null;
        for(Lane l : merge.getLanes()) {
            if(l.getInitialHeading() == 0)
                targetLane = l;
            else
                mergeLane = l;
        }
        assert targetLane != null && mergeLane != null;
        WayPoint mergeEntryPoint = merge.getEntryPoint(mergeLane);
        WayPoint targetEntryPoint = merge.getEntryPoint(targetLane);

        // BEFORE //
        assertEquals(0, sim.getNumCompletedVehicles());
        assertEquals(0, sim.getSimulationTime(), 0);

        // AFTER 1 STEP //
        int stepsTaken = 0;
        sim.step(TIME_STEP);
        stepsTaken++;

        //ACQUIRE VEHICLE AND DRIVER
        int vin = sim.getVinToVehicles().keySet().iterator().next();
        assert sim.getActiveVehicle(vin) instanceof MergeCentralAutoVehicle;
        MergeCentralAutoVehicle vehicle = (MergeCentralAutoVehicle) sim.getActiveVehicle(vin);
        MergeCentralAutoDriver driver = vehicle.getDriver();
        double startXPosition = vehicle.getPosition().getX();
        double startYPosition = vehicle.getPosition().getY();

        /*
        During this step, the vehicle has sent a proposal to the merge manager and is now awaiting a response.
         */

        //SIM CHECKS
        assertEquals(0, sim.getNumCompletedVehicles());
        assertEquals(stepsTaken * TIME_STEP, sim.getSimulationTime(), 0);
        assertEquals(1, sim.getVinToVehicles().size());

        //VEHICLE CHECKS
        assertEquals(2 * Math.PI - mergeAngleRad, vehicle.getHeading(), 0.001);
        assertNotNull(vehicle.getAccelSchedule());

        //Accel schedule should aim to stop vehicle at merge. These tests confirm this.
        AccelSchedule toStopAccelSchedule = vehicle.getAccelSchedule();
        List<AccelSchedule.TimeAccel> accelList = toStopAccelSchedule.getList();
        double stoppingDistance =
                0.5 * Math.abs(vehicle.getMaxDeceleration()) *
                        Math.pow(calculateTimeToDecelerate(vehicle.getVelocity(), 0, vehicle.getMaxDeceleration()), 2);
        double distanceToStopPoint =
                mergeLane.getStartPoint().distance(merge.getEntryPoint(driver.getCurrentLane()))
                        - MergeAutoPilot.DEFAULT_STOP_DISTANCE_BEFORE_MERGE;
        double distanceToSlowDown = distanceToStopPoint - stoppingDistance;

        //Accel 1
        double timeAtDeceleration = (distanceToSlowDown / vehicle.getVelocity());
        AccelSchedule.TimeAccel slowToStop = accelList.get(0);
        assertEquals(vehicle.getMaxDeceleration(), slowToStop.getAcceleration(), 0);
        assertEquals(timeAtDeceleration, slowToStop.getTime(), 0.01);

        //Accel 2
        double timeAtStop = timeAtDeceleration + calculateTimeToDecelerate(vehicle.getVelocity(), 0, vehicle.getMaxDeceleration());
        AccelSchedule.TimeAccel stop = accelList.get(1);
        assertEquals(0, stop.getAcceleration(), 0);
        assertEquals(timeAtStop, stop.getTime(), 0.01);

        //DRIVER CHECKS
        assertEquals(driver.getStateString(), MergeCentralAutoCoordinator.State.AWAITING_RESPONSE.toString());

        // AFTER 2 STEPS //
        sim.step(TIME_STEP);
        stepsTaken++;

        /*
        During the next step the driver will still be awaiting a response. Then the merge manager will handle the
        request before sending a confirmation to the driver.
         */

        //SIM CHECKS
        assertEquals(0, sim.getNumCompletedVehicles());
        assertEquals(stepsTaken * TIME_STEP, sim.getSimulationTime(), 0);
        assertEquals(1, sim.getVinToVehicles().size());

        //VEHICLE CHECKS
        assertEquals(2 * Math.PI - mergeAngleRad, vehicle.getHeading(), 0.001);
        assertEquals(vehicle.getAccelSchedule(), toStopAccelSchedule); //Schedule won't change because vehicle has not received response

        //DRIVER CHECKS
        assertEquals(driver.getStateString(), MergeCentralAutoCoordinator.State.AWAITING_RESPONSE.toString());

        //COMMS CHECKS
        final Field i2VInboxField = MergeCentralAutoVehicle.class.getDeclaredField("i2vInbox");
        i2VInboxField.setAccessible(true);
        assertEquals(((Queue<I2VMergeMessage>)i2VInboxField.get(vehicle)).size(), 1);

        // AFTER 3 STEPS //
        sim.step(TIME_STEP);
        stepsTaken++;

        /*
        During this step the driver processes the message and adjusts their acceleration profile to match their proposal
         */
        //SIM CHECKS
        assertEquals(0, sim.getNumCompletedVehicles());
        assertEquals(stepsTaken * TIME_STEP, sim.getSimulationTime(), 0);
        assertEquals(1, sim.getVinToVehicles().size());

        //VEHICLE CHECKS
        assertEquals(2 * Math.PI - mergeAngleRad, vehicle.getHeading(), 0.001);
        assertNotSame(vehicle.getAccelSchedule(), toStopAccelSchedule);
        AccelSchedule reservationAccelSchedule = vehicle.getAccelSchedule();
        List<AccelSchedule.TimeAccel> reservationList = reservationAccelSchedule.getList();
        double slowTime = reservationList.get(0).getTime();
        double slowAccel = reservationList.get(0).getAcceleration();
        double arrivalTime = reservationList.get(1).getTime();
        double arrivalAccel = reservationList.get(1).getAcceleration();

        //DRIVER CHECKS
        assertEquals(driver.getStateString(), MergeCentralAutoCoordinator.State.MAINTAINING_RESERVATION.toString());

        //RESERVATION MANAGER CHECKS
        assertTrue(reservationMerge.hasReservation(vehicle.getVIN()));
        assertTrue(reservationMerge.isReserved(reservationMerge.calcDiscreteTime(arrivalTime + TIME_STEP)));

        // STEPS UNTIL HITTING THE ZONE //
        boolean hitSlowTimeCheck = false;
        boolean hitArrivalTimeCheck = false;
        while(vehicle.getPosition().getX() != mergeEntryPoint.getX() && vehicle.getPosition().getY() != mergeEntryPoint.getY()) {
            sim.step(TIME_STEP);
            stepsTaken++;
            assertEquals(0, sim.getNumCompletedVehicles());
            assertEquals(stepsTaken * TIME_STEP, sim.getSimulationTime(), 0.0001);
            assertEquals(1, sim.getVinToVehicles().size());
            if(reservationMerge.calcDiscreteTime(sim.getSimulationTime()) == reservationMerge.calcDiscreteTime(slowTime)) {
                assertEquals(vehicle.getAcceleration(), slowAccel, 0);
                hitSlowTimeCheck = true;
            }
            else if(reservationMerge.calcDiscreteTime(sim.getSimulationTime()) == reservationMerge.calcDiscreteTime(arrivalTime)) {
                assertEquals(vehicle.getAcceleration(), arrivalAccel, 0);
                hitArrivalTimeCheck = true;
            }
        }
        assertEquals(arrivalTime, sim.getSimulationTime(), 0);
        assertTrue(hitSlowTimeCheck);
        assertTrue(hitArrivalTimeCheck);
    }

    private double calculateTimeToDecelerate(double vInitial, double vFinal, double decel) {
        return (vFinal - vInitial) / decel;
    }
}