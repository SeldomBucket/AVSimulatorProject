package aim4.gui.screen.cpm.components;

import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator.*;
import aim4.sim.simulator.merge.CoreMergeSimulator;
import aim4.sim.simulator.merge.MergeSimulator;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * This holds the global information shown at the top of the stat screen.
 */
public class CPMTopBar extends JPanel implements CPMStatScreenComponent{
    private JLabel simTimeLabel;
    private JLabel completedVehiclesLabel;

    public CPMTopBar() {
        simTimeLabel = new JLabel("Simulation Time: ");
        simTimeLabel.setOpaque(true);

        completedVehiclesLabel = new JLabel("Completed Vehicles: ");
        completedVehiclesLabel.setOpaque(true);

        this.setLayout(new FlowLayout());
        this.add(simTimeLabel);
        this.add(completedVehiclesLabel);
        this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    }

    public void update(CPMAutoDriverSimulator sim, List<CPMAutoDriverSimStepResult> results) {
        double simTime = sim.getSimulationTime();
        int completedVehicles = sim.getNumCompletedVehicles();
        updateSimTimeLabel(simTime);
        updateCompletedVehiclesLabel(completedVehicles);
    }

    private void updateSimTimeLabel(double simTime){
        simTimeLabel.setText("Simulation Time: " + String.format("%.2fs", simTime));
    }

    private void updateCompletedVehiclesLabel(int completedVehicles){
        completedVehiclesLabel.setText("Completed Vehicles: " + completedVehicles);
    }
}
