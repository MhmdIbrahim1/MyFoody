package com.example.myfoody.domain.usecase.filters

import com.example.myfoody.domain.model.MealAndDietFilter
import com.example.myfoody.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveMealAndDietFilterUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<MealAndDietFilter> = repository.observeMealAndDietFilter()
}
