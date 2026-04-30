package com.essence.essenceapp.feature.profile.ui

import com.essence.essenceapp.core.network.auth.SessionManager
import com.essence.essenceapp.feature.profile.data.api.ProfileApiService
import com.essence.essenceapp.feature.profile.data.local.ProfileLocalDataSource
import com.essence.essenceapp.feature.profile.data.repository.ProfileRepositoryImpl
import com.essence.essenceapp.feature.profile.domain.repository.ProfileRepository
import com.essence.essenceapp.feature.profile.domain.usecase.GetCurrentUserUseCase
import com.essence.essenceapp.feature.profile.domain.usecase.ObserveUserProfileUseCase
import com.essence.essenceapp.feature.profile.domain.usecase.RefreshUserProfileUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {

    @Provides
    @Singleton
    fun provideProfileApiService(retrofit: Retrofit): ProfileApiService {
        return retrofit.create(ProfileApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(
        apiService: ProfileApiService,
        localDataSource: ProfileLocalDataSource,
        sessionManager: SessionManager
    ): ProfileRepository {
        return ProfileRepositoryImpl(
            apiService = apiService,
            localDataSource = localDataSource,
            sessionManager = sessionManager
        )
    }

    @Provides
    fun provideGetCurrentUserUseCase(repository: ProfileRepository): GetCurrentUserUseCase {
        return GetCurrentUserUseCase(repository)
    }

    @Provides
    fun provideObserveUserProfileUseCase(repository: ProfileRepository): ObserveUserProfileUseCase {
        return ObserveUserProfileUseCase(repository)
    }

    @Provides
    fun provideRefreshUserProfileUseCase(repository: ProfileRepository): RefreshUserProfileUseCase {
        return RefreshUserProfileUseCase(repository)
    }
}