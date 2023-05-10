package com.example.filemanagerapp.model.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomDao {

    @Query("SELECT EXISTS(SELECT * FROM recentChanges WHERE path = :path)")
    suspend fun isExists(path: String): Boolean

    @Query("SELECT * FROM recentChanges WHERE path = :path")
    suspend fun getFileId(path: String): FileEntity

    @Query("SELECT * FROM recentChanges WHERE isRecentChanged = 1")
    fun getAllRecentChanged(): Flow<List<FileEntity>>

    @Query("SELECT * FROM recentChanges")
    suspend fun getAllFiles(): List<FileEntity>

    @Insert
    suspend fun addFile(fileEntity: FileEntity)

    @Query("DELETE FROM recentChanges")
    suspend fun deleteAllFiles()

    @Query("DELETE FROM recentChanges WHERE id = :id")
    suspend fun deleteFile(id: Int)

    @Update
    suspend fun updateFile(fileEntity: FileEntity)

    @Query("UPDATE recentChanges SET hash=:hash, isRecentChanged = :isRecentChanged WHERE id = :id")
    suspend fun updateHash(hash: String, isRecentChanged: Boolean, id: Int)

    @Query("UPDATE recentChanges SET isRecentChanged = :isRecentChanged WHERE id = :id")
    suspend fun updateRecentChanged(isRecentChanged: Boolean, id: Int)
}