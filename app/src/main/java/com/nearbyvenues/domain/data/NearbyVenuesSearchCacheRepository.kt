package com.nearbyvenues.domain.data

import com.nearbyvenues.model.Coordinates
import com.nearbyvenues.model.VenueType
import com.nearbyvenues.model.data.NearVenuesSearchRequestResult

interface NearbyVenuesSearchCacheRepository {

    suspend fun requestVenues(location: Coordinates, venueType: VenueType): NearVenuesSearchRequestResult?

    suspend fun putRequestVenuesResult(location: Coordinates, venueType: VenueType, result: NearVenuesSearchRequestResult)

    suspend fun invalidate()

}