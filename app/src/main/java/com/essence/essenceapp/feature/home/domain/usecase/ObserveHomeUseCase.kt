package com.essence.essenceapp.feature.home.domain.usecase

import com.essence.essenceapp.feature.home.domain.model.Home
import com.essence.essenceapp.feature.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ObserveHomeUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    operator fun invoke(): StateFlow<Home?> = repository.observeHome()
}
