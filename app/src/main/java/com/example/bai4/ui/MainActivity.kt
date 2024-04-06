package com.example.bai4.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bai4.MyReceiver
import com.example.bai4.data.model.Job
import com.example.bai4.databinding.ActivityMainBinding
import com.example.bai4.util.AUTHORITY
import com.example.bai4.util.CONTENT_PROVIDER_SCHEME
import com.example.bai4.util.MILLI_SECONDS_IN_DAY
import com.example.bai4.util.TABLE_JOBS_NAME
import java.util.Calendar
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    /**
     * Adapter for rcJobs
     */
    private var adapter: JobAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Navigate to AddJobActivity with activity
        binding.btnAdd.setOnClickListener {
            startActivity(Intent(this, AddJobActivity::class.java))
        }

        // Init adapter and set it to recycler view
        adapter = JobAdapter()
        binding.rcJobs.adapter = adapter

        // Get alarm service
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        // Pending intent send receiver to notify jobs at 6:00 every day
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            99,
            Intent(this, MyReceiver::class.java),
            PendingIntent.FLAG_MUTABLE
        )

        // Cancel old alarm
        alarmManager.cancel(pendingIntent)

        // Set time
        val date = Calendar.getInstance()
        if (date.get(Calendar.HOUR_OF_DAY) >= 5) {
            date.timeInMillis += MILLI_SECONDS_IN_DAY
        }
        date.set(Calendar.HOUR_OF_DAY, 6)
        date.set(Calendar.MINUTE, 0)
        date.set(Calendar.SECOND, 0)
        date.set(Calendar.MILLISECOND, 0)

        // Set alarm
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            Calendar.getInstance().timeInMillis + 2 * 60 * 1000,
            MILLI_SECONDS_IN_DAY,
            pendingIntent
        )
    }

    override fun onStart() {
        super.onStart()

        // Submit list jobs
        submitList()
    }

    override fun onRestart() {
        super.onRestart()

        // Submit list jobs
        submitList()
    }

    private fun submitList() {
        // Cannot access database on the main thread
        thread {
            // Build content uri
            val uri =
                Uri.Builder()
                    .scheme(CONTENT_PROVIDER_SCHEME)
                    .authority(AUTHORITY)
                    .path(TABLE_JOBS_NAME)
                    .build()

            // Get all jobs
            val cursor = contentResolver.query(uri, null, null, null, null)

            if (cursor == null) {
                runOnUiThread {
                    Toast.makeText(
                        this, "Hiện có lỗi xảy ra...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return@thread
            }

            cursor.let { cursorNN ->
                when (cursorNN.count) {
                    // Check, if no jobs added
                    0 -> {
                        runOnUiThread {
                            Toast.makeText(this, "Chưa có công việc nào...", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    // Else
                    else -> {
                        // Get list form cursor
                        val jobs = mutableListOf<Job>()
                        while (cursorNN.moveToNext()) {
                            val iColId = cursorNN.columnNames.indexOf("id")
                            val iColName = cursorNN.columnNames.indexOf("name")
                            val iColDate = cursorNN.columnNames.indexOf("date")
                            jobs.add(
                                Job(
                                    cursorNN.getInt(iColId),
                                    cursorNN.getString(iColName),
                                    cursorNN.getLong(iColDate)
                                )
                            )
                        }

                        runOnUiThread {
                            // Submit list for adapter
                            adapter?.submitList(jobs)
                        }
                    }
                }
            }

            cursor.close()
        }.interrupt()
    }
}