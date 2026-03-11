package com.essence.essenceapp.feature.playlist.ui

import com.essence.essenceapp.feature.playlist.data.api.PlaylistApiService
import com.essence.essenceapp.feature.playlist.data.repository.PlaylistRepositoryImpl
import com.essence.essenceapp.feature.playlist.domain.repository.PlaylistRepository
import com.essence.essenceapp.feature.playlist.domain.usecase.AddSongToPlaylistUseCase
import com.essence.essenceapp.feature.playlist.domain.usecase.CreatePlaylistUseCase
import com.essence.essenceapp.feature.playlist.domain.usecase.DeletePlaylistUseCase
import com.essence.essenceapp.feature.playlist.domain.usecase.DeleteSongOfPlaylistUseCase
import com.essence.essenceapp.feature.playlist.domain.usecase.GetForUpdateUseCase
import com.essence.essenceapp.feature.playlist.domain.usecase.GetListSongsUseCase
import com.essence.essenceapp.feature.playlist.domain.usecase.GetPlaylistUseCase
import com.essence.essenceapp.feature.playlist.domain.usecase.GetPlaylistsByUserUseCase
import com.essence.essenceapp.feature.playlist.domain.usecase.UpdatePlaylistUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlaylistModule {

    // API
    @Provides
    @Singleton
    fun providePlaylistApiService(retrofit: Retrofit): PlaylistApiService {
        return retrofit.create(PlaylistApiService::class.java)
    }

    // Repository
    @Provides
    @Singleton
    fun providePlaylistRepository(apiService: PlaylistApiService): PlaylistRepository {
        return PlaylistRepositoryImpl(apiService)
    }

    // UseCases - List
    @Provides
    fun provideGetPlaylistsByUserUseCase(repo: PlaylistRepository): GetPlaylistsByUserUseCase {
        return GetPlaylistsByUserUseCase(repo)
    }

    @Provides
    fun provideDeletePlaylistUseCase(repo: PlaylistRepository): DeletePlaylistUseCase {
        return DeletePlaylistUseCase(repo)
    }

    // UseCases - Detail
    @Provides
    fun provideGetPlaylistUseCase(repo: PlaylistRepository): GetPlaylistUseCase {
        return GetPlaylistUseCase(repo)
    }

    @Provides
    fun provideGetListSongsUseCase(repo: PlaylistRepository): GetListSongsUseCase {
        return GetListSongsUseCase(repo)
    }

    @Provides
    fun provideAddSongToPlaylistUseCase(repo: PlaylistRepository): AddSongToPlaylistUseCase {
        return AddSongToPlaylistUseCase(repo)
    }

    @Provides
    fun provideRemoveSongFromPlaylistUseCase(repo: PlaylistRepository): DeleteSongOfPlaylistUseCase {
        return DeleteSongOfPlaylistUseCase(repo)
    }

    // UseCases - Form
    @Provides
    fun provideCreatePlaylistUseCase(repo: PlaylistRepository): CreatePlaylistUseCase {
        return CreatePlaylistUseCase(repo)
    }

    @Provides
    fun provideUpdatePlaylistUseCase(repo: PlaylistRepository): UpdatePlaylistUseCase {
        return UpdatePlaylistUseCase(repo)
    }

    @Provides
    fun provideGetForUpdateUseCase(repo: PlaylistRepository): GetForUpdateUseCase {
        return GetForUpdateUseCase(repo)
    }
}
