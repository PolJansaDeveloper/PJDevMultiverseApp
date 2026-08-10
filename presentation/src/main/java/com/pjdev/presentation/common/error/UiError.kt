package com.pjdev.presentation.common.error

sealed interface UiError {

    data object Network : UiError

    data object NotFound : UiError

    data object RateLimited : UiError

    data object Server : UiError

    data object Unknown : UiError
}
