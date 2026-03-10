package com.essence.essenceapp.feature.search.ui

import com.essence.essenceapp.feature.search.data.api.SearchApiService
import com.essence.essenceapp.feature.search.data.repository.SearchRepositoryImpl
import com.essence.essenceapp.feature.search.domain.repository.SearchRepository
import com.essence.essenceapp.feature.search.domain.usecase.GetAvailableCategoriesUseCase
import com.essence.essenceapp.feature.search.domain.usecase.SearchUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SearchModule {
    @Provides
    @Singleton
    fun provideSearchApiService(retrofit: Retrofit): SearchApiService {
        return retrofit.create(SearchApiService::class.java)
    }
    @Provides
    @Singleton
    fun provideSearchRepository(apiService: SearchApiService): SearchRepository {
        return SearchRepositoryImpl(apiService)
    }
    @Provides
    fun provideSearchUseCase(searchRepository: SearchRepository): SearchUseCase {
        return SearchUseCase(searchRepository)
    }
    @Provides
    fun provideGetAvailableCategoriesUseCase(searchRepository: SearchRepository): GetAvailableCategoriesUseCase {
        return GetAvailableCategoriesUseCase(searchRepository)
    }
}