package com.example.myfoody.data.repository

import com.example.myfoody.data.local.datastore.PreferencesDataSource
import com.example.myfoody.di.IoDispatcher
import com.example.myfoody.domain.model.MealAndDietFilter
import com.example.myfoody.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : UserPreferencesRepository {

    override fun observeMealAndDietFilter(): Flow<MealAndDietFilter> =
        preferencesDataSource.mealAndDietFilter

    override suspend fun saveMealAndDietFilter(filter: MealAndDietFilter) =
        withContext(ioDispatcher) {
            preferencesDataSource.saveMealAndDietFilter(filter)
        }

    override fun observeBackOnline(): Flow<Boolean> =
        preferencesDataSource.backOnline

    override suspend fun saveBackOnline(backOnline: Boolean) =
        withContext(ioDispatcher) {
            preferencesDataSource.saveBackOnline(backOnline)
        }
}
