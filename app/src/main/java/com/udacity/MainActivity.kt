package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var downloadInfo: String = ""


    private lateinit var notificationManager: NotificationManager
    private lateinit var downloadManager: DownloadManager

    private lateinit var glideButton: RadioButton
    private lateinit var loadAPPButton: RadioButton
    private lateinit var retrofitButton: RadioButton

    override fun onDestroy() {
        super.onDestroy()
        notificationManager.cancelAll()
        unregisterReceiver(receiver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        glideButton = findViewById(R.id.radio1)
        loadAPPButton = findViewById(R.id.radio2)
        retrofitButton = findViewById(R.id.radio3)

        custom_button.setOnClickListener {
            download()
        }

        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            var statusDownload = "unknown"

            if (id == downloadID) {
                val query = DownloadManager.Query().setFilterById(downloadID)

                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(
                        cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    )
                    statusDownload = when (status) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            "Success"
                        }
                        DownloadManager.STATUS_FAILED -> {
                            "Failed"
                        }
                        else -> {
                            "Not supported"
                        }
                    }


                }

                if (context != null) {

                    notificationManager.sendNotification(
                        context.getString(
                            R.string.notification_description
                        ), context, downloadInfo, statusDownload
                    )

                }
            }
        }
    }


    private fun download() {
        val but = findViewById<LoadingButton>(R.id.custom_button)
        but.animateButton()
        val radioGroup = findViewById<RadioGroup>(R.id.radio_group)
        val selID = radioGroup.checkedRadioButtonId

        var downloadURL = ""
        var fileName = ""
        when (selID) {
            glideButton.id -> {
                downloadURL = URL_GLIDE
                fileName = FILE_GLIDE
                downloadInfo = getString(R.string.glide_text)
            }
            loadAPPButton.id -> {
                downloadURL = URL_LOADAPP
                fileName = FILE_LOADAPP
                downloadInfo = getString(R.string.loadApp_text)
            }
            retrofitButton.id -> {
                downloadURL = URL_RETROFIT
                fileName = FILE_RETROFIT
                downloadInfo = getString(R.string.retrofit_text)
            }
            else -> {
                Toast.makeText(this, getString(R.string.toast_nothing_selected), Toast.LENGTH_LONG)
                    .show()
            }
        }

        if (downloadURL.isNotEmpty()) {

            val request =
                DownloadManager.Request(Uri.parse(downloadURL))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            downloadID =
                downloadManager.enqueue(request)
        }
    }

    companion object {
        private const val URL_GLIDE =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val FILE_GLIDE = "glideMaster.zip"
        private const val URL_LOADAPP =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val FILE_LOADAPP = "loadAppMaster.zip"
        private const val URL_RETROFIT =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"
        private const val FILE_RETROFIT = "retrofitMaster.zip"
    }

    private fun createChannel(channelId: String, channelName: String) {
        // Create channel to show notifications.
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )

        notificationManager.createNotificationChannel(notificationChannel)

    }


}
