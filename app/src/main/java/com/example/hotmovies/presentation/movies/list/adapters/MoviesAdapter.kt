package com.example.hotmovies.presentation.movies.list.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import androidx.paging.LoadState.Loading
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.hotmovies.R
import com.example.hotmovies.appplication.movies.MovieImageModel
import com.example.hotmovies.databinding.MovieListItemBinding
import com.example.hotmovies.domain.Movie
import com.example.hotmovies.presentation.shared.helpers.DrawableFactory
import com.example.hotmovies.presentation.shared.imageLoaders.ImageLoaderInterface
import com.example.hotmovies.presentation.shared.imageLoaders.ImageThumbnailLoaderContextInterface
import com.example.hotmovies.shared.Constants
import com.example.hotmovies.shared.ResultState
import kotlin.random.Random

class MoviesAdapter(
    private val imageLoader: ImageLoaderInterface,
    private val onItemClicked: (movie: Movie, transitionElements: Set<View>) -> Unit
) :
    PagingDataAdapter<Movie, MoviesAdapter.ViewHolder>(REPO_COMPARATOR) {

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
                oldItem == newItem
        }

        private val ITEM_TYPES =
            arrayOf(R.dimen.movie_image_height_type_long, R.dimen.movie_image_height_type_short)
    }

    private var adapterLoadingTimestamp = 0

    init {
        addLoadStateListener {
            if (arrayOf(it.refresh, it.prepend, it.append).any { it is Loading }) {
                adapterLoadingTimestamp = Random.nextInt()
            }
        }
    }

    class ViewHolder(val binding: MovieListItemBinding, override val loadingTimestamp: Int) :
        RecyclerView.ViewHolder(binding.root),
        ImageThumbnailLoaderContextInterface {

        override val animationDuration: Long = Constants.AnimationDurations.DEFAULT
        override val errorDrawable: Drawable get() = DrawableFactory.getInstance(itemView.context).cross

        override fun onLoadState(state: ResultState<Any>) {
            binding.indicator.isVisible = state.isProgress
            binding.root.isClickable = state.isSuccess
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = getItem(position) ?: return
        holder.binding.apply {
            executePendingBindings()

            val movieId = movie.id.toString()
            itemRoot.setTag(R.id.movieIdTag, movie.backdropPath ?: movie.posterPath.orEmpty())
            movieTitle.text = movie.title
            movieOverview.text = movie.overview
            ratingBar.rating = movie.voteAverage.let { it.toFloat() * 5f * 0.1f }
            itemRoot.transitionName = "root_$movieId"
            root.setOnClickListener {
                this@MoviesAdapter.onItemClicked(movie, setOf(itemRoot))
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val index = when (position % 8) {
            in 0..2 -> 0
            3 -> 1
            in 4..6 -> 0
            else -> 1
        }
        return ITEM_TYPES[index]
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.binding.apply {
            val url = itemRoot.getTag(R.id.movieIdTag) as String
            imageLoader.asAnimatedThumbnailInto(
                holder, MovieImageModel(
                    url
                ), this.backdropImage
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DataBindingUtil.inflate<MovieListItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.movie_list_item,
            parent,
            false
        ).let { binding ->
            binding.backdropImage.updateLayoutParams<ViewGroup.LayoutParams> {
                height = parent.resources.getDimensionPixelSize(viewType)
            }
            ViewHolder(binding, adapterLoadingTimestamp)
        }
}