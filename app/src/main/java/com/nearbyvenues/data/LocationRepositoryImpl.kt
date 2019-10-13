package com.nearbyvenues.data

import android.location.Location
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

    override suspend fun calcDistanceBetweenCoordinates(point1: Coordinates, point2: Coordinates): Double {
        // TODO: calc with distance matrix api:
        // https://developers.google.com/maps/documentation/distance-matrix/intro#DistanceMatrixRequests
        // Temporary calc

        val results = FloatArray(3)

        Location.distanceBetween(point1.lat, point1.lng, point2.lat, point2.lng, results)

        return results[0].toDouble()
    }

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(applicationProvider.applicationContext)
    }

    override suspend fun getLastLocation(): Coordinates? = suspendCoroutine { continuation ->
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    continuation.resume(Coordinates(location.latitude, location.longitude))
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { continuation.resumeWithException(it) }
    }
}