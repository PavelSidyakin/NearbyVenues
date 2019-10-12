package com.nearbyvenues.domain.data

import com.nearbyvenues.model.Coordinates
import com.nearbyvenues.model.data.NearVenuesSearchRequestResult
import com.nearbyvenues.model.domain.VenueType

interface NearVenuesSearchRepository {

    suspend fun requestVenues(location: Coordinates, radius: Int, venueType: VenueType): NearVenuesSearchRequestResult

}