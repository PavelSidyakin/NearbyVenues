package com.nearbyvenues.di.app

import com.nearbyvenues.data.ApplicationProviderImpl
import com.nearbyvenues.domain.data.ApplicationProvider
import com.nearbyvenues.utils.DispatcherProvider
import com.nearbyvenues.utils.DispatcherProviderImpl
import com.nearbyvenues.utils.NetworkUtils
import com.nearbyvenues.utils.NetworkUtilsImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class AppModule {

    @Singleton
    @Binds
    abstract fun provideNetworkUtils(networkUtils: NetworkUtilsImpl): NetworkUtils

    @Singleton
    @Binds
    abstract fun provideApplicationProvider(applicationProvider: ApplicationProviderImpl): ApplicationProvider

    @Singleton
    @Binds
    abstract fun provideDispatcherProvider(dispatcherProvider: DispatcherProviderImpl): DispatcherProvider

}