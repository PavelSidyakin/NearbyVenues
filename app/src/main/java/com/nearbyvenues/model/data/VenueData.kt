package com.nearbyvenues.model.data

import com.nearbyvenues.model.Coordinates
import com.nearbyvenues.model.domain.VenueType

data class VenueData (
    val coordinates: Coordinates,
    val id: String,
    val name: String,
    val openNow: Boolean?,
    val rating: Float,
    val types: List<VenueType>
)