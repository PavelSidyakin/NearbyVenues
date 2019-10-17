package com.nearbyvenues.presentation.find_venues.presenter.recycler

import androidx.paging.PageKeyedDataSource
import com.nearbyvenues.domain.NearbyVenuesSearchInteractor
import com.nearbyvenues.model.Coordinates
import com.nearbyvenues.model.VenueType
import com.nearbyvenues.model.domain.NearVenuesSearchResultCode
import com.nearbyvenues.model.domain.NearbyVenuesSearchResult
import com.nearbyvenues.model.domain.NextPageData
import com.nearbyvenues.model.domain.Venue
import com.nearbyvenues.presentation.find_venues.presenter.NearbyVenuesSearchPresenter
import com.nearbyvenues.utils.logs.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
@FlowPreview
class NearbyVenuesDataSource @ExperimentalCoroutinesApi constructor(
    private val location: Coordinates,
    private val venueTypes: List<VenueType>,
    private val nearbyVenuesSearchInteractor: NearbyVenuesSearchInteractor,
    private val nearbyVenuesSearchPresenter: NearbyVenuesSearchPresenter,
    override val coroutineContext: CoroutineContext,
    private val retryChannel: BroadcastChannel<Any>
    ) : PageKeyedDataSource<NextPageData, Venue>(), CoroutineScope {

    init {
        launch {
            retryChannel.asFlow()
                .collect {
                    retry()
                }
        }
    }

    private var retryRunnable: Runnable? = null

    override fun loadInitial(params: LoadInitialParams<NextPageData>, callback: LoadInitialCallback<NextPageData, Venue>) {
        launch {
            try {

                nearbyVenuesSearchPresenter.onRequestStarted()

                val nearbyVenuesSearchResult: NearbyVenuesSearchResult = nearbyVenuesSearchInteractor.findVenues(location, venueTypes)

                log { i(TAG, "NearbyVenuesDataSource.loadInitial() nearbyVenuesSearchResult=$nearbyVenuesSearchResult") }

                if (nearbyVenuesSearchResult.resultCode == NearVenuesSearchResultCode.OK
                    || nearbyVenuesSearchResult.resultCode == NearVenuesSearchResultCode.PARTIALLY_OK) {
                    nearbyVenuesSearchResult.data?.let { data ->
                        callback.onResult(data.venues, null, nearbyVenuesSearchResult.nextPageData)
                    }
                } else {
                    retryRunnable = Runnable { loadInitial(params, callback) }
                }
                nearbyVenuesSearchPresenter.onResult(nearbyVenuesSearchResult.resultCode)

            } catch (throwable: Throwable) {
                log { w(TAG, "NearbyVenuesDataSource.loadInitial()", throwable) }
                retryRunnable = Runnable { loadInitial(params, callback) }
                nearbyVenuesSearchPresenter.onResult(NearVenuesSearchResultCode.GENERAL_ERROR)
            }
        }
    }

    override fun loadAfter(params: LoadParams<NextPageData>, callback: LoadCallback<NextPageData, Venue>) {
        launch {
            try {

                nearbyVenuesSearchPresenter.onRequestStarted()

                val nearbyVenuesSearchResult: NearbyVenuesSearchResult = nearbyVenuesSearchInteractor.findNextVenues(params.key)
                log { i(TAG, "NearbyVenuesDataSource.loadAfter() searchImagesResult=$nearbyVenuesSearchResult") }

                if (nearbyVenuesSearchResult.resultCode == NearVenuesSearchResultCode.OK
                    || nearbyVenuesSearchResult.resultCode == NearVenuesSearchResultCode.PARTIALLY_OK) {
                    nearbyVenuesSearchResult.data?.let { data ->
                        callback.onResult(data.venues, nearbyVenuesSearchResult.nextPageData)
                    }
                } else {
                    retryRunnable = Runnable { loadAfter(params, callback) }
                }
                nearbyVenuesSearchPresenter.onResult(nearbyVenuesSearchResult.resultCode)

            } catch (throwable: Throwable) {
                log { w(TAG, "NearbyVenuesDataSource.loadAfter()", throwable) }
                retryRunnable = Runnable { loadAfter(params, callback) }
                nearbyVenuesSearchPresenter.onResult(NearVenuesSearchResultCode.GENERAL_ERROR)
            }
        }
    }

    override fun loadBefore(params: LoadParams<NextPageData>, callback: LoadCallback<NextPageData, Venue>) {
    }

    private fun retry() {
        retryRunnable?.run()
    }

    companion object {
        const val TAG = "NearbyVenuesDataSource"
    }
}