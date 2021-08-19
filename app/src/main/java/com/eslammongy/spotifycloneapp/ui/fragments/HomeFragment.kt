package com.eslammongy.spotifycloneapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.eslammongy.spotifycloneapp.adapters.SongsAdapter
import com.eslammongy.spotifycloneapp.constants.Status
import com.eslammongy.spotifycloneapp.databinding.FragmentHomeBinding
import com.eslammongy.spotifycloneapp.ui.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment:Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    lateinit var mainViewModel:MainViewModel
    @Inject
    lateinit var songAdapter:SongsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        displayRecyclerView()
        subscribeToObservers()

        songAdapter.setOnItemClickListener {
            mainViewModel.playingOrToggelSong(it)
        }
    }

    private fun displayRecyclerView() = binding.rvAllSongs.apply {
        adapter = songAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun subscribeToObservers(){
        mainViewModel.mediaItems.observe(viewLifecycleOwner){result->
            when(result.stats){
                Status.SUCCESS ->{
                    binding.allSongsProgressBar.visibility = View.GONE
                    result.data?.let { song ->
                        songAdapter.songsList = song

                    }
                }
                Status.ERROR -> Unit
                Status.LOADING -> binding.allSongsProgressBar.visibility = View.VISIBLE
            }
        }
    }





}