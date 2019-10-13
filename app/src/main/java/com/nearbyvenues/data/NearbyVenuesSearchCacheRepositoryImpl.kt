package com.nearbyvenues.data

import com.nearbyvenues.domain.data.NearbyVenuesSearchCacheRepository
import com.nearbyvenues.model.Coordinates
import com.nearbyvenues.model.VenueType
import com.nearbyvenues.model.data.NearVenuesSearchRequestResult
import com.nearbyvenues.utils.DispatcherProvider
import com.nearbyvenues.utils.logs.log
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NearbyVenuesSearchCacheRepositoryImpl
    @Inject
    constructor(private val dispatcherProvider: DispatcherProvider)
    : NearbyVenuesSearchCacheRepository {

    private val cacheMap = mutableMapOf<Request, Result>()

    private val mutex = Mutex()

    override suspend fun requestVenues(location: Coordinates, venueType: VenueType): NearVenuesSearchRequestResult? {
        return withContext(dispatcherProvider.io()) {
            mutex.withLock {
                val cacheResult = cacheMap[Request(location, venueType)]
                if (cacheResult != null) {
                    if (System.currentTimeMillis() - cacheResult.timeStamp < CACHE_VALIDITY_PERIOD_MS) {
                        log { i(TAG, "NearbyVenuesSearchCacheRepositoryImpl.requestVenues(). returning cached result") }
                        return@withLock cacheResult.result
                    } else {
                        cacheMap.remove(Request(location, venueType))
                    }
                }
                log { i(TAG, "NearbyVenuesSearchCacheRepositoryImpl.requestVenues(). returning null result") }
                return@withLock null
            }
        }
    }

    override suspend fun putRequestVenuesResult(location: Coordinates, venueType: VenueType, result: NearVenuesSearchRequestResult) {
        withContext(dispatcherProvider.io()) {
            mutex.withLock {
                cacheMap.put(Request(location, venueType), Result(result, System.currentTimeMillis()))
            }
        }
    }

    override suspend fun invalidate() {
        withContext(dispatcherProvider.io()) {
            mutex.withLock {
                cacheMap.clear()
            }
        }
    }

    data class Request (
        val location: Coordinates,
        val venueType: VenueType
    )

    data class Result (
        val result: NearVenuesSearchRequestResult,
        val timeStamp: Long
    )

    companion object {
        private const val TAG = "CacheRepo"
        private const val CACHE_VALIDITY_PERIOD_MS = 1000 * 60 * 60 * 5
    }
}