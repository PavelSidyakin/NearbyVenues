package com.nearbyvenues.domain.data

import com.nearbyvenues.model.Coordinates

interface LocationRepository {

    suspend fun getLastLocation(): Coordinates?

    suspend fun calcDistanceBetweenCoordinates(point1: Coordinates, point2: Coordinates): Double
}