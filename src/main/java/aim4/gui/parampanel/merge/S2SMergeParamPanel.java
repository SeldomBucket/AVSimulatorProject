package aim4.gui.parampanel.merge;

import aim4.gui.component.LabeledSlider;
import aim4.map.merge.MergeMapUtil;
import aim4.sim.setup.merge.MergeSimSetup;
import aim4.sim.setup.merge.S2SSimSetup;
import aim4.sim.setup.merge.enums.ProtocolType;
import org.json.simple.JSONArray;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Callum on 02/03/2017.
 */
public class S2SMergeParamPanel extends MergeParamPanel implements ActionListener {
    // COMBOBOX OPTIONS //
    final static String AIM_GRID_PROTOCOL_TITLE = "AIM Grid Protocol";
    final static String AIM_NO_GRID_PROTOCOL_TITLE = "AIM Gridless Protocol";
    final static String QUEUE_PROTOCOL_TITLE = "Queue Protocol";
    final static String TEST_MERGE_PROTOCOL_TITLE = "Test Merge Lane Only";
    final static String TEST_TARGET_PROTOCOL_TITLE = "Test Target Lane Only";

    // SIM SETUP //
    private S2SSimSetup simSetup;

    // GUI ELEMENTS //
    /**Tabbed pane differentiating between the setup and the schedule generation panels */
    private JTabbedPane tabbedPane;
    // SIM SETTINGS
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
    // SCHEDULE GEN
    /**Dictates the rate of traffic flow for the schedule*/
    private LabeledSlider scheduleTrafficRateSlider;
    /**Dictates the length of time the schedule will spawn for*/
    private LabeledSlider scheduleTimeLimitSlider;

    private JTextArea mergeSchedulePathTextbox;
    private JTextArea targetSchedulePathTextbox;

    //JSON FILES
    private File mergeSchedule;
    private File targetSchedule;

    //ENUM FOR BUTTON ACTIONS//
    private enum ButtonActionCommands {
        MERGE_CLEAR,
        MERGE_SELECT,
        TARGET_CLEAR,
        TARGET_SELECT,
        CREATE_SCHEDULE
    }

    public S2SMergeParamPanel() {
        JPanel settingsPanel = createSettingsPanel();
        JPanel scheduleGenPanel = createScheduleGenPanel();

        tabbedPane = new JTabbedPane();
        tabbedPane.add("Settings",settingsPanel);
        tabbedPane.add("Schedule Generation",scheduleGenPanel);
        add(tabbedPane);
    }

    private JPanel createSettingsPanel() {
        //Setup the protocolComboBox;
        JPanel comboBoxPane = new JPanel();
        comboBoxPane.setBackground(Color.WHITE);

        String comboBoxItems[] =
                {
                        AIM_GRID_PROTOCOL_TITLE,
                        AIM_NO_GRID_PROTOCOL_TITLE,
                        QUEUE_PROTOCOL_TITLE,
                        TEST_MERGE_PROTOCOL_TITLE,
                        TEST_TARGET_PROTOCOL_TITLE
                };
        protocolComboBox = new JComboBox(comboBoxItems);
        protocolComboBox.setEditable(false);
        comboBoxPane.add(protocolComboBox);

        //Create option components
        //Sliders
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


        //Schedule selectors
        JLabel scheduleLabel = new JLabel("If using schedules, both schedules must be set");

        JLabel mergeScheduleLabel = new JLabel("Merge Schedule:");
        mergeSchedulePathTextbox = new JTextArea(1,20);
        JButton mergeScheduleSelectButton = new JButton("Browse...");
        JButton mergeScheduleClearButton = new JButton("Clear");

        JLabel targetScheduleLabel = new JLabel("Target Schedule:");
        targetSchedulePathTextbox = new JTextArea(1,20);
        JButton targetScheduleSelectButton = new JButton("Browse...");
        JButton targetScheduleClearButton = new JButton("Clear");

        //Set up buttons
        mergeScheduleSelectButton.addActionListener(this);
        mergeScheduleSelectButton.setActionCommand(ButtonActionCommands.MERGE_SELECT.toString());
        mergeScheduleClearButton.addActionListener(this);
        mergeScheduleClearButton.setActionCommand(ButtonActionCommands.MERGE_CLEAR.toString());
        targetScheduleSelectButton.addActionListener(this);
        targetScheduleSelectButton.setActionCommand(ButtonActionCommands.TARGET_SELECT.toString());
        targetScheduleClearButton.addActionListener(this);
        targetScheduleClearButton.setActionCommand(ButtonActionCommands.TARGET_CLEAR.toString());

        //Create schedule pane
        JPanel mergeSchedulePane = new JPanel();
        JPanel targetSchedulePane = new JPanel();
        mergeSchedulePane.setLayout(new FlowLayout());
        targetSchedulePane.setLayout(new FlowLayout());

        mergeSchedulePane.add(mergeScheduleLabel);
        mergeSchedulePane.add(mergeSchedulePathTextbox);
        mergeSchedulePane.add(mergeScheduleSelectButton);
        mergeSchedulePane.add(mergeScheduleClearButton);

        targetSchedulePane.add(targetScheduleLabel);
        targetSchedulePane.add(targetSchedulePathTextbox);
        targetSchedulePane.add(targetScheduleSelectButton);
        targetSchedulePane.add(targetScheduleClearButton);

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
        optionPane.add(mergeSchedulePane);
        optionPane.add(targetSchedulePane);
        optionPane.add(scheduleLabel);

        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BorderLayout());
        settingsPanel.add(comboBoxPane, BorderLayout.PAGE_START);
        settingsPanel.add(optionPane, BorderLayout.CENTER);

        return settingsPanel;
    }

    private JPanel createScheduleGenPanel() {
        //Create slider
        scheduleTrafficRateSlider =
                new LabeledSlider(0.0, 2500.0,
                        S2SSimSetup.DEFAULT_TRAFFIC_LEVEL * 3600.0,
                        500.0, 100.0,
                        "Traffic Level: %.0f vehicles/hour/lane",
                        "%.0f");
        scheduleTimeLimitSlider =
                new LabeledSlider(0.0, 5000.0,
                        1000.0,
                        500.0, 100.0,
                        "Schedule time limit: %.0fs",
                        "%.0fs");
        //Create button
        JButton createScheduleButton = new JButton("Create schedule");
        createScheduleButton.addActionListener(this);
        createScheduleButton.setActionCommand(ButtonActionCommands.CREATE_SCHEDULE.toString());

        //Create Panel
        JPanel scheduleGenPanel = new JPanel();
        scheduleGenPanel.setLayout(new BoxLayout(scheduleGenPanel, BoxLayout.PAGE_AXIS));
        scheduleGenPanel.add(trafficRateSlider);
        scheduleGenPanel.add(scheduleTimeLimitSlider);
        scheduleGenPanel.add(createScheduleButton);

        return scheduleGenPanel;
    }

    @Override
    public MergeSimSetup getSimSetup() {
        if(mergeSchedule != null && targetSchedule != null) {
            if (!mergeSchedule.getAbsolutePath().equals(mergeSchedulePathTextbox.getText()))
                mergeSchedule = new File(mergeSchedulePathTextbox.getText());
            if (!targetSchedule.getAbsolutePath().equals(targetSchedulePathTextbox.getText()))
                targetSchedule = new File(targetSchedulePathTextbox.getText());
        } else if(!mergeSchedulePathTextbox.getText().equals("") && !targetSchedulePathTextbox.getText().equals("")) {
            mergeSchedule = new File(mergeSchedulePathTextbox.getText());
            targetSchedule = new File(targetSchedulePathTextbox.getText());
        } else if(!mergeSchedulePathTextbox.getText().equals("") ^ !targetSchedulePathTextbox.getText().equals("")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Both merge and target schedules must be set before proceeding. Setting both schedules to null",
                    "Scheduling error!",
                    JOptionPane.WARNING_MESSAGE
            );
            mergeSchedule = null;
            targetSchedule = null;
        }

        double trafficLevel = trafficRateSlider.getValue()/3600;
        return new S2SSimSetup(
                getSelectedProtocol(), trafficLevel,
                targetLaneSpeedSlider.getValue(),       mergeLaneSpeedSlider.getValue(),
                targetLeadInDistanceSlider.getValue(),  targetLeadOutDistanceSlider.getValue(),
                mergeLeadInDistanceSlider.getValue(),   mergingAngleSlider.getValue(),
                targetSchedule, mergeSchedule
        );
    }

    public ProtocolType getProtocolType() {
        return getSelectedProtocol();
    }

    private ProtocolType getSelectedProtocol(){
        switch (protocolComboBox.getSelectedIndex()) {
            case 0: return ProtocolType.AIM_GRID;
            case 1: return ProtocolType.AIM_NO_GRID;
            case 2: return ProtocolType.QUEUE;
            case 3: return ProtocolType.TEST_MERGE;
            case 4: return ProtocolType.TEST_TARGET;
            default: throw new RuntimeException("Protocol type combo box went out of range");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch(ButtonActionCommands.valueOf(e.getActionCommand())) {
            case MERGE_CLEAR:
                mergeSchedule = null;
                mergeSchedulePathTextbox.setText("");
                break;
            case MERGE_SELECT:
                mergeSchedule = getFileFromUser();
                if(mergeSchedule != null)
                    mergeSchedulePathTextbox.setText(mergeSchedule.getAbsolutePath());
                break;
            case TARGET_CLEAR:
                targetSchedule = null;
                targetSchedulePathTextbox.setText("");
                break;
            case TARGET_SELECT:
                targetSchedule = getFileFromUser();
                if(targetSchedule != null)
                    targetSchedulePathTextbox.setText(targetSchedule.getAbsolutePath());
                break;
            case CREATE_SCHEDULE:
                double trafficLevel = scheduleTrafficRateSlider.getValue() / 3600;
                double timeLimit = scheduleTimeLimitSlider.getValue();
                JSONArray schedule = MergeMapUtil.createSpawnSchedule(trafficLevel, timeLimit);
                try {
                    saveJSON(schedule);
                } catch (IOException ex) {
                    String stackTraceMessage = "";
                    for(StackTraceElement line : ex.getStackTrace())
                        stackTraceMessage += line.toString() + "\n";
                    String errorMessage = String.format(
                            "Error Occured whilst saving: %s\nStack Trace:\n%s",
                            ex.getLocalizedMessage(),
                            stackTraceMessage);
                    JOptionPane.showMessageDialog(this,errorMessage,"Saving error",JOptionPane.ERROR_MESSAGE);
                    throw new RuntimeException(ex);
                }
                break;
        }
    }

    private File getFileFromUser() {
        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(this);

        if(returnVal == JFileChooser.APPROVE_OPTION)
            return fc.getSelectedFile();
        else
            return null;
    }

    private void saveJSON(JSONArray json) throws IOException {
        String jsonString = json.toJSONString();
        List<String> jsonList = new ArrayList<String>();
        jsonList.add(jsonString);
        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showSaveDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            Files.write(Paths.get(file.getAbsolutePath()), jsonList, Charset.forName("UTF-8"));
        }
    }
}
