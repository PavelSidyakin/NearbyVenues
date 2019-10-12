package com.nearbyvenues.data.google_places.model.json

import com.nearbyvenues.model.GsonSerializable

data class NearbySearchResponse (

    val next_page_token: String,
    val results: List<Result>,
    val status: String

) : GsonSerializable