package aim4.gui.parampanel.aim;

import aim4.gui.component.LabeledSlider;
import aim4.sim.setup.aim.AIMSimSetup;
import aim4.sim.setup.aim.MergeMimicSimSetup;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by Callum on 21/04/2017.
 */
public class MergeModeParamPanel extends JPanel implements ActionListener {
    //CONSTS
    private static final double DEFAULT_TARGET_LANE_SPEED_LIMIT = 40.0;

    //GUI ELEMENTS
    private LabeledSlider speedLimitSlider;
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
        TARGET_SELECT
    }

    public MergeModeParamPanel() {
        //Sliders
        speedLimitSlider =
                new LabeledSlider(0.0, 80.0,
                        DEFAULT_TARGET_LANE_SPEED_LIMIT,
                        10.0, 5.0,
                        "Lanes Speed Limit: %.0f metres/second",
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

        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(mergeSchedulePane);
        this.add(targetSchedulePane);
    }


    public AIMSimSetup getSimSetup() {
        if(mergeSchedule != null && targetSchedule != null) {
            if (!mergeSchedule.getAbsolutePath().equals(mergeSchedulePathTextbox.getText()))
                mergeSchedule = new File(mergeSchedulePathTextbox.getText());
            if (!targetSchedule.getAbsolutePath().equals(targetSchedulePathTextbox.getText()))
                targetSchedule = new File(targetSchedulePathTextbox.getText());
        } else if(!mergeSchedulePathTextbox.getText().equals("") && !targetSchedulePathTextbox.getText().equals("")) {
            mergeSchedule = new File(mergeSchedulePathTextbox.getText());
            targetSchedule = new File(targetSchedulePathTextbox.getText());
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Both merge and target schedules must be set before proceeding",
                    "Scheduling error!",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        return new MergeMimicSimSetup(mergeSchedule, targetSchedule, speedLimitSlider.getValue());
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
        }
    }

    private File getFileFromUser() {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON Files","json");
        final JFileChooser fc = new JFileChooser();
        fc.setFileFilter(filter);
        int returnVal = fc.showOpenDialog(this);

        if(returnVal == JFileChooser.APPROVE_OPTION)
            return fc.getSelectedFile();
        else
            return null;
    }

}
