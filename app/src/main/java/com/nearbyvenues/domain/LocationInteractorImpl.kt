package com.nearbyvenues.domain

import com.nearbyvenues.domain.data.LocationRepository
import com.nearbyvenues.model.Coordinates
import javax.inject.Inject

class LocationInteractorImpl
    @Inject
    constructor(private val locationRepository: LocationRepository)
    : LocationInteractor {

    override suspend fun getLastLocation(): Coordinates? {
        return locationRepository.getLastLocation()
    }

}