package com.example.bai4.data

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import androidx.room.Room
import com.example.bai4.data.model.Job
import com.example.bai4.util.AUTHORITY

private const val DATABASE_NAME = "job_database"


class MyProvider : ContentProvider() {

    private lateinit var jobDatabase: JobRoomDatabase

    private var dao: JobDao? = null

    private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(AUTHORITY, "tblJob", 0)
        addURI(AUTHORITY, "tblJob/#", 1)
    }

    override fun onCreate(): Boolean {
        context?.let {
            // Create database
            jobDatabase = Room.databaseBuilder(
                it,
                JobRoomDatabase::class.java,
                DATABASE_NAME
            ).build()

            // Get Dao
            dao = jobDatabase.dao()

            return true
        } ?: return false
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {

        when (sUriMatcher.match(uri)) {
            0 -> {
                return dao?.getAll(sortOrder ?: "date ASC")
            }

            1 -> {
                // Todo handle get job by id
            }

            else -> {
                // Error
            }
        }

        return null
    }

    override fun getType(p0: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        contentValues?.apply {
            dao?.insert(
                Job(
                    0,
                    getAsString("name"),
                    getAsLong("date")
                )
            )
        }
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        // TODO delete job
        return 0
    }

    override fun update(
        uri: Uri,
        contentValues: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        //Todo update job
        return 0
    }
}