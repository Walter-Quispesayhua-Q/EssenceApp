package com.essence.essenceapp.feature.album.ui

import com.essence.essenceapp.feature.album.data.api.AlbumApiService
import com.essence.essenceapp.feature.album.data.repository.AlbumRepositoryImpl
import com.essence.essenceapp.feature.album.domain.repository.AlbumRepository
import com.essence.essenceapp.feature.album.domain.usecase.AddLikeAlbumUseCase
import com.essence.essenceapp.feature.album.domain.usecase.DeleteLikeAlbumUseCase
import com.essence.essenceapp.feature.album.domain.usecase.GetAlbumUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AlbumDetailModule {

    @Provides
    @Singleton
    fun provideAlbumApiService(retrofit: Retrofit): AlbumApiService =
        retrofit.create(AlbumApiService::class.java)

    @Provides
    @Singleton
    fun provideAlbumRepository(apiService: AlbumApiService): AlbumRepository =
        AlbumRepositoryImpl(apiService)

    @Provides
    fun provideGetAlbumUseCase(repo: AlbumRepository): GetAlbumUseCase =
        GetAlbumUseCase(repo)

    @Provides
    fun provideAddLikeAlbumUseCase(repo: AlbumRepository): AddLikeAlbumUseCase =
        AddLikeAlbumUseCase(repo)

    @Provides
    fun provideDeleteLikeAlbumUseCase(repo: AlbumRepository): DeleteLikeAlbumUseCase =
        DeleteLikeAlbumUseCase(repo)
}
