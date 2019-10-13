package com.nearbyvenues.presentation.find_venues.presenter.recycler

import androidx.paging.DataSource
import com.nearbyvenues.domain.NearbyVenuesSearchInteractor
import com.nearbyvenues.model.Coordinates
import com.nearbyvenues.model.VenueType
import com.nearbyvenues.model.domain.NextPageData
import com.nearbyvenues.model.domain.Venue
import com.nearbyvenues.presentation.find_venues.presenter.NearbyVenuesSearchPresenter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlin.coroutines.CoroutineContext

class NearbyVenuesDataSourceFactory @ExperimentalCoroutinesApi constructor(
    private val location: Coordinates,
    private val venueTypes: List<VenueType>,
    private val nearbyVenuesSearchInteractor: NearbyVenuesSearchInteractor,
    private val nearbyVenuesSearchPresenter: NearbyVenuesSearchPresenter,
    private val coroutineContext: CoroutineContext,
    private val retryChannel: BroadcastChannel<Any>

    ) : DataSource.Factory<NextPageData, Venue>()  {

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun create(): DataSource<NextPageData, Venue> {
        return NearbyVenuesDataSource(location,
            venueTypes,
            nearbyVenuesSearchInteractor,
            nearbyVenuesSearchPresenter,
            coroutineContext,
            retryChannel)
    }
}