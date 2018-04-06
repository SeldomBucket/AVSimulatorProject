package aim4.gui.setuppanel;

import aim4.gui.parampanel.mixedcpm.MixedCPMManualDriverParamPanel;
import aim4.sim.setup.SimSetup;
import aim4.sim.setup.mixedcpm.BasicMixedCPMSimSetup;
import aim4.sim.setup.mixedcpm.MixedCPMAutoDriverSimSetup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * The SetupPanel for CPM.
 */
public class MixedCPMSimSetupPanel extends SimSetupPanel{
    private static final long serialVersionUID = 1L;

    final static String MIXED_CPM_PROTOCOL_MANUAL_AREA_ONLY = "Mixed CPM Protocol (Manual area only)";

    /** The card panel */
    private JPanel cards; //a panel that uses CardLayout
    /** The card layout */
    private CardLayout cardLayout;
    /** the auto driver only simulation setup panel */
    private MixedCPMManualDriverParamPanel autoDriverOnlySetupPanel;
    /** The simulation setup panel */
    private BasicMixedCPMSimSetup simSetup;

    /**
     * Create a simulation setup panel
     *
     * @param initSimSetup  the initial simulation setup
     */
    public MixedCPMSimSetupPanel(BasicMixedCPMSimSetup initSimSetup) {
        this.simSetup = initSimSetup;


        // create the cards pane
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        // add the parameter panels
        autoDriverOnlySetupPanel =
                new MixedCPMManualDriverParamPanel(simSetup);
        addParamPanel(autoDriverOnlySetupPanel, MIXED_CPM_PROTOCOL_MANUAL_AREA_ONLY);

        // add the combo box pane and cards pane
        setLayout(new BorderLayout());
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
        MixedCPMAutoDriverSimSetup newSimSetup = new MixedCPMAutoDriverSimSetup(simSetup);
        newSimSetup.setTrafficLevel(autoDriverOnlySetupPanel.getTrafficRate());
        newSimSetup.setLaneWidth(autoDriverOnlySetupPanel.getLaneWidth());
        newSimSetup.setCarParkHeight(autoDriverOnlySetupPanel.getCarParkHeight());
        newSimSetup.setCarParkWidth(autoDriverOnlySetupPanel.getCarParkWidth());
        newSimSetup.setSpawnSpecType(autoDriverOnlySetupPanel.getSpawnSpecType());
        newSimSetup.setMapType(autoDriverOnlySetupPanel.getMapType());
        return newSimSetup;

    }
}
