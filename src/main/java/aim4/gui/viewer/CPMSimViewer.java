package aim4.gui.viewer;

import aim4.gui.StatusPanelContainer;
import aim4.gui.Viewer;
import aim4.gui.screen.cpm.CPMStatScreen;
import aim4.gui.setuppanel.CPMSimSetupPanel;
import aim4.gui.setuppanel.SimSetupPanel;
import aim4.map.cpm.CPMMapUtil.*;
import aim4.sim.Simulator;
import aim4.sim.setup.cpm.BasicCPMSimSetup;
import aim4.sim.setup.cpm.CPMAutoDriverSimSetup;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import javafx.util.Pair;

import java.awt.event.MouseEvent;

/**
 * A Viewer for CPM.
 */
public class CPMSimViewer extends SimViewer {
    /**
     * Creates the CPMSimViewer
     *
     * @param statusPanel   A reference to the StatusPanelContainer in Viewer
     * @param viewer
     */
    public CPMSimViewer(StatusPanelContainer statusPanel, Viewer viewer) {
        super(statusPanel, viewer, new CPMSimSetupPanel(new BasicCPMSimSetup(
                5.0, // speedLimit - approx. 10mph
                0.28, // trafficLevel
                2.0, // laneWidth
                1, // numberOfParkingLanes
                50.0, // parkingLength
                1.0, // accessLength,
                SpawnSpecType.SINGLE, // spawn spec type
                new Pair<Boolean, String>(false, "")
        )), false);
    }

    @Override
    protected void createStatScreen(Viewer viewer) {
        SimSetupPanel generalSetupPanel = getSimSetupPanel();
        assert generalSetupPanel instanceof CPMSimSetupPanel;
        CPMSimSetupPanel setupPanel = (CPMSimSetupPanel) generalSetupPanel;

        this.statScreen = new CPMStatScreen(viewer, this, setupPanel);
    }

    @Override
    protected Simulator.SimStepResult runSimulationStep() {
        Simulator.SimStepResult stepResult = super.runSimulationStep();

        assert stepResult instanceof CPMAutoDriverSimulator.CPMAutoDriverSimStepResult;
        CPMAutoDriverSimulator.CPMAutoDriverSimStepResult cpmStepResult =
                (CPMAutoDriverSimulator.CPMAutoDriverSimStepResult) stepResult;
        ((CPMStatScreen) this.statScreen).addResultToProcess(cpmStepResult);

        return stepResult;
    }

    @Override
    protected void runBeforeCreatingSimulator() {

    }

    @Override
    protected void runBeforeResettingSimulator() {

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
