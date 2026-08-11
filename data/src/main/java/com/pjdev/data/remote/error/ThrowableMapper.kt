package com.pjdev.data.remote.error

import com.pjdev.domain.error.DomainException
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException

internal fun Throwable.toDomainFailure(): Throwable {
    return when (this) {
        is CancellationException -> this
        is DomainException -> this
        is IOException -> DomainException.Network(this)

        is HttpException -> when (code()) {
            HTTP_NOT_FOUND -> DomainException.NotFound(this)
            HTTP_TOO_MANY_REQUESTS -> DomainException.RateLimited(this)
            in HTTP_SERVER_ERROR_START..HTTP_SERVER_ERROR_END ->
                DomainException.Server(this)

            else -> DomainException.Unknown(this)
        }

        is SerializationException -> DomainException.Unknown(this)

        else -> DomainException.Unknown(this)
    }
}

private const val HTTP_NOT_FOUND = 404
private const val HTTP_TOO_MANY_REQUESTS = 429
private const val HTTP_SERVER_ERROR_START = 500
private const val HTTP_SERVER_ERROR_END = 599
