package com.example.hotmovies.presentation.shared.imageLoaders

import android.widget.ImageView
import com.example.hotmovies.presentation.shared.views.ImageViewWithAnimator
import com.example.hotmovies.shared.Async

interface ImageThumbnailLoaderContextInterface {
    val loadingTimestamp: Int
    val animationDuration: Long
    fun onLoadState(state: Async<Any>)
}

interface ImageLoaderInterface {
    fun asAnimatedThumbnailInto(
        context: ImageThumbnailLoaderContextInterface,
        model: Any,
        imageView: ImageViewWithAnimator
    )

    fun into(
        model: Any,
        imageView: ImageView
    )

    fun clear(imageView: ImageView)
}