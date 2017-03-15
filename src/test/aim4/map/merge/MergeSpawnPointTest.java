package aim4.map.merge;

import aim4.map.lane.Lane;
import aim4.map.lane.LineSegmentLane;
import aim4.vehicle.VehicleSpec;
import org.junit.Before;
import org.junit.Test;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Callum on 15/03/2017.
 */
public class MergeSpawnPointTest {
    private static final double LANE_WIDTH = 4.0;
    private static final double SPEED_LIMIT = 60.0;

    //MergeSpawnPoint parameters
    private double currentTime;
    private double heading;
    private double steeringAngle;
    private double acceleration;
    private Point2D pos;
    private Point2D laneStart;
    private Point2D laneEnd;
    private Lane lane;
    private Rectangle2D noVehicleZone;
    private MergeSpawnPoint spawnPoint;

    //Vehicle Spec Properties
    private MergeSpawnPoint.MergeSpawnSpecGenerator mockVehicleSpecChooser;
    private VehicleSpec vehicleSpec1;
    private VehicleSpec vehicleSpec2;
    private VehicleSpec vehicleSpec3;
    private MergeSpawnPoint.MergeSpawnSpec mergeSpawnSpec1;
    private MergeSpawnPoint.MergeSpawnSpec mergeSpawnSpec2;
    private MergeSpawnPoint.MergeSpawnSpec mergeSpawnSpec3;


    @Before
    public void setUp() throws Exception {
        //Create lane
        laneStart = new Point2D.Double(10.0, 20.0);
        laneEnd = new Point2D.Double(300.0, 600.0);
        lane = new LineSegmentLane(laneStart, laneEnd, LANE_WIDTH, SPEED_LIMIT);

        //Setup calculation tools for other parameters
        double startDistance = 0.0;
        double noVehicleZoneLength = 10.0;
        double normalisedStartDistance = lane.normalizedDistance(startDistance);

        currentTime = 10.0;
        pos = lane.getPointAtNormalizedDistance(normalisedStartDistance);
        heading = lane.getInitialHeading();
        steeringAngle = 12.0;
        acceleration = 5.0;
        noVehicleZone = lane.getShape(
                normalisedStartDistance,
                lane.normalizedDistance(startDistance + noVehicleZoneLength)
        ).getBounds2D();

        //Create Spawn Point
        spawnPoint = new MergeSpawnPoint(
                currentTime,
                pos,
                heading,
                steeringAngle,
                acceleration,
                lane,
                noVehicleZone
        );

        //Create test vehicle specs
        double maxAcceleration1 = 1.1;
        double maxDeceleration1 = 2.1;
        double maxVelocity1 = 3.1;
        double minVelocity1 = 4.1;
        double length1 = 5.1;
        double width1 = 6.1;
        double frontAxleDisplacement1 = 7.1;
        double rearAxleDisplacement1 = 8.1;
        double wheelSpan1 = 9.1;
        double wheelRadius1 = 10.1;
        double wheelWidth1 = 11.1;
        double maxSteeringAngle1 = 11.1;
        double maxTurnPerSecond1 = 13.1;

        double maxAcceleration2 = 1.2;
        double maxDeceleration2 = 2.2;
        double maxVelocity2 = 3.2;
        double minVelocity2 = 4.2;
        double length2 = 5.2;
        double width2 = 6.2;
        double frontAxleDisplacement2 = 7.2;
        double rearAxleDisplacement2 = 8.2;
        double wheelSpan2 = 9.2;
        double wheelRadius2 = 10.2;
        double wheelWidth2 = 11.2;
        double maxSteeringAngle2 = 11.2;
        double maxTurnPerSecond2 = 13.2;

        double maxAcceleration3 = 1.3;
        double maxDeceleration3 = 2.3;
        double maxVelocity3 = 3.3;
        double minVelocity3 = 4.3;
        double length3 = 5.3;
        double width3 = 6.3;
        double frontAxleDisplacement3 = 7.3;
        double rearAxleDisplacement3 = 8.3;
        double wheelSpan3 = 9.3;
        double wheelRadius3 = 10.3;
        double wheelWidth3 = 11.3;
        double maxSteeringAngle3 = 11.3;
        double maxTurnPerSecond3 = 13.3;

        vehicleSpec1 = new VehicleSpec(
                "Vehicle 1",
                maxAcceleration1,
                maxDeceleration1,
                maxVelocity1,
                minVelocity1,
                length1,
                width1,
                frontAxleDisplacement1,
                rearAxleDisplacement1,
                wheelSpan1,
                wheelRadius1,
                wheelWidth1,
                maxSteeringAngle1,
                maxTurnPerSecond1
        );

        vehicleSpec2 = new VehicleSpec(
                "Vehicle 2",
                maxAcceleration2,
                maxDeceleration2,
                maxVelocity2,
                minVelocity2,
                length2,
                width2,
                frontAxleDisplacement2,
                rearAxleDisplacement2,
                wheelSpan2,
                wheelRadius2,
                wheelWidth2,
                maxSteeringAngle2,
                maxTurnPerSecond2
        );

        vehicleSpec3 = new VehicleSpec(
                "Vehicle 3",
                maxAcceleration3,
                maxDeceleration3,
                maxVelocity3,
                minVelocity3,
                length3,
                width3,
                frontAxleDisplacement3,
                rearAxleDisplacement3,
                wheelSpan3,
                wheelRadius3,
                wheelWidth3,
                maxSteeringAngle3,
                maxTurnPerSecond3
        );

        //Create test merge specs
        mergeSpawnSpec1 = new MergeSpawnPoint.MergeSpawnSpec(1.0, vehicleSpec1);
        mergeSpawnSpec2 = new MergeSpawnPoint.MergeSpawnSpec(2.0, vehicleSpec2);
        mergeSpawnSpec3 = new MergeSpawnPoint.MergeSpawnSpec(3.0, vehicleSpec3);

        mockVehicleSpecChooser = mock(MergeSpawnPoint.MergeSpawnSpecGenerator.class);
        when(mockVehicleSpecChooser.act(any(MergeSpawnPoint.class), anyDouble())).thenReturn(
                new ArrayList<MergeSpawnPoint.MergeSpawnSpec>(){{
                    add(mergeSpawnSpec1);
                    add(mergeSpawnSpec2);
                    add(mergeSpawnSpec3);
                }}
        );

        spawnPoint.setVehicleSpecChooser(mockVehicleSpecChooser);
    }

    @Test
    public void testConstructor() throws Exception {
        assertEquals(spawnPoint.getCurrentTime(), currentTime, 0);
        assertEquals(spawnPoint.getPosition(), pos);
        assertEquals(spawnPoint.getHeading(), heading, 0);
        assertEquals(spawnPoint.getSteeringAngle(), steeringAngle, 0);
        assertEquals(spawnPoint.getAcceleration(), acceleration, 0);
        assertEquals(spawnPoint.getLane(), lane);
        assertEquals(spawnPoint.getNoVehicleZone(), noVehicleZone);
    }

    @Test
    public void testAct() throws Exception {
        double timestep = 5.0;
        List<MergeSpawnPoint.MergeSpawnSpec> results = spawnPoint.act(timestep);

        assertEquals("mergeSpawnSpec1 did not match the results of the act", results.get(0), mergeSpawnSpec1);
        assertEquals("mergeSpawnSpec2 did not match the results of the act", results.get(1), mergeSpawnSpec2);
        assertEquals("mergeSpawnSpec3 did not match the results of the act", results.get(2), mergeSpawnSpec3);
        assertEquals(currentTime + timestep, spawnPoint.getCurrentTime(), 0);
    }

    @Test
    public void testSetVehicleSpecChooser() throws Exception {
        final Field field = MergeSpawnPoint.class.getDeclaredField("vehicleSpecChooser");
        field.setAccessible(true);
        assertEquals("Fields didn't match", field.get(spawnPoint), mockVehicleSpecChooser);
    }

    @Test
    public void testGetCurrentTime() throws Exception {
        final Field field = MergeSpawnPoint.class.getSuperclass().getDeclaredField("currentTime");
        field.setAccessible(true);

        double testValue = currentTime + 1;
        field.set(spawnPoint, testValue);

        final double result = spawnPoint.getCurrentTime();
        assertEquals(result, testValue, 0);
    }

    @Test
    public void testGetPosition() throws Exception {
        final Field field = MergeSpawnPoint.class.getSuperclass().getDeclaredField("pos");
        field.setAccessible(true);

        Point2D testValue = mock(Point2D.class);
        field.set(spawnPoint, testValue);

        final Point2D result = spawnPoint.getPosition();
        assertEquals("pos not returned correctly", result, testValue);
    }

    @Test
    public void testGetHeading() throws Exception {
        final Field field = MergeSpawnPoint.class.getSuperclass().getDeclaredField("heading");
        field.setAccessible(true);

        double testValue = heading + 1;
        field.set(spawnPoint, testValue);

        final double result = spawnPoint.getHeading();
        assertEquals(result, testValue, 0);
    }

    @Test
    public void testGetSteeringAngle() throws Exception {
        final Field field = MergeSpawnPoint.class.getSuperclass().getDeclaredField("steeringAngle");
        field.setAccessible(true);

        double testValue = steeringAngle + 1;
        field.set(spawnPoint, testValue);

        final double result = spawnPoint.getSteeringAngle();
        assertEquals(result, testValue, 0);
    }

    @Test
    public void testGetAcceleration() throws Exception {
        final Field field = MergeSpawnPoint.class.getSuperclass().getDeclaredField("acceleration");
        field.setAccessible(true);

        double testValue = acceleration + 1;
        field.set(spawnPoint, testValue);

        final double result = spawnPoint.getAcceleration();
        assertEquals(result, testValue, 0);
    }

    @Test
    public void testGetLane() throws Exception {
        final Field field = MergeSpawnPoint.class.getSuperclass().getDeclaredField("lane");
        field.setAccessible(true);

        Lane testValue = mock(Lane.class);
        field.set(spawnPoint, testValue);

        final Lane result = spawnPoint.getLane();
        assertEquals("lane not returned correctly", result, testValue);
    }

    @Test
    public void testGetNoVehicleZone() throws Exception {
        final Field field = MergeSpawnPoint.class.getSuperclass().getDeclaredField("noVehicleZone");
        field.setAccessible(true);

        Rectangle2D testValue = mock(Rectangle2D.class);
        field.set(spawnPoint, testValue);

        final Rectangle2D result = spawnPoint.getNoVehicleZone();
        assertEquals("noVehicleZone not returned correctly", result, testValue);
    }
}