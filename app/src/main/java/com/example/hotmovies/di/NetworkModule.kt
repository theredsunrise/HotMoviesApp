package com.example.hotmovies.di

import com.example.hotmovies.infrastructure.dataRepository.tmdb.AuthorizationInterceptor
import com.example.hotmovies.infrastructure.dataRepository.tmdb.TmdbMovieDataApiInterface
import com.example.hotmovies.infrastructure.dataRepository.tmdb.TmdbMovieImageApiInterface
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
class NetworkModule {
    @Singleton
    @Provides
    @Named("dataBaseUrl")
    fun provideDataBaseUrl(): String = "https://api.themoviedb.org/"

    @Singleton
    @Provides
    @Named("imageBaseUrl")
    fun provideImageBaseUrl(): String = "https://image.tmdb.org/t/p/"

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(AuthorizationInterceptor())
            .cache(null)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofitBuilder(okHttpClient: OkHttpClient): Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
    }

    @Singleton
    @Provides
    fun provideTmdbMovieDataApiInterface(
        @Named("dataBaseUrl") dataBaseUrl: String,
        retrofitBuilder: Retrofit.Builder
    ): TmdbMovieDataApiInterface {
        return retrofitBuilder.baseUrl(dataBaseUrl).build()
            .create(TmdbMovieDataApiInterface::class.java)
    }

    @Singleton
    @Provides
    fun provideTmdbMovieImageApiInterface(
        @Named("imageBaseUrl") imageBaseUrl: String,
        retrofitBuilder: Retrofit.Builder
    ): TmdbMovieImageApiInterface {
        return retrofitBuilder.baseUrl(imageBaseUrl).build()
            .create(TmdbMovieImageApiInterface::class.java)
    }
}