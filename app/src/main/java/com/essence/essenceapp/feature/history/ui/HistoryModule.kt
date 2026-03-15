package com.essence.essenceapp.feature.history.ui

import com.essence.essenceapp.feature.history.data.api.HistoryApiService
import com.essence.essenceapp.feature.history.data.repository.HistoryRepositoryImpl
import com.essence.essenceapp.feature.history.domain.repository.HistoryRepository
import com.essence.essenceapp.feature.history.domain.usecase.GetSongsOfHistoryUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HistoryModule {

    @Provides
    @Singleton
    fun provideHistoryApiService(retrofit: Retrofit): HistoryApiService {
        return retrofit.create(HistoryApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideHistoryRepository(apiService: HistoryApiService): HistoryRepository {
        return HistoryRepositoryImpl(apiService)
    }

    @Provides
    fun provideGetSongsOfHistoryUseCase(
        historyRepository: HistoryRepository
    ): GetSongsOfHistoryUseCase {
        return GetSongsOfHistoryUseCase(historyRepository)
    }
}