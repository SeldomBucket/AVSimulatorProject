package aim4.sim.simulator.merge;

import aim4.config.SimConfig;
import aim4.driver.Driver;
import aim4.map.DataCollectionLine;
import aim4.map.merge.MergeMap;
import aim4.map.merge.MergeSpawnPoint;
import aim4.sim.simulator.merge.helper.SensorInputHelper;
import aim4.sim.simulator.merge.helper.SpawnHelper;
import aim4.vehicle.merge.MergeVehicleSimModel;
import com.sun.scenario.effect.Merge;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.css.Rect;

import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Callum on 15/03/2017.
 */
public class CoreMergeSimulatorTest {
    private CoreMergeSimulator sim;
    private MergeMap mockMap;
    private SpawnHelper mockSpawnHelper;
    private SensorInputHelper mockSensorInputHelper;
    private Map<Integer, MergeVehicleSimModel> mockVinToVehicles;

    private Driver mockDriver1;
    private Driver mockDriver2;
    private Driver mockDriver3;
    private MergeVehicleSimModel mockVehicle1;
    private MergeVehicleSimModel mockVehicle2;
    private MergeVehicleSimModel mockVehicle3;
    private DataCollectionLine mockDataLine1;
    private DataCollectionLine mockDataLine2;
    private DataCollectionLine mockDataLine3;


    @Before
    public void setUp() throws Exception {
        //Create Map
        mockMap = mock(MergeMap.class);
        mockDataLine1 = mock(DataCollectionLine.class);
        mockDataLine2 = mock(DataCollectionLine.class);
        mockDataLine3 = mock(DataCollectionLine.class);
        when(mockDataLine1
                .intersect(any(MergeVehicleSimModel.class), anyDouble(), any(Point2D.class), any(Point2D.class)))
                .thenReturn(false);
        when(mockDataLine2
                .intersect(any(MergeVehicleSimModel.class), anyDouble(), any(Point2D.class), any(Point2D.class)))
                .thenReturn(true);
        when(mockDataLine3
                .intersect(any(MergeVehicleSimModel.class), anyDouble(), any(Point2D.class), any(Point2D.class)))
                .thenReturn(false);
        when(mockMap.getDataCollectionLines()).thenReturn(new ArrayList<DataCollectionLine>(){{
            add(mockDataLine1);
            add(mockDataLine2);
            add(mockDataLine3);
        }});

        Rectangle2D mapBoundary = mock(Rectangle2D.class);
        when(mockMap.getDimensions()).thenReturn(mapBoundary);

        //Create Sim
        sim = new CoreMergeSimulator(mockMap);

        //Replace Helpers
        mockSpawnHelper = mock(SpawnHelper.class);
        mockSensorInputHelper = mock(SensorInputHelper.class);
        doNothing().when(mockSpawnHelper).spawnVehicles(anyDouble());
        doNothing().when(mockSensorInputHelper).provideSensorInput();

        final Field spawnHelperField = CoreMergeSimulator.class.getDeclaredField("spawnHelper");
        final Field sensorInputHelperField = CoreMergeSimulator.class.getDeclaredField("sensorInputHelper");
        spawnHelperField.setAccessible(true);
        sensorInputHelperField.setAccessible(true);
        spawnHelperField.set(sim, mockSpawnHelper);
        sensorInputHelperField.set(sim, mockSensorInputHelper);

        //Create vehicles and drivers
        Shape mockVehicle1Shape = mock(Shape.class);
        Shape mockVehicle2Shape = mock(Shape.class);
        Shape mockVehicle3Shape = mock(Shape.class);
        when(mockVehicle1Shape.intersects(mapBoundary)).thenReturn(true);
        when(mockVehicle2Shape.intersects(mapBoundary)).thenReturn(false); //Vehicle 2 Completes
        when(mockVehicle3Shape.intersects(mapBoundary)).thenReturn(true);

        Point2D pos1 = new Point2D.Double(1,1);
        Point2D pos2 = new Point2D.Double(2,2);
        Point2D pos3 = new Point2D.Double(3,3);

        mockDriver1 = mock(Driver.class);
        mockDriver2 = mock(Driver.class);
        mockDriver3 = mock(Driver.class);
        doNothing().when(mockDriver1).act();
        doNothing().when(mockDriver2).act();
        doNothing().when(mockDriver3).act();

        mockVehicle1 = mock(MergeVehicleSimModel.class);
        mockVehicle2 = mock(MergeVehicleSimModel.class);
        mockVehicle3 = mock(MergeVehicleSimModel.class);
        when(mockVehicle1.getShape()).thenReturn(mockVehicle1Shape);
        when(mockVehicle2.getShape()).thenReturn(mockVehicle2Shape);
        when(mockVehicle3.getShape()).thenReturn(mockVehicle3Shape);
        when(mockVehicle1.getPosition()).thenReturn(pos1);
        when(mockVehicle2.getPosition()).thenReturn(pos2);
        when(mockVehicle3.getPosition()).thenReturn(pos3);
        when(mockVehicle1.getDriver()).thenReturn(mockDriver1);
        when(mockVehicle2.getDriver()).thenReturn(mockDriver2);
        when(mockVehicle3.getDriver()).thenReturn(mockDriver3);
        when(mockVehicle1.getVIN()).thenReturn(1);
        when(mockVehicle2.getVIN()).thenReturn(2);
        when(mockVehicle3.getVIN()).thenReturn(3);

        mockVinToVehicles = new HashMap<Integer, MergeVehicleSimModel>(){{
            put(mockVehicle1.getVIN(), mockVehicle1);
            put(mockVehicle2.getVIN(), mockVehicle2);
            put(mockVehicle3.getVIN(), mockVehicle3);
        }};

        final Field vinToVehiclesField = CoreMergeSimulator.class.getDeclaredField("vinToVehicles");
        vinToVehiclesField.setAccessible(true);
        vinToVehiclesField.set(sim, mockVinToVehicles);
    }

    @Test
    public void testConstructor() throws Exception {
        assertEquals(sim.getNumCompletedVehicles(), 0);
        assertEquals(sim.getSimulationTime(), 0, 0);
    }

    @Test
    public void testStep() throws Exception {
        double timeStep = SimConfig.TIME_STEP;
        CoreMergeSimulator.CoreMergeSimStepResult result = sim.step(timeStep);

        assertEquals(sim.getSimulationTime(), timeStep, 0);
        assertEquals(sim.getNumCompletedVehicles(), 1); //Vehicle 2 completes
        assertEquals(result.getCompletedVehicles().keySet().size(), 1);
        assertTrue(result.getCompletedVehicles().keySet().contains(mockVehicle2.getVIN()));
        verify(mockSpawnHelper, times(1)).spawnVehicles(timeStep);
        verify(mockSensorInputHelper, times(1)).provideSensorInput();
        verify(mockDriver1, times(1)).act();
        verify(mockDriver2, times(1)).act();
        verify(mockDriver3, times(1)).act();
        verify(mockVehicle1, times(1)).move(timeStep);
        verify(mockVehicle2, times(1)).move(timeStep);
        verify(mockVehicle3, times(1)).move(timeStep);
        verify(mockDataLine1, times(3))
                .intersect(any(MergeVehicleSimModel.class), anyDouble(), any(Point2D.class), any(Point2D.class));
        verify(mockDataLine2, times(3))
                .intersect(any(MergeVehicleSimModel.class), anyDouble(), any(Point2D.class), any(Point2D.class));
        verify(mockDataLine3, times(3))
                .intersect(any(MergeVehicleSimModel.class), anyDouble(), any(Point2D.class), any(Point2D.class));
    }

    @Test
    public void testGetMap() throws Exception {
        final Field field = CoreMergeSimulator.class.getDeclaredField("map");
        field.setAccessible(true);

        MergeMap testValue = mock(MergeMap.class);
        field.set(sim, testValue);

        assertEquals(sim.getMap(), testValue);
    }

    @Test
    public void testGetSimulationTime() throws Exception {
        final Field field = CoreMergeSimulator.class.getDeclaredField("currentTime");
        field.setAccessible(true);

        double testValue = 42.0;
        field.set(sim, testValue);

        assertEquals(sim.getSimulationTime(), testValue, 0);
    }

    @Test
    public void testGetNumCompletedVehicles() throws Exception {
        final Field field = CoreMergeSimulator.class.getDeclaredField("numberOfCompletedVehicles");
        field.setAccessible(true);

        int testValue = 42;
        field.set(sim, testValue);

        assertEquals(sim.getNumCompletedVehicles(), testValue, 0);
    }

    @Test
    public void testGetAvgBitsTransmittedByCompletedVehicles() throws Exception {
        assertEquals(sim.getAvgBitsTransmittedByCompletedVehicles(), 0, 0);
    }

    @Test
    public void testGetAvgBitsReceivedByCompletedVehicles() throws Exception {
        assertEquals(sim.getAvgBitsReceivedByCompletedVehicles(), 0, 0);
    }

    @Test
    public void testGetActiveVehicle() throws Exception {
        final Field field = CoreMergeSimulator.class.getDeclaredField("vinToVehicles");
        field.setAccessible(true);

        final MergeVehicleSimModel activeVehicle1 = mock(MergeVehicleSimModel.class);
        final MergeVehicleSimModel activeVehicle2 = mock(MergeVehicleSimModel.class);
        final MergeVehicleSimModel activeVehicle3 = mock(MergeVehicleSimModel.class);
        HashMap<Integer, MergeVehicleSimModel> vinToVehicles = new HashMap<Integer, MergeVehicleSimModel>(){{
            put(11, activeVehicle1);
            put(22, activeVehicle2);
            put(33, activeVehicle3);
        }};
        field.set(sim, vinToVehicles);

        assertEquals(sim.getActiveVehicle(11), activeVehicle1);
        assertEquals(sim.getActiveVehicle(22), activeVehicle2);
        assertEquals(sim.getActiveVehicle(33), activeVehicle3);
    }
}