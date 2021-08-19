package com.eslammongy.spotifycloneapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.eslammongy.spotifycloneapp.data.entities.SongModel
import com.eslammongy.spotifycloneapp.databinding.SongListItemBinding
import javax.inject.Inject

class SongsAdapter @Inject constructor(private val glide:RequestManager):RecyclerView.Adapter<SongsAdapter.SongViewHolder>(){

    class SongViewHolder(val binding:SongListItemBinding):RecyclerView.ViewHolder(binding.root)

    private val diffUtilsCallback = object :DiffUtil.ItemCallback<SongModel>(){

        override fun areItemsTheSame(oldItem: SongModel, newItem: SongModel): Boolean {
            return oldItem.mediaID == newItem.mediaID
        }

        override fun areContentsTheSame(oldItem: SongModel, newItem: SongModel): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this , diffUtilsCallback)
    var songsList:List<SongModel>
    get() = differ.currentList
    set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(SongListItemBinding.inflate(LayoutInflater.from(parent.context) ,
            parent , false))
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songsList[position]
        holder.binding.tvSongName.text = song.songName
        holder.binding.tvSingerName.text = song.subTitle
        glide.load(song.songImage).into(holder.binding.ivSongImage)
       holder.binding.root.setOnClickListener {
           onItemClickListener?.let { click->
            click(song)
           }
       }
    }

    private var onItemClickListener: ((SongModel) ->Unit)? = null

    fun setOnItemClickListener(listener: (SongModel) ->Unit){
        onItemClickListener = listener
    }
    override fun getItemCount(): Int {
        return songsList.size
    }
}