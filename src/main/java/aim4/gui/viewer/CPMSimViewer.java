package aim4.gui.viewer;

import aim4.config.Debug;
import aim4.config.SimConfig;
import aim4.gui.StatusPanelContainer;
import aim4.gui.Viewer;
import aim4.gui.ViewerCardType;
import aim4.gui.screen.cpm.CPMStatScreen;
import aim4.gui.setuppanel.CPMSimSetupPanel;
import aim4.gui.setuppanel.SimSetupPanel;
import aim4.map.cpm.CPMMapUtil.SpawnSpecType;
import aim4.sim.Simulator;
import aim4.sim.setup.cpm.CPMSingleWidthSimSetup;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import javafx.util.Pair;

import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A Viewer for CPM.
 */
public class CPMSimViewer extends SimViewer {

    private Viewer viewer;

    /**
     * Creates the CPMSimViewer
     *
     * @param statusPanel A reference to the StatusPanelContainer in Viewer
     * @param viewer
     */
    public CPMSimViewer(StatusPanelContainer statusPanel, Viewer viewer) {
        super(statusPanel, viewer, new CPMSimSetupPanel(new CPMSingleWidthSimSetup(
                5.0, // speedLimit - approx. 10mph
                0.0027, // trafficLevel
                50.0, // parkingLength
                1.0, // accessLength,
                SpawnSpecType.SINGLE, // spawn spec type
                new Pair<Boolean, String>(false, ""), // useCsvFile
                new Pair<Boolean, Double>(false, -1.0), // useSpecificSimTime
                "COUPE", // singleSpawnSpec
                new ArrayList<Double>(), //mixedSpawnDistribution
                2.0, // laneWidth
                10, // numberOfParkingLanes
                1, // numberOfSimulations
                ""
        )), false);
        this.viewer = viewer;
    }

    @Override
    protected void createStatScreen(Viewer viewer) {
        SimSetupPanel generalSetupPanel = getSimSetupPanel();
        assert generalSetupPanel instanceof CPMSimSetupPanel;
        CPMSimSetupPanel setupPanel = (CPMSimSetupPanel) generalSetupPanel;

        this.statScreen = new CPMStatScreen(viewer, this, setupPanel, 1);
    }

    @Override
    protected Simulator.SimStepResult runSimulationStep() {
        Simulator.SimStepResult stepResult = null;

        if (sim instanceof CPMAutoDriverSimulator && statScreen instanceof CPMStatScreen) {
            // check if the simulation should continue to run.
            if (((CPMAutoDriverSimulator) sim).hasSimTimeElapsed()) {
                pauseSimProcess();

                Integer numberOfSimulations = ((CPMStatScreen) statScreen).getNumberOfSimulations();
                Integer currentSimulationNumber = ((CPMStatScreen) statScreen).getCurrentSimulationNumber();

                String filepath = ((CPMStatScreen) statScreen).getFileLocation();
                printStatScreenToCsv(currentSimulationNumber, filepath);

                // If there are more simulations to run
                if (!currentSimulationNumber.equals(numberOfSimulations)) {

                    // Reset
                    resetSimProcess();
                    cleanUp();
                    ((CPMStatScreen) statScreen).updateCurrentSimulationNumber();

                    // Start
                    createSimulator();
                    showCard(ViewerCardType.SCREEN);
                    startViewer();
                    startSimProcess();
                } else {
                    ((CPMStatScreen) statScreen).updateSimulationStatus(true);
                    simThread.terminate();
                }
            } else { // If simulation time has not elapsed
                stepResult = super.runSimulationStep();

                assert stepResult instanceof CPMAutoDriverSimulator.CPMAutoDriverSimStepResult;
                CPMAutoDriverSimulator.CPMAutoDriverSimStepResult cpmStepResult =
                        (CPMAutoDriverSimulator.CPMAutoDriverSimStepResult) stepResult;
                ((CPMStatScreen) this.statScreen).addResultToProcess(cpmStepResult);
            }
        }
        return stepResult;
    }

    private void printStatScreenToCsv(int currentSimulationNumber, String fileLocation) {
        // output the statscreen data to a file with "sim" + simulationNumber + timestamp.csv
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String filename = "sim" + currentSimulationNumber + "_" + timestamp + ".csv";
        String fullFileName = fileLocation + "\\" + filename;
        statScreen.printData(fullFileName);
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
