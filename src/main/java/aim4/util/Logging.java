package aim4.util;

import aim4.map.mixedcpm.statusmonitor.IStatusMonitor;
import aim4.vehicle.mixedcpm.MixedCPMBasicAutoVehicle;
import aim4.vehicle.mixedcpm.MixedCPMBasicManualVehicle;
import aim4.vehicle.mixedcpm.MixedCPMBasicVehicle;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;

public final class Logging {

    private static PrintWriter spawnLogFileWriter = null;
    private static PrintWriter logFileWriter = null;

    public static void initialiseLogWriter(){
        try {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());


            String logFilename = "Log_" + timestamp.toString() + ".txt";
            logFileWriter = new PrintWriter(logFilename, "UTF-8");
            logFileWriter.println("Timestamp\tLineType\tNoOfParkedVehicles\tEfficiency\tAreaPerVehicle\tAllowedEntries\tDeniedEntries\tCompletedVehicles\tParkedVehicles");

        } catch (FileNotFoundException ex){ }
        catch (UnsupportedEncodingException ex) { }
    }

    public static void initialiseSpawnLogWriter(){
        try {

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            String spawnLogFilename = "vehicleSpawnLog_" + timestamp.toString() + ".csv";
            spawnLogFileWriter = new PrintWriter(spawnLogFilename, "UTF-8");
            spawnLogFileWriter.println("Spec,Disabled,Automated,Entry,Parking");

        } catch (FileNotFoundException ex){ }
        catch (UnsupportedEncodingException ex) { }
    }

    public static void logVehicleSpawn(MixedCPMBasicVehicle vehicle){
        if (spawnLogFileWriter != null) {
            if (vehicle instanceof MixedCPMBasicManualVehicle) {
                String logLine = "";
                logLine += vehicle.getSpec().getName();
                logLine += ",";
                logLine += ((MixedCPMBasicManualVehicle)vehicle).isDisabledVehicle() ? "Y" : "N";
                logLine += ",";
                logLine += "N";
                logLine += ",";
                logLine += Math.round(vehicle.getEntryTime());
                logLine += ",";
                logLine += Math.round(vehicle.getParkingTime());
                spawnLogFileWriter.println(logLine);
            }else if (vehicle instanceof MixedCPMBasicAutoVehicle) {
                String logLine = "";
                logLine += vehicle.getSpec().getName();
                logLine += ",";
                logLine += "N";
                logLine += ",";
                logLine += "Y";
                logLine += ",";
                logLine += Math.round(vehicle.getEntryTime());
                logLine += ",";
                logLine += Math.round(vehicle.getParkingTime());
                spawnLogFileWriter.println(logLine);

            }
        }
    }

    public static void logStats(IStatusMonitor monitor){
        if (logFileWriter != null) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String logLine = timestamp.toString();
            logLine += "\t";
            logLine += "Overall Stats";
            logLine += "\t";
            logLine += monitor.getNoOfParkedVehicles();
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
            
            logLine = timestamp.toString();
            logLine += "\t";
            logLine += "Manual Stats";
            logLine += "\t";
            logLine += monitor.getNoOfParkedManualVehicles();
            logLine += "\t";
            logLine += monitor.getCurrentManualEfficiency();
            logLine += "\t";
            logLine += monitor.getAreaPerManualVehicle();
            logLine += "\t";
            logLine += monitor.getNumberOfAllowedManualEntries();
            logLine += "\t";
            logLine += monitor.getNumberOfDeniedManualEntries();
            logLine += "\t";
            logLine += monitor.getNumberOfCompletedManualVehicles();
            logLine += "\t";
            logLine += monitor.getNoOfParkedManualVehicles();
            logFileWriter.println(logLine);

            logLine = timestamp.toString();
            logLine += "\t";
            logLine += "Automated Stats";
            logLine += "\t";
            logLine += monitor.getNoOfParkedManualVehicles();
            logLine += "\t";
            logLine += monitor.getCurrentAutoEfficiency();
            logLine += "\t";
            logLine += monitor.getAreaPerAutoVehicle();
            logLine += "\t";
            logLine += monitor.getNumberOfAllowedAutoEntries();
            logLine += "\t";
            logLine += monitor.getNumberOfDeniedAutoEntries();
            logLine += "\t";
            logLine += monitor.getNumberOfCompletedAutoVehicles();
            logLine += "\t";
            logLine += monitor.getNoOfParkedAutoVehicles();
            logFileWriter.println(logLine);
        }
    }

    public static void logFinalStats(IStatusMonitor monitor){
        if (logFileWriter != null) {
            logFileWriter.println();
            logFileWriter.println("FINAL STATISTICS");
            logFileWriter.println();
            logFileWriter.println("CAR PARK");
            logFileWriter.println("Max Efficiency:\t" + String.valueOf(monitor.getMaxEfficiency()));
            logFileWriter.println("Min Area Per Vehicle:\t" + String.valueOf(monitor.getMinAreaPerVehicle()));
            logFileWriter.println("Max No Of Parked Vehicles:\t" + String.valueOf(monitor.getMostNumberOfParkedVehicles()));
            logFileWriter.println("No Of Allowed Entries:\t" + String.valueOf(monitor.getNumberOfAllowedEntries()));
            logFileWriter.println("No Of Denied Entries:\t" + String.valueOf(monitor.getNumberOfDeniedEntries()));
            logFileWriter.println("No Of Completed Vehicles:\t" + String.valueOf(monitor.getNumberOfCompletedVehicles()));
            logFileWriter.println();
            logFileWriter.println("MANUAL PARKING AREA");
            logFileWriter.println("Max Efficiency:\t" + String.valueOf(monitor.getMaxManualEfficiency()));
            logFileWriter.println("Min Area Per Vehicle:\t" + String.valueOf(monitor.getMinAreaPerManualVehicle()));
            logFileWriter.println("Max No Of Parked Vehicles:\t" + String.valueOf(monitor.getMostNumberOfParkedManualVehicles()));
            logFileWriter.println("No Of Allowed Entries:\t" + String.valueOf(monitor.getNumberOfAllowedManualEntries()));
            logFileWriter.println("No Of Denied Entries:\t" + String.valueOf(monitor.getNumberOfDeniedManualEntries()));
            logFileWriter.println("No Of Completed Vehicles:\t" + String.valueOf(monitor.getNumberOfCompletedManualVehicles()));
            logFileWriter.println();
            logFileWriter.println("AUTOMATED PARKING AREA");
            logFileWriter.println("Max Efficiency:\t" + String.valueOf(monitor.getMaxAutoEfficiency()));
            logFileWriter.println("Min Area Per Vehicle:\t" + String.valueOf(monitor.getMinAreaPerAutoVehicle()));
            logFileWriter.println("Max No Of Parked Vehicles:\t" + String.valueOf(monitor.getMostNumberOfParkedAutoVehicles()));
            logFileWriter.println("No Of Allowed Entries:\t" + String.valueOf(monitor.getNumberOfAllowedAutoEntries()));
            logFileWriter.println("No Of Denied Entries:\t" + String.valueOf(monitor.getNumberOfDeniedAutoEntries()));
            logFileWriter.println("No Of Completed Vehicles:\t" + String.valueOf(monitor.getNumberOfCompletedAutoVehicles()));

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
