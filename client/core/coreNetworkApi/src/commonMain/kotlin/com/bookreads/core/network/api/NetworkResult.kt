package com.bookreads.core.network.api

sealed interface NetworkResult<out T> {
    data class Success<T>(
        val data: T,
    ) : NetworkResult<T>

    data class Failure<T>(
        val error: Throwable,
    ) : NetworkResult<T>
}
