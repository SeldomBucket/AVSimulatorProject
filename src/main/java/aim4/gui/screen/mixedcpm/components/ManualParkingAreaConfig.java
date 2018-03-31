package aim4.gui.screen.mixedcpm.components;

import aim4.gui.screen.merge.components.MapKeyTableModel;
import aim4.gui.setuppanel.MixedCPMSimSetupPanel;
import aim4.map.cpm.CPMCarParkWithStatus;
import aim4.map.mixedcpm.MixedCPMBasicMap;
import aim4.sim.setup.cpm.BasicCPMSimSetup;
import aim4.sim.setup.mixedcpm.BasicMixedCPMSimSetup;
import aim4.sim.simulator.mixedcpm.MixedCPMAutoDriverSimulator.*;
import aim4.sim.simulator.mixedcpm.MixedCPMAutoDriverSimulator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.List;
import java.util.Map;

/**
 * A section displaying stats about the current state of the car park.
 */
public class ManualParkingAreaConfig extends JPanel {

    private JLabel carParkAreaLabel;
    private JLabel carParkHeightLabel;
    private JLabel carParkWidthLabel;
    private JLabel spawnTypeLabel;
    private JLabel trafficLevelLabel;

    public ManualParkingAreaConfig(MixedCPMSimSetupPanel setupPanel) {
        BasicMixedCPMSimSetup setup = (BasicMixedCPMSimSetup)setupPanel.getSimSetup();

        JLabel title = new JLabel("Manual Parking Area Config");
        Font font = title.getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        title.setFont(font.deriveFont(attributes));

        int carParkHeight = (int)   setup.getCarParkHeight();
        carParkHeightLabel = new JLabel("Manual parking area height (metres): " + carParkHeight);
        carParkHeightLabel.setOpaque(true);

        int carParkWidth = (int)    setup.getCarParkWidth();
        carParkWidthLabel = new JLabel("Manual parking area width (metres): " + carParkWidth);
        carParkWidthLabel.setOpaque(true);

        int carParkArea = (int)     (setup.getCarParkHeight()*setup.getCarParkWidth());
        carParkAreaLabel = new JLabel("Manual parking area area (square metres): " + carParkArea);
        carParkAreaLabel.setOpaque(true);

        int trafficLevel = (int)    (setup.getTrafficLevel()*3600);
        trafficLevelLabel = new JLabel("Traffic Level (vehicles per hour): " + trafficLevel);
        trafficLevelLabel.setOpaque(true);

        String spawnType = setup.getSpawnSpecType().name();
        spawnTypeLabel = new JLabel("Spawn Type: " + spawnType);
        spawnTypeLabel.setOpaque(true);

        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(title);

        this.add(carParkHeightLabel);
        this.add(carParkWidthLabel);
        this.add(carParkAreaLabel);
        this.add(trafficLevelLabel);
        this.add(spawnTypeLabel);


        updateLabel(carParkHeight, carParkHeightLabel);
        updateLabel(carParkWidth, carParkWidthLabel);
        updateLabel(carParkArea, carParkAreaLabel);
    }

    private void updateLabel(int newValue, JLabel label){
        String labelText = label.getText();
        // Split the text so we can remove the old value and reuse the label
        String[] labelSplit = labelText.split(":");
        String labelOnly = labelSplit [0];
        label.setText(labelOnly + ": " + newValue);
    }


    private void updateLabel(String newValue, JLabel label){
        String labelText = label.getText();
        // Split the text so we can remove the old value and reuse the label
        String[] labelSplit = labelText.split(":");
        String labelOnly = labelSplit [0];
        label.setText(labelOnly + ": " + newValue);
    }
}
