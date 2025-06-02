package com.example.hotmovies.infrastructure.dataRepository.tmdb

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface TmdbMovieImageApiInterface {
    @GET("w500/{filePath}")
    fun getImage(@Path("filePath") filePath: String): Call<ResponseBody>

    @GET("w500/{filePath}")
    suspend fun getImageAsync(@Path("filePath") filePath: String): Response<ResponseBody>
}