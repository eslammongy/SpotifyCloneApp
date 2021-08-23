package com.eslammongy.spotifycloneapp.exoPlayer

import android.support.v4.media.MediaMetadataCompat
import com.eslammongy.spotifycloneapp.data.entities.SongModel


fun MediaMetadataCompat.toSong():SongModel?{

    return description?.let {
        SongModel(it.mediaId?: "",
        it.title.toString(),
        it.subtitle.toString(),
        it.mediaUri.toString(),
        it.iconUri.toString())
    }
}