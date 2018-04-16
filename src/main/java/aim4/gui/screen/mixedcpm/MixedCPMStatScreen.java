package aim4.gui.screen.mixedcpm;

import aim4.gui.Viewer;
import aim4.gui.screen.StatScreen;
import aim4.gui.screen.mixedcpm.components.*;
import aim4.gui.setuppanel.MixedCPMSimSetupPanel;
import aim4.gui.viewer.MixedCPMSimViewer;
import aim4.map.mixedcpm.MixedCPMMapUtil;
import aim4.sim.Simulator;
import aim4.sim.setup.mixedcpm.BasicMixedCPMSimSetup;
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
        this.removeAll();
        componentsToUpdate.clear();
    }

    public void addResultToProcess(MixedCPMAutoDriverSimStepResult simStepResult) {
        this.resultsToProcess.add(simStepResult);
    }

    private void setupScreen(){

        SimulationStats simulationStats = new SimulationStats(setupPanel);
        simulationStats.setBorder(new EmptyBorder(10, 10, 10, 10));

        CarParkEfficiencyStats carParkEfficiencyStats = new CarParkEfficiencyStats();
        carParkEfficiencyStats.setBorder(new EmptyBorder(10, 10, 10, 10));

        ManualAreaEfficiencyStats manualAreaEfficiencyStats = new ManualAreaEfficiencyStats();
        manualAreaEfficiencyStats.setBorder(new EmptyBorder(10, 10, 10, 10));

        AutomatedAreaEfficiencyStats automatedAreaEfficiencyStats = new AutomatedAreaEfficiencyStats();
        automatedAreaEfficiencyStats.setBorder(new EmptyBorder(10, 10, 10, 10));

        CarParkVehicleStats carParkVehicleStats = new CarParkVehicleStats();
        carParkVehicleStats.setBorder(new EmptyBorder(10, 10, 10, 10));

        ManualParkingAreaStats manualParkingAreaVehicleStats = new ManualParkingAreaStats();
        manualParkingAreaVehicleStats.setBorder(new EmptyBorder(10, 10, 10, 10));

        AutomatedParkingAreaStats autoParkingAreaVehicleStats = new AutomatedParkingAreaStats();
        autoParkingAreaVehicleStats.setBorder(new EmptyBorder(10, 10, 10, 10));

        CompletedVehiclesTable completedVehiclesTable = new CompletedVehiclesTable();
        completedVehiclesTable.setMaximumSize(new Dimension(1000, 1000));

        setLayout(new FlowLayout());

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, 1));
        JPanel areaStatsPanel = new JPanel();
        areaStatsPanel.setLayout(new BoxLayout(areaStatsPanel, 1));


        BasicMixedCPMSimSetup setup = (BasicMixedCPMSimSetup)setupPanel.getSimSetup();


        statsPanel.add("simStats", simulationStats);
        statsPanel.add("efficiencyStats", carParkEfficiencyStats);
        add(statsPanel);

        areaStatsPanel.add("vehicleStats", carParkVehicleStats);
        add(areaStatsPanel);

        componentsToUpdate.add(simulationStats);
        componentsToUpdate.add(carParkEfficiencyStats);
        componentsToUpdate.add(carParkVehicleStats);
        componentsToUpdate.add(completedVehiclesTable);

        add(completedVehiclesTable);

        componentsToUpdate.add(manualParkingAreaVehicleStats);
        componentsToUpdate.add(manualAreaEfficiencyStats);
        areaStatsPanel.add("manualStats", manualParkingAreaVehicleStats);
        statsPanel.add("efficiencyStats", manualAreaEfficiencyStats);

        /*if (setup.getMapType() == MixedCPMMapUtil.MapType.ADJUSTABLE_MANUAL
                || setup.getMapType() == MixedCPMMapUtil.MapType.ADJUSTABLE_MIXED){

        }*/

        if(setup.getMapType() == MixedCPMMapUtil.MapType.ADJUSTABLE_MIXED){
            componentsToUpdate.add(autoParkingAreaVehicleStats);
            componentsToUpdate.add(automatedAreaEfficiencyStats);
            areaStatsPanel.add("autoStats", autoParkingAreaVehicleStats);
            statsPanel.add("efficiencyStats", automatedAreaEfficiencyStats);
        }


    }
}
