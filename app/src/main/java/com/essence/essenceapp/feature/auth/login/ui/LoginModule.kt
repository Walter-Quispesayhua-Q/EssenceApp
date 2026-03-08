package com.essence.essenceapp.feature.auth.login.ui

import com.essence.essenceapp.feature.auth.login.data.api.LoginApiService
import com.essence.essenceapp.feature.auth.login.data.repository.LoginRepositoryImpl
import com.essence.essenceapp.feature.auth.login.domain.repository.LoginRepository
import com.essence.essenceapp.feature.auth.login.domain.usecase.LoginUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object LoginModule {
    @Provides
    @Singleton
    fun provideLoginApiService(retrofit: Retrofit): LoginApiService {
        return retrofit.create(LoginApiService::class.java)
    }
    @Provides
    @Singleton
    fun provideLoginRepository(apiService: LoginApiService): LoginRepository {
        return LoginRepositoryImpl(apiService)
    }
    @Provides
    fun provideLoginUseCase(loginRepository: LoginRepository): LoginUseCase {
        return LoginUseCase(loginRepository)
    }
}