package com.eslammongy.spotifycloneapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.RequestManager
import com.eslammongy.spotifycloneapp.R
import com.eslammongy.spotifycloneapp.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var binding:ActivityHomeBinding
    @Inject
    lateinit var glide: RequestManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}