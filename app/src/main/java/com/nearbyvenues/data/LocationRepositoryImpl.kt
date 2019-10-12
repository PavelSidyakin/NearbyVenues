package com.nearbyvenues.data

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.nearbyvenues.domain.data.ApplicationProvider
import com.nearbyvenues.domain.data.LocationRepository
import com.nearbyvenues.model.Coordinates
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LocationRepositoryImpl

    @Inject
    constructor(
        private val applicationProvider: ApplicationProvider
    ) : LocationRepository {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(applicationProvider.applicationContext)
    }

    override suspend fun getLastLocation(): Coordinates? = suspendCoroutine { continuation ->
        fusedLocationClient.lastLocation.addOnSuccessListener { continuation.resume(Coordinates(it.latitude, it.longitude)) }
            .addOnFailureListener { continuation.resumeWithException(it) }
    }
}