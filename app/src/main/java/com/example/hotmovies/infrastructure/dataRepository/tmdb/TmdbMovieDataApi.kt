package com.example.hotmovies.infrastructure.dataRepository.tmdb

import com.example.hotmovies.BuildConfig
import com.example.hotmovies.infrastructure.dataRepository.tmdb.dtos.MovieDetailsDto
import com.example.hotmovies.infrastructure.dataRepository.tmdb.dtos.MoviesInfoDto
import com.example.hotmovies.infrastructure.dataRepository.tmdb.dtos.UserDto
import com.example.hotmovies.shared.toUse
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbMovieDataApiInterface {
    @GET("3/account/{accountId}")
    suspend fun getUser(@Path("accountId") accountId: String): retrofit2.Response<UserDto>

    @GET("3/movie/{movieId}")
    suspend fun getMovieDetails(@Path("movieId") movieId: Int): retrofit2.Response<MovieDetailsDto>

    @GET("3/trending/movie/day")
    suspend fun getTrendingMoviesInfo(@Query("page") pageId: Int): retrofit2.Response<MoviesInfoDto>
}

class AuthorizationInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newUrl = request.url.newBuilder()
            .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY.toUse(12)).build()
        val newRequest = request.newBuilder().url(newUrl).addHeader(
            "Authorization",
            "Bearer ${BuildConfig.TMDB_BEARER.toUse(45)}"
        ).build()
        return chain.proceed(newRequest)
    }
}