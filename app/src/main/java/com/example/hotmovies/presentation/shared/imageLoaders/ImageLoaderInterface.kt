package com.example.hotmovies.presentation.shared.imageLoaders

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.example.hotmovies.presentation.shared.views.ImageViewWithAnimator
import com.example.hotmovies.shared.ResultState

interface ImageThumbnailLoaderContextInterface {
    val loadingTimestamp: Int
    val animationDuration: Long
    val errorDrawable: Drawable
    fun onLoadState(state: ResultState<Any>)
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