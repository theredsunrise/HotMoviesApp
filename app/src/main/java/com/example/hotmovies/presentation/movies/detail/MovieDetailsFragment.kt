package com.example.hotmovies.presentation.movies.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.hotmovies.appplication.movies.MovieImageModel
import com.example.hotmovies.databinding.FragmentMovieDetailsBinding
import com.example.hotmovies.domain.Movie
import com.example.hotmovies.presentation.movies.detail.viewModel.MovieDetailsViewModel
import com.example.hotmovies.presentation.movies.detail.viewModel.MovieDetailsViewModel.Actions.LoadMovieDetails
import com.example.hotmovies.presentation.movies.detail.viewModel.MovieDetailsViewModel.UIState
import com.example.hotmovies.presentation.movies.detail.viewModel.MovieDetailsViewModelFactory
import com.example.hotmovies.presentation.shared.fragments.DialogFragment.Actions.Accept
import com.example.hotmovies.presentation.shared.helpers.DialogFragmentFactory
import com.example.hotmovies.presentation.shared.helpers.ToolbarConfigurator
import com.example.hotmovies.presentation.shared.imageLoaders.GlideImageLoader
import com.example.hotmovies.presentation.shared.imageLoaders.ImageThumbnailLoaderContextInterface
import com.example.hotmovies.presentation.shared.transitions.TransitionFactory
import com.example.hotmovies.shared.Async
import com.example.hotmovies.shared.Constants
import com.example.hotmovies.shared.checkMainThread
import com.example.hotmovies.shared.diContainer
import com.example.hotmovies.shared.doOnLayoutAsync
import com.example.hotmovies.shared.userInteractionComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MovieDetailsFragment : Fragment() {

    private val args: MovieDetailsFragmentArgs by navArgs()
    private val movieDetailsViewModel: MovieDetailsViewModel by viewModels {
        MovieDetailsViewModelFactory(diContainer())
    }
    private val movieDetailsDialogFactory = DialogFragmentFactory("movieDetails")

    private lateinit var movie: Movie
    private lateinit var binding: FragmentMovieDetailsBinding
    private lateinit var glideImageLoader: GlideImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideImageLoader = GlideImageLoader(this)
        lifecycle.addObserver(movieDetailsDialogFactory)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        movie = args.movie
        binding = FragmentMovieDetailsBinding.inflate(inflater, container, false).apply {
            layoutViewModel = movieDetailsViewModel
            lifecycleOwner = viewLifecycleOwner
            movieId = movie.id
        }
        sharedElementEnterTransition =
            TransitionFactory.materialContainerTransform(userInteractionComponent = userInteractionComponent)
        postponeDetailSharedTransitions()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main.immediate) {
            ToolbarConfigurator.bindToolbarToFragmentAsync(
                binding.toolbar,
                this@MovieDetailsFragment
            )
            view.doOnLayoutAsync()
            loadImage(movie.backdropPath ?: movie.posterPath.orEmpty(), binding.backdropImage)

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch(Dispatchers.Main.immediate) {
                    glideImageLoader.imageLoadingResultFlow.collect { _ ->
                        startPostponedEnterTransition()
                    }
                }
                launch(Dispatchers.Main.immediate) {
                    movieDetailsViewModel.state.collect { state ->
                        processLoadingMovieDetailsAction(movie.id, state)
                        processDataOfMovieDetailsAction(state)
                    }
                }
                launch(Dispatchers.Main.immediate) {
                    movieDetailsDialogFactory.state.collect { action ->
                        checkMainThread()
                        if (action is Accept) else {
                            return@collect
                        }
                        movieDetailsViewModel.doAction(LoadMovieDetails(movie.id))
                    }
                }
            }
        }
    }

    private fun loadPosterImage(url: String) {
        val context = object : ImageThumbnailLoaderContextInterface {
            override val loadingTimestamp = 0
            override val animationDuration = Constants.AnimationDurations.DEFAULT
            override fun onLoadState(state: Async<Any>) {
                binding.indicator.isVisible = state.isProgress
            }
        }
        glideImageLoader.asAnimatedThumbnailInto(context, MovieImageModel(url), binding.posterImage)
    }

    private fun loadImage(url: String, target: ImageView) {
        glideImageLoader.into(MovieImageModel(url), target)
    }

    private fun postponeDetailSharedTransitions() {
        sharedElementEnterTransition = TransitionFactory.materialContainerTransform(
            userInteractionComponent, Constants.AnimationDurations.DEFAULT
        )
        postponeEnterTransition()
    }

    private fun processDataOfMovieDetailsAction(state: UIState) {
        checkMainThread()
        if (state.loadAction.content.isSuccessTrue) {
            loadPosterImage(state.movieDetails.posterUrl.orEmpty())
        } else {
            glideImageLoader.clear(binding.posterImage)
        }
    }

    private fun processLoadingMovieDetailsAction(movieId: Int, state: UIState) {
        checkMainThread()
        val loadAction = state.loadAction.getContentIfNotHandled() ?: return
        when {
            loadAction.isSuccessFalse -> movieDetailsViewModel.doAction(LoadMovieDetails(movieId))
            loadAction is Async.Failure -> movieDetailsDialogFactory.showErrorDialog(
                findNavController(),
                loadAction.exception
            )
        }
    }
}