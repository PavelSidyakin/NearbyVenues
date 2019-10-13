package com.nearbyvenues.model.domain

data class NearbyVenuesSearchResult(
    val resultCode: NearVenuesSearchResultCode,
    val data: NearVenuesSearchData?,
    val nextPageData: NextPageData?
)