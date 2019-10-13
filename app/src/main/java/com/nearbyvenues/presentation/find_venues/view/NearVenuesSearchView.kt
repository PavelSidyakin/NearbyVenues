package com.nearbyvenues.presentation.find_venues.view

import androidx.paging.PagedList
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.nearbyvenues.model.Coordinates
import com.nearbyvenues.model.VenueType
import com.nearbyvenues.model.domain.Venue

interface NearVenuesSearchView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy ::class)
    fun setCurrentLocation(location: Coordinates)

    @StateStrategyType(AddToEndSingleStrategy ::class)
    fun showPressLocateMeWarning(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showGrantPermissionInSettingsDialog()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun addChipsForVenues(venues: List<VenueType>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setCheckedChipsForVenues(venues: List<VenueType>)

    @StateStrategyType(AddToEndSingleStrategy ::class)
    fun enableFilterVenueChips(enable: Boolean)

    @StateStrategyType(AddToEndSingleStrategy ::class)
    fun showProgress(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy ::class)
    fun showNoConnectionError(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy ::class)
    fun showGeneralError(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy ::class)
    fun showVenueDetails(venue: Venue)

    @StateStrategyType(AddToEndSingleStrategy ::class)
    fun showCannotLocateError(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun updateVenueList(venues: PagedList<Venue>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun clearList()
}