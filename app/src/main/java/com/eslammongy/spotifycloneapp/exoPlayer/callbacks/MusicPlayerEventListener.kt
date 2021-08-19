package com.eslammongy.spotifycloneapp.exoPlayer.callbacks

import android.widget.Toast
import com.eslammongy.spotifycloneapp.exoPlayer.MusicService
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player

class MusicPlayerEventListener(private val musicServices:MusicService): Player.EventListener {

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)
        if (playbackState == Player.STATE_READY && !playWhenReady){
            musicServices.stopForeground(false)
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        super.onPlayerError(error)
        Toast.makeText(musicServices, "An unknown error occurred", Toast.LENGTH_SHORT).show()
    }
    
}