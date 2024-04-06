package com.example.bai4.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bai4.data.model.Job

@Database(entities = [Job::class], version = 1, exportSchema = false)
abstract class JobRoomDatabase: RoomDatabase() {
    abstract fun dao(): JobDao
}