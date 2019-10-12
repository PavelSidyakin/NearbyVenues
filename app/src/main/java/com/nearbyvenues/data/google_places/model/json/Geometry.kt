package com.nearbyvenues.data.google_places.model.json

import com.nearbyvenues.model.GsonSerializable

data class Geometry (
    val location: GLocation
) : GsonSerializable
