package com.essence.essenceapp.feature.home.ui

import com.essence.essenceapp.feature.home.data.api.FakeHomeApiService
import com.essence.essenceapp.feature.home.data.api.HomeApiService
import com.essence.essenceapp.feature.home.data.repository.HomeRepositoryImpl
import com.essence.essenceapp.feature.home.domain.repository.HomeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HomeModule {

    @Provides
    fun provideHomeApiService(): HomeApiService {
        return FakeHomeApiService()
    }

    @Provides
    fun provideHomeRepository(apiService: HomeApiService): HomeRepository {
        return HomeRepositoryImpl(apiService)
    }
}