package aim4.sim.setup.cpm;

import aim4.map.cpm.CPMMap;
import aim4.map.cpm.CPMMapCarPark;
import aim4.map.cpm.CPMMapUtil;
import aim4.map.cpm.testmaps.CPMMapIntersection3Roads;
import aim4.map.cpm.testmaps.CPMMapWithCornersOneLane;
import aim4.map.cpm.testmaps.CPMMapWithSimpleIntersection;
import aim4.map.cpm.testmaps.CPMMapWithTJunction;
import aim4.sim.CPMAutoDriverSimulator;
import aim4.sim.Simulator;

/**
 * Setup for simulation of AVs in an AV specific car park which are self-organising.
 */
public class CPMAutoDriverSimSetup extends BasicCPMSimSetup {

    /**
     * Create a setup for the simulator in which all vehicles are autonomous.
     *
     * @param basicSimSetup  the basic simulator setup
     */
    public CPMAutoDriverSimSetup(BasicCPMSimSetup basicSimSetup) {
        super(basicSimSetup);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Simulator getSimulator() {
        double currentTime = 0.0;
        /*CPMMap layout = new CPMMapWithCornersOneLane(4, // laneWidth
                10.0, // speedLimit
                currentTime, // initTime
                500, //width
                500); //height*/

        /*CPMMap layout = new CPMMapWithSimpleIntersection(4, // laneWidth
        10.0, // speedLimit
                currentTime, // initTime
                500, //width
                500); //height*/

        /*CPMMap layout = new CPMMapWithTJunction(4, // laneWidth
                10.0, // speedLimit
                currentTime, // initTime
                500, //width
                500); //height*/

        /*CPMMap layout = new CPMMapCarPark(4, // laneWidth
                10.0, // speedLimit
                currentTime, // initTime
                5, // numberOfParkingLanes
                20, // parkingLength
                5); // access length*/

        CPMMap layout = new CPMMapIntersection3Roads(4, // laneWidth
                10.0, // speedLimit
                currentTime, // initTime
                500, //width
                500); //height

        // set up the spawn points: create a new method for this.
        CPMMapUtil.setUpSimpleSpawnPoints(layout);
        return new CPMAutoDriverSimulator(layout);
    }
}
