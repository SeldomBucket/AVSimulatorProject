package aim4.gui.screen.cpm;

import aim4.gui.screen.StatScreen;

/**
 * The StatScreen that displays statistics for the CPM simulation that is running.
 */
public class CPMStatScreen extends StatScreen {

    int count = 0;

    @Override
    public void start() {
        System.out.println("Woo! CPM StatScreen started.");
    }

    @Override
    public void update() {

    }

    @Override
    public void cleanUp() {
        System.out.println("Clean up crew active for CPMStatScreen");
    }
}
