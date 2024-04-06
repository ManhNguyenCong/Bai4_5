package com.example.bai4.data

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.bai4.data.model.Job

@Dao
interface JobDao {

    @Query("SELECT * FROM tblJob ORDER BY :sortOrder")
    fun getAll(sortOrder: String): Cursor

    @Query("SELECT * FROM tblJob WHERE id = :id")
    fun getJobById(id: Int): Cursor

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(job: Job)

    @Update
    fun update(job: Job)

    @Delete
    fun delete(job: Job)
}