package com.nearbyvenues.di.screen

import com.nearbyvenues.di.PerFeature
import com.nearbyvenues.di.location.LocationModule
import com.nearbyvenues.di.venues.VenuesModule
import com.nearbyvenues.presentation.find_venues.presenter.NearbyVenuesSearchPresenter
import com.nearbyvenues.presentation.find_venues.view.NearVenuesSearchActivity
import dagger.Subcomponent

@Subcomponent(modules = [LocationModule::class, VenuesModule::class])
@PerFeature
interface SearchVenuesComponent {
    fun getNearVenuesSearchPresenter(): NearbyVenuesSearchPresenter

    fun inject(nearVenuesSearchActivity: NearVenuesSearchActivity)
}