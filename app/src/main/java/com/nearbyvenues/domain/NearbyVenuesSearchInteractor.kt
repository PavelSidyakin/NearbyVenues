package com.nearbyvenues.domain

import com.nearbyvenues.model.Coordinates
import com.nearbyvenues.model.VenueType
import com.nearbyvenues.model.domain.NearbyVenuesSearchResult
import com.nearbyvenues.model.domain.NextPageData

interface NearbyVenuesSearchInteractor {

    /**
     * Finds venues of given venueTypes near given location.
     *
     * @param location coordinates
     * @param venueTypes Venue types to search
     *
     * @return result of find.
     * [NearbyVenuesSearchResult.nextPageData] can be used for [findNextVenues].
     *
     */
    suspend fun findVenues(location: Coordinates, venueTypes: List<VenueType>): NearbyVenuesSearchResult

    /**
     * Finds next venues for previous search result.
     * [NextPageData] can be obtained from [NearbyVenuesSearchResult.nextPageData],
     * returned by [findVenues] or [findNextVenues].
     *
     * @param nextPageData Next page data
     *
     * @return result of find.
     * [NearbyVenuesSearchResult.nextPageData] can be used for [findNextVenues].
     *
     */
    suspend fun findNextVenues(nextPageData: NextPageData): NearbyVenuesSearchResult

}