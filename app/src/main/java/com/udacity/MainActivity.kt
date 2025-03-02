package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.udacity.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    companion object {
        private const val glideUrl = "https://github.com/bumptech/glide/archive/master.zip"
        private const val loadAppUrl = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val retrofitUrl = "https://github.com/square/retrofit/archive/trunk.zip"
        private const val CHANNEL_ID = "channelId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        binding.content.customButton.setOnClickListener {
            download()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadID) {
                val query = DownloadManager.Query().setFilterById(downloadID)
                val downloadManager = context!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    if (columnIndex != -1) {
                        val status = cursor.getInt(columnIndex)
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            Toast.makeText(context, "Download Complete", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Download Failed", Toast.LENGTH_SHORT).show()
                        }
                        binding.content.customButton.stopLoading()
                    }
                }
                cursor.close()
            }
        }
    }

    private fun download() {
        val url = getDownloadUrl()
        val downloadRepoName = getDownloadRepoName()
        if (url != null && downloadRepoName != null) {
            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS, "/repos/LoadApp/$downloadRepoName"
                )

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID = downloadManager.enqueue(request)
            binding.content.customButton.startLoading()
        } else {
            Toast.makeText(this, "Please select the file to download", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getDownloadUrl() : String? {
        return when (binding.content.rbgMain.checkedRadioButtonId) {
            R.id.rb_glide -> glideUrl
            R.id.rb_load_app -> loadAppUrl
            R.id.rb_retrofit -> retrofitUrl
            else -> return null
        }
    }

    private fun getDownloadRepoName() : String? {
        return when (binding.content.rbgMain.checkedRadioButtonId) {
            R.id.rb_glide -> "Glide.zip"
            R.id.rb_load_app -> "LoadApp.zip"
            R.id.rb_retrofit -> "Retrofit.zip"
            else -> return null
        }
    }

}