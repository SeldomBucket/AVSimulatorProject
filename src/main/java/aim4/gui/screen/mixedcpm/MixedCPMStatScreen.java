package aim4.gui.screen.mixedcpm;

import aim4.gui.Viewer;
import aim4.gui.screen.StatScreen;
import aim4.gui.screen.mixedcpm.components.MixedCPMStatScreenComponent;
import aim4.gui.setuppanel.MixedCPMSimSetupPanel;
import aim4.gui.viewer.MixedCPMSimViewer;
import aim4.sim.Simulator;
import aim4.sim.simulator.mixedcpm.MixedCPMAutoDriverSimulator;
import aim4.sim.simulator.mixedcpm.MixedCPMAutoDriverSimulator.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The StatScreen that displays statistics for the CPM simulation that is running.
 */
public class MixedCPMStatScreen extends StatScreen {

    Viewer viewer;
    MixedCPMSimSetupPanel setupPanel;
    MixedCPMSimViewer simViewer;
    List<MixedCPMStatScreenComponent> componentsToUpdate;
    List<MixedCPMAutoDriverSimStepResult> resultsToProcess;

    public MixedCPMStatScreen(Viewer viewer, MixedCPMSimViewer simViewer, MixedCPMSimSetupPanel setupPanel) {
        this.viewer = viewer;
        this.simViewer = simViewer;
        this.setupPanel = setupPanel;
        this.componentsToUpdate = new ArrayList<MixedCPMStatScreenComponent>();
        this.resultsToProcess = new ArrayList<MixedCPMAutoDriverSimStepResult>();
    }

    @Override
    public void start() {
        setupScreen();
    }

    @Override
    public void printData(String outFileName) {
        System.out.println("Printing CPM statscreen data");
    }

    @Override
    public synchronized void update() {
        Simulator generalSim = simViewer.getSimulator();
        assert(generalSim instanceof MixedCPMAutoDriverSimulator);
        MixedCPMAutoDriverSimulator sim = (MixedCPMAutoDriverSimulator) generalSim;

        for(MixedCPMStatScreenComponent comp : componentsToUpdate) {
            comp.update(sim, resultsToProcess);
        }
        resultsToProcess.clear();
    }

    @Override
    public void cleanUp() {
        for(MixedCPMStatScreenComponent comp : componentsToUpdate) {
            assert(comp instanceof JComponent); //Should always be true.
            this.remove((JComponent) comp);
        }
        componentsToUpdate.clear();
    }

    public void addResultToProcess(MixedCPMAutoDriverSimStepResult simStepResult) {
        this.resultsToProcess.add(simStepResult);
    }

    private void setupScreen(){
        /*GeneralInfo generalInfo = new GeneralInfo();
        generalInfo.setBorder(new EmptyBorder(10, 10, 10, 10));

        SimConfigSummary simConfigSummary = new SimConfigSummary(setupPanel);
        simConfigSummary.setBorder(new EmptyBorder(10, 10, 10, 10));

        CarParkStats carParkStats = new CarParkStats();
        carParkStats.setBorder(new EmptyBorder(10, 10, 10, 10));

        CompletedVehiclesTable completedVehiclesTable = new CompletedVehiclesTable();
        completedVehiclesTable.setMaximumSize(new Dimension(60, 60));


        setLayout(new FlowLayout());
        add(generalInfo);
        add(simConfigSummary);
        add(carParkStats);
        add(completedVehiclesTable);


        componentsToUpdate.add(generalInfo);
        componentsToUpdate.add(simConfigSummary);
        componentsToUpdate.add(carParkStats);
        componentsToUpdate.add(completedVehiclesTable);*/
    }
}
