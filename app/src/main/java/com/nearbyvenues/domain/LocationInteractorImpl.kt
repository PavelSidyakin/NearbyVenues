package com.nearbyvenues.domain

import android.location.Location
import com.nearbyvenues.domain.data.LocationRepository
import javax.inject.Inject

class LocationInteractorImpl
    @Inject
    constructor(private val locationRepository: LocationRepository)
    : LocationInteractor {

    override suspend fun getLastLocation(): Location {
        return locationRepository.getLastLocation()
    }

}