package aim4.gui.viewer;

import aim4.gui.StatusPanelContainer;
import aim4.gui.Viewer;
import aim4.gui.screen.merge.MergeStatScreen;
import aim4.gui.setuppanel.MergeSimSetupPanel;
import aim4.gui.setuppanel.SimSetupPanel;

import java.awt.event.MouseEvent;

/**
 * Created by Callum on 08/02/2017.
 */
public class MergeSimViewer extends SimViewer {
    /**
     * Creates the SimViewer
     *
     * @param statusPanel   A reference to the StatusPanelContainer in Viewer
     * @param viewer
     */
    public MergeSimViewer(StatusPanelContainer statusPanel, Viewer viewer) {
        super(statusPanel, viewer, new MergeSimSetupPanel(), false);
    }

    @Override
    protected void createStatScreen(Viewer viewer) {
        SimSetupPanel generalSetupPanel = getSimSetupPanel();
        assert(generalSetupPanel instanceof MergeSimSetupPanel);
        MergeSimSetupPanel setupPanel = (MergeSimSetupPanel) generalSetupPanel;

        this.statScreen = new MergeStatScreen(viewer, this, setupPanel);
    }

    @Override
    protected void runBeforeCreatingSimulator() {

    }

    @Override
    protected void runBeforeResettingSimulator() {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
