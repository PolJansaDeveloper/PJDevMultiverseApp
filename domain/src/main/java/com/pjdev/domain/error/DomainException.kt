package com.pjdev.domain.error

sealed class DomainException(
    cause: Throwable? = null,
) : Exception(cause) {

    class Network(
        cause: Throwable? = null,
    ) : DomainException(cause)

    class NotFound(
        cause: Throwable? = null,
    ) : DomainException(cause)

    class RateLimited(
        cause: Throwable? = null,
    ) : DomainException(cause)

    class Server(
        cause: Throwable? = null,
    ) : DomainException(cause)

    class Unknown(
        cause: Throwable? = null,
    ) : DomainException(cause)
}
