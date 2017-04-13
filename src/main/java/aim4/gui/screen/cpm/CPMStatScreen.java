package aim4.gui.screen.cpm;

import aim4.gui.Viewer;
import aim4.gui.screen.StatScreen;
import aim4.gui.screen.cpm.components.CPMStatScreenComponent;
import aim4.gui.screen.cpm.components.CPMTopBar;
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
        CPMTopBar topBar = new CPMTopBar();
        CompletedVehiclesTable completedVehiclesTable = new CompletedVehiclesTable();
        /*CompletedVehicleList completedVehicles = new CompletedVehicleList();*/

        setLayout(new BorderLayout());
        add(topBar, BorderLayout.PAGE_START);
        add(completedVehiclesTable, BorderLayout.CENTER);
        /*add(activeVehicles, BorderLayout.CENTER);
        add(completedVehicles, BorderLayout.LINE_END);*/

        componentsToUpdate.add(topBar);
        componentsToUpdate.add(completedVehiclesTable);
        /*componentsToUpdate.add(activeVehicles);
        componentsToUpdate.add(completedVehicles);*/
    }
}
