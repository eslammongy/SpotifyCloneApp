package com.eslammongy.spotifycloneapp.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.eslammongy.spotifycloneapp.R
import com.eslammongy.spotifycloneapp.constants.Status
import com.eslammongy.spotifycloneapp.data.entities.SongModel
import com.eslammongy.spotifycloneapp.databinding.FragmentSongBinding
import com.eslammongy.spotifycloneapp.exoPlayer.isPlaying
import com.eslammongy.spotifycloneapp.exoPlayer.toSong
import com.eslammongy.spotifycloneapp.ui.viewModels.MainViewModel
import com.eslammongy.spotifycloneapp.ui.viewModels.SongViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_song.*
import java.text.SimpleDateFormat
import java.util.*
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
    private var playbackState:PlaybackStateCompat? = null
    private var shouldUpdateSeekbar:Boolean = true

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

        binding.ivPlayPauseDetail.setOnClickListener {
            currentPlayingSong?.let {
                mainViewModel.playingOrToggelSong(it,true)
            }
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser){
                    setCurrentPlayerTimeToTxt(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                shouldUpdateSeekbar = false
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    mainViewModel.seekTo(it.progress.toLong())
                    shouldUpdateSeekbar = true
                }
            }
        })
        binding.ivSkipPrevious.setOnClickListener {
            mainViewModel.skipToPreviousSong()
        }
        binding.ivSkip.setOnClickListener {
            mainViewModel.skipToNextSong()
        }


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
        mainViewModel.playbackState.observe(viewLifecycleOwner){
            playbackState = it
            binding.ivPlayPauseDetail.setImageResource(if (playbackState?.isPlaying == true)
                R.drawable.ic_pause else R.drawable.ic_play)
            seekBar.progress = it?.position?.toInt() ?: 0
        }
        songViewModel.currentPlayerPosition.observe(viewLifecycleOwner){
            if (shouldUpdateSeekbar){
                seekBar.progress = it.toInt()
                setCurrentPlayerTimeToTxt(it)
            }
        }
        songViewModel.currentSongDuration.observe(viewLifecycleOwner){
            seekBar.max = it.toInt()
            val timeFormat = SimpleDateFormat("mm:ss" , Locale.getDefault())
            binding.tvSongDuration.text = timeFormat.format(it)
        }

    }

    private fun setCurrentPlayerTimeToTxt(ms:Long){
        val timeFormat = SimpleDateFormat("mm:ss" , Locale.getDefault())
        binding.tvCurTime.text = timeFormat.format(ms)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}