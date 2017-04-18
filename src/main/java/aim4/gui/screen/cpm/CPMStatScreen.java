package aim4.gui.screen.cpm;

import aim4.gui.Viewer;
import aim4.gui.screen.StatScreen;
import aim4.gui.screen.cpm.components.CPMStatScreenComponent;
import aim4.gui.screen.cpm.components.GeneralInfo;
import aim4.gui.screen.cpm.components.CarParkStats;
import aim4.gui.screen.cpm.components.CompletedVehiclesTable;
import aim4.gui.setuppanel.CPMSimSetupPanel;
import aim4.gui.viewer.CPMSimViewer;
import aim4.sim.Simulator;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The StatScreen that displays statistics for the CPM simulation that is running.
 */
public class CPMStatScreen extends StatScreen {

    Viewer viewer;
    CPMSimSetupPanel setupPanel;
    CPMSimViewer simViewer;
    List<CPMStatScreenComponent> componentsToUpdate;
    List<CPMAutoDriverSimStepResult> resultsToProcess;

    public CPMStatScreen(Viewer viewer, CPMSimViewer simViewer, CPMSimSetupPanel setupPanel) {
        this.viewer = viewer;
        this.simViewer = simViewer;
        this.setupPanel = setupPanel;
        this.componentsToUpdate = new ArrayList<CPMStatScreenComponent>();
        this.resultsToProcess = new ArrayList<CPMAutoDriverSimStepResult>();
    }

    @Override
    public void start() {
        setupScreen();
    }

    @Override
    public synchronized void update() {
        Simulator generalSim = simViewer.getSimulator();
        assert(generalSim instanceof CPMAutoDriverSimulator);
        CPMAutoDriverSimulator sim = (CPMAutoDriverSimulator) generalSim;

        for(CPMStatScreenComponent comp : componentsToUpdate) {
            comp.update(sim, resultsToProcess);
        }
        resultsToProcess.clear();
    }

    @Override
    public void cleanUp() {
        for(CPMStatScreenComponent comp : componentsToUpdate) {
            assert(comp instanceof JComponent); //Should always be true.
            this.remove((JComponent) comp);
        }
        componentsToUpdate.clear();
    }

    public void addResultToProcess(CPMAutoDriverSimStepResult simStepResult) {
        this.resultsToProcess.add(simStepResult);
    }

    private void setupScreen(){
        GeneralInfo topBar = new GeneralInfo();
        CarParkStats carParkStats = new CarParkStats();
        CompletedVehiclesTable completedVehiclesTable = new CompletedVehiclesTable();
        completedVehiclesTable.setMaximumSize(new Dimension(60, 60));


        setLayout(new FlowLayout());
        add(topBar);
        add(carParkStats);
        add(completedVehiclesTable);


        componentsToUpdate.add(topBar);
        componentsToUpdate.add(carParkStats);
        componentsToUpdate.add(completedVehiclesTable);
    }
}
