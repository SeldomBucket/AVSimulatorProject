package aim4.cpm.vehicle;

import aim4.gui.StatusPanelContainer;
import aim4.gui.Viewer;
import aim4.gui.viewer.CPMSimViewer;
import aim4.gui.viewer.SimViewer;
import aim4.gui.viewer.SimViewer.*;
import aim4.map.cpm.CPMMap;
import aim4.map.cpm.CPMMapUtil;
import aim4.map.cpm.testmaps.CPMCarParkWithStatus;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import org.junit.Test;

import static org.junit.Assert.*;

public class CPMBasicAutoVehicleTest {
    CPMCarParkWithStatus map = new CPMCarParkWithStatus(4, // laneWidth
            10.0, // speedLimit
            0.0, // initTime
            2, // numberOfParkingLanes
            20, // parkingLength
            5); // access length

    @Test
    public void testRunSimulator() throws Exception {
        /** Viewer doesn't seem to do anything */
        /*Viewer viewer = new Viewer();

        StatusPanelContainer statusPanel = new StatusPanelContainer(viewer);
        CPMSimViewer cpmSimViewer = new CPMSimViewer(statusPanel, viewer);
        cpmSimViewer.createSimulator();
        viewer.startSimProcess();*/

        /** Need a SimViewer to create a new SimThread*/
        /*CPMMapUtil.setUpOneVehicleSpawnPoint(map);
        CPMAutoDriverSimulator sim = new CPMAutoDriverSimulator(map);*/
        /*double targetFrameRate = 20.0;
        long timerDelay = (long) (1000.0 / targetFrameRate);
        SimThread simThread = new SimThread(true, timerDelay);*/
    }



}