package com.example.myfoody.util

/**
 * A sealed class representing the result of a network operation.
 *
 * @param T The type of data returned from a successful network request.
 * @param data The data returned from a successful network request, if any (default: null).
 * @param message An error message, if the network request resulted in an error (default: null).
 */
sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null
) {

    /**
     * A subclass of NetworkResult, representing a successful network request with the returned data.
     *
     * @param T The type of data returned from a successful network request.
     * @param data The data returned from a successful network request.
     */
    class Success<T>(data: T?) : NetworkResult<T>(data)

    /**
     * A subclass of NetworkResult, representing a failed network request with an error message and optional data.
     *
     * @param T The type of data returned from a successful network request.
     * @param message An error message for the failed network request.
     * @param data The optional data returned from the failed network request (default: null).
     */
    class Error<T>(message: String?, data: T? = null) : NetworkResult<T>(data, message)

    /**
     * A subclass of NetworkResult, representing a network request in progress, with no data or error message.
     *
     * @param T The type of data returned from a successful network request.
     */
    class Loading<T> : NetworkResult<T>()
}