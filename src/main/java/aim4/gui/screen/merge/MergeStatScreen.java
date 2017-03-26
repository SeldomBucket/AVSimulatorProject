package aim4.gui.screen.merge;

import aim4.gui.Viewer;
import aim4.gui.screen.StatScreen;
import aim4.gui.setuppanel.MergeSimSetupPanel;
import aim4.gui.viewer.MergeSimViewer;
import aim4.sim.setup.merge.enums.MapType;
import aim4.sim.setup.merge.enums.ProtocolType;

import java.awt.*;

/**
 * Created by Callum on 08/02/2017.
 */
public class MergeStatScreen extends StatScreen {
    Viewer viewer;
    MergeSimSetupPanel setupPanel;
    MergeSimViewer simViewer;

    public MergeStatScreen(Viewer viewer, MergeSimViewer simViewer, MergeSimSetupPanel setupPanel) {
        this.viewer = viewer;
        this.simViewer = simViewer;
        this.setupPanel = setupPanel;
    }

    @Override
    public void start() {
        MapType mapType = setupPanel.getMapType();
        ProtocolType protocolType = setupPanel.getProtocolType();

        switch(mapType) {
            case SINGLE: setupSingleLaneScreen(); break;
            case S2S:
                switch(protocolType) {
                    case AIM: setupS2SAimScreen(); break;
                    case DECENTRALISED: setupS2SDecentralisedScreen(); break;
                    default: throw new RuntimeException("Unexpected protocol type for S2S map: " + protocolType.toString());
                }
            default: throw new RuntimeException("Unexpected map type: " + mapType.toString());
        }
    }

    @Override
    public void update() {

    }

    @Override
    public void cleanUp() {

    }

    //SINGLE LANE//
    private void setupSingleLaneScreen(){
        setLayout(new BorderLayout());
        add(new )
    }

    //S2S//
    private void setupS2SAimScreen(){

    }

    private void setupS2SDecentralisedScreen(){

    }
}
