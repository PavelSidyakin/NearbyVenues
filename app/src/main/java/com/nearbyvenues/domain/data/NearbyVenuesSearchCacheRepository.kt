package com.nearbyvenues.domain.data

import com.nearbyvenues.model.Coordinates
import com.nearbyvenues.model.VenueType
import com.nearbyvenues.model.data.NearVenuesSearchRequestResult

interface NearbyVenuesSearchCacheRepository {

    /**
     * Returns cached result for venues search request.
     *
     * @param location Coordinates
     * @param venueType Venue type
     *
     * @return Request result. Can be null if data for the request is not found in cache.
     *
     */
    suspend fun requestVenues(location: Coordinates, venueType: VenueType): NearVenuesSearchRequestResult?

    /**
     * Puts venues request data and result to cache.
     *
     * @param location Coordinates
     * @param venueType Venue type
     * @return Request result
     *
     */
    suspend fun putRequestVenuesResult(location: Coordinates, venueType: VenueType, result: NearVenuesSearchRequestResult)

    /**
     * Invalidates cache (removes all cached items).
     *
     */
    suspend fun invalidate()

}