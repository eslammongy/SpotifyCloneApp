package com.eslammongy.spotifycloneapp.exoPlayer

import android.content.ComponentName
import android.content.Context
import android.media.browse.MediaBrowser
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.eslammongy.spotifycloneapp.constants.Constants.NETWORK_ERROR
import com.eslammongy.spotifycloneapp.constants.Event
import com.eslammongy.spotifycloneapp.constants.Resource

class MusicServicesConnection(context: Context) {

    private val _isConnected = MutableLiveData<Event<Resource<Boolean>>>()
    val isConnnected: LiveData<Event<Resource<Boolean>>> = _isConnected

    private val _netWorkError = MutableLiveData<Event<Resource<Boolean>>>()
    val netWorkError: LiveData<Event<Resource<Boolean>>> = _netWorkError

    private val _playbackState= MutableLiveData<PlaybackStateCompat?>()
    val playbackState: LiveData<PlaybackStateCompat?> = _playbackState

    private val _currentPlayingSong = MutableLiveData<MediaMetadataCompat?>()
    val currentPlayingSong: LiveData<MediaMetadataCompat?> = _currentPlayingSong

    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)

    lateinit var mediaController:MediaControllerCompat

    private val mediaBrowser = MediaBrowserCompat(context , ComponentName(
        context,
        MusicService::class.java
    ),mediaBrowserConnectionCallback , null).apply {
        connect()
    }

    val transportController:MediaControllerCompat.TransportControls
    get() = mediaController.transportControls

    fun subscribtion(parentID:String , callback:MediaBrowserCompat.SubscriptionCallback){
        mediaBrowser.subscribe(parentID , callback)
    }

    fun unSubscribtion(parentID:String , callback:MediaBrowserCompat.SubscriptionCallback){
        mediaBrowser.unsubscribe(parentID , callback)
    }

    private inner class MediaBrowserConnectionCallback(private val context: Context):MediaBrowserCompat.ConnectionCallback(){

        override fun onConnected() {
            mediaController = MediaControllerCompat(context , mediaBrowser.sessionToken).apply {
                registerCallback(MediaControllerCallbac())
            }
        }

        override fun onConnectionSuspended() {
            _isConnected.postValue(Event(Resource.error(
                "Your Connection Is Suspended .." , false)))
        }

        override fun onConnectionFailed() {
            _isConnected.postValue(Event(Resource.error(
                "Couldn't Connect To Media Browser .." , false)))
        }
    }

    private inner class MediaControllerCallbac:MediaControllerCompat.Callback(){

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            _playbackState.postValue(state)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            _currentPlayingSong.postValue(metadata)
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            super.onSessionEvent(event, extras)

            when(event){
                NETWORK_ERROR ->{
                    _netWorkError.postValue(Event(Resource.error(
                        "Couldn't Connect To The Server. Please Try Again Or Check Your Internet Connection ..",
                        null
                    )))
                }
            }
        }

        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }
    }

}