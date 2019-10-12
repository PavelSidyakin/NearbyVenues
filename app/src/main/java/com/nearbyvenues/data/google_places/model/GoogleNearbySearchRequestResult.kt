package com.nearbyvenues.data.google_places.model

data class GoogleNearbySearchRequestResult(
    val resultCode: GoogleNearbySearchRequestResultCode,
    val data: GoogleNearbySearchRequestData?
)