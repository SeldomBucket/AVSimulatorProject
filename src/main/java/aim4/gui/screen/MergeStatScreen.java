package aim4.gui.screen;

import aim4.gui.Viewer;
import aim4.gui.statuspanel.SystemPanel;
import aim4.gui.viewer.SimViewer;
import aim4.map.BasicMap;

import java.awt.*;

/**
 * Created by Callum on 08/02/2017.
 */
public class MergeStatScreen extends StatScreen {

    int count = 0;

    @Override
    public void start() {
        System.out.println("Woo!");
    }

    @Override
    public void update() {
        count++;
        System.out.println("Help, I'm stuck in a loop! " + count);
    }

    @Override
    public void cleanUp() {
        System.out.println("Clean up crew active");
    }
}
