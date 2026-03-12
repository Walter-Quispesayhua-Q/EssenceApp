package com.essence.essenceapp.feature.artist.ui

import com.essence.essenceapp.feature.artist.data.api.ArtistApiService
import com.essence.essenceapp.feature.artist.data.repository.ArtistRepositoryImpl
import com.essence.essenceapp.feature.artist.domain.repository.ArtistRepository
import com.essence.essenceapp.feature.artist.domain.usecase.GetArtistUseCase

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ArtistDetailModule {

    @Provides
    @Singleton
    fun providePlaylistApiService(retrofit: Retrofit): ArtistApiService {
        return retrofit.create(ArtistApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePlaylistRepository(apiService: ArtistApiService): ArtistRepository {
        return ArtistRepositoryImpl(apiService)
    }

    @Provides
    fun provideGetPlaylistsByUserUseCase(repo: ArtistRepository): GetArtistUseCase {
        return GetArtistUseCase(repo)
    }
}