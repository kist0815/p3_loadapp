package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.coroutines.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val okButton = findViewById<Button>(R.id.okButton)
        val contentIntent = Intent(applicationContext, MainActivity::class.java)
        okButton.setOnClickListener {
            startActivity(contentIntent)
        }

        val sendBundle = intent.extras
        if (sendBundle != null) {
            val fileName = sendBundle.getString("FILENAME")
            val downloadStatus = sendBundle.getString("STATUSDOWNLOAD")
            val notificationID = sendBundle.getInt("NOTIFY_ID")
            val testFileName = findViewById<TextView>(R.id.textFileName)
            testFileName.text = fileName
            val textStatus = findViewById<TextView>(R.id.textStatus)
            textStatus.text = downloadStatus

            val notificationManager = ContextCompat.getSystemService(
                this,
                NotificationManager::class.java
            ) as NotificationManager

            notificationManager.cancel(notificationID)

        }


    }

    override fun onResume() {
        super.onResume()

        val motionLayout = findViewById<MotionLayout>(R.id.filenameID)

        GlobalScope.launch(Dispatchers.IO) {
            delay(500)
            withContext(Dispatchers.Main) {
                motionLayout.transitionToEnd()
            }
        }
    }


}
