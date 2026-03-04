package com.bookreads.core.common

sealed interface CoreResult<T> {
    data class Success<T>(
        val result: T,
    ) : CoreResult<T>

    data class Failure<T>(
        val error: Throwable,
    ) : CoreResult<T>

    companion object {
        inline fun <T, R> CoreResult<T>?.mapNullableCoreResult(
            noinline failure: ((Throwable) -> Throwable)? = null,
            success: (T) -> R,
        ): CoreResult<R?> =
            when (this) {
                is Failure -> Failure(failure?.invoke(error) ?: error)
                is Success -> Success(success(result))
                null -> Success(null)
            }

        suspend inline fun <T, R> CoreResult<T>?.flatMapNullableCoreResult(
            noinline failure: ((Throwable) -> Throwable)? = null,
            noinline success: suspend (T) -> CoreResult<R?>,
        ): CoreResult<R?> =
            when (this) {
                is Failure -> Failure(failure?.invoke(error) ?: error)
                is Success -> success(result)
                null -> Success(null)
            }

        inline fun <T, R> CoreResult<T>.mapCoreResult(
            noinline failure: ((Throwable) -> Throwable)? = null,
            success: (T) -> R,
        ): CoreResult<R> =
            when (this) {
                is Failure -> Failure(failure?.invoke(error) ?: error)
                is Success -> Success(success(result))
            }

        suspend inline fun <T, R> CoreResult<T>.flatMapCoreResult(
            noinline failure: ((Throwable) -> Throwable)? = null,
            noinline success: suspend (T) -> CoreResult<R>,
        ): CoreResult<R> =
            when (this) {
                is Failure -> Failure(failure?.invoke(error) ?: error)
                is Success -> success(result)
            }
    }
}
