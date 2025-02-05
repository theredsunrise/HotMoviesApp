package com.example.hotmovies.appplication.movies

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Priority
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.example.hotmovies.appplication.login.interfaces.MovieImageRepositoryInterface
import com.example.hotmovies.presentation.shared.CustomApplication
import com.example.hotmovies.shared.checkNotMainThread
import java.io.InputStream

data class MovieImageModel(val id: String)

private class MovieImageFetcher(
    private val model: MovieImageModel,
    private val imageRepository: MovieImageRepositoryInterface
) :
    DataFetcher<InputStream> {

    private var stream: InputStream? = null
    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) =
        try {
            checkNotMainThread()
            val stream = imageRepository.getImage(model.id)
            callback.onDataReady(stream)
            this.stream = stream
        } catch (e: Exception) {
            callback.onLoadFailed(e)
        }

    override fun cleanup() {
        stream?.apply { close() }
    }

    override fun cancel() {}
    override fun getDataClass() = InputStream::class.java
    override fun getDataSource() = DataSource.REMOTE
}

private class MovieImageModelLoader(private val imageRepository: MovieImageRepositoryInterface) :
    ModelLoader<MovieImageModel, InputStream> {
    override fun buildLoadData(
        model: MovieImageModel,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? =
        ModelLoader.LoadData(ObjectKey(model), MovieImageFetcher(model, imageRepository))

    override fun handles(model: MovieImageModel) = true
}

private class MovieImageModelLoaderFactory(private val imageRepository: MovieImageRepositoryInterface) :
    ModelLoaderFactory<MovieImageModel, InputStream> {
    override fun build(unused: MultiModelLoaderFactory) = MovieImageModelLoader(imageRepository)
    override fun teardown() {}
}

@GlideModule
class CustomGlideModule : AppGlideModule() {
    private lateinit var imageRepository: MovieImageRepositoryInterface

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        require(context is CustomApplication)
        builder.setDefaultRequestOptions(
            RequestOptions()
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        )
        imageRepository = context.diContainer.tmdbMovieImageRepository
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.prepend(
            MovieImageModel::class.java,
            InputStream::class.java,
            MovieImageModelLoaderFactory(imageRepository)
        )
    }
}
