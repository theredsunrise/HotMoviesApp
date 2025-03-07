package com.example.hotmovies.presentation.shared.imageLoaders

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DataSource.REMOTE
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.hotmovies.R
import com.example.hotmovies.presentation.shared.views.ImageViewWithAnimator
import com.example.hotmovies.shared.ResultState
import com.example.hotmovies.shared.progress
import com.example.hotmovies.shared.state
import com.example.hotmovies.shared.stateFailure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.UnknownHostException
import kotlin.math.hypot

class GlideImageLoader private constructor() : ImageLoaderInterface {
    private var _imageLoadingResultFlow = MutableStateFlow<ResultState<Any>>(progress)
    val imageLoadingResultFlow = _imageLoadingResultFlow.asStateFlow()
    private lateinit var requestManager: RequestManager

    constructor(fragment: Fragment) : this() {
        requestManager = Glide.with(fragment)
    }

    constructor(context: Context) : this() {
        requestManager = Glide.with(context)
    }

    override fun clear(imageView: ImageView) {
        requestManager.clear(imageView)
    }

    override fun into(model: Any, imageView: ImageView) {
        _imageLoadingResultFlow.tryEmit(progress)
        requestManager
            .asDrawable()
            .load(model)
            .dontTransform()
            .dontAnimate().listener(requestListener()).into(imageView)
    }

    override fun asAnimatedThumbnailInto(
        context: ImageThumbnailLoaderContextInterface,
        model: Any,
        imageView: ImageViewWithAnimator
    ) {
        imageView.cancelAnimator()
        imageView.visibility = View.INVISIBLE

        context.onLoadState(progress)
        _imageLoadingResultFlow.tryEmit(progress)

        requestManager
            .asDrawable()
            .load(model)
            .error(context.errorDrawable)
            .dontTransform()
            .sizeMultiplier(0.5f)
            .dontAnimate()
            .listener(animatedRequestListener(context, imageView)).into(imageView)
    }

    private fun requestListener() = object : RequestListener<Drawable> {

        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>,
            isFirstResource: Boolean
        ): Boolean {
            _imageLoadingResultFlow.tryEmit((e ?: UnknownHostException()).stateFailure())
            return false
        }

        override fun onResourceReady(
            resource: Drawable,
            model: Any,
            target: Target<Drawable>?,
            dataSource: DataSource,
            isFirstResource: Boolean
        ): Boolean {
            _imageLoadingResultFlow.tryEmit(model.state())
            return false
        }
    }

    private fun animatedRequestListener(
        context: ImageThumbnailLoaderContextInterface,
        imageView: ImageViewWithAnimator
    ) = object : RequestListener<Drawable> {

        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>,
            isFirstResource: Boolean
        ): Boolean {
            imageView.scaleType = ImageView.ScaleType.CENTER
            imageView.visibility = View.VISIBLE

            val someException = e ?: UnknownHostException()
            context.onLoadState(someException.stateFailure())
            _imageLoadingResultFlow.tryEmit(someException.stateFailure())
            return false
        }

        override fun onResourceReady(
            resource: Drawable,
            model: Any,
            target: Target<Drawable>?,
            dataSource: DataSource,
            isFirstResource: Boolean
        ): Boolean {
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.visibility = View.VISIBLE

            val loadingTimestamp = context.loadingTimestamp
            val allowForceAnimationCondition =
                loadingTimestamp != 0 && loadingTimestamp != imageView.getTag(R.id.forceAnimationTag)

            if ((isFirstResource && imageView.isAttachedToWindow && dataSource == REMOTE) ||
                allowForceAnimationCondition
            ) {
                imageView.setTag(R.id.forceAnimationTag, loadingTimestamp)
                val finalRadius =
                    hypot(imageView.width.toDouble(), imageView.height.toDouble())
                        .toFloat()

                imageView.runAnimator {
                    ViewAnimationUtils.createCircularReveal(
                        imageView,
                        0,
                        0,
                        0f,
                        finalRadius
                    ).setDuration(context.animationDuration)
                }
            }

            context.onLoadState(model.state())
            _imageLoadingResultFlow.tryEmit(model.state())
            return false
        }
    }
}