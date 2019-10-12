package com.nearbyvenues.data.google_places

import com.nearbyvenues.data.google_places.model.GoogleNearbySearchRequestResult
import com.nearbyvenues.model.Coordinates

interface GoogleNearbySearchDataProvider {

    suspend fun requestVenues(
        location: Coordinates,
        radius: Int,
        type: String
    ): GoogleNearbySearchRequestResult

}