package aim4.gui.screen.cpm;

import aim4.gui.Viewer;
import aim4.gui.screen.StatScreen;
import aim4.gui.screen.cpm.components.*;
import aim4.gui.setuppanel.CPMSimSetupPanel;
import aim4.gui.viewer.CPMSimViewer;
import aim4.sim.Simulator;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator;
import aim4.sim.simulator.cpm.CPMAutoDriverSimulator.*;
import com.csvreader.CsvWriter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The StatScreen that displays statistics for the CPM simulation that is running.
 */
public class CPMStatScreen extends StatScreen {

    Viewer viewer;
    CPMSimSetupPanel setupPanel;
    CPMSimViewer simViewer;
    List<CPMStatScreenComponent> componentsToUpdate;
    List<CPMAutoDriverSimStepResult> resultsToProcess;

    // Components
    GeneralInfo generalInfo;
    SimConfigSummary simConfigSummary;
    CarParkStats carParkStats;
    CompletedVehiclesTable completedVehiclesTable;

    public CPMStatScreen(Viewer viewer, CPMSimViewer simViewer, CPMSimSetupPanel setupPanel) {
        this.viewer = viewer;
        this.simViewer = simViewer;
        this.setupPanel = setupPanel;
        this.componentsToUpdate = new ArrayList<CPMStatScreenComponent>();
        this.resultsToProcess = new ArrayList<CPMAutoDriverSimStepResult>();
    }

    @Override
    public void start() {
        setupScreen();
    }

    @Override
    public void printData(String outFileName) {

        // before we open the file check to see if it already exists
        // boolean alreadyExists = new File(outFileName).exists();
        // if the file didn't already exist then we need to write out the header line
        // TODO CPM shouldn't be an assertion.
        // assert !alreadyExists;

        printComponentOfLabels(carParkStats, outFileName);
        printComponentOfLabels(simConfigSummary, outFileName);
        printCompletedVehiclesTable(outFileName);

    }

    private void printCompletedVehiclesTable(String outFileName){
        try {
            // use FileWriter constructor that specifies open for appending
            CsvWriter csvOutput = new CsvWriter(new FileWriter(outFileName, true), ',');

            TableModel tableModel = completedVehiclesTable.getTable().getModel();
            int colCount = completedVehiclesTable.getTable().getModel().getColumnCount();
            int rowCount = completedVehiclesTable.getTable().getModel().getRowCount();

            // Print the headers
            for (int colIndex = 0 ; colIndex < colCount ; colIndex ++){
                csvOutput.write(tableModel.getColumnName(colIndex));
            }
            csvOutput.endRecord();

            // Print the values
            for (int rowIndex = 0 ; rowIndex < rowCount ; rowIndex ++){
                for (int colIndex = 0 ; colIndex < colCount ; colIndex ++){
                    // output the value in that cell
                    csvOutput.write(tableModel.getValueAt(rowIndex, colIndex).toString());
                    // if its the last cell in that row, end the record
                    if (colIndex + 1 == colCount){
                        csvOutput.endRecord();
                    }
                }
            }

            csvOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printComponentOfLabels(CPMStatScreenComponent component, String outFileName){
        try {
            // use FileWriter constructor that specifies open for appending
            CsvWriter csvOutput = new CsvWriter(new FileWriter(outFileName, true), ',');

            List<String> allLabelsText = component.getAllLabelsText();
            for (int i = 0 ; i < allLabelsText.size() ; i++){
                String[] labelSplit = allLabelsText.get(i).split(":");
                csvOutput.write(labelSplit [0]);
                csvOutput.write(labelSplit [1]);
                csvOutput.endRecord();
            }

            csvOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public synchronized void update() {
        Simulator generalSim = simViewer.getSimulator();
        assert(generalSim instanceof CPMAutoDriverSimulator);
        CPMAutoDriverSimulator sim = (CPMAutoDriverSimulator) generalSim;

        for(CPMStatScreenComponent comp : componentsToUpdate) {
            comp.update(sim, resultsToProcess);
        }
        resultsToProcess.clear();
    }

    @Override
    public void cleanUp() {
        for(CPMStatScreenComponent comp : componentsToUpdate) {
            assert(comp instanceof JComponent); //Should always be true.
            this.remove((JComponent) comp);
        }
        componentsToUpdate.clear();
    }

    public void addResultToProcess(CPMAutoDriverSimStepResult simStepResult) {
        this.resultsToProcess.add(simStepResult);
    }

    private void setupScreen(){
        generalInfo = new GeneralInfo();
        generalInfo.setBorder(new EmptyBorder(10, 10, 10, 10));

        simConfigSummary = new SimConfigSummary(setupPanel);
        simConfigSummary.setBorder(new EmptyBorder(10, 10, 10, 10));

        carParkStats = new CarParkStats();
        carParkStats.setBorder(new EmptyBorder(10, 10, 10, 10));

        completedVehiclesTable = new CompletedVehiclesTable();
        completedVehiclesTable.setMaximumSize(new Dimension(60, 60));


        setLayout(new FlowLayout());
        add(generalInfo);
        add(simConfigSummary);
        add(carParkStats);
        add(completedVehiclesTable);


        componentsToUpdate.add(generalInfo);
        componentsToUpdate.add(simConfigSummary);
        componentsToUpdate.add(carParkStats);
        componentsToUpdate.add(completedVehiclesTable);
    }
}
