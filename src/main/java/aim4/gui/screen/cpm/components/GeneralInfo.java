package aim4.gui.screen.cpm.components;

import aim4.map.cpm.CPMSpawnPoint;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator.*;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * This holds the global information shown at the top of the stat screen.
 */
public class GeneralInfo extends JPanel implements CPMStatScreenComponent{
    private JLabel simTimeLabel;
    private JLabel completedVehiclesLabel;
    private JLabel remainingVehiclesToSpawnLabel;

    public GeneralInfo() {
        simTimeLabel = new JLabel("Simulation Time: ");
        simTimeLabel.setOpaque(true);

        completedVehiclesLabel = new JLabel("Completed Vehicles: ");
        completedVehiclesLabel.setOpaque(true);

        remainingVehiclesToSpawnLabel = new JLabel("Number of vehicles left to spawn: ");
        remainingVehiclesToSpawnLabel.setOpaque(true);

        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(simTimeLabel);
        this.add(completedVehiclesLabel);
        this.add(remainingVehiclesToSpawnLabel);
        this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    }

    public void update(CPMAutoDriverSimulator sim, List<CPMAutoDriverSimStepResult> results) {
        double simTime = sim.getSimulationTime();
        int completedVehicles = sim.getNumCompletedVehicles();
        updateSimTimeLabel(simTime);
        updateLabel(completedVehicles, completedVehiclesLabel);
        CPMSpawnPoint.CPMSpawnSpecGenerator spawnSpecGenerator = sim.getMap().getSpawnPoints().get(0).getVehicleSpecChooser();
        updateLabel(spawnSpecGenerator.getNumberOfVehiclesLeftToSpawn(), remainingVehiclesToSpawnLabel);
    }

    @Override
    public List<String> getAllLabelsText() {
        return null;
    }

    private String convertSecondsToTimeString(double timeInSeconds) {
        Date date = new Date((long)(timeInSeconds*1000));
        String formattedDate = new SimpleDateFormat("HH:mm:ss").format(date);
        return formattedDate;
    }

    private void updateLabel(int newValue, JLabel label){
        String labelText = label.getText();
        // Split the text so we can remove the old value and reuse the label
        String[] labelSplit = labelText.split(":");
        String labelOnly = labelSplit [0];
        if (newValue == -1) {
            label.setText(labelOnly + ": " + "N/A");
        } else {
            label.setText(labelOnly + ": " + newValue);
        }
    }

    private void updateSimTimeLabel(double simTime){
        simTimeLabel.setText("Simulation Time: " + String.format("%.2fs", simTime) + "/"
        + convertSecondsToTimeString(simTime));
    }
}
