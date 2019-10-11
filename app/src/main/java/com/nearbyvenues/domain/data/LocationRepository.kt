package com.nearbyvenues.domain.data

import android.location.Location

interface LocationRepository {

    suspend fun getLastLocation(): Location
}