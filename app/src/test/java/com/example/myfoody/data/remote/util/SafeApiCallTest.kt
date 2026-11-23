package com.example.myfoody.data.remote.util

import com.example.myfoody.domain.common.AppResult
import com.example.myfoody.domain.common.DataError
import com.example.myfoody.domain.repository.NetworkMonitor
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.Response
import java.net.SocketTimeoutException

@OptIn(ExperimentalCoroutinesApi::class)
class SafeApiCallTest {

    private class FakeNetworkMonitor(private val online: Boolean) : NetworkMonitor {
        override val isOnline: Flow<Boolean> = flowOf(online)
        override fun isCurrentlyOnline(): Boolean = online
    }

    @Test
    fun `returns NoInternet without calling api when offline`() = runTest {
        val safeApiCall = SafeApiCall(FakeNetworkMonitor(online = false))
        var called = false

        val result = safeApiCall.execute<String> {
            called = true
            Response.success("ok")
        }

        assertFalse(called)
        assertEquals(AppResult.Error(DataError.NoInternet), result)
    }

    @Test
    fun `returns Success when response is successful with non-null body`() = runTest {
        val safeApiCall = SafeApiCall(FakeNetworkMonitor(online = true))

        val result = safeApiCall.execute { Response.success("payload") }

        assertEquals(AppResult.Success("payload"), result)
    }

    @Test
    fun `returns NotFound when successful response has null body`() = runTest {
        val safeApiCall = SafeApiCall(FakeNetworkMonitor(online = true))

        val result = safeApiCall.execute<String> { Response.success(null) }

        assertEquals(AppResult.Error(DataError.NotFound), result)
    }

    @Test
    fun `returns ApiKeyLimited on HTTP 402`() = runTest {
        val safeApiCall = SafeApiCall(FakeNetworkMonitor(online = true))

        val result = safeApiCall.execute<String> {
            Response.error(402, ResponseBody.create(null, ""))
        }

        assertEquals(AppResult.Error(DataError.ApiKeyLimited), result)
    }

    @Test
    fun `returns NotFound on HTTP 404`() = runTest {
        val safeApiCall = SafeApiCall(FakeNetworkMonitor(online = true))

        val result = safeApiCall.execute<String> {
            Response.error(404, ResponseBody.create(null, ""))
        }

        assertEquals(AppResult.Error(DataError.NotFound), result)
    }

    @Test
    fun `returns Timeout when response message contains timeout`() = runTest {
        val safeApiCall = SafeApiCall(FakeNetworkMonitor(online = true))
        val raw = okhttp3.Response.Builder()
            .code(504)
            .message("Gateway Timeout")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url("https://api.spoonacular.com/").build())
            .build()

        val result = safeApiCall.execute<String> {
            Response.error(ResponseBody.create(null, ""), raw)
        }

        assertEquals(AppResult.Error(DataError.Timeout), result)
    }

    @Test
    fun `returns Unknown on generic HTTP error`() = runTest {
        val safeApiCall = SafeApiCall(FakeNetworkMonitor(online = true))
        val raw = okhttp3.Response.Builder()
            .code(500)
            .message("Internal Server Error")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url("https://api.spoonacular.com/").build())
            .build()

        val result = safeApiCall.execute<String> {
            Response.error(ResponseBody.create(null, ""), raw)
        }

        assertEquals(AppResult.Error(DataError.Unknown("Internal Server Error")), result)
    }

    @Test
    fun `returns Timeout on SocketTimeoutException`() = runTest {
        val safeApiCall = SafeApiCall(FakeNetworkMonitor(online = true))

        val result = safeApiCall.execute<String> {
            throw SocketTimeoutException("timed out")
        }

        assertEquals(AppResult.Error(DataError.Timeout), result)
    }

    @Test
    fun `returns Unknown on unexpected exception`() = runTest {
        val safeApiCall = SafeApiCall(FakeNetworkMonitor(online = true))

        val result = safeApiCall.execute<String> {
            throw IllegalStateException("boom")
        }

        assertEquals(AppResult.Error(DataError.Unknown("boom")), result)
    }

    @Test
    fun `rethrows CancellationException without swallowing it`() = runTest {
        val safeApiCall = SafeApiCall(FakeNetworkMonitor(online = true))

        try {
            safeApiCall.execute<String> {
                throw CancellationException("cancelled")
            }
            fail("Expected CancellationException to be rethrown")
        } catch (expected: CancellationException) {
            assertEquals("cancelled", expected.message)
        }
    }
}
