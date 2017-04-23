package aim4.gui.setuppanel;

import aim4.gui.parampanel.cpm.CPMMultiWidthParamPanel;
import aim4.gui.parampanel.cpm.CPMSingleWidthParamPanel;
import aim4.sim.setup.SimSetup;
import aim4.sim.setup.cpm.BasicCPMSimSetup;
import aim4.sim.setup.cpm.CPMMultiWidthSimSetup;
import aim4.sim.setup.cpm.CPMSingleWidthSimSetup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * The SetupPanel for CPM.
 */
public class CPMSimSetupPanel extends SimSetupPanel implements ItemListener {
    private static final long serialVersionUID = 1L;

    final static String CPM_SINGLE_WIDTH_SETUP_PANEL = "CPM - Single Parking Lane Width";
    final static String CPM_MULTI_WIDTH_SETUP_PANEL = "CPM - Multi Parking Lane Width";

    /** The combox box */
    private JComboBox comboBox;
    /** The card panel */
    private JPanel cards; //a panel that uses CardLayout
    /** The card layout */
    private CardLayout cardLayout;
    /** the auto driver only simulation setup panel */
    private CPMSingleWidthParamPanel singleWidthParamPanel;
    /** the auto driver only simulation setup panel */
    private CPMMultiWidthParamPanel multiWidthParamPanel;
    /** The simulation setup panel */
    private CPMSingleWidthSimSetup simSetup;

    /**
     * Create a simulation setup panel
     *
     * @param initSimSetup  the initial simulation setup
     */
    public CPMSimSetupPanel(CPMSingleWidthSimSetup initSimSetup) {
        this.simSetup = initSimSetup;

        // create the combo box pane
        JPanel comboBoxPane = new JPanel(); //use FlowLayout
        comboBoxPane.setBackground(Color.WHITE);

        String comboBoxItems[] =
                { CPM_SINGLE_WIDTH_SETUP_PANEL,
                CPM_MULTI_WIDTH_SETUP_PANEL};
        comboBox = new JComboBox(comboBoxItems);
        comboBox.setEditable(false);
        comboBox.addItemListener(this);
        comboBoxPane.add(comboBox);

        // create the cards pane
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        // add the parameter panels
        singleWidthParamPanel =
                new CPMSingleWidthParamPanel(simSetup);
        addParamPanel(singleWidthParamPanel, CPM_SINGLE_WIDTH_SETUP_PANEL);

        multiWidthParamPanel =
                new CPMMultiWidthParamPanel(simSetup);
        cards.add(multiWidthParamPanel, CPM_MULTI_WIDTH_SETUP_PANEL);
        // TODO CPM Problem: not all cars have a scroll pane.

        // add the combo box pane and cards pane
        setLayout(new BorderLayout());
        add(comboBoxPane, BorderLayout.PAGE_START);
        add(cards, BorderLayout.CENTER);
    }

    /**
     * Add a parameter panel.
     *
     * @param paramPanel  the parameter panel
     * @param paramLabel  the label of the parameter panel
     */
    private void addParamPanel(JPanel paramPanel, String paramLabel) {
        JScrollPane scrollPane = new JScrollPane(paramPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        cards.add(scrollPane, paramLabel);
    }

    @Override
    public SimSetup getSimSetup() {
        if (comboBox.getSelectedIndex() == 0) {
            CPMSingleWidthSimSetup newSimSetup = new CPMSingleWidthSimSetup(simSetup);
            newSimSetup.setTrafficLevel(singleWidthParamPanel.getTrafficRate());
            newSimSetup.setLaneWidth(singleWidthParamPanel.getLaneWidth());
            newSimSetup.setNumberOfParkingLanes(singleWidthParamPanel.getNumberOfParkingLanes());
            newSimSetup.setParkingLength(singleWidthParamPanel.getParkingLength());
            newSimSetup.setAccessLength(singleWidthParamPanel.getAccessLength());
            newSimSetup.setSpawnSpecType(singleWidthParamPanel.getSpawnSpecType());
            newSimSetup.setSingleSpawnSpecName(singleWidthParamPanel.getSingleSpawnSpecName());
            newSimSetup.setMixedSpawnDistribution(singleWidthParamPanel.getMixedSpawnDistribution());
            newSimSetup.setUseCSVFile(singleWidthParamPanel.getUseCSVFileDetails());
            newSimSetup.setUseSpecificSimTime(singleWidthParamPanel.getUseSpecificSimTimeDetails());
            return newSimSetup;
        } else if (comboBox.getSelectedIndex() == 1) {
            return new CPMMultiWidthSimSetup(
                    10.0, // speedLimit // TODO CPM Where should this come from? Should share this.
                    multiWidthParamPanel.getTrafficRate(),
                    multiWidthParamPanel.getParkingLength(),
                    multiWidthParamPanel.getAccessLength(),
                    multiWidthParamPanel.getSpawnSpecType(),
                    multiWidthParamPanel.getUseCSVFileDetails(),
                    multiWidthParamPanel.getUseSpecificSimTimeDetails(),
                    multiWidthParamPanel.getSingleSpawnSpecName(),
                    multiWidthParamPanel.getMixedSpawnDistribution(),
                    multiWidthParamPanel.getParkingLaneSets()
            );
        } else {
            throw new RuntimeException(
                    "SimSetupPane::getSimSetup(): not implemented yet");
        }

    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        cardLayout.show(cards, (String)e.getItem());
    }
}
