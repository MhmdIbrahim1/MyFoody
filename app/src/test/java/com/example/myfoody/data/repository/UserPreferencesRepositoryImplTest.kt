package com.example.myfoody.data.repository

import com.example.myfoody.data.local.datastore.PreferencesDataSource
import com.example.myfoody.domain.model.MealAndDietFilter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserPreferencesRepositoryImplTest {

    private class FakePreferencesDataSource : PreferencesDataSource {
        private val filterFlow = MutableStateFlow(MealAndDietFilter.Default)
        private val backOnlineFlow = MutableStateFlow(false)

        override val mealAndDietFilter: Flow<MealAndDietFilter> = filterFlow
        override val backOnline: Flow<Boolean> = backOnlineFlow

        override suspend fun saveMealAndDietFilter(filter: MealAndDietFilter) {
            filterFlow.value = filter
        }

        override suspend fun saveBackOnline(backOnline: Boolean) {
            backOnlineFlow.value = backOnline
        }
    }

    @Test
    fun `saveMealAndDietFilter persists filter and emits updates`() = runTest {
        val dataSource = FakePreferencesDataSource()
        val repository = UserPreferencesRepositoryImpl(
            preferencesDataSource = dataSource,
            ioDispatcher = UnconfinedTestDispatcher(testScheduler)
        )

        assertEquals(MealAndDietFilter.Default, repository.observeMealAndDietFilter().first())

        val updated = MealAndDietFilter(
            selectedMealType = "dessert",
            selectedMealTypeId = 4,
            selectedDietType = "vegan",
            selectedDietTypeId = 3
        )
        repository.saveMealAndDietFilter(updated)

        assertEquals(updated, repository.observeMealAndDietFilter().first())
    }

    @Test
    fun `saveBackOnline persists flag and emits updates`() = runTest {
        val dataSource = FakePreferencesDataSource()
        val repository = UserPreferencesRepositoryImpl(
            preferencesDataSource = dataSource,
            ioDispatcher = UnconfinedTestDispatcher(testScheduler)
        )

        assertFalse(repository.observeBackOnline().first())

        repository.saveBackOnline(true)
        assertTrue(repository.observeBackOnline().first())

        repository.saveBackOnline(false)
        assertFalse(repository.observeBackOnline().first())
    }
}
