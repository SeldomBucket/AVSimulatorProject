package aim4.gui.viewer;

import aim4.gui.StatusPanelContainer;
import aim4.gui.Viewer;
import aim4.gui.screen.mixedcpm.MixedCPMStatScreen;
import aim4.gui.setuppanel.MixedCPMSimSetupPanel;
import aim4.gui.setuppanel.SimSetupPanel;
import aim4.map.mixedcpm.MixedCPMMapUtil.*;
import aim4.sim.Simulator;
import aim4.sim.setup.mixedcpm.BasicMixedCPMSimSetup;
import aim4.sim.simulator.mixedcpm.MixedCPMAutoDriverSimulator;
import javafx.util.Pair;

import java.awt.event.MouseEvent;

/**
 * A Viewer for CPM.
 */
public class MixedCPMSimViewer extends SimViewer {
    /**
     * Creates the CPMSimViewer
     *
     * @param statusPanel   A reference to the StatusPanelContainer in Viewer
     * @param viewer
     */
    public MixedCPMSimViewer(StatusPanelContainer statusPanel, Viewer viewer) {
        super(statusPanel, viewer, new MixedCPMSimSetupPanel(new BasicMixedCPMSimSetup(
                1, // speedLimit -
                0.28, // trafficLevel
                3.0, // laneWidth
                30, // carParkWidth
                20, // carParkHeight,
                SpawnSpecType.SINGLE, // spawn spec type
                new Pair<Boolean, String>(false, "")
        )), false);
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
