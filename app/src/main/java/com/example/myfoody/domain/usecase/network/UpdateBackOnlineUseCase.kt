package com.example.myfoody.domain.usecase.network

import com.example.myfoody.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class UpdateBackOnlineUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(backOnline: Boolean) {
        repository.saveBackOnline(backOnline)
    }
}
