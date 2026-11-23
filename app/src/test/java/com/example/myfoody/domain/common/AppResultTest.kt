package com.example.myfoody.domain.common

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AppResultTest {

    @Test
    fun `map transforms success data and preserves error`() {
        val success: AppResult<Int> = AppResult.Success(10)
        val mappedSuccess = success.map { it * 2 }

        assertEquals(AppResult.Success(20), mappedSuccess)
        assertEquals(20, mappedSuccess.getOrNull())
        assertNull(mappedSuccess.errorOrNull())

        val error: AppResult<Int> = AppResult.Error(DataError.NoInternet)
        val mappedError = error.map { it * 2 }

        assertEquals(AppResult.Error(DataError.NoInternet), mappedError)
        assertNull(mappedError.getOrNull())
        assertEquals(DataError.NoInternet, mappedError.errorOrNull())
    }
}
