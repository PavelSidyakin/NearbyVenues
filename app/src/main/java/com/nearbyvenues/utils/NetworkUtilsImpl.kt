package com.nearbyvenues.utils

import android.content.Context
import android.net.ConnectivityManager
import com.nearbyvenues.utils.logs.log
import com.nearbyvenues.domain.data.ApplicationProvider
import javax.inject.Inject

class NetworkUtilsImpl
    @Inject
    constructor(
        private val applicationProvider: ApplicationProvider
        ) : NetworkUtils {

    override val networkConnectionOn: Boolean
        get() = isNetworkConnectionOn()

    private fun isNetworkConnectionOn(): Boolean {
        log { i(TAG, "isNetworkConnectionOn() checking...") }
        var on = false
        val cm = applicationProvider.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (cm != null) {
            val activeNetwork = cm.getActiveNetworkInfo()
            if (activeNetwork != null)
                on = activeNetwork.isConnected() //check any available network
        }
        log { i(TAG, "isNetworkConnectionOn() network is ${if (on) "on" else "off"}") }
        return on
    }

    private companion object {
        private const val TAG: String = "NetworkUtils"
    }
}