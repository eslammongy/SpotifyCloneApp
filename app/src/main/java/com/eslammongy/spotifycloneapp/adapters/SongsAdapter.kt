package com.eslammongy.spotifycloneapp.adapters


import androidx.recyclerview.widget.AsyncListDiffer
import com.bumptech.glide.RequestManager
import com.eslammongy.spotifycloneapp.R
import kotlinx.android.synthetic.main.song_list_item.view.*
import javax.inject.Inject

class SongsAdapter @Inject constructor(private val glide:RequestManager)
    : BaseSongAdapter(R.layout.song_list_item) {

    override var differ = AsyncListDiffer(this , diffUtilsCallback)

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songsList[position]
        holder.itemView.tvSongName.text = song.songName
        holder.itemView.tvSingerName.text = song.subTitle
        glide.load(song.songImage).into(holder.itemView.ivSongImage)
       holder.itemView.setOnClickListener {
           onItemClickListener?.let { click->
            click(song)
           }
       }
    }
}