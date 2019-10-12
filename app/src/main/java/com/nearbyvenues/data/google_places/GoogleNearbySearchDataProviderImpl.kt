package com.nearbyvenues.data.google_places

import com.nearbyvenues.data.google_places.model.GoogleNearbySearchRequestData
import com.nearbyvenues.data.google_places.model.GoogleNearbySearchRequestResult
import com.nearbyvenues.data.google_places.model.GoogleNearbySearchRequestResultCode
import com.nearbyvenues.data.google_places.model.json.NearbySearchResponse
import com.nearbyvenues.model.Coordinates
import com.nearbyvenues.utils.logs.log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

class GoogleNearbySearchDataProviderImpl

    @Inject
    constructor()
    : GoogleNearbySearchDataProvider {

    private val retrofit: Retrofit by lazy { createRetrofit() }

    override suspend fun requestVenues(location: Coordinates, radius: Int, type: String): GoogleNearbySearchRequestResult {

        log { i(TAG, "GoogleNearbySearchDataProviderImpl.requestVenues(). location = [${location}], radius = [${radius}], type = [${type}]") }

        try {

            val nearbySearchResponse: NearbySearchResponse =
                createNearbySearchService().nearbySearch(
                    convertLocation2String(location),
                    radius,
                    type
                )

            log { i(TAG, "GoogleNearbySearchDataProviderImpl.requestVenues() nearbySearchResponse=$nearbySearchResponse") }

            return if (nearbySearchResponse.status != "OK") {
                GoogleNearbySearchRequestResult(
                    GoogleNearbySearchRequestResultCode.GENERAL_ERROR,
                    null
                )
            } else {
                GoogleNearbySearchRequestResult(
                    GoogleNearbySearchRequestResultCode.OK,
                    GoogleNearbySearchRequestData(nearbySearchResponse)
                )
            }
        } catch (throwable: Throwable) {
            log { w(TAG, "GoogleNearbySearchDataProviderImpl.requestVenues(). location = [${location}], radius = [${radius}], type = [${type}]", throwable) }
            return GoogleNearbySearchRequestResult(
                GoogleNearbySearchRequestResultCode.GENERAL_ERROR,
                null
            )

        }
    }

    private fun createRetrofit(): Retrofit {
        val interceptor = HttpLoggingInterceptor {message ->
            log { i(TAG, message) }
        }
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun createNearbySearchService(): NearbySearchService {
        return retrofit.create(NearbySearchService::class.java)
    }

    private interface NearbySearchService {
        @GET("json?key=${API_KEY}")
        suspend fun nearbySearch(
            @Query("location") location: String,
            @Query("radius") radius: Int,
            @Query("type") type: String): NearbySearchResponse
    }

    private fun convertLocation2String(location: Coordinates): String {
        return "${location.lat},${location.lng}"
    }

    companion object {
        private const val TAG = "GoogleNearbyData"
        private const val API_KEY = "AIzaSyBXJ5GpNKdV_bc_x8rl0TwT2eHGqo8bWYg"
    }
}