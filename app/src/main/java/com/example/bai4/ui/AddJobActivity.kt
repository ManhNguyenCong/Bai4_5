package com.example.bai4.ui

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.bai4.databinding.ActivityAddJobBinding
import com.example.bai4.util.AUTHORITY
import com.example.bai4.util.CONTENT_PROVIDER_SCHEME
import com.example.bai4.util.TABLE_JOBS_NAME
import com.example.bai4.util.TAG
import com.example.bai4.util.setDefaultTime
import java.util.Calendar
import java.util.TimeZone
import kotlin.concurrent.thread

class AddJobActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddJobBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddJobBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set min date is today
        binding.datePicker.minDate = Calendar.getInstance(TimeZone.getDefault()).timeInMillis

        binding.btnSave.setOnClickListener {
            saveNewJob()
        }
    }

    /**
     * This function is used to save new job to database
     * with content provider
     */
    private fun saveNewJob() {
        if (binding.edtJob.text.isEmpty()) {
            binding.edtJob.error = "Không được bỏ trống!!!"
            return
        }

        // Cannot access database on the main thread
        thread {
            // Get job
            val name = binding.edtJob.text.toString()

            // Build content uri
            val uri = Uri.Builder()
                .scheme(CONTENT_PROVIDER_SCHEME)
                .authority(AUTHORITY)
                .path(TABLE_JOBS_NAME)
                .build()

            // Get time
            val date = Calendar.getInstance()
            date.set(Calendar.DAY_OF_MONTH, binding.datePicker.dayOfMonth)
            date.set(Calendar.MONTH, binding.datePicker.month)
            date.set(Calendar.YEAR, binding.datePicker.year)
            date.setDefaultTime()

            // Create content values
            val newValues = ContentValues().apply {
                /*
                 * Sets the values of each column and inserts the word. The arguments to the "put"
                 * method are "column name" and "value".
                 */
                put("name", name)
                put("date", date.timeInMillis)
            }

            try {
                // Insert
                contentResolver.insert(uri, newValues)
                finish()
            } catch (e: Exception) {
                Log.d(TAG, "saveNewJob: " + e.message)
            }
        }.interrupt()
    }
}
