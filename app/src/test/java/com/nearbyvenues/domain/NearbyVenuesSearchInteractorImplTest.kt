package com.nearbyvenues.domain

import com.nearbyvenues.domain.data.LocationRepository
import com.nearbyvenues.domain.data.NearbyVenuesSearchCacheRepository
import com.nearbyvenues.domain.data.NearbyVenuesSearchRepository
import com.nearbyvenues.model.Coordinates
import com.nearbyvenues.model.VenueType
import com.nearbyvenues.model.data.NearVenuesSearchRequestData
import com.nearbyvenues.model.data.NearVenuesSearchRequestResult
import com.nearbyvenues.model.data.NearVenuesSearchRequestResultCode
import com.nearbyvenues.model.data.VenueData
import com.nearbyvenues.model.domain.NearVenuesSearchResultCode
import com.nearbyvenues.utils.logs.XLog
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class `NearbyVenuesSearchInteractorImpl tests` {

    @Mock
    private lateinit var nearbyVenuesSearchRepository: NearbyVenuesSearchRepository

    @Mock
    private lateinit var nearbyVenuesSearchCacheRepository: NearbyVenuesSearchCacheRepository

    @Mock
    private lateinit var locationRepository: LocationRepository


    @InjectMocks
    private lateinit var interactor: NearbyVenuesSearchInteractorImpl


    @BeforeEach
    fun beforeEachTest() {
        XLog.enableLogging(false)

        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun `when repository returns NETWORK_ERROR error interactor should return NO_NETWORK_ERROR`() {
        runBlocking {

            whenever(nearbyVenuesSearchRepository.requestVenues(any(), any()))
                .thenReturn(NearVenuesSearchRequestResult(NearVenuesSearchRequestResultCode.NETWORK_ERROR, null, null))

             val result = interactor.findVenues(COORDINATES, VENUE_LIST)

             assert(result.resultCode == NearVenuesSearchResultCode.NO_NETWORK_ERROR)


        }
    }

    @Test
    fun `when repository returns GENERAL_ERROR error interactor should return GENERAL_ERROR`() {
        runBlocking {

            whenever(nearbyVenuesSearchRepository.requestVenues(any(), any()))
                .thenReturn(NearVenuesSearchRequestResult(NearVenuesSearchRequestResultCode.GENERAL_ERROR, null, null))

            val result = interactor.findVenues(COORDINATES, VENUE_LIST)

            assert(result.resultCode == NearVenuesSearchResultCode.GENERAL_ERROR)


        }
    }

    @Nested
    inner class `When success repository result` {

        private val venue0Coordinates = Coordinates(0.1, 0.1)
        private val venue0Id = "venue0Id"
        private val venue0Name = "venue0Name"
        private val venue0OpenNow = true
        private val venue0Rating = 0.1f
        private val venue0Types = listOf(VenueType.RESTAURANT, VenueType.BAR)

        private val venue1Coordinates = Coordinates(1.1, 1.1)
        private val venue1Id = "venue1Id"
        private val venue1Name = "venue1Name"
        private val venue1OpenNow = true
        private val venue1Rating = 1.1f
        private val venue1Types = listOf(VenueType.CAFE)

        private val venue2Coordinates = Coordinates(2.2, 2.2)
        private val venue2Id = "venue2Id"
        private val venue2Name = "venue2Name"
        private val venue2OpenNow = true
        private val venue2Rating = 2.2f
        private val venue2Types = listOf(VenueType.BAR)

        private val nextPageToken0 = "rjvriljfvjeri"
        private val nextPageToken1 = "werwvkjviktrji"
        
        @BeforeEach
        fun beforeEachTest() {
            runBlocking {

                whenever(nearbyVenuesSearchCacheRepository.requestVenues(any(), any())).thenReturn(null)

                doReturn(nearbyVenuesSearchCacheRepository.putRequestVenuesResult(any(), any(), any()))
                    .whenever(nearbyVenuesSearchCacheRepository).putRequestVenuesResult(any(), any(), any())

                whenever(locationRepository.calcDistanceBetweenCoordinates(any(), any())).thenReturn(22.11)

                whenever(nearbyVenuesSearchRepository.requestVenues(any(), any()))
                    .thenReturn(NearVenuesSearchRequestResult(
                        NearVenuesSearchRequestResultCode.OK, 
                        NearVenuesSearchRequestData(
                            listOf(VenueData(venue0Coordinates, venue0Id, venue0Name, venue0OpenNow, venue0Rating, venue0Types),
                                VenueData(venue1Coordinates, venue1Id, venue1Name, venue1OpenNow, venue1Rating, venue1Types))), 
                        nextPageToken0))
                    .thenReturn(NearVenuesSearchRequestResult(
                        NearVenuesSearchRequestResultCode.OK,
                        NearVenuesSearchRequestData(
                            listOf(VenueData(venue2Coordinates, venue2Id, venue2Name, venue2OpenNow, venue2Rating, venue2Types))),
                        nextPageToken1))

            }

        }
        
        @Test
        fun `interactor should return corresponding result`() {
            runBlocking {
                val result = interactor.findVenues(COORDINATES, listOf(VenueType.CAFE, VenueType.BAR, VenueType.RESTAURANT))

                assert(result.resultCode == NearVenuesSearchResultCode.OK )
                assert(result.nextPageData!!.nextPageTokens[0] == nextPageToken0)
                assert(result.nextPageData!!.nextPageTokens[1] == nextPageToken1)

                with(result.data!!.venues[0]) {
                    assert(coordinates == venue0Coordinates)
                    assert(name == venue0Name)                    
                    assert(openNow == venue0OpenNow)
                    assert(rating == venue0Rating)
                    assert(types == venue0Types)
                }

                with(result.data!!.venues[1]) {
                    assert(coordinates == venue1Coordinates)
                    assert(name == venue1Name)
                    assert(openNow == venue1OpenNow)
                    assert(rating == venue1Rating)
                    assert(types == venue1Types)
                }

            }
            
        }

    }

    companion object {
        private val COORDINATES = Coordinates(1.0, 2.0)
        private val VENUE_LIST = listOf(VenueType.CAFE, VenueType.RESTAURANT)
        
    }

}
