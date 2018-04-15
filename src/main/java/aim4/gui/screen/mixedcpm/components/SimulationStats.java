package aim4.gui.screen.mixedcpm.components;

import aim4.gui.screen.merge.components.MapKeyTableModel;
import aim4.gui.setuppanel.MixedCPMSimSetupPanel;
import aim4.map.cpm.CPMCarParkWithStatus;
import aim4.map.mixedcpm.MixedCPMBasicMap;
import aim4.sim.setup.cpm.BasicCPMSimSetup;
import aim4.sim.setup.mixedcpm.BasicMixedCPMSimSetup;
import aim4.sim.simulator.mixedcpm.MixedCPMAutoDriverSimulator.*;
import aim4.sim.simulator.mixedcpm.MixedCPMAutoDriverSimulator;
import aim4.util.Util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.List;
import java.util.Map;

/**
 * A section displaying stats about the current state of the car park.
 */
public class SimulationStats extends JPanel implements MixedCPMStatScreenComponent  {

    private JLabel simulationTimeLabel;
    private JLabel spawnTypeLabel;
    private JLabel mapTypeLabel;
    private JLabel trafficLevelLabel;

    public SimulationStats(MixedCPMSimSetupPanel setupPanel) {
        BasicMixedCPMSimSetup setup = (BasicMixedCPMSimSetup)setupPanel.getSimSetup();

        JLabel title = new JLabel("Car Park Config");
        Font font = title.getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        title.setFont(font.deriveFont(attributes));

        simulationTimeLabel = new JLabel("Simulation time: ");
        simulationTimeLabel.setOpaque(true);


        int trafficLevel = (int)    (setup.getTrafficLevel()*3600);
        trafficLevelLabel = new JLabel("Traffic Level (vehicles per hour): " + trafficLevel);
        trafficLevelLabel.setOpaque(true);

        String spawnType = setup.getSpawnSpecType().name();
        spawnTypeLabel = new JLabel("Spawn Type: " + spawnType);
        spawnTypeLabel.setOpaque(true);

        String mapType = setup.getMapType().name();
        mapTypeLabel = new JLabel("Map Type: " + mapType);
        mapTypeLabel.setOpaque(true);

        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(title);

        this.add(simulationTimeLabel);
        this.add(trafficLevelLabel);
        this.add(spawnTypeLabel);
        this.add(mapTypeLabel);

    }


    public void update(MixedCPMAutoDriverSimulator sim, List<MixedCPMAutoDriverSimulator.MixedCPMAutoDriverSimStepResult> resultToProcess) {
        double simulationTime = Math.round(sim.getSimulationTime());

        updateLabel(simulationTime, simulationTimeLabel);
    }

    private void updateLabel(int newValue, JLabel label){
        String labelText = label.getText();
        // Split the text so we can remove the old value and reuse the label
        String[] labelSplit = labelText.split(":");
        String labelOnly = labelSplit [0];
        label.setText(labelOnly + ": " + newValue);
    }

    private void updateLabel(Double newValue, JLabel label){
        String labelText = label.getText();
        // Split the text so we can remove the old value and reuse the label
        String[] labelSplit = labelText.split(":");
        String labelOnly = labelSplit [0];
        label.setText(labelOnly + ": " + Util.roundToDecimalPlaces(newValue,2));
    }
}
