package com.nearbyvenues.domain.data

import android.content.Context
import com.nearbyvenues.TheApplication

interface ApplicationProvider {
    /**
     * Initializes ApplicationProvider. Must be called before any interaction with ApplicationProvider.
     *
     * @param theApplication application object
     */
    fun init(theApplication: TheApplication)

    /**
     * @returns application context
     */
    val applicationContext: Context
}