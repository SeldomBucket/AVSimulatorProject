package aim4.gui.viewer;

import aim4.gui.StatusPanelContainer;
import aim4.gui.Viewer;
import aim4.gui.screen.mixedcpm.MixedCPMStatScreen;
import aim4.gui.setuppanel.MixedCPMSimSetupPanel;
import aim4.gui.setuppanel.SimSetupPanel;
import aim4.map.mixedcpm.MixedCPMBasicMap;
import aim4.map.mixedcpm.MixedCPMMapUtil.*;
import aim4.sim.Simulator;
import aim4.sim.setup.SimSetup;
import aim4.sim.setup.aim.BasicSimSetup;
import aim4.sim.setup.mixedcpm.BasicMixedCPMSimSetup;
import aim4.sim.setup.mixedcpm.MixedCPMSimSetup;
import aim4.sim.simulator.aim.AIMSimulator;
import aim4.sim.simulator.mixedcpm.MixedCPMAutoDriverSimulator;
import aim4.util.Logging;
import javafx.util.Pair;
import sun.rmi.runtime.Log;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * A Viewer for CPM.
 */
public class MixedCPMSimViewer extends SimViewer {
    private Viewer viewer;
    private List<String> csvFiles;
    private boolean isRestartImmediately = false;

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
        if(cpmStepResult.isCompleted() && csvFiles != null){
            if (!isRestartImmediately){
                csvFiles = null;
            }
            this.viewer.resetSimProcess();
        }
        return stepResult;
    }

    protected void runBeforeCreatingSimulator() {

    }

    protected SimSetup getSetup(){
        BasicMixedCPMSimSetup setup = (BasicMixedCPMSimSetup)getSimSetupPanel().getSimSetup();
        if (csvFiles == null) {
            Pair<Boolean, String> csv = setup.getMultipleCSVFile();
            if (csv.getKey()) {
                try {
                    Path path = Paths.get(csv.getValue());
                    this.csvFiles = new ArrayList<>(Files.readAllLines(path));
                    setup.setUseCSVFile(new Pair<Boolean, String>(true, csvFiles.get(0)));
                    csvFiles.remove(0);
                    isRestartImmediately = true;
                }catch (IOException e){
                    System.out.println(e.getStackTrace());
                }
            }
        }else if (csvFiles.size() > 0) {
            setup.setUseCSVFile(new Pair<Boolean, String>(true, csvFiles.get(0)));
            csvFiles.remove(0);
            if (csvFiles.size() == 0) {
                isRestartImmediately = false;
            }
        }
        return setup;
    }

    protected void runBeforeResettingSimulator() {
        Logging.logFinalStats(((MixedCPMBasicMap)sim.getMap()).getStatusMonitor());
        Logging.closeLogFiles();
    }

    public boolean runningMultipleTests(){
        return csvFiles != null;
    }

    @Override
    public boolean isRestartImmediately(){
        return isRestartImmediately;
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
