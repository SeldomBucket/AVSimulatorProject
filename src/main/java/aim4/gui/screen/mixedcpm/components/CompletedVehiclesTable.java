package aim4.gui.screen.mixedcpm.components;

import aim4.gui.screen.merge.components.MapKeyTableModel;
import aim4.sim.simulator.mixedcpm.MixedCPMAutoDriverSimulator;
import aim4.sim.simulator.mixedcpm.MixedCPMAutoDriverSimulator.*;
import aim4.vehicle.mixedcpm.MixedCPMBasicManualVehicle;

import javax.swing.*;
import java.util.List;

/**
 * A table displaying information for the vehicles that have completed the simulation.
 */
public class CompletedVehiclesTable extends JPanel implements MixedCPMStatScreenComponent {
    private MapKeyTableModel model; // TODO CPM Move this class out of merge
    private JTable table;
    private JScrollPane scrollPane;

    public CompletedVehiclesTable() {
        this.model = new MapKeyTableModel( new String[]{
                "VIN",
                "Spec",
                "Entry time",
                "Parking time",
                "Exit time"
        });

        this.table = new JTable(model);
        scrollPane = new JScrollPane(table);
        scrollPane.setVisible(true);
        this.add(scrollPane);
    }

    @Override
    public void update(MixedCPMAutoDriverSimulator sim,
                       List<MixedCPMAutoDriverSimStepResult> results) {
        for(MixedCPMAutoDriverSimStepResult stepResult : results) {
            for (MixedCPMBasicManualVehicle vehicle : stepResult.getCompletedVehicles()) {
                model.addOrUpdateRow(vehicle.getVIN(), new Object[]{
                        vehicle.getVIN(),
                        vehicle.getSpec().getName(),
                        vehicle.getEntryTime(),
                        vehicle.getParkingTime(),
                        vehicle.getExitTime()
                });
            }
        }
    }

    private double calculateRetrievalTime(MixedCPMBasicManualVehicle vehicle) {
        double timeRetrieved = vehicle.getEntryTime() + vehicle.getParkingTime();
        double timeTakenToRetrieve = vehicle.getExitTime() - timeRetrieved;
        return timeTakenToRetrieve;
    }

}
