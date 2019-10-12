package com.nearbyvenues.domain

import android.location.Location

interface LocationInteractor {

    suspend fun getLastLocation(): Location

}