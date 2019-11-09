package com.nearbyvenues.di.venues

import com.nearbyvenues.data.NearbyVenuesSearchCacheRepositoryImpl
import com.nearbyvenues.data.NearbyVenuesSearchRepositoryImpl
import com.nearbyvenues.data.google_places.GoogleNearbySearchDataProvider
import com.nearbyvenues.data.google_places.GoogleNearbySearchDataProviderImpl
import com.nearbyvenues.di.PerFeature
import com.nearbyvenues.domain.NearbyVenuesSearchInteractor
import com.nearbyvenues.domain.NearbyVenuesSearchInteractorImpl
import com.nearbyvenues.domain.data.NearbyVenuesSearchCacheRepository
import com.nearbyvenues.domain.data.NearbyVenuesSearchRepository
import dagger.Binds
import dagger.Module

@Module
abstract class VenuesModule {

    @PerFeature
    @Binds
    abstract fun provideGoogleNearbySearchDataProvider(googleNearbySearchDataProviderImpl: GoogleNearbySearchDataProviderImpl): GoogleNearbySearchDataProvider

    @PerFeature
    @Binds
    abstract fun provideNearVenuesSearchRepository(nearVenuesSearchRepositoryImpl: NearbyVenuesSearchRepositoryImpl): NearbyVenuesSearchRepository

    @PerFeature
    @Binds
    abstract fun provideNearbyVenuesSearchInteractor(nearbyVenuesSearchInteractorImpl: NearbyVenuesSearchInteractorImpl): NearbyVenuesSearchInteractor

    @PerFeature
    @Binds
    abstract fun provideNearbyVenuesSearchCacheRepository(nearbyVenuesSearchCacheRepository: NearbyVenuesSearchCacheRepositoryImpl): NearbyVenuesSearchCacheRepository

}