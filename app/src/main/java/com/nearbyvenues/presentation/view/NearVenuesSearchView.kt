package com.nearbyvenues.presentation.view

import android.location.Location
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

interface NearVenuesSearchView : MvpView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun setCurrentLocation(location: Location)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showPressLocateMeWarning(show: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showTurnOnLocationDialog()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showGrantPermissionsDialog()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showGrantPermissionInSettingsDialog()

}