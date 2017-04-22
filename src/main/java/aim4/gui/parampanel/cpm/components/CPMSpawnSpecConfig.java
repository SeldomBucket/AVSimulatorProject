package aim4.gui.parampanel.cpm.components;

import aim4.util.Util;
import aim4.vehicle.VehicleSpecDatabase;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * The components needed to configure the type of spawn spec.
 */
public class CPMSpawnSpecConfig extends JPanel {

    /** The group for the radio buttons. */
    ButtonGroup group;
    /** The list of vehicle specs that can be spawned on single spawn spec */
    JComboBox specList;
    /** The label which indicates the length and width of the selected vehicle spec for single spawn */
    JLabel specSizeLabel;
    List<JTextField> specDistributionFields = new ArrayList<JTextField>();

    public CPMSpawnSpecConfig(){
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel singleSpecPanel = new JPanel();
        JPanel mixedSpecPanel = new JPanel();
        JPanel randomSpecPanel = new JPanel();
        // mixedSpecPanel.setLayout(new BoxLayout(mixedSpecPanel, BoxLayout.PAGE_AXIS));

        List<String> specNames = VehicleSpecDatabase.getSpecNames();

        // SINGLE SPEC
        JRadioButton singleSpecButton = new JRadioButton("Single:", false);
        singleSpecButton.setActionCommand("SINGLE");
        singleSpecButton.setSelected(true);
        singleSpecPanel.add(singleSpecButton);

        specList = new JComboBox(specNames.toArray());
        specList.setSelectedIndex(0);
        specList.addActionListener (new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateVehicleSpecSizeLabel();
            }
        });
        singleSpecPanel.add(specList);

        specSizeLabel = new JLabel("size: " + getSelectedVehicleSpecSizeString());
        singleSpecPanel.add(specSizeLabel);

        // MIXED SPEC
        JRadioButton mixedSpecButton = new JRadioButton("Mixed:", false);
        mixedSpecButton.setActionCommand("MIXED");
        mixedSpecPanel.add(mixedSpecButton);

        for (String specName : specNames){
            // Create the label
            JLabel label = new JLabel(specName);
            mixedSpecPanel.add(label);

            // Create the text field
            JTextField distributionField = new JTextField();
            distributionField.setColumns(3);
            mixedSpecPanel.add(distributionField);

            // Add to list of fields
            specDistributionFields.add(distributionField);
        }

        // RANDOM SPEC
        JRadioButton randomSpecButton = new JRadioButton("Random", false);
        randomSpecButton.setActionCommand("RANDOM");
        randomSpecButton.setSelected(true);
        randomSpecPanel.add(randomSpecButton);


        group = new ButtonGroup();
        group.add(singleSpecButton);
        group.add(mixedSpecButton);
        group.add(randomSpecButton);

        this.add(singleSpecPanel);
        this.add(mixedSpecPanel);
        this.add(randomSpecPanel);

        this.setVisible(true);
    }

    public ButtonModel getSelected(){
        return group.getSelection();
    }

    public String getSelectedSingleSpec(){ return specList.getSelectedItem().toString(); }

    public List<Double> getVehicleSpecDistribution(){
        List<Double> distribution = new ArrayList<Double>(specList.getItemCount());
        for (JTextField field : specDistributionFields) {
            try {
                distribution.add(Double.parseDouble(field.getText()));
            } catch (NumberFormatException e) {
                throw new RuntimeException("All vehicles must be given a proportion of the distribution.");
                // handle the error
            }
        }

        double sum = 0;
        for (Double proportion : distribution) {
            sum += proportion;
        }
        if(sum != 1) {
            throw new RuntimeException("The distribution total must equal 1.");
        }

        return distribution;
    }

    private String getSelectedVehicleSpecSizeString() {
        double specLength = VehicleSpecDatabase.getVehicleSpecByName(specList.getSelectedItem().toString()).getLength();
        double specWidth = VehicleSpecDatabase.getVehicleSpecByName(specList.getSelectedItem().toString()).getWidth();
        return specLength + "m(L) x" + specWidth + "m(W)";
    }

    private void updateVehicleSpecSizeLabel(){
        String value = getSelectedVehicleSpecSizeString();
        Util.updateLabel(value, specSizeLabel);

    }
}
