package com.nearbyvenues.domain

import com.nearbyvenues.model.Coordinates

interface LocationInteractor {

    suspend fun getLastLocation(): Coordinates?

}