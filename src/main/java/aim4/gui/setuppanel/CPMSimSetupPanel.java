package aim4.gui.setuppanel;

import aim4.gui.parampanel.cpm.CPMAutoDriverParamPanel;
import aim4.sim.setup.SimSetup;
import aim4.sim.setup.cpm.BasicCPMSimSetup;
import aim4.sim.setup.cpm.CPMAutoDriverSimSetup;

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
    private CPMAutoDriverParamPanel autoDriverOnlySetupPanel;
    /** The simulation setup panel */
    private BasicCPMSimSetup simSetup;

    /**
     * Create a simulation setup panel
     *
     * @param initSimSetup  the initial simulation setup
     */
    public CPMSimSetupPanel(BasicCPMSimSetup initSimSetup) {
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
        autoDriverOnlySetupPanel =
                new CPMAutoDriverParamPanel(simSetup);
        addParamPanel(autoDriverOnlySetupPanel, CPM_SINGLE_WIDTH_SETUP_PANEL);



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
            CPMAutoDriverSimSetup newSimSetup = new CPMAutoDriverSimSetup(simSetup);
            newSimSetup.setTrafficLevel(autoDriverOnlySetupPanel.getTrafficRate());
            newSimSetup.setLaneWidth(autoDriverOnlySetupPanel.getLaneWidth());
            newSimSetup.setNumberOfParkingLanes(autoDriverOnlySetupPanel.getNumberOfParkingLanes());
            newSimSetup.setParkingLength(autoDriverOnlySetupPanel.getParkingLength());
            newSimSetup.setAccessLength(autoDriverOnlySetupPanel.getAccessLength());
            newSimSetup.setSpawnSpecType(autoDriverOnlySetupPanel.getSpawnSpecType());
            newSimSetup.setSingleSpawnSpecName(autoDriverOnlySetupPanel.getSingleSpawnSpecName());
            newSimSetup.setMixedSpawnDistribution(autoDriverOnlySetupPanel.getMixedSpawnDistribution());
            newSimSetup.setUseCSVFile(autoDriverOnlySetupPanel.getUseCSVFileDetails());
            newSimSetup.setUseSpecificSimTime(autoDriverOnlySetupPanel.getUseSpecificSimTimeDetails());
            return newSimSetup;
        } else {
            throw new RuntimeException(
                    "SimSetupPane::getSimSetup(): not implemented yet");
        }

    }

    @Override
    public void itemStateChanged(ItemEvent e) {

    }
}
