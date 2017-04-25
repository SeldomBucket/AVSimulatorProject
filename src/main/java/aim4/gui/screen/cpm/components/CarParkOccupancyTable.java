package aim4.gui.screen.cpm.components;

import aim4.gui.screen.MapKeyTableModel;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import aim4.util.Util;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A table displaying information about the occupancy of the car park at hourly intervals.
 */
public class CarParkOccupancyTable extends JPanel
        implements CPMStatScreenComponent, StatScreenTable {

    private MapKeyTableModel model;
    private JTable table;
    private JScrollPane scrollPane;
    List<Integer> collectionHours;

    public CarParkOccupancyTable() {
        this.model = new MapKeyTableModel(new String[]{
                "Hour",
                "Time",
                "Number of vehicles in car park",
                "% of free parking space"
        });

        this.table = new JTable(model);
        scrollPane = new JScrollPane(table);
        scrollPane.setVisible(true);
        this.add(scrollPane);

        initialiseHoursToCollectData();
    }

    private void initialiseHoursToCollectData(){
        // TODO CPM should get number of hours from simSetup
        this.collectionHours = new ArrayList<Integer>(24);

        for (int i = 1 ; i < 24 ; i++) {
            collectionHours.add(i);
        }
    }

    @Override
    public void update(CPMAutoDriverSimulator sim,
                       List<CPMAutoDriverSimulator.CPMAutoDriverSimStepResult> results) {

        String timeString = Util.convertSecondsToTimeString(sim.getSimulationTime());
        String[] data = timeString.split(":");
        double hours = Double.parseDouble(data[0]);

        if ((int)hours == collectionHours.get(0)) {
            collectionHours.remove(0);

            model.addOrUpdateRow((int)hours,
                    new Object[]{
                            (int)hours,
                            Util.convertSecondsToTimeString(sim.getSimulationTime()),
                            sim.getMap().getStatusMonitor().getVehicles().size(),
                            String.format("%.1f", getAvailableSpaceAsPercent(sim))
                    });
        }
    }

    private double getAvailableSpaceAsPercent(CPMAutoDriverSimulator sim) {
        double availableSpace = sim.getMap().getStatusMonitor().getAvailableParkingArea();
        double totalCarParkArea = sim.getMap().getTotalCarParkArea();
        return (availableSpace/totalCarParkArea)*100;
    }

    @Override
    public List<String> getAllLabelsText() {
        return null;
    }

    public JTable getTable() {
        return table;
    }
}
