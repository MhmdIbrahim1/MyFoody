package com.example.myfoody.domain.usecase.filters

import com.example.myfoody.domain.model.MealAndDietFilter
import com.example.myfoody.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class SaveMealAndDietFilterUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(filter: MealAndDietFilter) {
        repository.saveMealAndDietFilter(filter)
    }
}
