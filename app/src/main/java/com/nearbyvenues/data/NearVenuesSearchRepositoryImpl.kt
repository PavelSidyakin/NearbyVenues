package com.nearbyvenues.data

import com.nearbyvenues.data.google_places.GoogleNearbySearchDataProvider
import com.nearbyvenues.data.google_places.model.GoogleNearbySearchRequestData
import com.nearbyvenues.data.google_places.model.GoogleNearbySearchRequestResult
import com.nearbyvenues.data.google_places.model.GoogleNearbySearchRequestResultCode
import com.nearbyvenues.data.google_places.model.json.Result
import com.nearbyvenues.domain.data.NearVenuesSearchRepository
import com.nearbyvenues.model.Coordinates
import com.nearbyvenues.model.data.NearVenuesSearchRequestData
import com.nearbyvenues.model.data.NearVenuesSearchRequestResult
import com.nearbyvenues.model.data.NearVenuesSearchRequestResultCode
import com.nearbyvenues.model.data.VenueData
import com.nearbyvenues.model.domain.VenueType
import com.nearbyvenues.utils.logs.log
import com.uchuhimo.collections.biMapOf
import javax.inject.Inject

class NearVenuesSearchRepositoryImpl
    @Inject
    constructor(private val googleNearbySearchDataProvider: GoogleNearbySearchDataProvider)
    : NearVenuesSearchRepository {

    override suspend fun requestVenues(location: Coordinates, venueType: VenueType): NearVenuesSearchRequestResult {
        log { i(TAG, "NearVenuesSearchRepositoryImpl.requestVenues(). location = [${location}], venueType = [${venueType}]") }

        return requestVenuesImpl { googleNearbySearchDataProvider.requestVenues(location, convertVenueType2GoogleTypeString(venueType)) }
    }

    override suspend fun requestVenuesNextPage(pageToken: String): NearVenuesSearchRequestResult {
        log { i(TAG, "NearVenuesSearchRepositoryImpl.requestVenuesNextPage(). pageToken = [${pageToken}]") }
        return requestVenuesImpl { googleNearbySearchDataProvider.requestVenuesNextPage(pageToken) }
    }

    private suspend fun requestVenuesImpl(requestBlock: suspend () -> GoogleNearbySearchRequestResult): NearVenuesSearchRequestResult {
        try {

            val googleNearbySearchRequestResult: GoogleNearbySearchRequestResult = requestBlock()

            log { i(TAG, "NearVenuesSearchRepositoryImpl.requestVenuesImpl() googleNearbySearchRequestResult=$googleNearbySearchRequestResult") }

            if (googleNearbySearchRequestResult.resultCode != GoogleNearbySearchRequestResultCode.OK) {
                return NearVenuesSearchRequestResult(NearVenuesSearchRequestResultCode.GENERAL_ERROR, null, null)
            }

            if (googleNearbySearchRequestResult.data == null) {
                return NearVenuesSearchRequestResult(NearVenuesSearchRequestResultCode.GENERAL_ERROR, null, null)
            }

            val data: NearVenuesSearchRequestData =
                convertGoogleNearbySearchRequestData2NearVenuesSearchRequestData(googleNearbySearchRequestResult.data)

            return NearVenuesSearchRequestResult(NearVenuesSearchRequestResultCode.OK, data, googleNearbySearchRequestResult.data.nearbySearchResponse.next_page_token)
        } catch (throwable: Throwable) {
            log { w(TAG, "NearVenuesSearchRepositoryImpl.requestVenuesImpl()", throwable) }
            return NearVenuesSearchRequestResult(NearVenuesSearchRequestResultCode.GENERAL_ERROR, null, null)
        }
    }

    private fun convertGoogleNearbySearchRequestData2NearVenuesSearchRequestData(googleData: GoogleNearbySearchRequestData): NearVenuesSearchRequestData {

        return NearVenuesSearchRequestData(googleData.nearbySearchResponse.results.map { convertGoogleResult2VenueData(it) })
    }

    private fun convertGoogleResult2VenueData(result: Result): VenueData {
        return VenueData(Coordinates(result.geometry.location.lat, result.geometry.location.lng),
            result.place_id,
            result.name,
            result.opening_hours?.open_now,
            result.rating,
            result.types.mapNotNull { convertGoogleTypeString2VenueType(it) })
    }

    private fun convertVenueType2GoogleTypeString(venueType: VenueType): String {
        return bimapVenueTypeAndGoogleStringType[venueType] ?: throw IllegalArgumentException("Unknown VenueType: $venueType")
    }

    private fun convertGoogleTypeString2VenueType(googleTypeString: String): VenueType? {
        return bimapVenueTypeAndGoogleStringType.inverse[googleTypeString]
    }

    companion object {
        private const val TAG = "NearVenuesSearchRepo"

        // See https://developers.google.com/places/web-service/supported_types
        private val bimapVenueTypeAndGoogleStringType = biMapOf(
            VenueType.BAR to "bar",
            VenueType.RESTAURANT to "restaurant",
            VenueType.CAFE to "cafe"
        )
    }

}