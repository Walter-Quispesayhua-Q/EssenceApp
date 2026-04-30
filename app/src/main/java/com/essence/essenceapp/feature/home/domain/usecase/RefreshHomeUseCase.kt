package com.essence.essenceapp.feature.home.domain.usecase

import com.essence.essenceapp.feature.home.domain.model.Home
import com.essence.essenceapp.feature.home.domain.repository.HomeRepository
import javax.inject.Inject

class RefreshHomeUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    suspend operator fun invoke(): Result<Home?> = repository.refreshHome()
}
