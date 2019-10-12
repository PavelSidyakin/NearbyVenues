package com.nearbyvenues.data.google_places.model.json

import com.nearbyvenues.model.GsonSerializable

data class GLocation (
    val lat: Double,
    val lng: Double
) : GsonSerializable