package com.udacity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ActivityDetailBinding
import com.udacity.util.Constants


class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val status = intent.getStringExtra(Constants.INTENT_DOWNLOAD_STATUS)
        binding.content.tvDetailFileName.text = intent.getStringExtra(Constants.INTENT_DOWNLOAD_FILENAME)
        binding.content.tvDetailDownloadStatus.text = status

        // Set MotionLayout transition based on the download status
        if (status != null) {
            if (status == getString(R.string.detail_download_status_success)) {
                binding.content.motionLayout.setTransition(R.id.transition_download_success)
            } else {
                binding.content.motionLayout.setTransition(R.id.transition_download_fail)
            }
            binding.content.motionLayout.transitionToStart()
        }

        // Navigate to MainActivity when OK button is clicked
        binding.content.btnDetail.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}