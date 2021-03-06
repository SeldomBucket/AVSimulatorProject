package aim4.gui.screen.merge;

import aim4.gui.Viewer;
import aim4.gui.screen.StatScreen;
import aim4.gui.screen.merge.components.ActiveVehicleList;
import aim4.gui.screen.merge.components.CompletedVehicleList;
import aim4.gui.screen.merge.components.MergeStatScreenComponent;
import aim4.gui.screen.merge.components.TopBar;
import aim4.gui.setuppanel.MergeSimSetupPanel;
import aim4.gui.viewer.MergeSimViewer;
import aim4.sim.Simulator;
import aim4.sim.setup.merge.enums.MapType;
import aim4.sim.setup.merge.enums.ProtocolType;
import aim4.sim.simulator.merge.CoreMergeSimulator;
import aim4.sim.simulator.merge.MergeSimulator;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Callum on 08/02/2017.
 */
public class MergeStatScreen extends StatScreen {
    Viewer viewer;
    MergeSimSetupPanel setupPanel;
    MergeSimViewer simViewer;
    List<MergeStatScreenComponent> componentsToUpdate;
    List<CoreMergeSimulator.CoreMergeSimStepResult> resultsToProcess;

    public MergeStatScreen(Viewer viewer, MergeSimViewer simViewer, MergeSimSetupPanel setupPanel) {
        this.viewer = viewer;
        this.simViewer = simViewer;
        this.setupPanel = setupPanel;
        this.componentsToUpdate = new ArrayList<MergeStatScreenComponent>();
        this.resultsToProcess = new ArrayList<CoreMergeSimulator.CoreMergeSimStepResult>();
    }

    @Override
    public void start() {
        MapType mapType = setupPanel.getMapType();
        ProtocolType protocolType = setupPanel.getProtocolType();

        switch(mapType) {
            case SINGLE: setupSingleLaneScreen(); break;
            case S2S:
                switch(protocolType) {
                    case AIM_GRID: setupS2SAimScreen(); break;
                    case AIM_NO_GRID: setupS2SAimScreen(); break;
                    case QUEUE: setupS2SAimScreen(); break;
                    case TEST_MERGE: setupS2STestMergeScreen(); break;
                    case TEST_TARGET: setupS2STestTargetScreen(); break;
                    default: throw new RuntimeException("Unexpected protocol type for S2S map: " + protocolType.toString());
                }
                break;
            default: throw new RuntimeException("Unexpected map type: " + mapType.toString());
        }
    }

    @Override
    public void printData(String outFileName) {
        System.out.println("Printing MERGE statscreen data");
    }

    @Override
    public synchronized void update() {
        Simulator generalSim = simViewer.getSimulator();
        assert(generalSim instanceof MergeSimulator);
        MergeSimulator sim = (MergeSimulator) generalSim;

        for(MergeStatScreenComponent comp : componentsToUpdate) {
            comp.update(sim, resultsToProcess);
        }
        resultsToProcess.clear();
    }

    @Override
    public void cleanUp() {
        for(MergeStatScreenComponent comp : componentsToUpdate) {
            assert(comp instanceof JComponent); //Should always be true.
            this.remove((JComponent) comp);
        }
        componentsToUpdate.clear();
        this.removeAll();
        this.revalidate();
        this.repaint();
    }

    public void addResultToProcess(CoreMergeSimulator.CoreMergeSimStepResult simStepResult) {
        this.resultsToProcess.add(simStepResult);
    }

    //SINGLE LANE//
    private void setupSingleLaneScreen(){
        TopBar topBar = new TopBar();
        ActiveVehicleList activeVehicles = new ActiveVehicleList();
        CompletedVehicleList completedVehicles = new CompletedVehicleList();

        setLayout(new BorderLayout());
        add(topBar, BorderLayout.PAGE_START);
        add(activeVehicles, BorderLayout.CENTER);
        add(completedVehicles, BorderLayout.LINE_END);

        componentsToUpdate.add(topBar);
        componentsToUpdate.add(activeVehicles);
        componentsToUpdate.add(completedVehicles);
    }

    //S2S//
    private void setupS2SAimScreen(){
        TopBar topBar = new TopBar();
        ActiveVehicleList activeVehicles = new ActiveVehicleList();
        CompletedVehicleList completedVehicles = new CompletedVehicleList();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, activeVehicles, completedVehicles);

        setLayout(new BorderLayout());
        add(topBar, BorderLayout.PAGE_START);
        add(splitPane, BorderLayout.CENTER);

        componentsToUpdate.add(topBar);
        componentsToUpdate.add(activeVehicles);
        componentsToUpdate.add(completedVehicles);
    }

    private void setupS2SDecentralisedScreen(){
        TopBar topBar = new TopBar();
        ActiveVehicleList activeVehicles = new ActiveVehicleList();
        CompletedVehicleList completedVehicles = new CompletedVehicleList();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, activeVehicles, completedVehicles);

        setLayout(new BorderLayout());
        add(topBar, BorderLayout.PAGE_START);
        add(splitPane, BorderLayout.CENTER);

        componentsToUpdate.add(topBar);
        componentsToUpdate.add(activeVehicles);
        componentsToUpdate.add(completedVehicles);
    }

    private void setupS2STestTargetScreen() {
        TopBar topBar = new TopBar();
        ActiveVehicleList activeVehicles = new ActiveVehicleList();
        CompletedVehicleList completedVehicles = new CompletedVehicleList();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, activeVehicles, completedVehicles);

        setLayout(new BorderLayout());
        add(topBar, BorderLayout.PAGE_START);
        add(splitPane, BorderLayout.CENTER);

        componentsToUpdate.add(topBar);
        componentsToUpdate.add(activeVehicles);
        componentsToUpdate.add(completedVehicles);
    }

    private void setupS2STestMergeScreen() {
        TopBar topBar = new TopBar();
        ActiveVehicleList activeVehicles = new ActiveVehicleList();
        CompletedVehicleList completedVehicles = new CompletedVehicleList();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, activeVehicles, completedVehicles);

        setLayout(new BorderLayout());
        add(topBar, BorderLayout.PAGE_START);
        add(splitPane, BorderLayout.CENTER);

        componentsToUpdate.add(topBar);
        componentsToUpdate.add(activeVehicles);
        componentsToUpdate.add(completedVehicles);
    }

}
