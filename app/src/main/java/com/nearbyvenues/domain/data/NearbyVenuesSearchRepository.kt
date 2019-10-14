package com.nearbyvenues.domain.data

import com.nearbyvenues.model.Coordinates
import com.nearbyvenues.model.data.NearVenuesSearchRequestResult
import com.nearbyvenues.model.VenueType

interface NearbyVenuesSearchRepository {

    /**
     * Requests venues of given venue type near given coordinates.
     *
     * @param location Coordinates
     * @param venueType Venue type
     *
     * @return Request result.
     * [NearVenuesSearchRequestResult.nextPageToken] can be used for [requestVenuesNextPage]
     *
     */
    suspend fun requestVenues(location: Coordinates, venueType: VenueType): NearVenuesSearchRequestResult

    /**
     * Requests next venues for previous search result.
     * Next page token can be obtained from [NearVenuesSearchRequestResult.nextPageToken],
     * returned by [requestVenues] or [requestVenuesNextPage]
     *
     * @param pageToken Next page token.
     *
     * @return Request result.
     * [NearVenuesSearchRequestResult.nextPageToken] can be used for [requestVenuesNextPage]
     *
     */
    suspend fun requestVenuesNextPage(pageToken: String): NearVenuesSearchRequestResult

}