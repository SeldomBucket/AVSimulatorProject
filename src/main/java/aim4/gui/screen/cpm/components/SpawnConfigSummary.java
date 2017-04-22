package aim4.gui.screen.cpm.components;

import aim4.gui.setuppanel.CPMSimSetupPanel;
import aim4.map.cpm.CPMCarParkWithStatus;
import aim4.map.cpm.CPMMapUtil;
import aim4.sim.setup.cpm.BasicCPMSimSetup;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import aim4.util.Util;
import aim4.vehicle.VehicleSpecDatabase;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.*;
import java.util.List;

/**
 * A section displaying the configuration and information about the specification of spawned vehicles
 */
public class SpawnConfigSummary extends JPanel implements CPMStatScreenComponent{

    private BasicCPMSimSetup setup;
    private JLabel spawnSpecTypeLabel;
    private JLabel singleSpecNameLabel;
    private JLabel spawnDistributionLabel;
    private List<JLabel> allSpecNamesLabels = new ArrayList<JLabel>(VehicleSpecDatabase.getNumOfSpec());

    public SpawnConfigSummary(CPMSimSetupPanel setupPanel) {

        setup = (BasicCPMSimSetup)setupPanel.getSimSetup();

        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        JLabel title = new JLabel("Spawn Spec Info");
        Font font = title.getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        title.setFont(font.deriveFont(attributes));
        this.add(title);


        spawnSpecTypeLabel = new JLabel("Spawn spec type: " + setup.getSpawnSpecType());
        spawnSpecTypeLabel.setOpaque(true);
        this.add(spawnSpecTypeLabel);


        if(setup.getSpawnSpecType() == CPMMapUtil.SpawnSpecType.SINGLE){
            singleSpecNameLabel = new JLabel("Only spawning: " + setup.getSingleSpawnSpecName());
            singleSpecNameLabel.setOpaque(true);
            this.add(singleSpecNameLabel);
        } else {
            if (setup.getSpawnSpecType() == CPMMapUtil.SpawnSpecType.MIXED) {
                spawnDistributionLabel = new JLabel("Spec distribution: " + setup.getMixedSpawnDistribution().toString());
                spawnDistributionLabel.setOpaque(true);
                this.add(spawnDistributionLabel);
            }
            for (String name : VehicleSpecDatabase.getSpecNames()){
                JLabel label = new JLabel(name + ": 0");
                this.add(label);
                allSpecNamesLabels.add(label);
            }
        }
    }

    @Override
    public void update(CPMAutoDriverSimulator sim, java.util.List<CPMAutoDriverSimulator.CPMAutoDriverSimStepResult> resultToProcess) {
        int numberOfDeniedEntries = sim.getMap().getStatusMonitor().getNumberOfDeniedEntries();
        int numberOfCarsNotCateredFor = sim.getNumberOfVehiclesNotCateredFor();
        int maxVehiclesInCarPark = sim.getMap().getStatusMonitor().getMostNumberOfVehicles();
        int numOfVehiclesInCarPark = sim.getMap().getStatusMonitor().getVehicles().size();
        int carParkArea = (int)Math.ceil(((CPMCarParkWithStatus)sim.getMap()).getTotalCarParkArea());
        /*Util.updateLabel(numberOfDeniedEntries, numberOfCarsDeniedEntryLabel);
        Util.updateLabel(numberOfCarsNotCateredFor, numberOfCarsNotCateredForLabel);
        Util.updateLabel(maxVehiclesInCarPark, maxVehiclesInCarParkLabel);
        Util.updateLabel(numOfVehiclesInCarPark, numOfVehiclesInCarParkLabel);
        Util.updateLabel(carParkArea, carParkAreaLabel);*/
    }

    @Override
    public java.util.List<String> getAllLabelsText(){
        java.util.List<String> labelsText = new ArrayList<String>();
        /*labelsText.add(numberOfCarsDeniedEntryLabel.getText());
        labelsText.add(numberOfCarsNotCateredForLabel.getText());
        labelsText.add(maxVehiclesInCarParkLabel.getText());
        labelsText.add(numOfVehiclesInCarParkLabel.getText());
        labelsText.add(carParkAreaLabel.getText());*/
        return labelsText;
    }
}
