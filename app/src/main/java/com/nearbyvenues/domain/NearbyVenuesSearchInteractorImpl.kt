package com.nearbyvenues.domain

import com.nearbyvenues.domain.data.LocationRepository
import com.nearbyvenues.domain.data.NearbyVenuesSearchCacheRepository
import com.nearbyvenues.domain.data.NearbyVenuesSearchRepository
import com.nearbyvenues.model.Coordinates
import com.nearbyvenues.model.VenueType
import com.nearbyvenues.model.data.NearVenuesSearchRequestResult
import com.nearbyvenues.model.data.NearVenuesSearchRequestResultCode
import com.nearbyvenues.model.data.VenueData
import com.nearbyvenues.model.domain.NearVenuesSearchData
import com.nearbyvenues.model.domain.NearbyVenuesSearchResult
import com.nearbyvenues.model.domain.NearVenuesSearchResultCode
import com.nearbyvenues.model.domain.NextPageData
import com.nearbyvenues.model.domain.Venue
import com.nearbyvenues.utils.logs.log
import com.nearbyvenues.utils.pmap
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class NearbyVenuesSearchInteractorImpl

    @Inject
    constructor(
        private val nearbyVenuesSearchRepository: NearbyVenuesSearchRepository,
        private val nearbyVenuesSearchCacheRepository: NearbyVenuesSearchCacheRepository,
        private val locationRepository: LocationRepository
    )
    : NearbyVenuesSearchInteractor {

    override suspend fun findVenues(location: Coordinates, venueTypes: List<VenueType>): NearbyVenuesSearchResult {
        log { i(TAG, "NearbyVenuesSearchInteractorImpl.findVenues(). location = [${location}], venueTypes = [${venueTypes}]") }

        return findNextVenuesImpl(location) { requestVenuesForList(location, venueTypes) }
    }

    override suspend fun findNextVenues(nextPageData: NextPageData): NearbyVenuesSearchResult {
        log { i(TAG, "NearbyVenuesSearchInteractorImpl.findNextVenues(). nextPageData = [${nextPageData}]") }
        return findNextVenuesImpl(nextPageData.location) { requestVenuesForNextPageData(nextPageData) }
    }

    private suspend fun findNextVenuesImpl(location: Coordinates, block: suspend () -> List<NearVenuesSearchRequestResult>): NearbyVenuesSearchResult {
        try {
            val requestVenuesResults = block()

            log { i(TAG, "NearbyVenuesSearchInteractorImpl.findNextVenuesImpl(). requestVenuesResults=$requestVenuesResults") }

            val okCount = requestVenuesResults.count { it.resultCode == NearVenuesSearchRequestResultCode.OK }
            val noNetWorkCount = requestVenuesResults.count { it.resultCode == NearVenuesSearchRequestResultCode.NETWORK_ERROR }

            log { i(TAG, "NearbyVenuesSearchInteractorImpl.findNextVenuesImpl(). okCount=$okCount") }

            if (okCount == 0) {
                if (noNetWorkCount > 0) {
                    return NearbyVenuesSearchResult(NearVenuesSearchResultCode.NO_NETWORK_ERROR, null, null)
                }
                return NearbyVenuesSearchResult(NearVenuesSearchResultCode.GENERAL_ERROR, null, null)
            }

            val totalResult: NearVenuesSearchResultCode = if (okCount > 0 && okCount < requestVenuesResults.size) {
                NearVenuesSearchResultCode.PARTIALLY_OK
            } else {
                NearVenuesSearchResultCode.OK
            }

            val venueList: List<Venue> = requestVenuesResults.filter { it.resultCode == NearVenuesSearchRequestResultCode.OK }
                .filter { it.data != null }
                .map { it.data?.venues ?: listOf() }
                .flatten()
                .distinctBy { venueData -> venueData.id }
                .pmap { convertVenueDataToVenue(location, it) }
                .sortedBy { it.distance }

            val pageTokens: List<String> = requestVenuesResults.mapNotNull { it.nextPageToken }
                .filter { it.isNotEmpty() }

            return NearbyVenuesSearchResult(
                totalResult,
                NearVenuesSearchData(venueList),
                if (pageTokens.isNotEmpty()) NextPageData(location, pageTokens) else null
            )
        } catch (throwable: Throwable) {
            log { i(TAG, "NearbyVenuesSearchInteractorImpl.findNextVenuesImpl()", throwable) }
            return NearbyVenuesSearchResult(NearVenuesSearchResultCode.GENERAL_ERROR, null, null)
        }
    }

    private suspend fun requestVenuesForList(location: Coordinates, venueTypes: List<VenueType>): List<NearVenuesSearchRequestResult> {
        return coroutineScope {
            val requestVenuesResultsMutex = Mutex()
            val requestVenuesResults = mutableListOf<NearVenuesSearchRequestResult>()
            val deferredList = mutableListOf<Deferred<Any>>()
            venueTypes.forEach { venueType ->
                deferredList.add(async {

                    val cacheResult: NearVenuesSearchRequestResult? = nearbyVenuesSearchCacheRepository.requestVenues(location, venueType)

                    log { i(TAG, "NearbyVenuesSearchInteractorImpl.requestVenuesForList(). cacheResult=$cacheResult") }
                    val result: NearVenuesSearchRequestResult?
                    result = cacheResult ?: nearbyVenuesSearchRepository.requestVenues(location, venueType)

                    requestVenuesResultsMutex.withLock {
                        requestVenuesResults.add(result)
                    }

                    if (cacheResult == null && result.resultCode == NearVenuesSearchRequestResultCode.OK) {
                        nearbyVenuesSearchCacheRepository.putRequestVenuesResult(location, venueType, result)
                    }

                })

            }

            deferredList.awaitAll()

            requestVenuesResults
        }
    }

    private suspend fun requestVenuesForNextPageData(nextPageData: NextPageData): List<NearVenuesSearchRequestResult> {
        return coroutineScope {

            val requestVenuesResults = mutableListOf<NearVenuesSearchRequestResult>()
            val deferredList = mutableListOf<Deferred<Any>>()
            nextPageData.nextPageTokens.forEach { nextPageToken ->
                deferredList.add(async {
                    requestVenuesResults.add(nearbyVenuesSearchRepository.requestVenuesNextPage(nextPageToken))
                })
            }

            deferredList.awaitAll()

            requestVenuesResults
        }
    }

    private suspend fun convertVenueDataToVenue(startPoint: Coordinates, venueData: VenueData): Venue {

        return Venue(
            venueData.coordinates,
            locationRepository.calcDistanceBetweenCoordinates(startPoint, venueData.coordinates),
            venueData.id,
            venueData.name,
            venueData.openNow,
            venueData.rating,
            venueData.types
        )
    }

    companion object {
        private const val TAG = "NearbyVenuesSearchInt"

    }
}
