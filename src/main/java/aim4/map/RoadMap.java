package aim4.map;

import aim4.map.connections.Corner;
import aim4.map.connections.Junction;
import aim4.map.connections.SimpleIntersection;
import aim4.map.lane.Lane;
import aim4.util.Registry;

import java.awt.geom.Rectangle2D;
import java.util.List;

public interface RoadMap {

    /**
     * Get the Roads that are part of this Layout.
     *
     * @return the Roads that are part of this Layout
     */
    List<Road> getRoads();

    /**
     * Get the dimensions of this Layout, in Rectangle form.
     *
     * @return a Rectangle representing the dimensions of this Layout
     */
    Rectangle2D getDimensions();

    /**
     * Get the maximum speed limit of any Road in the Layout.
     *
     * @return the maximum speed, in meters per second, of any Lane in any Road
     *         in the Layout
     */
    double getMaximumSpeedLimit();

    /**
     * Get the lane registry.
     *
     * @return the lane registry.
     */
    Registry<Lane> getLaneRegistry();

    /**
     * Given a Lane, get the Road of which that Lane is a part.
     *
     * @param lane the Lane for which to get the enclosing Road
     * @return     the Road of which the given Lane is a part.
     */
    Road getRoad(Lane lane);

    /**
     * Given a Lane ID number, get the Road of which that Lane is a part.
     *
     * @param laneID the ID of the Lane for which to get the enclosing Road
     * @return       the Road of which the given Lane is a part.
     */
    Road getRoad(int laneID);

    List<Lane> getExitLanes();

    List<Corner> getCorners();

    abstract List<Junction> getJunctions();

    List<SimpleIntersection> getIntersections();

    abstract Road getRoadByName(String name);

    double getLaneWidth();
}
