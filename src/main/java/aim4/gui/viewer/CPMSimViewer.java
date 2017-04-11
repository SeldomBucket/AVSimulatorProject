package aim4.gui.viewer;

import aim4.gui.StatusPanelContainer;
import aim4.gui.Viewer;
import aim4.gui.screen.CPMStatScreen;
import aim4.gui.setuppanel.CPMSimSetupPanel;
import aim4.map.cpm.CPMMapUtil.*;
import aim4.sim.setup.cpm.BasicCPMSimSetup;
import javafx.util.Pair;

import java.awt.event.MouseEvent;

/**
 * A Viewer for CPM.
 */
public class CPMSimViewer extends SimViewer {
    /**
     * Creates the CPMSimViewer
     *
     * @param statusPanel   A reference to the StatusPanelContainer in Viewer
     * @param viewer
     */
    public CPMSimViewer(StatusPanelContainer statusPanel, Viewer viewer) {
        super(statusPanel, viewer, new CPMSimSetupPanel(new BasicCPMSimSetup(
                2.25, // speedLimit - approx. 5mph
                0.28, // trafficLevel
                2.0, // laneWidth
                1, // numberOfParkingLanes
                20.0, // parkingLength
                1.0, // accessLength,
                SpawnSpecType.SINGLE, // spawn spec type
                new Pair<Boolean, String>(false, "")
        )), false);
    }

    @Override
    protected void createStatScreen() {
        this.statScreen = new CPMStatScreen();
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
