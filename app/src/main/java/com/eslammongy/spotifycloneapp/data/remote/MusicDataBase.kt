package com.eslammongy.spotifycloneapp.data.remote

import com.eslammongy.spotifycloneapp.constants.Constants
import com.eslammongy.spotifycloneapp.data.entities.SongModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MusicDataBase {

    private val fireStore = FirebaseFirestore.getInstance()
    private val songsCollection = fireStore.collection(Constants.SONG_COLLECTION)

    suspend fun getAllSongs():List<SongModel>{

        return try {
            songsCollection.get().await().toObjects(SongModel::class.java)
        }catch (e:Exception){
            emptyList()
        }
    }

}