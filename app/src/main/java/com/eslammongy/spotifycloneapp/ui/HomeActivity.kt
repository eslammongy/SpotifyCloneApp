package com.eslammongy.spotifycloneapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.RequestManager
import com.eslammongy.spotifycloneapp.R
import com.eslammongy.spotifycloneapp.adapters.SwipeSongAdapter
import com.eslammongy.spotifycloneapp.constants.Status
import com.eslammongy.spotifycloneapp.data.entities.SongModel
import com.eslammongy.spotifycloneapp.databinding.ActivityHomeBinding
import com.eslammongy.spotifycloneapp.exoPlayer.isPlaying
import com.eslammongy.spotifycloneapp.exoPlayer.toSong
import com.eslammongy.spotifycloneapp.ui.viewModels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.view.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    @Inject
    lateinit var glide: RequestManager
    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var swipeSongAdapter: SwipeSongAdapter
    private var currentPlayingSong: SongModel? = null
    private var playbackState: PlaybackStateCompat? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        subscribeToObserver()
        binding.vpSong.adapter = swipeSongAdapter

        val navigationHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        //val navController = navHostFragment.findNavController()
        binding.vpSong.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (playbackState?.isPlaying == true) {
                    mainViewModel.playingOrToggelSong(swipeSongAdapter.songsList[position])
                    glide.load(swipeSongAdapter.songsList[position].songImage)
                        .into(binding.ivCurSongImage)
                } else {
                    currentPlayingSong = swipeSongAdapter.songsList[position]
                    glide.load(swipeSongAdapter.songsList[position].songImage)
                        .into(binding.ivCurSongImage)
                }
            }
        })
        binding.ivPlayPause.setOnClickListener {
            currentPlayingSong?.let {
                mainViewModel.playingOrToggelSong(it, true)
            }
        }
        swipeSongAdapter.setItemClickListener {
            navigationHostFragment.findNavController().navigate(R.id.globalActionToSongFragment)
        }

        navigationHostFragment.findNavController()
            .addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.songFragment -> hideBottomViewPager()
                    R.id.homeFragment -> showBottomViewPager()
                    else -> showBottomViewPager()
                }
            }

    }

    private fun hideBottomViewPager() {
        binding.ivCurSongImage.isVisible = false
        binding.vpSong.isVisible = false
        binding.ivPlayPause.isVisible = false
        binding.bottomView.isVisible = false
    }

    private fun showBottomViewPager() {
        binding.ivCurSongImage.isVisible = true
        binding.vpSong.isVisible = true
        binding.ivPlayPause.isVisible = true
        binding.bottomView.isVisible = true

    }

    private fun setViewPagerWithCurrentSong(songModel: SongModel) {

        val newSongIndex = swipeSongAdapter.songsList.indexOf(songModel)
        if (newSongIndex != -1) {
            binding.vpSong.currentItem = newSongIndex
            currentPlayingSong = songModel
        }
    }

    private fun subscribeToObserver() {

        mainViewModel.mediaItems.observe(this) {
            it?.let { result ->

                when (result.stats) {
                    Status.SUCCESS -> {
                        result.data?.let { songs ->
                            swipeSongAdapter.songsList = songs
                            if (songs.isNotEmpty()) {
                                glide.load((currentPlayingSong ?: songs[0].songImage))
                                    .into(binding.ivCurSongImage)

                            }
                            setViewPagerWithCurrentSong(currentPlayingSong ?: return@observe)
                        }
                    }
                    Status.ERROR -> Unit
                    Status.LOADING -> Unit
                }

            }
        }
        mainViewModel.currentPlayingSong.observe(this) {
            if (it == null) return@observe

            currentPlayingSong = it.toSong()
            glide.load((currentPlayingSong?.songImage)).into(binding.ivCurSongImage)
            setViewPagerWithCurrentSong(currentPlayingSong ?: return@observe)

        }
        mainViewModel.playbackState.observe(this) {
            playbackState = it
            binding.ivPlayPause.setImageResource(if (playbackState?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play)
        }
        mainViewModel.isConnected.observe(this) {
            it?.getContentInfoNotHandled()?.let { result ->
                when (result.stats) {
                    Status.ERROR -> {
                        Snackbar.make(
                            binding.root,
                            result.message ?: "An Unknown Error Occur",
                            Snackbar.LENGTH_LONG
                        ).show()

                    }
                    else -> Unit
                }

            }
        }

        mainViewModel.networkError.observe(this) {
            it?.getContentInfoNotHandled()?.let { result ->
                when (result.stats) {
                    Status.ERROR -> {
                        Snackbar.make(
                            binding.root,
                            result.message ?: "An Unknown Error Occur",
                            Snackbar.LENGTH_LONG
                        ).show()

                    }
                    else -> Unit
                }

            }
        }

    }
}
