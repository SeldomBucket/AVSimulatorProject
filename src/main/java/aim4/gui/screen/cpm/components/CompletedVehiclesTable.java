package aim4.gui.screen.cpm.components;

import aim4.gui.screen.merge.components.MapKeyTableModel;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator.*;
import aim4.vehicle.cpm.CPMBasicAutoVehicle;

import javax.swing.*;
import java.util.List;

/**
 * A table displaying information for the vehicles that have completed the simulation.
 */
public class CompletedVehiclesTable extends JPanel implements CPMStatScreenComponent {
    private MapKeyTableModel model; // TODO CPM Move this class out of merge
    private JTable table;
    private JScrollPane scrollPane;

    public CompletedVehiclesTable() {
        this.model = new MapKeyTableModel( new String[]{
                "VIN",
                "Spec",
                "Entry time",
                "Parking time",
                "Exit time",
                "Retrieval time",
                "Number of re-entries",
                "Distance travelled"
        });

        this.table = new JTable(model);
        scrollPane = new JScrollPane(table);
        scrollPane.setVisible(true);
        this.add(scrollPane);
    }

    @Override
    public void update(CPMAutoDriverSimulator sim,
                       List<CPMAutoDriverSimStepResult> results) {
        for(CPMAutoDriverSimStepResult stepResult : results) {
            for (CPMBasicAutoVehicle vehicle : stepResult.getCompletedVehicles()) {
                model.addOrUpdateRow(vehicle.getVIN(), new Object[]{
                        vehicle.getVIN(),
                        vehicle.getSpec().getName(),
                        vehicle.getEntryTime(),
                        vehicle.getParkingTime(),
                        vehicle.getExitTime(),
                        calculateRetrievalTime(vehicle),
                        vehicle.getNumberOfReEntries(),
                        vehicle.getEstimatedDistanceTravelled()
                });
            }
        }
    }

    private double calculateRetrievalTime(CPMBasicAutoVehicle vehicle) {
        double timeRetrieved = vehicle.getEntryTime() + vehicle.getParkingTime();
        double timeTakenToRetrieve = vehicle.getExitTime() - timeRetrieved;
        return timeTakenToRetrieve;
    }

}
