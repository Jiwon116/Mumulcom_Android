package com.example.mumulcom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.mumulcom.databinding.ActivityImageBinding

class ImageActivity : AppCompatActivity() {

    private lateinit var binding : ActivityImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val imageUrl=intent.getStringExtra("imgUrl")

        Glide.with(this).load(imageUrl).into(binding.photoView)


        binding.closeActivity.setOnClickListener {
            finish()
        }
    }
}