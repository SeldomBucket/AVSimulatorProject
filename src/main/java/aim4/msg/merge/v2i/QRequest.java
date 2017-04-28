package aim4.msg.merge.v2i;

/**
 * Created by Callum on 19/04/2017.
 */
public class QRequest extends V2IMergeMessage {
    // PRIVATE FIELDS//
    private double distanceToMerge;
    private int vehicleInFrontVin;

    // CONSTRUCTOR //
    public QRequest(int vin, int vehicleInFrontVIN, int mmID, double distanceToMerge) {
        super(vin, mmID);
        this.distanceToMerge = distanceToMerge;
        this.vehicleInFrontVin = vehicleInFrontVIN;
    }

    public QRequest(QRequest msg) {
        this(
                msg.getVin(),
                msg.getVehicleInFrontVIN(),
                msg.getMMID(),
                msg.getDistanceToMerge()
        );
    }

    public Type getMessageType() {
        return Type.Q_REQUEST;
    }

    public int getVehicleInFrontVIN() {
        return vehicleInFrontVin;
    }

    public double getDistanceToMerge() {
        return this.distanceToMerge;
    }
}
