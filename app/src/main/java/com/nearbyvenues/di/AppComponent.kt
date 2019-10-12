package com.nearbyvenues.di

import com.nearbyvenues.TheApplication
import com.nearbyvenues.presentation.presenter.NearVenuesSearchPresenter
import com.nearbyvenues.presentation.view.NearVenuesSearchActivity
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {
    fun inject(theApplication: TheApplication)
    fun inject(theApplication: NearVenuesSearchActivity)

    fun getNearVenuesSearchPresenter(): NearVenuesSearchPresenter

    interface Builder {
        fun build(): AppComponent
    }
}