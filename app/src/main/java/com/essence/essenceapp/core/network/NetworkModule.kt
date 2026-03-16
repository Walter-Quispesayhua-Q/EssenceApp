package com.essence.essenceapp.core.network

import android.content.Context
import com.essence.essenceapp.core.network.storage.TokenManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.lang.reflect.Type
import java.time.Instant
import java.time.LocalDate
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
        return TokenManager(context)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenManager: TokenManager): AuthInterceptor {
        return AuthInterceptor(tokenManager)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        val localDateAdapter = object : JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
            override fun serialize(
                src: LocalDate?,
                typeOfSrc: Type?,
                context: JsonSerializationContext?
            ): JsonElement? {
                return src?.let { JsonPrimitive(it.toString()) }
            }

            override fun deserialize(
                json: JsonElement?,
                typeOfT: Type?,
                context: JsonDeserializationContext?
            ): LocalDate? {
                if (json == null || json.isJsonNull) return null
                val value = json.asString
                return value.takeIf { it.isNotBlank() }?.let(LocalDate::parse)
            }
        }

        val instantAdapter = object : JsonSerializer<Instant>, JsonDeserializer<Instant> {
            override fun serialize(
                src: Instant?,
                typeOfSrc: Type?,
                context: JsonSerializationContext?
            ): JsonElement? {
                return src?.let { JsonPrimitive(it.toString()) }
            }

            override fun deserialize(
                json: JsonElement?,
                typeOfT: Type?,
                context: JsonDeserializationContext?
            ): Instant? {
                if (json == null || json.isJsonNull) return null
                val value = json.asString
                return value.takeIf { it.isNotBlank() }?.let(Instant::parse)
            }
        }

        return GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, localDateAdapter)
            .registerTypeAdapter(Instant::class.java, instantAdapter)
            .create()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}