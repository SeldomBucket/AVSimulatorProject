/*
Copyright (c) 2011 Tsz-Chiu Au, Peter Stone
University of Texas at Austin
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

3. Neither the name of the University of Texas at Austin nor the names of its
contributors may be used to endorse or promote products derived from this
software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package aim4.vehicle;

import aim4.driver.AutoDriver;
import aim4.driver.Driver;
import aim4.driver.aim.AIMAutoDriver;
import aim4.map.lane.Lane;
import aim4.noise.DoubleGauge;

/**
 * The interface of an autonomous vehicle from the viewpoint of a driver.
 */
public interface AutoVehicleDriverModel extends VehicleDriverModel {

  /////////////////////////////////
  // NESTED CLASSES
  /////////////////////////////////

  /**
   * Potential operating modes for a vehicle's laser range finder.  These are
   * used to ensure that the intersection simulator doesn't have to simulate
   * sensor inputs if they are not going to be used by the driver agent.
   */
  public enum LRFMode {
    /** The laser range finder is completely turned off */
    DISABLED,
    /**
     * The laser range finder only tracks the nearest point on the Vehicle in
     * front of this Vehicle
     */
    LIMITED,
    /**
     * The laser range finder is operating at full capacity, limited only by
     * the sensor angle limits and sensor range limits
     */
    ENABLED;
  }

  /////////////////////////////////
  // PUBLIC METHODS
  /////////////////////////////////

  /**
   * {@inheritDoc}
   */
  @Override
  AutoDriver getDriver();

  /////////////////////////////////
  // PUBLIC METHODS
  /////////////////////////////////

  // intervalometer

  /**
   * Get this Vehicle's interval-to-vehicle-in-front gauge. This should
   * <b>only</b> be followed by a call to <code>read</code>, <b>except</b> in
   * the actual physical simulator which is allowed to set these values.
   *
   * @return the Vehicle's interval-to-vehicle-in-front gauge
   */
  DoubleGauge getIntervalometer();


  /////////////////////////////////
  // PUBLIC METHODS
  /////////////////////////////////

  // LRF

  /**
   * Get the Vehicle's laser range finder operating mode.
   *
   * @return the current operating mode of the laser range finder
   * @see    LRFMode
   */
  LRFMode getLRFMode();

  /**
   * Get whether or not the laser range finder is sensing anything.
   *
   * @return whether or not the laser range finder is sensing anything
   */
  boolean isLRFSensing();

  /**
   * Get this Vehicle's laser range finder distance gauge.
   * This should <b>only</b> be followed by a call to <code>read</code>,
   * <b>except</b> in the actual physical simulator which is allowed to set
   * these values.
   *
   * @return the Vehicle's laser range finder angle gauge
   */
  DoubleGauge getLRFDistance();



  /////////////////////////////////
  // PUBLIC METHODS
  /////////////////////////////////

  // vehicle tracking

  /**
   * Get whether or not the vehicle tracking sensors are sensing anything.
   *
   * @return whether or not the vehicle tracking sensors are sensing
   * anything
   */
  boolean isVehicleTracking();

  /**
   * Set whether or not the vehicle tracking sensors are sensing anything.
   *
   * @param sensing  whether or not the vehicle tracking sensors are sensing
   * anything
   */
  void setVehicleTracking(boolean sensing);

  /**
   * Return the target lane for vehicle tracking.
   *
   * @return  the target lane for vehicle tracking
   */
  Lane getTargetLaneForVehicleTracking();

  /**
   * Set the target lane for vehicle tracking.
   *
   * @param lane  the target lane for vehicle tracking
   */
  void setTargetLaneForVehicleTracking(Lane lane);


  /**
   * Get this Vehicle's gauge of sensors about the distance, in meters,
   * between p1 and p2, both of them are points on the target lane,
   * where p1 is the point projected from the center of the front of the
   * vehicle, and p2 is the nearest point of another vehicle in the front
   * of the vehicle to p1 on the target lane.  If there is no vehicle in
   * the front on the target lane, the value should be Double.MAX_VALUE.
   *
   * @return the distance of the vehicle in the front on the target lane.
   */
  DoubleGauge getFrontVehicleDistanceSensor();

  /**
   * Get this Vehicle's gauge of sensors about the distance, in meters,
   * between p1 and p2, both of them are points on the target lane,
   * where p1 is the point projected from the center of the front of the
   * vehicle, and p2 is the nearest point of another vehicle behind
   * the vehicle to p1 on the target lane.  If there is no vehicle behind
   * on the target lane, the value should be Double.MAX_VALUE.
   *
   * @return the distance of the vehicle behind on the target lane.
   */
  DoubleGauge getRearVehicleDistanceSensor();

  /**
   * Get this Vehicle's gauge of sensors about the speed, in meters per second,
   * of the vehicle in front of the vehicle on the target lane.  If there is no
   * vehicle in the front on the target lane, the value should be
   * Double.MAX_VALUE.
   *
   * @return the speed of the vehicle in the front on the target lane.
   */
  DoubleGauge getFrontVehicleSpeedSensor();

  /**
   * Get this Vehicle's gauge of sensors about the speed, in meters per second,
   * of the vehicle behind the vehicle on the target lane.  If there is no
   * vehicle behind on the target lane, the value should be Double.MAX_VALUE.
   *
   * @return the speed of the vehicle behind on the target lane.
   */
  DoubleGauge getRearVehicleSpeedSensor();

}
