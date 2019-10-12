package com.nearbyvenues.data.google_places.model.json

import com.nearbyvenues.model.GsonSerializable

data class OpeningHours (
    val open_now: Boolean?
) : GsonSerializable
