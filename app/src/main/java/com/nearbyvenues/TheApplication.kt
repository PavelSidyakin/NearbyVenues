package com.nearbyvenues

import android.app.Application
import com.nearbyvenues.di.app.AppComponent
import com.nearbyvenues.di.app.DaggerAppComponent
import com.nearbyvenues.domain.data.ApplicationProvider
import javax.inject.Inject

class TheApplication : Application() {
    @Inject
    lateinit var applicationProvider: ApplicationProvider

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .build()

        appComponent.inject(this)

        applicationProvider.init(this)

    }

    companion object {
        private const val TAG = "TheApplication"

        private lateinit var appComponent: AppComponent

        fun getAppComponent(): AppComponent {
            return appComponent
        }
    }
}