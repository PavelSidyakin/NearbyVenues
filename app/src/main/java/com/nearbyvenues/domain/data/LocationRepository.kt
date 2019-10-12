package com.nearbyvenues.domain.data

import com.nearbyvenues.model.Coordinates

interface LocationRepository {

    suspend fun getLastLocation(): Coordinates?
}