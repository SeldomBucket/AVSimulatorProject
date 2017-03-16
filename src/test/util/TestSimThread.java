package util;

import aim4.config.SimConfig;
import aim4.sim.Simulator;

/**
 * Created by Callum on 16/03/2017.
 */
public class TestSimThread implements Runnable {

    private volatile Thread blinker;
    private static final long timeDelay = (long) (1000.0 / 30.0); //Assuming 30fps
    private Simulator sim;

    public TestSimThread(Simulator sim) {
        this.sim = sim;
    }

    public synchronized void start() {
        this.blinker = new Thread(this, "Test Simulation Thread");
        blinker.start();
    }

    public synchronized void terminate() {
        assert blinker != null;
        blinker = null;
    }

    @Override
    public void run() {
        Thread thisThread = Thread.currentThread();
        while (blinker == thisThread) {
            long nextInvokeTime = System.currentTimeMillis() + timeDelay;
            runSimulationStep();
            Thread.yield();
        }
        System.err.printf("Simulation was terminated");
    }

    private Simulator.SimStepResult runSimulationStep() {
        Simulator.SimStepResult simStepResult = sim.step(SimConfig.TIME_STEP);
        return simStepResult;
    }
}
