package com.bookreads.core.common

import kotlinx.coroutines.CoroutineDispatcher

interface DispatcherSet {
    fun defaultDispatcher(): CoroutineDispatcher

    fun ioDispatcher(): CoroutineDispatcher

    fun mainDispatcher(): CoroutineDispatcher
}
