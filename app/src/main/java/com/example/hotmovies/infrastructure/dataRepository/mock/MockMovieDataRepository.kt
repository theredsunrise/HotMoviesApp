package com.example.hotmovies.infrastructure.dataRepository.mock

import android.content.Context
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import com.example.hotmovies.domain.Movie
import com.example.hotmovies.domain.MovieDetails
import com.example.hotmovies.domain.MoviesInfo
import com.example.hotmovies.domain.User
import com.example.hotmovies.infrastructure.NetworkStatusResolver
import com.example.hotmovies.infrastructure.dataRepository.MovieDataRepositoryInterface
import com.example.hotmovies.infrastructure.dataRepository.mock.MockMovieDataRepository.Exceptions.NoNetworkConnectionException
import com.example.hotmovies.shared.checkNotMainThread
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MockMovieDataRepository(
    private val appContext: Context,
    @DrawableRes private val avatarId: Int
) :
    MovieDataRepositoryInterface {

    sealed class Exceptions(msg: String) : Exception(msg) {
        class NoNetworkConnectionException : Exceptions("No network connection is available.")
    }

    override fun getUser(): Flow<User> = flow {
        checkNotMainThread()
        delay(100)
        if (!NetworkStatusResolver.isNetworkAvailable(appContext)) throw NoNetworkConnectionException()
        val user = User(
            123,
            "Brano Bench",
            "theredsunrise",
            BitmapFactory.decodeResource(appContext.resources, avatarId)
        )
        emit(user)
    }

    override fun getMovieDetails(movieId: Int): Flow<MovieDetails> = flow {
        checkNotMainThread()
        delay(2000)
        if (!NetworkStatusResolver.isNetworkAvailable(appContext)) throw NoNetworkConnectionException()
        val movieDetails = MovieDetails(
            1,
            "backdrop",
            "poster",
            "Test title wekljwrljwlrejwlerkjwlrjwlrjwlrj",
            "Original title",
            "This is test. aslakjdlakdjalkdaldjaldkjaldjalkdjaldjalkdjalkdjqwlkejqlejqlwejqlwejqlwekjqlwkejqlwejlqwej",
            listOf(Movie.Genre.TV_MOVIE),
            565877,
            7.66,
            876786,
            "14 Maj 2014"
        )
        emit(movieDetails)
    }

    override fun getTrendingMoviesInfo(pageId: Int, itemsPerPage: Int): Flow<MoviesInfo> = flow {
        checkNotMainThread()
        if (!NetworkStatusResolver.isNetworkAvailable(appContext)) throw NoNetworkConnectionException()
        val movies = (1..40).map { id ->
            Movie(
                id,
                1,
                "backdrop",
                "Test title wekljwrljwlrejwlerkjwlrjwlrjwlrj",
                "Original title",
                "This is test. aslakjdlakdjalkdaldjaldkjaldjalkdjaldjalkdjalkdjqwlkejqlejqlwejqlwejqlwekjqlwkejqlwejlqwej",
                "poster",
                listOf(Movie.Genre.TV_MOVIE),
                4.0,
                "Oktober",
                3.0,
                345
            )
        }
        val moviesInfo = MoviesInfo(1, movies, 2, 40)
        emit(moviesInfo)
    }
}