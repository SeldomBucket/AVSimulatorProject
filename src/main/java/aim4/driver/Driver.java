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
package aim4.driver;

import java.awt.Color;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import aim4.config.Debug;
import aim4.config.DebugPoint;
import aim4.map.lane.Lane;
import aim4.util.GeomMath;
import aim4.vehicle.AutoVehicleDriverModel;
import aim4.vehicle.VehicleDriverModel;

/**
 * An agent that drives a {@link aim4.vehicle.AutoVehicleDriverModel}.
 */
public abstract class Driver implements DriverSimModel {

  /////////////////////////////////
  // CONSTANTS
  /////////////////////////////////

  // debugging

  /**
   * The maximum length, in meters, for which to display the sensed interval
   * between this DriverAgent's vehicle and the one in front of it.
   * {@value} meters.
   */
  private static final double MAX_INTERVAL_DISPLAY_DIST = 40; // m


  /////////////////////////////////
  // PROTECTED FIELDS
  /////////////////////////////////

  // lane

  /** The Lane that the driver is currently following. */
  protected Lane currentLane;

  /** The set of Lanes that the vehicle is currently occupied */
  protected Set<Lane> currentlyOccupiedLanes;

  /////////////////////////////////
  // PUBLIC METHODS
  /////////////////////////////////

  /**
   * Take control actions for driving the agent's Vehicle.  This includes
   * physical manipulation of the Vehicle as well as sending any messages
   * or performing any coordination tasks.
   */
  @Override
  public void act() {
    if (Debug.isTargetVIN(getVehicle().getVIN())) {
      if (getVehicle() instanceof AutoVehicleDriverModel) {  // TODO: it is ugly
        AutoVehicleDriverModel autoVehicle =
          (AutoVehicleDriverModel)getVehicle();
        if (autoVehicle.getIntervalometer().read() < MAX_INTERVAL_DISPLAY_DIST){
          Debug.addShortTermDebugPoint(
            new DebugPoint(
              GeomMath.polarAdd(autoVehicle.gaugePosition(),
                                autoVehicle.getIntervalometer().read(),
                                autoVehicle.gaugeHeading()),
              autoVehicle.gaugePosition(),
              "follow",
              Color.BLUE.brighter()));
        }
      }
    }
  }

  /////////////////////////////////
  // PUBLIC METHODS
  /////////////////////////////////

  /**
   * Get the Vehicle this driver agent is controlling.
   *
   * @return the Vehicle this driver agent is controlling
   */
  @Override
  public abstract VehicleDriverModel getVehicle();

  /////////////////////////////////
  // PUBLIC METHODS
  /////////////////////////////////

  // lane

  /**
   * Get the Lane the DriverAgent is currently following.
   *
   * @return the Lane the DriverAgent is currently following
   */
  @Override
  public Lane getCurrentLane() {
    return currentLane;
  }

  /**
   * Set the Lane the DriverAgent is currently following.
   *
   * @param lane the Lane the DriverAgent should follow
   */
  @Override
  public void setCurrentLane(Lane lane) {
    currentLane = lane;
    currentlyOccupiedLanes = new HashSet<Lane>(1);
    currentlyOccupiedLanes.add(lane);
  }

  /**
   * Get the lanes the DriverAgent's vehicle currently occupies.
   *
   * @return the lanes the DriverAgent's vehicle currently occupies
   */
  @Override
  public Set<Lane> getCurrentlyOccupiedLanes() {
    return Collections.unmodifiableSet(currentlyOccupiedLanes);
  }

  /**
   * Add a lane that the DriverAgent's vehicle currently occupies.
   *
   * @param lane a lane that the DriverAgent's vehicle currently occupies
   */
  public void addCurrentlyOccupiedLane(Lane lane) {
    currentlyOccupiedLanes.add(lane);
  }


}
