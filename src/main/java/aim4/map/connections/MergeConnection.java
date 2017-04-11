package aim4.map.connections;

import aim4.map.Road;

import java.util.List;

/**
 * Created by Callum on 10/04/2017.
 */
public abstract class MergeConnection extends BasicConnection {
    /**
     * Basic class constructor.
     * Takes the Roads which meet to make this connection.
     *
     * @param roads the roads involved in this connection.
     */
    public MergeConnection(List<Road> roads) {
        super(roads);
    }
}
