package com.eslammongy.spotifycloneapp.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.eslammongy.spotifycloneapp.constants.Status
import com.eslammongy.spotifycloneapp.data.entities.SongModel
import com.eslammongy.spotifycloneapp.databinding.FragmentSongBinding
import com.eslammongy.spotifycloneapp.exoPlayer.toSong
import com.eslammongy.spotifycloneapp.ui.viewModels.MainViewModel
import com.eslammongy.spotifycloneapp.ui.viewModels.SongViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SongFragment :Fragment(){
    private var _binding:FragmentSongBinding? = null
    private val binding get() = _binding!!


    @Inject
    lateinit var glide: RequestManager

    private lateinit var mainViewModel: MainViewModel
    private val songViewModel:SongViewModel by viewModels()
    private var currentPlayingSong :SongModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSongBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        binding.tvSongName.isSelected = true
        subscribeToObserver()


    }

    @SuppressLint("SetTextI18n")
    private fun displayFragmentUi(songModel: SongModel){
        binding.tvSongName.text = "${songModel.songName} - ${songModel.subTitle}"
        glide.load(songModel.songImage)
            .into(binding.ivSongImage as ImageView)
    }

    private fun subscribeToObserver(){
        mainViewModel.mediaItems.observe(viewLifecycleOwner){
            it?.let { result ->
                when(result.stats){
                    Status.SUCCESS ->{
                        result.data?.let { songs ->
                            if (currentPlayingSong == null && songs.isNotEmpty()){
                                currentPlayingSong = songs[0]
                                displayFragmentUi(songs[0])
                            }
                        }
                    }
                    else -> Unit
                }

            }
        }
        mainViewModel.currentPlayingSong.observe(viewLifecycleOwner){
            if (it ==null) return@observe
            currentPlayingSong = it.toSong()
            displayFragmentUi(currentPlayingSong!!)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}