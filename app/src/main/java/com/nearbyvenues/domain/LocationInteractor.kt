package com.nearbyvenues.domain

import com.nearbyvenues.model.Coordinates

interface LocationInteractor {

    /**
     * Returns current device location.
     * If the location is not available - subscribes to location availability and waits when the location is available.
     *
     * @param timeout Wait location timeout
     *
     * @return Current device location. Null if location is not available
     *
     */
    suspend fun requestLocation(timeout: Long): Coordinates?
}