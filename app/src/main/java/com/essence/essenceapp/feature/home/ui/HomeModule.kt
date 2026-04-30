package com.essence.essenceapp.feature.home.ui

import com.essence.essenceapp.core.network.auth.SessionManager
import com.essence.essenceapp.feature.home.data.api.HomeApiService
import com.essence.essenceapp.feature.home.data.repository.HomeRepositoryImpl
import com.essence.essenceapp.feature.home.domain.repository.HomeRepository
import com.essence.essenceapp.feature.home.domain.usecase.ObserveHomeUseCase
import com.essence.essenceapp.feature.home.domain.usecase.RefreshHomeUseCase
import com.essence.essenceapp.shared.cache.QueueCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object HomeModule {

    @Provides
    @Singleton
    fun provideHomeApiService(retrofit: Retrofit): HomeApiService {
        return retrofit.create(HomeApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideHomeRepository(
        apiService: HomeApiService,
        queueCache: QueueCache,
        sessionManager: SessionManager
    ): HomeRepository {
        return HomeRepositoryImpl(
            apiService = apiService,
            queueCache = queueCache,
            sessionManager = sessionManager
        )
    }

    @Provides
    fun provideObserveHomeUseCase(repository: HomeRepository): ObserveHomeUseCase {
        return ObserveHomeUseCase(repository)
    }

    @Provides
    fun provideRefreshHomeUseCase(repository: HomeRepository): RefreshHomeUseCase {
        return RefreshHomeUseCase(repository)
    }
}