package com.thatmobiledevagency.helparticles.core.presentation

import com.thatmobiledevagency.helparticles.core.domain.DataError
import helparticles.composeapp.generated.resources.Res
import helparticles.composeapp.generated.resources.error_disk_full
import helparticles.composeapp.generated.resources.error_no_internet
import helparticles.composeapp.generated.resources.error_request_timeout
import helparticles.composeapp.generated.resources.error_serialization
import helparticles.composeapp.generated.resources.error_too_many_requests
import helparticles.composeapp.generated.resources.error_unknown

fun DataError.toUiText(): UiText {
    val stringRes = when(this) {
        DataError.LocalErrors.DISK_FULL -> Res.string.error_disk_full
        DataError.LocalErrors.UNKNOWN -> Res.string.error_unknown
        DataError.RemoteErrors.REQUEST_TIMEOUT -> Res.string.error_request_timeout
        DataError.RemoteErrors.TOO_MANY_REQUESTS -> Res.string.error_too_many_requests
        DataError.RemoteErrors.NO_INTERNET -> Res.string.error_no_internet
        DataError.RemoteErrors.SERVER -> Res.string.error_unknown
        DataError.RemoteErrors.SERIALIZATION -> Res.string.error_serialization
        DataError.RemoteErrors.UNKNOWN -> Res.string.error_unknown
    }

    return UiText.StringResourceId(stringRes)
}