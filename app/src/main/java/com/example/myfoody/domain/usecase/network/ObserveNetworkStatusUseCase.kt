package com.example.myfoody.domain.usecase.network

import com.example.myfoody.domain.repository.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveNetworkStatusUseCase @Inject constructor(
    private val networkMonitor: NetworkMonitor
) {
    operator fun invoke(): Flow<Boolean> = networkMonitor.isOnline
}
