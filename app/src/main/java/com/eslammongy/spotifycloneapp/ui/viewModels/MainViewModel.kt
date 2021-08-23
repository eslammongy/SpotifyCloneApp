package com.eslammongy.spotifycloneapp.ui.viewModels

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eslammongy.spotifycloneapp.constants.Constants.MEDIA_ROOT_ID
import com.eslammongy.spotifycloneapp.constants.Resource
import com.eslammongy.spotifycloneapp.data.entities.SongModel
import com.eslammongy.spotifycloneapp.exoPlayer.MusicServicesConnection
import com.eslammongy.spotifycloneapp.exoPlayer.isPlayEnabled
import com.eslammongy.spotifycloneapp.exoPlayer.isPlaying
import com.eslammongy.spotifycloneapp.exoPlayer.isPrepared

class MainViewModel
@ViewModelInject constructor(
    private val musicServicesConnection: MusicServicesConnection):ViewModel() {

    private val _mediaItems = MutableLiveData<Resource<List<SongModel>>>()
    val mediaItems:LiveData<Resource<List<SongModel>>> = _mediaItems

    val isConnected = musicServicesConnection.isConnected
    val networkError = musicServicesConnection.netWorkError
    val currentPlayingSong = musicServicesConnection.currentPlayingSong
    val playbackState = musicServicesConnection.playbackState

    init {
        _mediaItems.postValue(Resource.loading(null))
        musicServicesConnection.subscription(MEDIA_ROOT_ID ,
            object :MediaBrowserCompat.SubscriptionCallback(){

                override fun onChildrenLoaded(
                    parentId: String,
                    children: MutableList<MediaBrowserCompat.MediaItem>
                ) {
                    super.onChildrenLoaded(parentId, children)
                    val items = children.map {
                        SongModel(
                            it.mediaId!!,
                            it.description.title.toString(),
                            it.description.subtitle.toString(),
                            it.description.mediaUri.toString(),
                            it.description.iconUri.toString())
                    }
                    _mediaItems.postValue(Resource.success(items))
                }

        })
    }

    fun skipToNextSong(){
        musicServicesConnection.transportController.skipToNext()
    }

    fun skipToPreviousSong(){
        musicServicesConnection.transportController.skipToPrevious()
    }

    fun seekTo(position:Long){
        musicServicesConnection.transportController.seekTo(position)
    }

    fun playingOrToggelSong(mediaItem:SongModel , toggle:Boolean = false){

        val isPrepared = playbackState.value?.isPrepared ?: false
        if (isPrepared && mediaItem.mediaID ==
            currentPlayingSong.value?.getString(METADATA_KEY_MEDIA_ID)){

            playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying -> if (toggle) musicServicesConnection.transportController.pause()
                    playbackState.isPlayEnabled -> musicServicesConnection.transportController.play()
                    else ->Unit
                }
            }
        }else{
            musicServicesConnection.transportController.playFromMediaId(mediaItem.mediaID , null)
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServicesConnection.unSubscription(MEDIA_ROOT_ID , object :MediaBrowserCompat.SubscriptionCallback(){})
    }


}