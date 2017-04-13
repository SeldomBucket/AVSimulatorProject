package aim4.gui.screen.cpm.components;

import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import aim4.sim.simulator.merge.CoreMergeSimulator;
import aim4.sim.simulator.merge.MergeSimulator;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Created by Becci on 13-Apr-17.
 */
public class CPMTopBar extends JPanel implements CPMStatScreenComponent{
    private JLabel simTimeLabel;
    private JLabel completedVehiclesLabel;
    private JLabel throughputLabel;

    public CPMTopBar() {
        simTimeLabel = new JLabel("Simulation Time: ");
        simTimeLabel.setOpaque(true);

        completedVehiclesLabel = new JLabel("Completed Vehicles: ");
        completedVehiclesLabel.setOpaque(true);

        throughputLabel = new JLabel("Throughput: ");
        throughputLabel.setOpaque(true);

        this.setLayout(new FlowLayout());
        this.add(simTimeLabel);
        this.add(completedVehiclesLabel);
        this.add(throughputLabel);
        this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    }

    public void update(MergeSimulator sim, List<CoreMergeSimulator.CoreMergeSimStepResult> results) {
        double simTime = sim.getSimulationTime();
        int completedVehicles = sim.getNumCompletedVehicles();
        updateSimTimeLabel(simTime);
        updateCompletedVehiclesLabel(completedVehicles);
        updateThroughputLabel(simTime, completedVehicles);
    }

    private void updateSimTimeLabel(double simTime){
        simTimeLabel.setText("Simulation Time: " + String.format("%.2fs", simTime));
    }

    private void updateCompletedVehiclesLabel(int completedVehicles){
        completedVehiclesLabel.setText("Completed Vehicles: " + completedVehicles);
    }

    private void updateThroughputLabel(double simTime, int completedVehicles){
        throughputLabel.setText("Throughput: " + String.format("%.2fvehicles/s", completedVehicles/simTime));
    }
}
