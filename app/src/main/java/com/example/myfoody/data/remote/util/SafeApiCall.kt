package com.example.myfoody.data.remote.util

import com.example.myfoody.domain.common.AppResult
import com.example.myfoody.domain.common.DataError
import com.example.myfoody.domain.repository.NetworkMonitor
import kotlinx.coroutines.CancellationException
import retrofit2.Response
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SafeApiCall @Inject constructor(
    private val networkMonitor: NetworkMonitor
) {
    suspend fun <T> execute(apiCall: suspend () -> Response<T>): AppResult<T> {
        if (!networkMonitor.isCurrentlyOnline()) {
            return AppResult.Error(DataError.NoInternet)
        }
        return try {
            val response = apiCall()
            handleResponse(response)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: SocketTimeoutException) {
            AppResult.Error(DataError.Timeout)
        } catch (exception: Exception) {
            AppResult.Error(DataError.Unknown(exception.message))
        }
    }

    private fun <T> handleResponse(response: Response<T>): AppResult<T> {
        val message = response.message().orEmpty()
        return when {
            message.contains("timeout", ignoreCase = true) -> AppResult.Error(DataError.Timeout)
            response.code() == 402 -> AppResult.Error(DataError.ApiKeyLimited)
            response.code() == 404 -> AppResult.Error(DataError.NotFound)
            response.isSuccessful -> {
                val body = response.body()
                if (body != null) {
                    AppResult.Success(body)
                } else {
                    AppResult.Error(DataError.NotFound)
                }
            }
            else -> AppResult.Error(DataError.Unknown(message.ifBlank { null }))
        }
    }
}
