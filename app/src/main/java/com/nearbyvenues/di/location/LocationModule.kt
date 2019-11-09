package com.nearbyvenues.di.location

import com.nearbyvenues.data.LocationRepositoryImpl
import com.nearbyvenues.di.PerFeature
import com.nearbyvenues.domain.LocationInteractor
import com.nearbyvenues.domain.LocationInteractorImpl
import com.nearbyvenues.domain.data.LocationRepository
import dagger.Binds
import dagger.Module

@Module
abstract class LocationModule {

    @PerFeature
    @Binds
    abstract fun provideLocationRepository(locationRepository: LocationRepositoryImpl): LocationRepository

    @PerFeature
    @Binds
    abstract fun provideLocationInteractor(locationInteractor: LocationInteractorImpl): LocationInteractor

}