package aim4.util;

import aim4.map.mixedcpm.parking.IStatusMonitor;
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

                String logFilename = "Log_" + timestamp.toString() + ".csv";
                logFileWriter = new PrintWriter(logFilename, "UTF-8");
                spawnLogFileWriter.println("Timestamp,Efficiency,AreaPerVehicle,AllowedEntries,DeniedEntries,CompletedVehicles,ParkedVehicles");

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
            logLine += ",";
            logLine += monitor.getCurrentEfficiency();
            logLine += ",";
            logLine += monitor.getAreaPerVehicle();
            logLine += ",";
            logLine += monitor.getNumberOfAllowedEntries();
            logLine += ",";
            logLine += monitor.getNumberOfDeniedEntries();
            logLine += ",";
            logLine += monitor.getNumberOfCompletedVehicles();
            logLine += ",";
            logLine += monitor.getNoOfParkedVehicles();
            logFileWriter.println(logLine);
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
