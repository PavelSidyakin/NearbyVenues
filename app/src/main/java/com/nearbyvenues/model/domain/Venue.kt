package com.nearbyvenues.model.domain

import com.nearbyvenues.model.Coordinates
import com.nearbyvenues.model.VenueType

data class Venue (
    val coordinates: Coordinates,
    val distance: Double,
    val id: String,
    val name: String,
    val openNow: Boolean?,
    val rating: Float,
    val types: List<VenueType>
)