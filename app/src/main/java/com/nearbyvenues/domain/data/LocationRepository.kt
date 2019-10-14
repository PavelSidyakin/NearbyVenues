package com.nearbyvenues.domain.data

import com.nearbyvenues.model.Coordinates

interface LocationRepository {

    /**
     * Returns last device location.
     *
     * @return Last device coordinates.
     * Can be null. In this case can be used [waitForLocation].
     *
     */
    suspend fun getLastLocation(): Coordinates?

    /**
     * Subscribes for location availability and returns coordinates.
     *
     * @param timeout Timeout for waiting location.
     *
     * @return Device coordinates. Can be null if timeout occurred or error happened.
     *
     */
    suspend fun waitForLocation(timeout: Long): Coordinates?

    /**
     * Returns distance in meters between two points.
     * Current implementation returns straight distance between the points.
     *
     * @param point1 First point
     * @param point2 Second point
     *
     * @return Distance in meters.
     *
     */
    suspend fun calcDistanceBetweenCoordinates(point1: Coordinates, point2: Coordinates): Double
}