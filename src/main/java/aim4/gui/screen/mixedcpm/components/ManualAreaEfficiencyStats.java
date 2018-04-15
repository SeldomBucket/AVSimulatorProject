package aim4.gui.screen.mixedcpm.components;

import aim4.map.mixedcpm.MixedCPMBasicMap;
import aim4.sim.simulator.mixedcpm.MixedCPMAutoDriverSimulator;
import aim4.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.List;
import java.util.Map;

public class ManualAreaEfficiencyStats extends JPanel implements MixedCPMStatScreenComponent {

    private JLabel carParkAreaLabel;
    private JLabel carParkHeightLabel;
    private JLabel carParkWidthLabel;
    private JLabel currentEfficiencyLabel;
    private JLabel maxEfficiencyLabel;
    private JLabel currentAreaPerVehicleLabel;
    private JLabel minAreaPerVehicleLabel;

    public ManualAreaEfficiencyStats(){
        JLabel title = new JLabel("Manual Parking Area Efficiency Stats");
        Font font = title.getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        title.setFont(font.deriveFont(attributes));

        carParkHeightLabel = new JLabel("Manual parking area height (metres): ");
        carParkHeightLabel.setOpaque(true);

        carParkWidthLabel = new JLabel("Manual parking area width (metres): ");
        carParkWidthLabel.setOpaque(true);

        carParkAreaLabel = new JLabel("Manual parking area area (m^2): ");
        carParkAreaLabel.setOpaque(true);

        currentEfficiencyLabel = new JLabel("Current efficiency: ");
        currentEfficiencyLabel.setOpaque(true);

        maxEfficiencyLabel = new JLabel("Max efficiency: ");
        maxEfficiencyLabel.setOpaque(true);

        currentAreaPerVehicleLabel = new JLabel("Current area per vehicle (m^2): ");
        currentAreaPerVehicleLabel.setOpaque(true);

        minAreaPerVehicleLabel = new JLabel("Min area per vehicle (m^2): ");
        minAreaPerVehicleLabel.setOpaque(true);

        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(title);
        this.add(carParkHeightLabel);
        this.add(carParkWidthLabel);
        this.add(carParkAreaLabel);
        this.add(currentEfficiencyLabel);
        this.add(maxEfficiencyLabel);
        this.add(currentAreaPerVehicleLabel);
        this.add(minAreaPerVehicleLabel);
    }

    @Override
    public void update(MixedCPMAutoDriverSimulator sim, List<MixedCPMAutoDriverSimulator.MixedCPMAutoDriverSimStepResult> resultToProcess) {
        double carParkWidth = sim.getMap().getManualParkingArea().getDimensions().getWidth();
        double carParkHeight = sim.getMap().getManualParkingArea().getDimensions().getHeight();
        double carParkArea = carParkHeight*carParkWidth;

        double currentEfficiency = sim.getMap().getStatusMonitor().getCurrentManualEfficiency();
        double maxEfficiency = sim.getMap().getStatusMonitor().getMaxManualEfficiency();
        double currentAreaPerVehicle = sim.getMap().getStatusMonitor().getAreaPerManualVehicle();
        double minAreaPerVehicle = sim.getMap().getStatusMonitor().getMinAreaPerManualVehicle();


        updateLabel(carParkHeight, carParkHeightLabel);
        updateLabel(carParkWidth, carParkWidthLabel);
        updateLabel(carParkArea, carParkAreaLabel);

        updateLabel(currentEfficiency, currentEfficiencyLabel);
        updateLabel(maxEfficiency, maxEfficiencyLabel);
        updateLabel(currentAreaPerVehicle, currentAreaPerVehicleLabel);
        updateLabel(minAreaPerVehicle, minAreaPerVehicleLabel);
    }

    private void updateLabel(Double newValue, JLabel label){
        String labelText = label.getText();
        // Split the text so we can remove the old value and reuse the label
        String[] labelSplit = labelText.split(":");
        String labelOnly = labelSplit [0];
        label.setText(labelOnly + ": " + Util.roundToDecimalPlaces(newValue,4));
    }


}
