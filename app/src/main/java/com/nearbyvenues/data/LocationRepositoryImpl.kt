package com.nearbyvenues.data

import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.nearbyvenues.domain.data.ApplicationProvider
import com.nearbyvenues.domain.data.LocationRepository
import com.nearbyvenues.model.Coordinates
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull

class LocationRepositoryImpl

    @Inject
    constructor(
        private val applicationProvider: ApplicationProvider
    ) : LocationRepository {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(applicationProvider.applicationContext)
    }

    override suspend fun waitForLocation(timeout: Long): Coordinates? = withTimeoutOrNull(timeout) {
        subscribeForLocation(timeout)
    }

    private suspend fun subscribeForLocation(maxWaitTime: Long): Coordinates? = suspendCancellableCoroutine { continuation ->

        val locationRequest = LocationRequest()
            .setPriority(LocationRequest.PRIORITY_LOW_POWER)
            .setMaxWaitTime(maxWaitTime)

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                if (result.lastLocation != null) {
                    continuation.resume(Coordinates(result.lastLocation.latitude, result.lastLocation.longitude))
                } else {
                    continuation.resume(null)
                }
                fusedLocationClient.removeLocationUpdates(this)
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)

        continuation.invokeOnCancellation { fusedLocationClient.removeLocationUpdates(locationCallback) }
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

    override suspend fun calcDistanceBetweenCoordinates(point1: Coordinates, point2: Coordinates): Double {
        // TODO: calc with distance matrix api:
        // https://developers.google.com/maps/documentation/distance-matrix/intro#DistanceMatrixRequests
        // Temporary calculate straight distance between points

        val results = FloatArray(3)

        Location.distanceBetween(point1.lat, point1.lng, point2.lat, point2.lng, results)

        return results[0].toDouble()
    }

}
