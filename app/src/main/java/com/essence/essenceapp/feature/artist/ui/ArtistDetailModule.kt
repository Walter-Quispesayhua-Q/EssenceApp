package com.essence.essenceapp.feature.artist.ui

import com.essence.essenceapp.feature.artist.data.api.ArtistApiService
import com.essence.essenceapp.feature.artist.data.repository.ArtistRepositoryImpl
import com.essence.essenceapp.feature.artist.domain.repository.ArtistRepository
import com.essence.essenceapp.feature.artist.domain.usecase.AddLikeArtistUseCase
import com.essence.essenceapp.feature.artist.domain.usecase.DeleteLikeArtistUseCase
import com.essence.essenceapp.feature.artist.domain.usecase.GetArtistUseCase
import com.essence.essenceapp.shared.cache.QueueCache
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
    fun provideArtistApiService(retrofit: Retrofit): ArtistApiService =
        retrofit.create(ArtistApiService::class.java)

    @Provides
    @Singleton
    fun provideArtistRepository(apiService: ArtistApiService, queueCache: QueueCache): ArtistRepository =
        ArtistRepositoryImpl(apiService, queueCache)

    @Provides
    fun provideGetArtistUseCase(repo: ArtistRepository): GetArtistUseCase =
        GetArtistUseCase(repo)

    @Provides
    fun provideAddLikeArtistUseCase(repo: ArtistRepository): AddLikeArtistUseCase =
        AddLikeArtistUseCase(repo)

    @Provides
    fun provideDeleteLikeArtistUseCase(repo: ArtistRepository): DeleteLikeArtistUseCase =
        DeleteLikeArtistUseCase(repo)
}
