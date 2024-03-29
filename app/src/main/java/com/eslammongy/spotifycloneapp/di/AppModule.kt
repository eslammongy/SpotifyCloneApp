package com.eslammongy.spotifycloneapp.di

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.eslammongy.spotifycloneapp.R
import com.eslammongy.spotifycloneapp.adapters.SwipeSongAdapter
import com.eslammongy.spotifycloneapp.exoPlayer.MusicServicesConnection
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideMusicServicesConnection(
        @ApplicationContext context: Context) = MusicServicesConnection(context)

    @Singleton
    @Provides
    fun provideSwipeSongAdapter() = SwipeSongAdapter()

    @Singleton
    @Provides
    fun provideGlideInstance(@ApplicationContext context:Context)
     = Glide.with(context).setDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.ic_undraw_happy_music)
            .error(R.drawable.ic_undraw_happy_music)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
     )


}