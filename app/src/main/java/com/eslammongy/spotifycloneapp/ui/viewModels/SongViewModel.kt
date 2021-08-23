package com.eslammongy.spotifycloneapp.ui.viewModels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eslammongy.spotifycloneapp.constants.Constants.UPDATE_PLAYER_POSITION_INTERVAL
import com.eslammongy.spotifycloneapp.exoPlayer.MusicService
import com.eslammongy.spotifycloneapp.exoPlayer.MusicServicesConnection
import com.eslammongy.spotifycloneapp.exoPlayer.currentPlayBACKPosition
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SongViewModel @ViewModelInject
constructor(musicServicesConnection: MusicServicesConnection):ViewModel(){

    private val playbackState = musicServicesConnection.playbackState
    private val _currentSongDuration = MutableLiveData<Long>()
    val currentSongDuration:LiveData<Long> = _currentSongDuration

    private val _currentPlayerPosition = MutableLiveData<Long>()
    private val currentPlayerPosition:LiveData<Long> = _currentPlayerPosition

    init {
        updateCurrentPlayerPosition()
    }
    private fun updateCurrentPlayerPosition(){
        viewModelScope.launch {
            while (true){
                val pos = playbackState.value?.currentPlayBACKPosition
                if (currentPlayerPosition.value != pos){
                    _currentPlayerPosition.postValue(pos)
                    _currentSongDuration.postValue(MusicService.curSongDuration)
                }
                delay(UPDATE_PLAYER_POSITION_INTERVAL)
            }
        }
    }


}