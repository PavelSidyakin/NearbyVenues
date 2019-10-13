package com.nearbyvenues.domain

import com.nearbyvenues.model.Coordinates
import com.nearbyvenues.model.VenueType
import com.nearbyvenues.model.domain.NearVenuesSearchResult

interface NearbyVenuesSearchInteractor {

    suspend fun findVenues(location: Coordinates, venueTypes: List<VenueType>): NearVenuesSearchResult
}