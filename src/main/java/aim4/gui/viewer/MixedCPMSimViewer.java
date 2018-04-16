package aim4.gui.viewer;

import aim4.gui.StatusPanelContainer;
import aim4.gui.Viewer;
import aim4.gui.screen.mixedcpm.MixedCPMStatScreen;
import aim4.gui.setuppanel.MixedCPMSimSetupPanel;
import aim4.gui.setuppanel.SimSetupPanel;
import aim4.map.mixedcpm.MixedCPMBasicMap;
import aim4.map.mixedcpm.MixedCPMMapUtil.*;
import aim4.sim.Simulator;
import aim4.sim.setup.mixedcpm.BasicMixedCPMSimSetup;
import aim4.sim.simulator.aim.AIMSimulator;
import aim4.sim.simulator.mixedcpm.MixedCPMAutoDriverSimulator;
import aim4.util.Logging;
import javafx.util.Pair;
import sun.rmi.runtime.Log;

import java.awt.event.MouseEvent;

/**
 * A Viewer for CPM.
 */
public class MixedCPMSimViewer extends SimViewer {
    Viewer viewer;
    /**
     * Creates the CPMSimViewer
     *
     * @param statusPanel   A reference to the StatusPanelContainer in Viewer
     * @param viewer
     */
    public MixedCPMSimViewer(StatusPanelContainer statusPanel, Viewer viewer) {
        super(statusPanel, viewer, new MixedCPMSimSetupPanel(new BasicMixedCPMSimSetup(
                2.7, // speedLimit - approx 6mph
                0.28, // trafficLevel
                6.0, // laneWidth
                60, // carParkWidth
                40, // carParkHeight,
                SpawnSpecType.FINITE_SINGLE, // spawn spec type
                MapType.ADJUSTABLE_MANUAL,
                new Pair<Boolean, String>(false, "")
        )), false);
        this.viewer = viewer;
    }

    @Override
    protected void createStatScreen(Viewer viewer) {
        SimSetupPanel generalSetupPanel = getSimSetupPanel();
        assert generalSetupPanel instanceof MixedCPMSimSetupPanel;
        MixedCPMSimSetupPanel setupPanel = (MixedCPMSimSetupPanel) generalSetupPanel;

        this.statScreen = new MixedCPMStatScreen(viewer, this, setupPanel);
    }

    @Override
    protected Simulator.SimStepResult runSimulationStep() {
        Simulator.SimStepResult stepResult = super.runSimulationStep();

        assert stepResult instanceof MixedCPMAutoDriverSimulator.MixedCPMAutoDriverSimStepResult;
        MixedCPMAutoDriverSimulator.MixedCPMAutoDriverSimStepResult cpmStepResult =
                (MixedCPMAutoDriverSimulator.MixedCPMAutoDriverSimStepResult) stepResult;
        ((MixedCPMStatScreen) this.statScreen).addResultToProcess(cpmStepResult);

        return stepResult;
    }
    protected void runBeforeCreatingSimulator() {
        //assert sim instanceof MixedCPMAutoDriverSimulator;
        //statScreen = null;
        //createStatScreen(this.viewer);
    }

    protected void runBeforeResettingSimulator() {
        Logging.logFinalStats(((MixedCPMBasicMap)sim.getMap()).getStatusMonitor());
        Logging.closeLogFiles();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
