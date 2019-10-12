package com.nearbyvenues.presentation.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.nearbyvenues.model.Coordinates
import com.nearbyvenues.model.domain.VenueType

interface NearVenuesSearchView : MvpView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun setCurrentLocation(location: Coordinates)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showPressLocateMeWarning(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showTurnOnLocationDialog()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showGrantPermissionsDialog()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showGrantPermissionInSettingsDialog()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun addChipForVenueType(venue: VenueType)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun enableFilterVenueChips(enable: Boolean)

}