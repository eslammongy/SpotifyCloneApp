package com.eslammongy.spotifyclone.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.eslammongy.spotifyclone.data.entities.Song
import com.eslammongy.spotifyclone.other.Constants.SONG_COLLECTION
import kotlinx.coroutines.tasks.await

class MusicDatabase {

    private val firestore = FirebaseFirestore.getInstance()
    private val songCollection = firestore.collection(SONG_COLLECTION)

    suspend fun getAllSongs(): List<Song> {
        return try {
            songCollection.get().await().toObjects(Song::class.java)
        } catch(e: Exception) {
            emptyList()
        }
    }
}