package com.example.filemanagerapp.viewModel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.filemanagerapp.model.Repository
import com.example.filemanagerapp.model.room.FileEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.lang.Exception
import java.math.BigInteger
import java.nio.ByteBuffer
import java.security.MessageDigest

class SplashViewModel(application: Application) : AndroidViewModel(application) {

    private val tag = "SplashViewModel"
    private val repository = Repository.getRepository(getApplication())

    private var dateOfCreationDatabase: String
    private var isDatabaseCreated: Boolean
    private val saveInfo: SharedPreferences

    private val databaseFilledMutableStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val databaseFilledStateFlow: StateFlow<Boolean> = databaseFilledMutableStateFlow

    var databaseIsFilling = false

    init {
        saveInfo = application.getSharedPreferences("saveInfo", Context.MODE_PRIVATE)
        dateOfCreationDatabase = saveInfo.getString("dateOfCreationDatabase", "") ?: ""
        isDatabaseCreated = saveInfo.getBoolean("isDatabaseCreated", false)
    }

    private fun listFiles(): MutableList<File> {
        val allFilesList: MutableList<File> = mutableListOf()
        Log.i(tag, Environment.getExternalStorageDirectory().path)
        File(Environment.getExternalStorageDirectory().path).walkTopDown().forEach {
            if (!it.isDirectory) {
                allFilesList.add(it)
            }
        }
        return allFilesList
    }

    fun fillDatabase() {

        viewModelScope.launch(Dispatchers.IO){
            val newFilesList = listFiles()
            newFilesList.sortByDescending { it.lastModified() }

            if (!isDatabaseCreated) {
                isDatabaseCreated = true
                saveInfo.edit().putBoolean("isDatabaseCreated", isDatabaseCreated).apply()
                var curFile: File
                var newHash: String
                var newFileEntity: FileEntity
                val filesList: MutableList<FileEntity> = mutableListOf()
                for (i in newFilesList.indices) {
                    curFile = newFilesList[i]
                    newHash = createSHA256(curFile)
                    newFileEntity = FileEntity(0, curFile.path, newHash, false)
                    filesList.add(newFileEntity)
                }
                repository.insertAll(filesList)
                databaseFilledMutableStateFlow.value = true
                return@launch
            }

            databaseFilledMutableStateFlow.value = true

            val filesFromDatabase = repository.getAllFiles()
            var tempFile: File
            for (i in filesFromDatabase.indices) {
                tempFile = File(filesFromDatabase[i].path)
                if (!tempFile.exists()) {
                    repository.deleteFile(filesFromDatabase[i].id)
                }
            }

            var curFile: File
            var oldFile: FileEntity
            var newHash: String
            for (i in newFilesList.indices) {
                curFile = newFilesList[i]

                if (repository.isExist(curFile.path)) {
                    oldFile = repository.getFileId(curFile.path)
                    newHash = createSHA256(curFile)
                    if (newHash.isNotEmpty()) {
                        if (oldFile.hash == newHash) {
                            repository.updateRecentChanged(false, oldFile.id)
                        } else {
                            repository.updateHash(newHash, true, oldFile.id)
                        }
                    }
                } else {
                    newHash = createSHA256(curFile)
                    if (newHash.isNotEmpty()) {
                        Log.i(tag, "Add new file: " + curFile.path)
                        val newFile = FileEntity(0, curFile.path, newHash, true)
                        repository.addFile(newFile)
                    }
                }
            }
            return@launch
        }
    }

    private var md: MessageDigest? = null
    private fun createSHA256(file: File): String {
        if (md == null) {
            md = MessageDigest.getInstance("SHA-256")
        }
        try {
            val channel = FileInputStream(file).channel
            val buffer = ByteBuffer.allocateDirect(1024 * 1024) // используем прямой буфер, чтобы уменьшить копирование данных
            while (channel.read(buffer) != -1) {
                buffer.flip()
                md?.update(buffer)
                buffer.clear()
            }
            channel.close()
            val messageDigest = md?.digest()
            val no = BigInteger(1, messageDigest)
            return String.format("%064x", no)
        } catch (e: Exception) {
            Log.e(tag, "createSHA_256: Сouldn't open the file: ${file.path}, ${e.stackTrace}")
        }
        return ""
    }

}