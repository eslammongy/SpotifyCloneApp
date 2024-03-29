package com.eslammongy.spotifycloneapp.exoPlayer

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import androidx.core.net.toUri
import com.eslammongy.spotifycloneapp.data.remote.MusicDataBase
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class FirebaseMusicSource @Inject constructor(private val musicDataBase: MusicDataBase){

    var songs = emptyList<MediaMetadataCompat>()

    suspend fun fetchMediaData() = withContext(Dispatchers.IO){
        state = MusicState.STATE_INITIALIZING
        val allSongs = musicDataBase.getAllSongs()
        songs = allSongs.map { song->
            MediaMetadataCompat.Builder()
                .putString(METADATA_KEY_ARTIST, song.subTitle)
                .putString(METADATA_KEY_MEDIA_ID, song.mediaID)
                .putString(METADATA_KEY_TITLE, song.songName)
                .putString(METADATA_KEY_DISPLAY_TITLE, song.songName)
                .putString(METADATA_KEY_DISPLAY_ICON_URI, song.songImage)
                .putString(METADATA_KEY_MEDIA_URI, song.songUrl)
                .putString(METADATA_KEY_ALBUM_ART_URI, song.songImage)
                .putString(METADATA_KEY_DISPLAY_SUBTITLE, song.subTitle)
                .putString(METADATA_KEY_DISPLAY_DESCRIPTION, song.songUrl)
                .build()
        }
        state = MusicState.STATE_INITIALIZED
    }

    fun asMediaSource(dataSourceFactory: DefaultDataSourceFactory):ConcatenatingMediaSource{
        val concatenatingMediaSource = ConcatenatingMediaSource()
        songs.forEach { song ->
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(song.getString(METADATA_KEY_MEDIA_URI).toUri())
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return  concatenatingMediaSource
    }

    fun asMediaItems() = songs.map { song ->
        val desc = MediaDescriptionCompat.Builder()
            .setMediaUri(song.getString(METADATA_KEY_MEDIA_URI).toUri())
            .setTitle(song.description.title)
            .setSubtitle(song.description.subtitle)
            .setMediaId(song.description.mediaId)
            .setIconUri(song.description.iconUri)
            .build()
        MediaBrowserCompat.MediaItem(desc, FLAG_PLAYABLE)
    }.toMutableList()
    private val onReadyListeners = mutableListOf<(Boolean)-> Unit>()

    private var state:MusicState = MusicState.STATE_CREATED
    set(value) {
        if (value == MusicState.STATE_INITIALIZED || value == MusicState.STATE_ERROR){
            synchronized(onReadyListeners){
                field = value
                onReadyListeners.forEach { listener->
                    listener(state == MusicState.STATE_INITIALIZED)
                }
            }
        }else{
            field = value
        }
    }

    fun whenReady(action: (Boolean) -> Unit):Boolean{
        if (state == MusicState.STATE_CREATED || state == MusicState.STATE_INITIALIZING){
            onReadyListeners += action
            return false
        }else{
            action(state == MusicState.STATE_INITIALIZED)
            return true
        }
    }
}

enum class MusicState {
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR
}