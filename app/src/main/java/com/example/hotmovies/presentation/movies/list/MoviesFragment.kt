package com.example.hotmovies.presentation.movies.list

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type.displayCutout
import androidx.core.view.WindowInsetsCompat.Type.statusBars
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.Fade
import androidx.transition.TransitionInflater
import com.example.hotmovies.R
import com.example.hotmovies.databinding.FragmentMoviesBinding
import com.example.hotmovies.domain.Movie
import com.example.hotmovies.presentation.movies.list.adapters.GridSpacingItemDecoration
import com.example.hotmovies.presentation.movies.list.adapters.MoviesAdapter
import com.example.hotmovies.presentation.movies.list.uiStateProcessors.ProcessMovies
import com.example.hotmovies.presentation.movies.list.uiStateProcessors.ProcessUserDetails
import com.example.hotmovies.presentation.movies.list.uiStateProcessors.ProcessUserLogout
import com.example.hotmovies.presentation.movies.list.viewModel.MoviesViewModel
import com.example.hotmovies.presentation.movies.list.viewModel.MoviesViewModel.Intents.ShowingMovieDetail
import com.example.hotmovies.presentation.movies.list.viewModel.MoviesViewModelFactory
import com.example.hotmovies.presentation.shared.helpers.ToolbarConfigurator
import com.example.hotmovies.presentation.shared.imageLoaders.GlideImageLoader
import com.example.hotmovies.presentation.shared.layouts.CustomStaggeredGridLayoutManager
import com.example.hotmovies.presentation.shared.transitions.TransitionFactory
import com.example.hotmovies.presentation.shared.transitions.UserInteractionTransitionWrapper
import com.example.hotmovies.shared.Constants
import com.example.hotmovies.shared.diContainer
import com.example.hotmovies.shared.doOnLayoutAsync
import com.example.hotmovies.shared.px
import com.example.hotmovies.shared.safeNavigation
import com.example.hotmovies.shared.toPairs
import com.example.hotmovies.shared.transitionToStartAsync
import com.example.hotmovies.shared.userInteractionComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MoviesFragment : Fragment() {
    companion object {
        private val GRID_LAYOUT_ITEM_SPACING = 10.px
    }

    private val args: MoviesFragmentArgs by navArgs()
    private val moviesViewModel: MoviesViewModel by viewModels {
        MoviesViewModelFactory(diContainer())
    }

    private lateinit var binding: FragmentMoviesBinding

    private lateinit var processUserDetails: ProcessUserDetails
    private lateinit var processUserLogout: ProcessUserLogout
    private lateinit var processCollectingMovies: ProcessMovies

    private var isViewModelCreated = false
    private var isFirstInstanceOfFragment = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isFirstInstanceOfFragment = savedInstanceState == null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        isViewModelCreated = moviesViewModel.singleUseCreatedFlag
        binding = FragmentMoviesBinding.inflate(inflater, container, false).apply {
            layoutViewModel = moviesViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        processUserDetails = ProcessUserDetails(this, moviesViewModel)
        processUserLogout = ProcessUserLogout(this, moviesViewModel)
        processCollectingMovies = ProcessMovies(this, binding, moviesViewModel)

        enterTransition = TransitionFactory.materialSharedAxis(userInteractionComponent, true)
        returnTransition = TransitionFactory.materialSharedAxis(userInteractionComponent, false)
        exitTransition = TransitionFactory.custom(
            userInteractionComponent,
            Fade(Fade.MODE_OUT),
            duration = Constants.AnimationDurations.DEFAULT
        )

        setupInsets()
        postponeLoginSharedTransitions()
        postponeMovieDetailSharedTransitions()
        return binding.root
    }

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main.immediate) {
            ToolbarConfigurator.bindToolbarToFragmentAsync(binding.toolbar, this@MoviesFragment)
            view.doOnLayoutAsync()
            startLoginSharedTransitionsAsync()

            binding.motionLayout.transitionToStartAsync(true)
            binding.bindRecyclerView()

            moviesViewModel.sendIntent(ShowingMovieDetail(false))

            viewLifecycleOwner.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                processUserLogout.collect(this@repeatOnLifecycle)
                processCollectingMovies.collect(this@repeatOnLifecycle)
                processUserDetails.collect(this@repeatOnLifecycle)
            }
        }
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val detectedInsets = insets.getInsets(statusBars() or displayCutout())
            view.updatePadding(
                view.paddingLeft,
                detectedInsets.top,
                view.paddingRight,
                view.paddingBottom
            )
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun postponeMovieDetailSharedTransitions() {
        moviesViewModel.state.value.movieDetailAction.apply {
            if (!hasBeenHandled && content) {
                postponeEnterTransition()
            }
        }
    }

    private fun postponeLoginSharedTransitions() {
        if (isViewModelCreated && isFirstInstanceOfFragment && args.useLoginSharedTransition) {
            val transition = TransitionInflater.from(requireContext())
                .inflateTransition(R.transition.login_transition)!!
            sharedElementEnterTransition = TransitionFactory.custom(
                userInteractionComponent, transition, Constants.AnimationDurations.DEFAULT
            )
            sharedElementReturnTransition = null
            postponeEnterTransition()
        }
    }

    private suspend fun startLoginSharedTransitionsAsync() {
        if (isViewModelCreated && isFirstInstanceOfFragment && args.useLoginSharedTransition) {
            startPostponedEnterTransition()
            (sharedElementEnterTransition as? UserInteractionTransitionWrapper)?.doOnEndOrCancelAsync()
        }
    }

    private fun FragmentMoviesBinding.bindRecyclerView() {
        (recyclerView.layoutManager as CustomStaggeredGridLayoutManager).also { layoutManager ->
            layoutManager.spacing = GRID_LAYOUT_ITEM_SPACING
            recyclerView.addItemDecoration(GridSpacingItemDecoration(GRID_LAYOUT_ITEM_SPACING))
        }
        recyclerView.adapter =
            MoviesAdapter(GlideImageLoader(this@MoviesFragment), ::onMovieItemClicked)
    }

    private fun onMovieItemClicked(movie: Movie, transitionElements: Set<View>) =
        findNavController().safeNavigation(R.id.moviesFragment) {

            moviesViewModel.sendIntent(ShowingMovieDetail(true))
            val navDirection =
                MoviesFragmentDirections.actionMovieFragmentToMovieDetailFragment(movie)
            val extras = FragmentNavigatorExtras(*transitionElements.toPairs())
            navigate(navDirection, extras)
        }
}
