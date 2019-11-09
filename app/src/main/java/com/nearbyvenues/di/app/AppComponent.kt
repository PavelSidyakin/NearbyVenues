package com.nearbyvenues.di.app

import com.nearbyvenues.TheApplication
import com.nearbyvenues.di.location.LocationModule
import com.nearbyvenues.di.screen.SearchVenuesComponent
import com.nearbyvenues.di.venues.VenuesModule
import com.nearbyvenues.presentation.find_venues.presenter.NearbyVenuesSearchPresenter
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {
    fun inject(theApplication: TheApplication)

    fun getSearchVenuesComponent(): SearchVenuesComponent

    interface Builder {
        fun build(): AppComponent
    }
}