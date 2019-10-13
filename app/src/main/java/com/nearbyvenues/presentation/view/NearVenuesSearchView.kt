package com.nearbyvenues.presentation.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.nearbyvenues.model.Coordinates
import com.nearbyvenues.model.VenueType

interface NearVenuesSearchView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy ::class)
    fun setCurrentLocation(location: Coordinates)

    @StateStrategyType(AddToEndSingleStrategy ::class)
    fun showPressLocateMeWarning(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showTurnOnLocationDialog()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showGrantPermissionsDialog()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showGrantPermissionInSettingsDialog()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun addChipsForVenues(venues: List<VenueType>)

    @StateStrategyType(AddToEndSingleStrategy ::class)
    fun enableFilterVenueChips(enable: Boolean)

}