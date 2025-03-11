package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityMainBinding
import com.udacity.util.Constants
import com.udacity.util.sendNotification

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationIntent: Intent

    companion object {
        private const val GLIDE_URL = "https://github.com/bumptech/glide/archive/master.zip"
        private const val LOAD_APP_URL = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL = "https://github.com/square/retrofit/archive/trunk.zip"
        private const val PERM_WRITE_EXT_STORAGE_CODE = 101
        private const val PERM_NOTIFICATION_REQUEST_CODE = 102
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), RECEIVER_EXPORTED)

        binding.content.customButton.setOnClickListener {
            if (hasExternalStoragePerm()) {
                download()
            } else {
                requestExternalStoragePerm()
            }
        }

        requestNotificationPerm()
        notificationManager = ContextCompat.getSystemService(this, NotificationManager::class.java) as NotificationManager
        createChannel(getString(R.string.notification_channel_id), getString(R.string.notification_channel_name))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERM_WRITE_EXT_STORAGE_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Do nothing (permission has been granted)
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == PERM_NOTIFICATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Do nothing (permission has been granted)
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
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
                            notificationIntent = createNotificationIntent(getString(R.string.detail_download_status_success))
                        } else {
                            notificationIntent = createNotificationIntent(getString(R.string.detail_download_status_fail))
                        }
                        notificationManager.sendNotification(notificationIntent, context)
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
            R.id.rb_glide -> GLIDE_URL
            R.id.rb_load_app -> LOAD_APP_URL
            R.id.rb_retrofit -> RETROFIT_URL
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

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_description)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun createNotificationIntent(downloadStatus : String) : Intent {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra(Constants.INTENT_DOWNLOAD_STATUS, downloadStatus)
            putExtra(Constants.INTENT_DOWNLOAD_FILENAME, getDownloadRepoName())
        }
        return intent
    }

    private fun hasExternalStoragePerm() : Boolean {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            return ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
        return true
    }

    private fun requestExternalStoragePerm() {
        ActivityCompat.requestPermissions(this,
            arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PERM_WRITE_EXT_STORAGE_CODE)
    }

    private fun requestNotificationPerm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                PERM_NOTIFICATION_REQUEST_CODE)
        }
    }

}