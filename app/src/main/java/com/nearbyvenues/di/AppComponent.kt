package com.nearbyvenues.di

import com.nearbyvenues.TheApplication
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {
    fun inject(theApplication: TheApplication)

    interface Builder {
        fun build(): AppComponent
    }
}