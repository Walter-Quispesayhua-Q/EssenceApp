package com.essence.essenceapp.core.network

import android.content.Context
import com.essence.essenceapp.BuildConfig
import com.essence.essenceapp.core.network.auth.AuthInterceptor
import com.essence.essenceapp.core.network.auth.UnauthorizedInterceptor
import com.essence.essenceapp.core.network.interceptor.RetryInterceptor
import com.essence.essenceapp.core.network.interceptor.UserAgentInterceptor
import com.essence.essenceapp.core.network.security.NetworkSecurity
import com.essence.essenceapp.core.storage.TokenManager
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
import java.io.File
import java.lang.reflect.Type
import java.time.Instant
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.Cache
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val HTTP_CACHE_DIR = "http_cache"
private const val HTTP_CACHE_SIZE_BYTES = 10L * 1024L * 1024L
private const val CONNECT_TIMEOUT_S = 20L
private const val READ_TIMEOUT_S = 60L
private const val WRITE_TIMEOUT_S = 60L
private const val CALL_TIMEOUT_S = 75L
private const val CONNECTION_POOL_MAX_IDLE = 8
private const val CONNECTION_POOL_KEEP_ALIVE_MIN = 5L

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
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
        return HttpLoggingInterceptor().apply { setLevel(level) }
    }

    @Provides
    @Singleton
    fun provideHttpCache(@ApplicationContext context: Context): Cache {
        val cacheDir = File(context.cacheDir, HTTP_CACHE_DIR)
        return Cache(cacheDir, HTTP_CACHE_SIZE_BYTES)
    }

    @Provides
    @Singleton
    fun provideConnectionPool(): ConnectionPool {
        return ConnectionPool(
            maxIdleConnections = CONNECTION_POOL_MAX_IDLE,
            keepAliveDuration = CONNECTION_POOL_KEEP_ALIVE_MIN,
            timeUnit = TimeUnit.MINUTES
        )
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        cache: Cache,
        connectionPool: ConnectionPool,
        userAgentInterceptor: UserAgentInterceptor,
        authInterceptor: AuthInterceptor,
        unauthorizedInterceptor: UnauthorizedInterceptor,
        retryInterceptor: RetryInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(cache)
            .connectionPool(connectionPool)
            .certificatePinner(NetworkSecurity.buildPinner())
            .retryOnConnectionFailure(true)
            .connectTimeout(CONNECT_TIMEOUT_S, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_S, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_S, TimeUnit.SECONDS)
            .callTimeout(CALL_TIMEOUT_S, TimeUnit.SECONDS)
            .addInterceptor(userAgentInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(unauthorizedInterceptor)
            .addInterceptor(retryInterceptor)
            .addInterceptor(loggingInterceptor)
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