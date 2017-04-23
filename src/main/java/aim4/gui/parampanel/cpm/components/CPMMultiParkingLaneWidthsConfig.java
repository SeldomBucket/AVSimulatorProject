package aim4.gui.parampanel.cpm.components;

import aim4.gui.parampanel.cpm.CPMMultiWidthParamPanel;
import aim4.sim.setup.cpm.BasicCPMSimSetup;
import javafx.util.Pair;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * A section where the widths of lanes in the car park are configured.
 */
public class CPMMultiParkingLaneWidthsConfig extends JPanel {
    /**
     * The param panel this slider belongs to.
     */
    private CPMMultiWidthParamPanel paramPanel;
    /**
     * A panel to hold the labels and text fields for each parking lane
     */
    JPanel parkingLanesSetPanel;
    /**
     * A list of the text fields which hold the number of parking lanes in the set
     * and the width of each parking lane in that set
     */
    List<Pair<JTextField, JTextField>> parkingLaneSetFields = new ArrayList<Pair<JTextField, JTextField>>();
    /**
     * A button to add a new set of parking lanes.
     */
    JButton addParkingLaneSetButton;
    /**
     * A button to remove a set of parking lanes.
     */
    JButton removeParkingLaneSetButton;

    public CPMMultiParkingLaneWidthsConfig(BasicCPMSimSetup simSetup,
                                           CPMMultiWidthParamPanel paramPanel) {
        this.paramPanel = paramPanel;

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        JLabel addButtonLabel = new JLabel("Add new set of parking lanes: ");
        addParkingLaneSetButton = new JButton("Add");
        addParkingLaneSetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addParkingLaneSet();
            }
        });
        buttonPanel.add(addButtonLabel);
        buttonPanel.add(addParkingLaneSetButton);

        JLabel removeButtonLabel = new JLabel("Remove set of parking lanes: ");
        removeParkingLaneSetButton = new JButton("Remove");
        removeParkingLaneSetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeParkingLaneSet();
            }
        });
        buttonPanel.add(removeButtonLabel);
        buttonPanel.add(removeParkingLaneSetButton);

        add(buttonPanel);

        parkingLanesSetPanel = new JPanel();
        parkingLanesSetPanel.setLayout(new BoxLayout(parkingLanesSetPanel, BoxLayout.Y_AXIS));
        add(parkingLanesSetPanel);

        addParkingLaneSet();
    }

    public void addParkingLaneSet() {
        JPanel panel = new JPanel();

        JLabel label = new JLabel("Number of parking lanes: ");
        panel.add(label);

        JTextField numberOfLanesField = new JTextField();
        numberOfLanesField.setColumns(3);
        panel.add(numberOfLanesField);

        label = new JLabel("Lane width: ");
        panel.add(label);

        JTextField widthField = new JTextField();
        widthField.setColumns(3);
        panel.add(widthField);

        parkingLanesSetPanel.add(panel);
        parkingLanesSetPanel.updateUI();

        parkingLaneSetFields.add(new Pair<JTextField, JTextField>(numberOfLanesField, widthField));
    }

    public void removeParkingLaneSet() {
        if (parkingLaneSetFields.size() == 1) {
            throw new RuntimeException("There must be at least one set of lanes in the car park.");
        }

        // TODO CPM Would be better to remove the last one added
        parkingLanesSetPanel.remove(0);
        parkingLanesSetPanel.updateUI();
        parkingLaneSetFields.remove(0);
    }

    public CPMMultiWidthParamPanel getParamPanel() {
        return paramPanel;
    }
}
