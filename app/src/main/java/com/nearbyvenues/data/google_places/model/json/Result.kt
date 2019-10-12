package com.nearbyvenues.data.google_places.model.json

import com.nearbyvenues.model.GsonSerializable

data class Result (

    val geometry: Geometry,
    val place_id: String,
    val name: String,
    val opening_hours: OpeningHours,
    val rating: Float,
    val types: List<String>

) : GsonSerializable