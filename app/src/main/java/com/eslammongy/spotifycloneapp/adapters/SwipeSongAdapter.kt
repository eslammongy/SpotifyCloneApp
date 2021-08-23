package com.eslammongy.spotifycloneapp.adapters

import androidx.recyclerview.widget.AsyncListDiffer
import com.eslammongy.spotifycloneapp.R
import kotlinx.android.synthetic.main.swipe_item.view.*

class SwipeSongAdapter  : BaseSongAdapter(R.layout.swipe_item) {

    override var differ = AsyncListDiffer(this , diffUtilsCallback)

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songsList[position]
        val songName = "${song.songName} - ${song.subTitle}"
        holder.itemView.tvSwipeSongName.text = songName

       holder.itemView.setOnClickListener {
           onItemClickListener?.let { click->
            click(song)
           }
       }
    }
}