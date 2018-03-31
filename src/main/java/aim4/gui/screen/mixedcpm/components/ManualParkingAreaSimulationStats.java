package aim4.gui.screen.mixedcpm.components;

import aim4.sim.simulator.mixedcpm.MixedCPMAutoDriverSimulator;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.List;
import java.util.Map;

public class ManualParkingAreaSimulationStats extends JPanel implements MixedCPMStatScreenComponent {

    private JLabel simulationTimeLabel;
    private JLabel currentEfficiencyLabel;
    private JLabel maxEfficiencyLabel;
    private JLabel currentAreaPerVehicleLabel;
    private JLabel minAreaPerVehicleLabel;

    public ManualParkingAreaSimulationStats(){
        JLabel title = new JLabel("Parking Area Simulation Stats");
        Font font = title.getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        title.setFont(font.deriveFont(attributes));

        simulationTimeLabel = new JLabel("Simulation time: ");
        simulationTimeLabel.setOpaque(true);

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
        this.add(simulationTimeLabel);
        this.add(currentEfficiencyLabel);
        this.add(maxEfficiencyLabel);
        this.add(currentAreaPerVehicleLabel);
        this.add(minAreaPerVehicleLabel);
    }

    @Override
    public void update(MixedCPMAutoDriverSimulator sim, List<MixedCPMAutoDriverSimulator.MixedCPMAutoDriverSimStepResult> resultToProcess) {
        double simulationTime = Math.round(sim.getSimulationTime());
        double currentEfficiency = sim.getMap().getStatusMonitor().getCurrentEfficiency();
        double maxEfficiency = sim.getMap().getStatusMonitor().getMaxEfficiency();
        double currentAreaPerVehicle = sim.getMap().getStatusMonitor().getAreaPerVehicle();
        double minAreaPerVehicle = sim.getMap().getStatusMonitor().getMinAreaPerVehicle();


        updateLabel(simulationTime, simulationTimeLabel);
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
        label.setText(labelOnly + ": " + round(newValue,2));
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
