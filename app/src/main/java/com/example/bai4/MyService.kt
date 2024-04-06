package com.example.bai4

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.bai4.util.AUTHORITY
import com.example.bai4.util.CONTENT_PROVIDER_SCHEME
import com.example.bai4.util.TABLE_JOBS_NAME
import com.example.bai4.util.TAG
import com.example.bai4.util.setDefaultTime
import java.util.Calendar
import kotlin.concurrent.thread

class MyService : Service() {
    override fun onCreate() {
        super.onCreate()

        // Get notification service
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(
                    "com.example.bai4.channelId",
                    "My notification channel",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: ")

        // Cannot access database on the main thread
        thread {
            // Build uri
            val uri = Uri.Builder()
                .scheme(CONTENT_PROVIDER_SCHEME)
                .authority(AUTHORITY)
                .path(TABLE_JOBS_NAME)
                .build()

            // Get all jobs
            val cursor = contentResolver.query(uri, null, null, null, null)

            // Content text of notification
            var content: String? = null
            when (cursor?.count) {
                // Check, if null, error
                null -> {
                    Log.e(TAG, "onStart: error")
                }

                // If 0, no jobs
                0 -> {
                    Log.d(TAG, "onStart: no jobs")
                    content = getString(R.string.txtNoJobsToday)
                }
                // Else
                else -> {
                    try {
                        // List jobs name today
                        val jobs = mutableListOf<String>()
                        // Get date of today
                        val today = Calendar.getInstance()
                        today.setDefaultTime()

                        // Get index of column name, date
                        val iColName = cursor.columnNames.indexOf("name")
                        val iColDate = cursor.columnNames.indexOf("date")
                        // Get job today from cursor
                        while (cursor.moveToNext()) {
                            if (cursor.getLong(iColDate) == today.timeInMillis) {
                                jobs.add(
                                    cursor.getString(iColName)
                                )
                            }
                        }

                        content = if (jobs.size == 0) {
                            getString(R.string.txtNoJobsToday)
                        } else {
                            jobs.joinToString("\n")
                        }
                    } catch (e: Exception) {
                        Log.d(TAG, "onStart: " + e.message)
                    }
                }
            }

            cursor?.close()

            // Intent to receiver to turn off notification
            val intentToReceiver = Intent(this, MyReceiver::class.java)
            intentToReceiver.putExtra("action", "turnOff")
            val pendingIntent =
                PendingIntent.getBroadcast(this, 101, intentToReceiver, PendingIntent.FLAG_MUTABLE)

            // Check, if not null, start service
            if (content != null) {
                val notification = NotificationCompat.Builder(this, "com.example.bai4.channelId")
                    .setContentTitle(getString(R.string.txtJodsToday))
                    .setContentText(content)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .addAction(0, getString(R.string.txtConfirm), pendingIntent)
                    .build()

                intent?.getStringExtra("type")?.let {
                    // Check, if is foreground service
                    if (it == "foreground") {
                        startForeground(100, notification)
                    } else {
                        // Else ...
                        if (ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            NotificationManagerCompat.from(this).notify(199, notification)
                        }
                    }
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }
}