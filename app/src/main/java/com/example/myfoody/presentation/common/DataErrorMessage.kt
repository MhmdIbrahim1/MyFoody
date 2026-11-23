package com.example.myfoody.presentation.common

import com.example.myfoody.domain.common.DataError

fun DataError.toDisplayMessage(notFoundMessage: String = "Recipes not found."): String = when (this) {
    DataError.NoInternet -> "No Internet Connection."
    DataError.Timeout -> "Timeout"
    DataError.ApiKeyLimited -> "API Key Limited."
    DataError.NotFound -> notFoundMessage
    is DataError.Unknown -> message?.takeIf { it.isNotBlank() } ?: notFoundMessage
}
