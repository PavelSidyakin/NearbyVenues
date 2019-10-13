package com.nearbyvenues.domain

import androidx.core.content.contentValuesOf
import com.nearbyvenues.domain.data.LocationRepository
import com.nearbyvenues.domain.data.NearVenuesSearchRepository
import com.nearbyvenues.model.Coordinates
import com.nearbyvenues.model.VenueType
import com.nearbyvenues.model.data.NearVenuesSearchRequestResult
import com.nearbyvenues.model.data.NearVenuesSearchRequestResultCode
import com.nearbyvenues.model.data.VenueData
import com.nearbyvenues.model.domain.NearVenuesSearchData
import com.nearbyvenues.model.domain.NearVenuesSearchResult
import com.nearbyvenues.model.domain.NearVenuesSearchResultCode
import com.nearbyvenues.model.domain.NextPageData
import com.nearbyvenues.model.domain.Venue
import com.nearbyvenues.utils.pmap
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import javax.inject.Inject

class NearbyVenuesSearchInteractorImpl

    @Inject
    constructor(
        private val nearVenuesSearchRepository: NearVenuesSearchRepository,
        private val locationRepository: LocationRepository
    )
    : NearbyVenuesSearchInteractor {

    override suspend fun findVenues(location: Coordinates, venueTypes: List<VenueType>): NearVenuesSearchResult {

        val requestVenuesResults = requestVenuesForList(location, venueTypes)

        val okCount = requestVenuesResults.count { it.resultCode == NearVenuesSearchRequestResultCode.OK }

        if (okCount == 0) {
            return NearVenuesSearchResult(NearVenuesSearchResultCode.GENERAL_ERROR, null, null)
        }

        val totalResult: NearVenuesSearchResultCode = if (okCount > 0 && okCount < requestVenuesResults.size) {
            NearVenuesSearchResultCode.PARTIALLY_OK
        } else {
            NearVenuesSearchResultCode.OK
        }

        val venueList: List<Venue> =  requestVenuesResults.filter { it.resultCode == NearVenuesSearchRequestResultCode.OK }
            .filter { it.data != null }
            .map { it.data?.venues?: listOf() }
            .flatten()
            .distinctBy { venueData -> venueData.id }
            .pmap { convertVenueDataToVenue(location, it) }
            .sortedBy { it.distance }

        val pageTokens: List<String> = requestVenuesResults.mapNotNull { it.nextPageToken }
            .filter { it.isNotEmpty() }

        return NearVenuesSearchResult(totalResult,
            NearVenuesSearchData(venueList),
            if (pageTokens.isNotEmpty()) NextPageData(pageTokens) else null)
    }

    private suspend fun requestVenuesForList(location: Coordinates, venueTypes: List<VenueType>): List<NearVenuesSearchRequestResult> {
        val requestVenuesResults = mutableListOf<NearVenuesSearchRequestResult>()
        val deferredList = mutableListOf<Deferred<Any>>()
        venueTypes.forEach { venueType ->
            deferredList.add(GlobalScope.async {
                requestVenuesResults.add(nearVenuesSearchRepository.requestVenues(location, venueType))
            })

        }
        awaitAll(*deferredList.toTypedArray())

        return requestVenuesResults
    }

    private suspend fun convertVenueDataToVenue(startPoint: Coordinates, venueData: VenueData): Venue {

        return Venue(venueData.coordinates,
            locationRepository.calcDistanceBetweenCoordinates(startPoint, venueData.coordinates),
            venueData.id,
            venueData.name,
            venueData.openNow,
            venueData.rating,
            venueData.types)
    }
}