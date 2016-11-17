package aim4.vehicle;

import aim4.driver.Driver;
import aim4.driver.aim.AIMAutoDriver;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Created by Callum on 17/11/2016.
 */
public interface VehicleSimModel extends VehicleDriverModel {

    /////////////////////////////////
    // PUBLIC METHODS
    /////////////////////////////////

    //vehicle accessors

    /**
    * Set the VIN number of this Vehicle.
    *
    * @param vin the vehicle's VIN number.
    */
    void setVIN(int vin);

    /**
     * Set this Vehicle's Driver.
     *
     * @param driver  the new driver to control this Vehicle
     */
    void setDriver(Driver driver);

    /**
     * Check whether this vehicle's time is current.
     *
     * @param currentTime  the current time
     */
    void checkCurrentTime(double currentTime);

    /////////////////////////////////
    // PUBLIC METHODS
    /////////////////////////////////

    // states

    /**
     * Get the position of the vehicle.
     *
     * @return the position of the vehicle
     */
    Point2D getPosition();

    /**
     * Get the heading of the vehicle
     *
     * @return the heading of the vehicle
     */
    double getHeading();

    /**
     * Get the velocity of the vehicle
     *
     * @return the velocity of the vehicle
     */
    double getVelocity();

    /**
     * Get the acceleration of the vehicle
     *
     * @return the acceleration of the vehicle
     */
    double getAcceleration();


    /////////////////////////////////
    // PUBLIC METHODS
    /////////////////////////////////

    // derived properties

    /**
     * Get a {@link Shape} describing the Vehicle.
     *
     * @return a Shape describing this Vehicle.
     */
    Shape getShape();

    /**
     * Get a {link Shape} describing this Vehicle, if it were larger in each
     * dimension.
     *
     * @param extra the fixed extra amount by which to increase the size of the
     *              Vehicle in each dimension
     * @return      a Shape describing a Vehicle larger in each dimension by the
     *              extra amount.
     */
    Shape getShape(double extra);

    /**
     * Get the edges that represent the boundaries of this Vehicle.
     *
     * @return an array of line segments that represent the edges of the
     *         Vehicle.
     */
    java.util.List<Line2D> getEdges();

    /**
     * Get the Shapes of each of the wheels.
     *
     * @return an array of wheel Shapes: front left, front right, rear left,
     *         rear right
     */
    Shape[] getWheelShapes();

    /**
     * Get the point in front of the middle point of the vehicle that is
     * at the distance of delta away from the vehicle.
     *
     * @param delta   the distance of the vehicle and the point
     *
     * @return the projected point
     */
    Point2D getPointAtMiddleFront(double delta);

    /**
     * Get the location of the center of the Vehicle at this point in time.
     *
     * @return the global coordinates of the center of the Vehicle.
     */
    Point2D getCenterPoint();

    /**
     * Get the current global coordinates of the corners of this Vehicle.
     *
     * @return an array of points representing the four corners.
     */
    Point2D[] getCornerPoints();

    /**
     * Get the point at the rear center of the Vehicle.
     *
     * @return the global coordinates of the point at the center of the
     * Vehicle's rear
     */
    Point2D getPointAtRear();

    /////////////////////////////////
    // PUBLIC METHODS
    /////////////////////////////////

    // controls

    /**
     * Move a single Vehicle according to some approximation of the laws
     * of physics.
     *
     * @param timeStep the size of the time step to simulate, in seconds
     */
    void move(double timeStep);
}
