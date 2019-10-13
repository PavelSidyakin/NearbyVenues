package com.nearbyvenues.di

import com.nearbyvenues.TheApplication
import com.nearbyvenues.presentation.find_venues.presenter.NearbyVenuesSearchPresenter
import com.nearbyvenues.presentation.find_venues.view.NearVenuesSearchActivity
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {
    fun inject(theApplication: TheApplication)
    fun inject(theApplication: NearVenuesSearchActivity)

    fun getNearVenuesSearchPresenter(): NearbyVenuesSearchPresenter

    interface Builder {
        fun build(): AppComponent
    }
}