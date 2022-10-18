package com.example.mumulcom

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mumulcom.databinding.ActivityPrivacyBinding

class PrivacyActivity : AppCompatActivity() {
    lateinit var binding: ActivityPrivacyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.privacyIv.setOnClickListener {
            //startActivity(Intent(this, ProfileFragment::class.java))
            finish()
        }
    }
}