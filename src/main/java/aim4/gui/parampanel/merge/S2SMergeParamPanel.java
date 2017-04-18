package aim4.gui.parampanel.merge;

import aim4.gui.component.LabeledSlider;
import aim4.sim.setup.merge.MergeSimSetup;
import aim4.sim.setup.merge.enums.ProtocolType;
import aim4.sim.setup.merge.S2SSimSetup;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Callum on 02/03/2017.
 */
public class S2SMergeParamPanel extends MergeParamPanel {
    //COMBOBOX OPTIONS//
    final static String AIM_GRID_PROTOCOL_TITLE = "AIM Grid Protocol";
    final static String AIM_NO_GRID_PROTOCOL_TITLE = "AIM Gridless Protocol";
    final static String DECENTRALISED_PROTOCOL_TITLE = "Decnetralised Protocol";
    final static String TEST_MERGE_PROTOCOL_TITLE = "Test Merge Lane Only";
    final static String TEST_TARGET_PROTOCOL_TITLE = "Test Target Lane Only";

    //SIM SETUP//
    private S2SSimSetup simSetup;

    //GUI ELEMENTS
    /**Combo box indicating the protocol to use**/
    private JComboBox protocolComboBox;
    /**The JPanel containing all of the option sliders**/
    private JPanel optionPane;
    /**Dictates the rate of traffic flow**/
    private LabeledSlider trafficRateSlider;
    /**Dictates the speed limit for the target lane**/
    private LabeledSlider targetLaneSpeedSlider;
    /**Dictates the speed limit for the merge lane**/
    private LabeledSlider mergeLaneSpeedSlider;
    /**Dictates the distance between the target lane start and the merge point**/
    private LabeledSlider targetLeadInDistanceSlider;
    /**Dictates the distance between the target lane end and the merge point**/
    private LabeledSlider targetLeadOutDistanceSlider;
    /**Dictates the length of the merging road as it leads into the merge point**/
    private LabeledSlider mergeLeadInDistanceSlider;
    /**Dictates the angle between the target lane and the merging lane**/
    private LabeledSlider mergingAngleSlider;

    public S2SMergeParamPanel() {
        //Setup the protocolComboBox;
        JPanel comboBoxPane = new JPanel();
        comboBoxPane.setBackground(Color.WHITE);

        String comboBoxItems[] =
                {
                        AIM_GRID_PROTOCOL_TITLE,
                        AIM_NO_GRID_PROTOCOL_TITLE,
                        DECENTRALISED_PROTOCOL_TITLE,
                        TEST_MERGE_PROTOCOL_TITLE,
                        TEST_TARGET_PROTOCOL_TITLE
                };
        protocolComboBox = new JComboBox(comboBoxItems);
        protocolComboBox.setEditable(false);
        comboBoxPane.add(protocolComboBox);

        //Create option components
        trafficRateSlider =
                new LabeledSlider(0.0, 2500.0,
                        S2SSimSetup.DEFAULT_TRAFFIC_LEVEL * 3600.0,
                        500.0, 100.0,
                        "Traffic Level: %.0f vehicles/hour/lane",
                        "%.0f");
        targetLaneSpeedSlider =
                new LabeledSlider(0.0, 80.0,
                        S2SSimSetup.DEFAULT_TARGET_LANE_SPEED_LIMIT,
                        10.0, 5.0,
                        "Target Lane Speed Limit: %.0f metres/second",
                        "%.0f");
        mergeLaneSpeedSlider =
                new LabeledSlider(0.0, 80.0,
                        S2SSimSetup.DEFAULT_MERGING_LANE_SPEED_LIMIT,
                        10.0, 5.0,
                        "Merge Lane Speed Limit: %.0f metres/second",
                        "%.0f");
        targetLeadInDistanceSlider =
                new LabeledSlider(50.0, 300.0,
                        S2SSimSetup.DEFAULT_TARGET_LEAD_IN_DISTANCE,
                        50.0, 10.0,
                        "Target Lane lead in distance: %.0f metres",
                        "%.0f");
        targetLeadOutDistanceSlider =
                new LabeledSlider(50.0, 300.0,
                        S2SSimSetup.DEFAULT_TARGET_LEAD_OUT_DISTANCE,
                        50.0, 10.0,
                        "Target Lane lead out distance: %.0f metres",
                        "%.0f");
        mergeLeadInDistanceSlider =
                new LabeledSlider(50.0, 300.0,
                        S2SSimSetup.DEFAULT_MERGE_LEAD_IN_DISTANCE,
                        50.0, 10.0,
                        "Merging Lane lead in distance: %.0f metres",
                        "%.0f");
        mergingAngleSlider =
                new LabeledSlider(0.0, 90.0,
                        S2SSimSetup.DEFAULT_MERGING_ANGLE,
                        10.0, 1.0,
                        "Merging Angle: %.0f degrees",
                        "%.0f");

        //Create option pane
        optionPane = new JPanel();
        optionPane.setLayout(new BoxLayout(optionPane, BoxLayout.PAGE_AXIS));
        optionPane.add(trafficRateSlider);
        optionPane.add(targetLaneSpeedSlider);
        optionPane.add(mergeLaneSpeedSlider);
        optionPane.add(targetLeadInDistanceSlider);
        optionPane.add(targetLeadOutDistanceSlider);
        optionPane.add(mergeLeadInDistanceSlider);
        optionPane.add(mergingAngleSlider);

        //Put them together
        setLayout(new BorderLayout());
        add(comboBoxPane, BorderLayout.PAGE_START);
        add(optionPane, BorderLayout.CENTER);
    }

    @Override
    public MergeSimSetup getSimSetup() {
        return new S2SSimSetup(
                getSelectedProtocol(), trafficRateSlider.getValue(),
                targetLaneSpeedSlider.getValue(),       mergeLaneSpeedSlider.getValue(),
                targetLeadInDistanceSlider.getValue(),  targetLeadOutDistanceSlider.getValue(),
                mergeLeadInDistanceSlider.getValue(),   mergingAngleSlider.getValue()
        );
    }

    public ProtocolType getProtocolType() {
        return getSelectedProtocol();
    }

    private ProtocolType getSelectedProtocol(){
        switch (protocolComboBox.getSelectedIndex()) {
            case 0: return ProtocolType.AIM_GRID;
            case 1: return ProtocolType.AIM_NO_GRID;
            case 2: return ProtocolType.DECENTRALISED;
            case 3: return ProtocolType.TEST_MERGE;
            case 4: return ProtocolType.TEST_TARGET;
            default: throw new RuntimeException("Protocol type combo box went out of range");
        }
    }
}
