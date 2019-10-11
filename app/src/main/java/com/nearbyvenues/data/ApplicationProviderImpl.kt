package com.nearbyvenues.data

import android.content.Context
import com.nearbyvenues.TheApplication
import com.nearbyvenues.domain.data.ApplicationProvider
import javax.inject.Inject

class ApplicationProviderImpl
    @Inject
    constructor() : ApplicationProvider {

    private lateinit var theApplication: TheApplication

    override fun init(theApplication: TheApplication) {
        this.theApplication = theApplication
    }

    override val applicationContext: Context
        get() = theApplication.applicationContext


}