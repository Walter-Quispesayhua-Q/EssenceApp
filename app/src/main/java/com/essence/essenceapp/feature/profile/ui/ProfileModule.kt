package com.essence.essenceapp.feature.profile.ui

import com.essence.essenceapp.feature.profile.data.api.ProfileApiService
import com.essence.essenceapp.feature.profile.data.repository.ProfileRepositoryImpl
import com.essence.essenceapp.feature.profile.domain.repository.ProfileRepository
import com.essence.essenceapp.feature.profile.domain.usecase.GetCurrentUserUseCase
import com.essence.essenceapp.feature.profile.domain.usecase.GetUserProfileUseCase
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
    fun provideProfileRepository(apiService: ProfileApiService): ProfileRepository {
        return ProfileRepositoryImpl(apiService)
    }

    @Provides
    fun provideGetCurrentUserUseCase(repository: ProfileRepository): GetCurrentUserUseCase {
        return GetCurrentUserUseCase(repository)
    }

    @Provides
    fun provideGetUserProfileUseCase(repository: ProfileRepository): GetUserProfileUseCase {
        return GetUserProfileUseCase(repository)
    }
}