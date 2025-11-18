package com.thatmobiledevagency.helparticles.core.domain

sealed interface DataError: Error {
    enum class RemoteErrors: DataError {
        REQUEST_TIMEOUT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        SERVER,
        SERIALIZATION,
        UNKNOWN
    }

    enum class LocalErrors: DataError {
        DISK_FULL,
        UNKNOWN
    }
}