package com.bookreads.core.data

import com.bookreads.core.common.CoreResult
import com.bookreads.core.network.api.NetworkResult

internal fun <T> NetworkResult<T>.toCoreResult(): CoreResult<T> =
    when (this) {
        is NetworkResult.Success -> CoreResult.Success(data)
        is NetworkResult.Failure -> CoreResult.Failure(error)
    }
