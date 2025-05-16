package com.example.hotmovies.appplication

//class DIContainer(val appContext: Context) {
//
//    //private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
//
//    private val tmdbMovieDataApiService: TmdbMovieDataApiInterface by lazy {
//        TmdbMovieDataApiServiceFactory.create()
//    }
//
//    private val tmdbMovieImageApiService: com.example.hotmovies.infrastructure.dataRepository.tmdb.TmdbMovieImageApiInterface by lazy {
//        TmdbMovieImageApiFactory.create()
//    }
//
//    val loginRepository: LoginRepositoryInterface by lazy {
//        LoginRepository(appContext)
//    }
//
//    val settingsRepository: SettingsRepositoryInterface by lazy {
//        SettingsRepository(appContext.dataStore)
//    }
//
//    val pager: Pager<Int, Movie> by lazy {
//        val pagingConfig = PagingConfig(20, enablePlaceholders = false)
//        Pager(
//            config = pagingConfig,
//            pagingSourceFactory = {
//                MoviePagingSource(
//                    movieDataRepository,
//                    Dispatchers.IO
//                )
//            }
//        )
//    }
//
//    val movieImageRepository: MovieImageRepositoryInterface by lazy {
//        TmdbMovieImageRepository(tmdbMovieImageApiService)
//    }
//
//    val _movieImageRepository: MovieImageRepositoryInterface by lazy {
//        MockMovieImageRepository(appContext, R.drawable.vector_background)
//    }
//
//    val movieDataRepository: MovieDataRepositoryInterface by lazy {
//        TmdbMovieDataRepository(tmdbMovieDataApiService, movieImageRepository)
//    }
//
//    val _movieDataRepository: MovieDataRepositoryInterface by lazy {
//        MockMovieDataRepository(appContext, R.drawable.vector_background)
//    }
//}