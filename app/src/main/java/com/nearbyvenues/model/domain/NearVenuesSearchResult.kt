package com.nearbyvenues.model.domain

data class NearVenuesSearchResult(
    val resultCode: NearVenuesSearchResultCode,
    val data: NearVenuesSearchData?,
    val nextPageData: NextPageData?
)