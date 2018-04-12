package aim4.util;

import aim4.map.mixedcpm.statusmonitor.IStatusMonitor;
import aim4.vehicle.mixedcpm.MixedCPMBasicManualVehicle;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;

public final class Logging {

    private static PrintWriter spawnLogFileWriter = null;
    private static PrintWriter logFileWriter = null;

    public static void initialiseLogWriters(){
        if (spawnLogFileWriter == null) {
            try {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                String spawnLogFilename = "vehicleSpawnLog_" + timestamp.toString() + ".csv";
                spawnLogFileWriter = new PrintWriter(spawnLogFilename, "UTF-8");
                spawnLogFileWriter.println("Spec,Disabled,Entry,Parking");

                String logFilename = "Log_" + timestamp.toString() + ".txt";
                logFileWriter = new PrintWriter(logFilename, "UTF-8");
                spawnLogFileWriter.println("Timestamp\tEfficiency\tAreaPerVehicle\tAllowedEntries\tDeniedEntries\tCompletedVehicles\tParkedVehicles");

            } catch (FileNotFoundException ex){ }
            catch (UnsupportedEncodingException ex) { }
        }
    }

    public static void logVehicleSpawn(MixedCPMBasicManualVehicle vehicle){
        if (spawnLogFileWriter != null) {
            String logLine = "";
            logLine += vehicle.getSpec().getName();
            logLine += ",";
            logLine += vehicle.isDisabledVehicle() ? "Y" : "N";
            logLine += ",";
            logLine += Math.round(vehicle.getEntryTime());
            logLine += ",";
            logLine += Math.round(vehicle.getParkingTime());
            spawnLogFileWriter.println(logLine);
        }
    }

    public static void logStats(IStatusMonitor monitor){
        if (logFileWriter != null) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String logLine = timestamp.toString();
            logLine += "\t";
            logLine += monitor.getCurrentEfficiency();
            logLine += "\t";
            logLine += monitor.getAreaPerVehicle();
            logLine += "\t";
            logLine += monitor.getNumberOfAllowedEntries();
            logLine += "\t";
            logLine += monitor.getNumberOfDeniedEntries();
            logLine += "\t";
            logLine += monitor.getNumberOfCompletedVehicles();
            logLine += "\t";
            logLine += monitor.getNoOfParkedVehicles();
            logFileWriter.println(logLine);
        }
    }

    public static void logFinalStats(IStatusMonitor monitor){
        if (logFileWriter != null) {
            logFileWriter.println();
            logFileWriter.println("FINAL STATISTICS");
            logFileWriter.println("Max Efficiency:\t" + String.valueOf(monitor.getMaxEfficiency()));
            logFileWriter.println("Min Area Per Vehicle:\t" + String.valueOf(monitor.getMinAreaPerVehicle()));
            logFileWriter.println("Max No Of Parked Vehicles:\t" + String.valueOf(monitor.getMostNumberOfParkedVehicles()));
            logFileWriter.println("No Of Allowed Entries:\t" + String.valueOf(monitor.getNumberOfAllowedEntries()));
            logFileWriter.println("No Of Denied Entries:\t" + String.valueOf(monitor.getNumberOfDeniedEntries()));
            logFileWriter.println("No Of Completed Vehicles:\t" + String.valueOf(monitor.getNumberOfCompletedVehicles()));
        }
    }


    public static void closeLogFiles(){
        if (spawnLogFileWriter != null) {
            spawnLogFileWriter.close();
            spawnLogFileWriter = null;
        }

        if (logFileWriter != null) {
            logFileWriter.close();
            logFileWriter = null;
        }
    }

}
