package com.nearbyvenues.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.nearbyvenues.domain.LocationInteractor
import com.nearbyvenues.model.domain.VenueType
import com.nearbyvenues.presentation.view.NearVenuesSearchView
import com.nearbyvenues.utils.DispatcherProvider
import com.nearbyvenues.utils.logs.log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import permissions.dispatcher.PermissionRequest
import javax.inject.Inject

@InjectViewState
class NearVenuesSearchPresenter

    @Inject
    constructor(
        private val locationInteractor: LocationInteractor,
        private val dispatcherProvider: DispatcherProvider
    ) : MvpPresenter<NearVenuesSearchView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        VenueType.values().forEach { venueType ->
            viewState.addChipForVenueType(venueType)
        }

        viewState.enableFilterVenueChips(false)
    }

    fun onLocateMePressed() {
        GlobalScope.launch(dispatcherProvider.main()) {
            val location = locationInteractor.getLastLocation()

            if (location != null) {

                viewState.setCurrentLocation(location)
                viewState.showPressLocateMeWarning(false)
                viewState.enableFilterVenueChips(true)

            } else {
                // Show location error
            }

        }
    }

    fun onVenueFilterChanged(currentVenues: List<VenueType>) {
        log { i(TAG, "onVenueFilterChanged($currentVenues)") }

    }

    fun onShowRationaleForLocationPermission(request: PermissionRequest) {
        request.proceed()
    }

    fun onLocationPermissionsDenied() {

    }

    fun onLocationPermissionsNeverAsAgain() {
        viewState.showGrantPermissionInSettingsDialog()
    }

    companion object {
        private const val TAG = "NearVenuesSearch"
    }
}
