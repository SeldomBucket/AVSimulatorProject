package aim4.gui.setuppanel;

import aim4.gui.parampanel.merge.*;
import aim4.sim.setup.SimSetup;
import aim4.sim.setup.merge.BasicMergeSimSetup;
import aim4.sim.setup.merge.MergeSimSetup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created by Callum on 08/02/2017.
 */
public class MergeSimSetupPanel extends SimSetupPanel implements ItemListener {
    //COMBOBOX OPTIONS//
    /**
     * Merge types for Merge Combobox
     */
    final static String S2S_MERGE_TITLE = "Single-to-Single Merge";
    final static String S2D_MERGE_TITLE = "Single-to-Double Merge";
    final static String D2D_MERGE_TITLE = "Double-to-Double Merge";
    final static String OBSTRUCTION_MERGE_TITLE = "Obstruction Merge";

    //GUI ELEMENTS//
    private JComboBox mergeComboBox;
    private JPanel cards;
    private CardLayout cardLayout;

    private S2SMergeParamPanel s2sPanel;
    private S2DMergeParamPanel s2dPanel;
    private D2DMergeParamPanel d2dPanel;
    private ObstructionMergeParamPanel obstructionPanel;

    public MergeSimSetupPanel(){
        //Setup the mergeComboBox
        JPanel comboBoxPane = new JPanel();
        comboBoxPane.setBackground(Color.WHITE);

        String comboBoxItems[] =
                {
                        S2S_MERGE_TITLE,
                        S2D_MERGE_TITLE,
                        D2D_MERGE_TITLE,
                        OBSTRUCTION_MERGE_TITLE
                };
        mergeComboBox = new JComboBox(comboBoxItems);
        mergeComboBox.setEditable(false);
        mergeComboBox.addItemListener(this);
        comboBoxPane.add(mergeComboBox);

        //Create the cards pane
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        //Create the parameter panels
        s2sPanel = new S2SMergeParamPanel();
        s2dPanel = new S2DMergeParamPanel();
        d2dPanel = new D2DMergeParamPanel();
        obstructionPanel = new ObstructionMergeParamPanel();
        addParamPanel(s2sPanel, S2S_MERGE_TITLE);
        addParamPanel(s2dPanel, S2D_MERGE_TITLE);
        addParamPanel(d2dPanel, D2D_MERGE_TITLE);
        addParamPanel(obstructionPanel, OBSTRUCTION_MERGE_TITLE);

        //Put them together
        setLayout(new BorderLayout());
        add(comboBoxPane, BorderLayout.PAGE_START);
        add(cards, BorderLayout.CENTER);
    }

    private void addParamPanel(MergeParamPanel paramPanel, String paramLabel) {
        JScrollPane scrollPane = new JScrollPane(paramPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        cards.add(scrollPane, paramLabel);
    }

    @Override
    public SimSetup getSimSetup() {
        switch(mergeComboBox.getSelectedIndex()){
            case 0: return s2sPanel.getSimSetup();
            case 1: return s2dPanel.getSimSetup();
            case 2: return d2dPanel.getSimSetup();
            case 3: return obstructionPanel.getSimSetup();
            default: throw new RuntimeException("Merge Combo Box went out of range");
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        cardLayout.show(cards, (String)e.getItem());
    }
}
