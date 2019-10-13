package com.nearbyvenues.model.domain

import com.nearbyvenues.model.Coordinates

data class NextPageData (
    val location: Coordinates,
    val nextPageTokens: List<String>
)