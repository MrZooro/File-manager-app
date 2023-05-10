package com.example.filemanagerapp.model

import android.content.Context
import androidx.room.Room
import com.example.filemanagerapp.model.room.AppDatabaseModel
import com.example.filemanagerapp.model.room.FileEntity
import com.example.filemanagerapp.viewModel.MainViewModel
import kotlinx.coroutines.flow.Flow

class Repository(context: Context) {

    private val roomDao = AppDatabaseModel.getDatabase(context).getDao()

    suspend fun isExist(path: String): Boolean {
        return roomDao.isExists(path)
    }

    fun getAllRecentChanged(): Flow<List<FileEntity>> {
        return roomDao.getAllRecentChanged()
    }

    suspend fun getAllFiles(): List<FileEntity> {
        return roomDao.getAllFiles()
    }

    suspend fun addFile(fileEntity: FileEntity) {
        roomDao.addFile(fileEntity)
    }

    suspend fun deleteAllFiles() {
        roomDao.deleteAllFiles()
    }

    suspend fun deleteFile(id: Int) {
        roomDao.deleteFile(id)
    }

    suspend fun updateFile(fileEntity: FileEntity) {
        roomDao.updateFile(fileEntity)
    }

    suspend fun updateHash(hash: String, isRecentChanged: Boolean, id: Int) {
        roomDao.updateHash(hash, isRecentChanged, id)
    }

    suspend fun updateRecentChanged(isRecentChanged: Boolean, id: Int) {
        roomDao.updateRecentChanged(isRecentChanged, id)
    }

    suspend fun getFileId(path: String): FileEntity {
        return roomDao.getFileId(path)
    }

    companion object {
        @Volatile
        private var INSTANCE: Repository? = null

        fun getRepository(context: Context): Repository {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Repository(context)
                INSTANCE = instance
                return instance }
        }
    }
}