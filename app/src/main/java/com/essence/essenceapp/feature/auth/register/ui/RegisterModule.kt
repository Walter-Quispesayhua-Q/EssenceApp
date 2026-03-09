package com.essence.essenceapp.feature.auth.register.ui

import com.essence.essenceapp.feature.auth.register.data.api.RegisterApiService
import com.essence.essenceapp.feature.auth.register.data.repository.RegisterRepositoryImpl
import com.essence.essenceapp.feature.auth.register.domain.repository.RegisterRepository
import com.essence.essenceapp.feature.auth.register.domain.usecase.RegisterUseCase
import com.essence.essenceapp.feature.auth.register.domain.usecase.UsernameAvailableUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object RegisterModule {

    @Provides
    @Singleton
    fun provideRegisterApiService(retrofit: Retrofit): RegisterApiService {
        return retrofit.create(RegisterApiService::class.java)
    }
    @Provides
    @Singleton
    fun provideRegisterRepository(apiService: RegisterApiService): RegisterRepository {
        return RegisterRepositoryImpl(apiService)
    }
    @Provides
    fun provideRegisterUseCase(registerRepository: RegisterRepository): RegisterUseCase {
        return RegisterUseCase(registerRepository)
    }
    @Provides
    fun provideUsernameAvailableUseCase(registerRepository: RegisterRepository): UsernameAvailableUseCase {
        return UsernameAvailableUseCase(registerRepository)
    }
}