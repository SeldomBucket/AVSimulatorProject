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
package aim4.map.aim;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import aim4.map.Road;
import aim4.map.SpawnPoint;
import aim4.map.lane.Lane;
import aim4.vehicle.VehicleSpec;

/**
 * A spawn point for AIM simulations.
 */
public class AIMSpawnPoint extends SpawnPoint {

    /////////////////////////////////
    // NESTED CLASSES
    /////////////////////////////////

    /** The specification of a spawn */
    public static class AIMSpawnSpec extends SpawnSpec {
        /** The destination road */
        Road destinationRoad;

        /**
         * Create a spawn specification.
         *
         * @param spawnTime       the spawn time
         * @param vehicleSpec     the vehicle specification
         * @param destinationRoad the destination road
         */
        public AIMSpawnSpec(double spawnTime, VehicleSpec vehicleSpec, Road destinationRoad) {
            super(spawnTime, vehicleSpec);
            this.destinationRoad = destinationRoad;
        }

        /**
         * Get the destination road.
         *
         * @return the destination road
         */
        public Road getDestinationRoad() {
            return destinationRoad;
        }
    }

    /**
     * The interface of the spawn specification generator.
     */
    public static interface AIMSpawnSpecGenerator {
        /**
         * Advance the time step.
         *
         * @param spawnPoint  the spawn point
         * @param timeStep    the time step
         * @return the list of spawn spec generated in this time step.
         */
        List<AIMSpawnSpec> act(AIMSpawnPoint spawnPoint, double timeStep);
    }

    /////////////////////////////////
    // PRIVATE FIELDS
    /////////////////////////////////

    /** The vehicle spec chooser */
    private AIMSpawnSpecGenerator vehicleSpecChooser;

    /////////////////////////////////
    // CONSTRUCTORS
    /////////////////////////////////

    /**
     * Create a spawn point.
     *
     * @param currentTime         the current time
     * @param pos                 the initial position
     * @param heading             the initial heading
     * @param steeringAngle       the initial steering angle
     * @param acceleration        the initial acceleration
     * @param lane                the lane
     * @param noVehicleZone       the no vehicle zone
     * @param vehicleSpecChooser  the vehicle spec chooser
     */
    public AIMSpawnPoint(double currentTime,
                         Point2D pos,
                         double heading,
                         double steeringAngle,
                         double acceleration,
                         Lane lane,
                         Rectangle2D noVehicleZone,
                         AIMSpawnSpecGenerator vehicleSpecChooser) {
        super(currentTime, pos, heading, steeringAngle,acceleration, lane, noVehicleZone);
        this.vehicleSpecChooser = vehicleSpecChooser;
    }

    /**
     * Create a spawn point.
     *
     * @param currentTime         the current time
     * @param pos                 the initial position
     * @param heading             the initial heading
     * @param steeringAngle       the initial steering angle
     * @param acceleration        the initial acceleration
     * @param lane                the lane
     * @param noVehicleZone       the no vehicle zone
     */
    public AIMSpawnPoint(double currentTime,
                         Point2D pos,
                         double heading,
                         double steeringAngle,
                         double acceleration,
                         Lane lane,
                         Rectangle2D noVehicleZone) {
        super(currentTime, pos, heading, steeringAngle, acceleration, lane, noVehicleZone);
        this.vehicleSpecChooser = null;
    }

    /////////////////////////////////
    // PUBLIC METHODS
    /////////////////////////////////

    /**
     * Advance the time step.
     *
     * @param timeStep  the time step
     * @return The list of spawn spec generated in this time step
     */
    public List<AIMSpawnSpec> act(double timeStep) {
        assert vehicleSpecChooser != null;
        List<AIMSpawnSpec> spawnSpecs = vehicleSpecChooser.act(this, timeStep);
        currentTime += timeStep;
        return spawnSpecs;
    }

    /////////////////////////////////
    // PUBLIC METHODS
    /////////////////////////////////

    /**
     * Set the vehicle spec chooser.
     *
     * @param vehicleSpecChooser the vehicle spec chooser
     */
    public void setVehicleSpecChooser(AIMSpawnSpecGenerator vehicleSpecChooser) {
        // assert this.vehicleSpecChooser == null;  // TODO think whether it is okay
        this.vehicleSpecChooser = vehicleSpecChooser;
    }

}
