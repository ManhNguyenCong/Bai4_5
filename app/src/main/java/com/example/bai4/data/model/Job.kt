package com.example.bai4.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bai4.util.TABLE_JOBS_NAME

@Entity(tableName = TABLE_JOBS_NAME)
data class Job(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo val name: String,
    @ColumnInfo val date: Long
)
