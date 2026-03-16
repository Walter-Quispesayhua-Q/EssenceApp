package com.essence.essenceapp.feature.home.ui

import com.essence.essenceapp.feature.home.data.api.HomeApiService
import com.essence.essenceapp.feature.home.data.repository.HomeRepositoryImpl
import com.essence.essenceapp.feature.home.domain.repository.HomeRepository
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
    fun provideHomeRepository(apiService: HomeApiService): HomeRepository {
        return HomeRepositoryImpl(apiService)
    }
}