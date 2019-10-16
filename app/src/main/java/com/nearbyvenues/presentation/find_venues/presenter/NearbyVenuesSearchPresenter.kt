package com.nearbyvenues.presentation.find_venues.presenter

import androidx.paging.PagedList
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.nearbyvenues.domain.LocationInteractor
import com.nearbyvenues.domain.NearbyVenuesSearchInteractor
import com.nearbyvenues.model.Coordinates
import com.nearbyvenues.model.VenueType
import com.nearbyvenues.model.domain.NearVenuesSearchResultCode
import com.nearbyvenues.model.domain.Venue
import com.nearbyvenues.presentation.find_venues.presenter.recycler.NearbyVenuesDataSourceFactory
import com.nearbyvenues.presentation.find_venues.view.NearVenuesSearchView
import com.nearbyvenues.utils.DispatcherProvider
import com.nearbyvenues.utils.logs.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.launch
import permissions.dispatcher.PermissionRequest
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@FlowPreview
@ExperimentalCoroutinesApi
@InjectViewState
class NearbyVenuesSearchPresenter

    @Inject
    constructor(
        private val locationInteractor: LocationInteractor,
        private val nearbyVenuesSearchInteractor: NearbyVenuesSearchInteractor,
        private val dispatcherProvider: DispatcherProvider
    ) : MvpPresenter<NearVenuesSearchView>(), CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext get() = job + dispatcherProvider.main()

    private var retryChannel = BroadcastChannel<Any>(1)

    private val pageListConfig by lazy { PagedList.Config.Builder()
        .setPageSize(DEFAULT_PAGE_SIZE)
        .setInitialLoadSizeHint(DEFAULT_PAGE_SIZE * DEFAULT_INITIAL_PAGE_SIZE_FACTOR)
        .setEnablePlaceholders(false)
        .build()
    }

    private var currentLocation: Coordinates? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.addChipsForVenues(VenueType.values().asList())

        viewState.enableFilterVenueChips(false)
    }

    fun onLocateMePressed() {
        GlobalScope.launch(dispatcherProvider.main()) {
            viewState.showCannotLocateError(false)
            viewState.showWaitingForLocationProgress(true)
            viewState.enableLocateMeButton(false)

            currentLocation = locationInteractor.requestLocation(WAIT_LOCATION_TIMEOUT_MS)

            viewState.showWaitingForLocationProgress(false)

            if (currentLocation != null) {

                currentLocation?.let { viewState.setCurrentLocation(it) }
                viewState.showPressLocateMeWarning(false)
                viewState.enableFilterVenueChips(true)
            } else {
                viewState.showCannotLocateError(true)
                viewState.showPressLocateMeWarning(true)
                viewState.enableFilterVenueChips(false)
            }

            viewState.enableLocateMeButton(true)
        }
    }

    private fun buildDataSource(location: Coordinates, venueTypes: List<VenueType>): PagedList<Venue> {


        val factory = NearbyVenuesDataSourceFactory(location, venueTypes, nearbyVenuesSearchInteractor,this, coroutineContext, retryChannel)

        return PagedList.Builder(factory.create(), pageListConfig)
            .setNotifyExecutor { GlobalScope.launch(dispatcherProvider.main()) { it.run() } }
            .setFetchExecutor { GlobalScope.launch(dispatcherProvider.io()) { it.run() } }
            .build()
    }


    fun onVenueFilterChanged(currentVenues: List<VenueType>) {
        log { i(TAG, "onVenueFilterChanged($currentVenues)") }

        viewState.setCheckedChipsForVenues(currentVenues)

        if (currentVenues.isNotEmpty()) {

            GlobalScope.launch {

                currentLocation?.let { currentLocation ->
                    reCreateRetryChannel()
                    viewState.updateVenueList(buildDataSource(currentLocation, currentVenues))
                }

            }
        } else {
            viewState.clearList()
        }

    }

    private fun reCreateRetryChannel() {
        retryChannel.cancel()
        retryChannel = BroadcastChannel(1)
    }

    fun onShowRationaleForLocationPermission(request: PermissionRequest) {
        request.proceed()
    }

    fun onLocationPermissionsNeverAsAgain() {
        viewState.showGrantPermissionInSettingsDialog()
    }

    fun onVenueClicked(item: Venue) {
        viewState.showVenueDetails(item)
    }

    fun onErrorClicked() {
        GlobalScope.launch(coroutineContext) {
            retryChannel.send(Any())
        }
    }

    // Data source callbacks

    fun onRequestStarted() {
        viewState.showDownloadingItemsProgress(true)
    }

    fun onResult(nearVenuesSearchResultCode: NearVenuesSearchResultCode) {
        log { i(TAG, "NearbyVenuesSearchPresenter.onResult(). nearVenuesSearchResultCode = [${nearVenuesSearchResultCode}]") }
        viewState.showDownloadingItemsProgress(false)

        when(nearVenuesSearchResultCode) {
            NearVenuesSearchResultCode.OK -> hideVenueListErrors()
            NearVenuesSearchResultCode.PARTIALLY_OK -> hideVenueListErrors()
            NearVenuesSearchResultCode.NO_NETWORK_ERROR -> viewState.showNoConnectionError(true)
            NearVenuesSearchResultCode.GENERAL_ERROR -> viewState.showGeneralError(true)
        }
    }


    // ^ Data source callbacks

    private fun hideVenueListErrors() {
        viewState.showGeneralError(false)
        viewState.showNoConnectionError(false)
    }

    override fun onDestroy() {
        super.onDestroy()

        retryChannel.cancel()
    }

    companion object {
        private const val TAG = "NearVenuesSearch"

        const val WAIT_LOCATION_TIMEOUT_MS: Long = 1000 * 60

        const val DEFAULT_PAGE_SIZE = 10
        const val DEFAULT_INITIAL_PAGE_SIZE_FACTOR = 1

    }
}
