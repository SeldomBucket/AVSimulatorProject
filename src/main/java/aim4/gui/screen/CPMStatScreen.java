package aim4.gui.screen;

/**
 * The StatScreen that displays statistics for the CPM simulation that is running.
 */
public class CPMStatScreen extends StatScreen {

    int count = 0;

    @Override
    public void start() {
        System.out.println("Woo!");
    }

    @Override
    public void update() {
        count++;
        System.out.println("Help, I'm stuck in a CPM loop! " + count);
    }

    @Override
    public void cleanUp() {
        System.out.println("Clean up crew active");
    }
}
