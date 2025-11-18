package com.thatmobiledevagency.helparticles.core.data

import com.thatmobiledevagency.helparticles.core.domain.DataError
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.ensureActive
import com.thatmobiledevagency.helparticles.core.domain.Result
import io.ktor.util.InternalAPI
import io.ktor.util.toByteArray
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

@OptIn(DelicateCoroutinesApi::class)
suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse
): Result<T, DataError.RemoteErrors> {
    val response = try {
        execute()
    } catch(e: SocketTimeoutException) {
        return Result.Error(DataError.RemoteErrors.REQUEST_TIMEOUT)
    } catch(e: UnresolvedAddressException) {
        return Result.Error(DataError.RemoteErrors.NO_INTERNET)
    } catch (e: Exception) {
        coroutineContext.ensureActive()
        return Result.Error(DataError.RemoteErrors.UNKNOWN)
    }

    return responseToResult(response)
}

@OptIn(InternalAPI::class)
suspend inline fun <reified T> responseToResult(
    response: HttpResponse
): Result<T, DataError.RemoteErrors> {
    return when(response.status.value) {
        in 200..299 -> {
            try {
                val bytes = response.content.toByteArray()
                val text = bytes.decodeToString()
                val parsed = try {
                    Json.decodeFromString<T>(text)
                } catch (se: SerializationException) {
                    // explicit serialization failure
                    // we could also enhance it to show payload errors as mentioned in the assignment
                    return Result.Error(DataError.RemoteErrors.SERIALIZATION)
                }
                Result.Success(parsed)
            } catch (e: Exception) {
                Result.Error(DataError.RemoteErrors.SERIALIZATION)
            }
        }
        408 -> Result.Error(DataError.RemoteErrors.REQUEST_TIMEOUT)
        429 -> Result.Error(DataError.RemoteErrors.TOO_MANY_REQUESTS)
        in 500..599 -> Result.Error(DataError.RemoteErrors.SERVER)
        else -> Result.Error(DataError.RemoteErrors.UNKNOWN)
    }
}