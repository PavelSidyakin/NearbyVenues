package com.nearbyvenues.utils

import kotlinx.coroutines.CoroutineDispatcher

interface DispatcherProvider {

    fun io(): CoroutineDispatcher
    fun main(): CoroutineDispatcher

}