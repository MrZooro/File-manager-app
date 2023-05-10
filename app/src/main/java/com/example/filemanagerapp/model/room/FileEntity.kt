package com.example.filemanagerapp.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import java.io.File

@Entity(tableName = "recentChanges",
    indices = [Index("path", unique = true)]
)
data class FileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val path: String,
    val hash: String,
    val isRecentChanged: Boolean
)