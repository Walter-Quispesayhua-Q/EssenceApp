package com.essence.essenceapp.feature.song.ui

import com.essence.essenceapp.feature.song.data.api.SongApiService
import com.essence.essenceapp.feature.song.data.repository.SongRepositoryImpl
import com.essence.essenceapp.feature.song.domain.repository.SongRepository
import com.essence.essenceapp.feature.song.domain.usecase.AddLikeSongUseCase
import com.essence.essenceapp.feature.song.domain.usecase.DeleteLikeSongUseCase
import com.essence.essenceapp.feature.song.domain.usecase.GetSongUseCase
import com.essence.essenceapp.feature.song.domain.usecase.RefreshStreamingUrlUseCase
import com.essence.essenceapp.shared.streaming.StreamingUrlSyncManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SongDetailModule {

    @Provides
    @Singleton
    fun provideSongApiService(retrofit: Retrofit): SongApiService =
        retrofit.create(SongApiService::class.java)

    @Provides
    @Singleton
    fun provideSongRepository(
        apiService: SongApiService,
        streamingUrlSyncManager: StreamingUrlSyncManager
    ): SongRepository =
        SongRepositoryImpl(apiService, streamingUrlSyncManager)

    @Provides
    fun provideGetSongUseCase(repo: SongRepository): GetSongUseCase =
        GetSongUseCase(repo)

    @Provides
    fun provideRefreshStreamingUrlUseCase(repo: SongRepository): RefreshStreamingUrlUseCase =
        RefreshStreamingUrlUseCase(repo)

    @Provides
    fun provideAddLikeSongUseCase(repo: SongRepository): AddLikeSongUseCase =
        AddLikeSongUseCase(repo)

    @Provides
    fun provideDeleteLikeSongUseCase(repo: SongRepository): DeleteLikeSongUseCase =
        DeleteLikeSongUseCase(repo)
}