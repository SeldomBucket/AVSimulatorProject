package aim4.gui.screen.cpm.components;

import aim4.gui.setuppanel.CPMSimSetupPanel;
import aim4.map.cpm.CPMCarParkSingleLaneWidth;
import aim4.map.cpm.CPMMapUtil;
import aim4.sim.setup.cpm.BasicCPMSimSetup;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import aim4.util.Util;
import aim4.vehicle.VehicleSpec;
import aim4.vehicle.VehicleSpecDatabase;
import javafx.util.Pair;

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
    private boolean isSingleSpec;
    private boolean isMixedSpec;
    private boolean isRandomSpec;

    public SpawnConfigSummary(CPMSimSetupPanel setupPanel) {

        setup = (BasicCPMSimSetup)setupPanel.getSimSetup();

        isSingleSpec = setup.getSpawnSpecType() == CPMMapUtil.SpawnSpecType.SINGLE;
        isMixedSpec = setup.getSpawnSpecType() == CPMMapUtil.SpawnSpecType.MIXED;
        isRandomSpec = setup.getSpawnSpecType() == CPMMapUtil.SpawnSpecType.RANDOM;

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


        if(isSingleSpec){
            singleSpecNameLabel = new JLabel("Only spawning: " + setup.getSingleSpawnSpecName());
            singleSpecNameLabel.setOpaque(true);
            this.add(singleSpecNameLabel);
        } else {
            if (isMixedSpec) {
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
    public void update(CPMAutoDriverSimulator sim,
                       List<CPMAutoDriverSimulator.CPMAutoDriverSimStepResult> resultToProcess) {
        updateSpecLabels(sim);
    }

    private void updateSpecLabels(CPMAutoDriverSimulator sim){
        for (JLabel label : allSpecNamesLabels) {

            String labelText = label.getText();
            // Split the text so we can get the number for the correct spec
            String[] labelSplit = labelText.split(":");
            String specName = labelSplit [0];

            int newValue = sim.getVehicleSpecToNumCompleted().get(specName);
            Util.updateLabel(newValue, label);
        }
    }

    @Override
    public java.util.List<String> getAllLabelsText(){
        java.util.List<String> labelsText = new ArrayList<String>();
        labelsText.add(spawnSpecTypeLabel.getText());
        if (isSingleSpec) {
            labelsText.add(singleSpecNameLabel.getText());
        }
        if (isMixedSpec) {
            labelsText.add(spawnDistributionLabel.getText());
        }
        for (JLabel label : allSpecNamesLabels){
            labelsText.add(label.getText());
        }
        return labelsText;
    }
}
