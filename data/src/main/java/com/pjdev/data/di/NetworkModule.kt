package com.pjdev.data.di

import retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.pjdev.data.source.remote.api.RickAndMortyApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
        }
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        json: Json,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                json.asConverterFactory(JSON_MEDIA_TYPE.toMediaType()),
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideRickAndMortyApi(
        retrofit: Retrofit,
    ): RickAndMortyApi {
        return retrofit.create(RickAndMortyApi::class.java)
    }

    private const val BASE_URL = "https://rickandmortyapi.com/api/"
    private const val JSON_MEDIA_TYPE = "application/json"
}
