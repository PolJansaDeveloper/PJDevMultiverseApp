package com.pjdev.presentation.common.error

import com.pjdev.domain.error.DomainException

fun Throwable.toUiError(): UiError {
    return when (this) {
        is DomainException.Network -> UiError.Network
        is DomainException.NotFound -> UiError.NotFound
        is DomainException.RateLimited -> UiError.RateLimited
        is DomainException.Server -> UiError.Server
        else -> UiError.Unknown
    }
}
