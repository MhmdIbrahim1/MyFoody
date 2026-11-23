package com.example.myfoody.domain.common

sealed interface DataError {
    object NoInternet : DataError
    object Timeout : DataError
    object ApiKeyLimited : DataError
    object NotFound : DataError
    data class Unknown(val message: String? = null) : DataError
}
