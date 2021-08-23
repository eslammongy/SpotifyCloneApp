package com.eslammongy.spotifycloneapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.eslammongy.spotifycloneapp.data.entities.SongModel
import kotlinx.android.synthetic.main.song_list_item.view.*

abstract class BaseSongAdapter (private val layoutID:Int): RecyclerView.Adapter<BaseSongAdapter.SongViewHolder>(){

    class SongViewHolder(itemView:View): RecyclerView.ViewHolder(itemView)

    protected val diffUtilsCallback = object : DiffUtil.ItemCallback<SongModel>(){

        override fun areItemsTheSame(oldItem: SongModel, newItem: SongModel): Boolean {
            return oldItem.mediaID == newItem.mediaID
        }

        override fun areContentsTheSame(oldItem: SongModel, newItem: SongModel): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    protected abstract val differ:AsyncListDiffer<SongModel>
    var songsList:List<SongModel>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(LayoutInflater.from(parent.context).inflate(layoutID , parent
        ,false))
    }

    protected var onItemClickListener: ((SongModel) ->Unit)? = null

    fun setItemClickListener(listener: (SongModel) ->Unit){
        onItemClickListener = listener
    }
    override fun getItemCount(): Int {
        return songsList.size
    }
}