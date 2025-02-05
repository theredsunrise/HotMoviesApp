package com.example.hotmovies.infrastructure.dataRepository.tmdb

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface TmdbMovieImageApiInterface {
    @GET("w500/{filePath}")
    fun getImage(@Path("filePath") filePath: String): Call<ResponseBody>

    @GET("w500/{filePath}")
    suspend fun getImageAsync(@Path("filePath") filePath: String): Response<ResponseBody>
}

object TmdbMovieImageApiFactory {
    private const val BASE_URL =
        "https://image.tmdb.org/t/p/"
    private val instance: Retrofit

    init {
        val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .cache(null)
            .build()
        com.example.hotmovies.infrastructure.dataRepository.tmdb.TmdbMovieImageApiFactory.instance =
            Retrofit.Builder().baseUrl(
                com.example.hotmovies.infrastructure.dataRepository.tmdb.TmdbMovieImageApiFactory.BASE_URL
            )
                .addConverterFactory(GsonConverterFactory.create())
                .client(client).build()
    }

    fun create(): com.example.hotmovies.infrastructure.dataRepository.tmdb.TmdbMovieImageApiInterface {
        return com.example.hotmovies.infrastructure.dataRepository.tmdb.TmdbMovieImageApiFactory.instance.create(
            com.example.hotmovies.infrastructure.dataRepository.tmdb.TmdbMovieImageApiInterface::class.java
        )
    }
}