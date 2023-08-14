package com.example.filemanagerapp.viewModel

import android.app.Application
import android.os.Environment
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val tag = "MainViewModel"

    val defaultDirectory = File(Environment.getExternalStorageDirectory().path)

    private var curFile = defaultDirectory
    private var sortBy: Int = 1
    private val fileTypesList: MutableList<String> = mutableListOf()

    private val filesInDirectory: MutableList<File> = mutableListOf()
    private val filesInDirectoryMutableStateFlow: MutableStateFlow<List<File>> =
        MutableStateFlow(filesInDirectory.toList())
    val filesInDirectoryStateFlow: StateFlow<List<File>> = filesInDirectoryMutableStateFlow.asStateFlow()

    private val recentFilesList: MutableList<File> = mutableListOf()

    var openRecentFragment: Boolean = false
    var openHomeFragment: Boolean = false

     fun getFilesInDirectory() {
         viewModelScope.launch(Dispatchers.IO) {
             getFilesInDirectory(curFile.listFiles(), fileTypesList, sortBy)
         }
     }

    fun setCurFile(newFile: File) {
        curFile = newFile

        viewModelScope.launch(Dispatchers.IO) {
            getFilesInDirectory(curFile.listFiles(), fileTypesList, sortBy)
        }
    }

    fun getCurFile(): File {
        return curFile
    }

    fun setSortBy(newSortBy: Int) {
        sortBy = newSortBy

        viewModelScope.launch(Dispatchers.IO) {
            getFilesInDirectory(curFile.listFiles(), fileTypesList, sortBy)
        }
    }

    fun getSortBy(): Int {
        return sortBy
    }

    fun setFileTypes(newList: List<String>) {
        fileTypesList.clear()
        fileTypesList.addAll(newList)

        viewModelScope.launch(Dispatchers.IO) {
            getFilesInDirectory(curFile.listFiles(), fileTypesList, sortBy)
        }
    }

    fun getFileTypes(): List<String> {
        return fileTypesList.toList()
    }

    private fun getFilesInDirectory(tempFilesList: Array<File>?, fileTypes: List<String>, sortBy: Int) {
        filesInDirectory.clear()
        val newList: MutableList<File> = mutableListOf()

        if (tempFilesList != null) {
            if (fileTypes.isNotEmpty()) {
                val newFileList = getFilesByTypes(tempFilesList, fileTypes)
                sortList(newFileList, sortBy)
                newList.addAll(newFileList)
            } else {
                sortList(tempFilesList, sortBy)
                newList.addAll(tempFilesList)
            }
        }

        filesInDirectory.addAll(newList)
        filesInDirectoryMutableStateFlow.value = filesInDirectory.toList()
    }

    private fun sortList(tempFilesList: MutableList<File>, sortBy: Int) {
        when (sortBy) {
            2 -> {
                tempFilesList.sortByDescending { it.length() }
            }
            3 -> {
                tempFilesList.sortBy { it.length() }
            }
            4 -> {
                tempFilesList.sortByDescending { it.lastModified() }
            }
            5 -> {
                tempFilesList.sortBy { it.lastModified() }
            }
        }
    }

    private fun sortList(tempFilesList: Array<File>, sortBy: Int) {
        when (sortBy) {
            2 -> {
                tempFilesList.sortByDescending { it.length() }
            }
            3 -> {
                tempFilesList.sortBy { it.length() }
            }
            4 -> {
                tempFilesList.sortByDescending { it.lastModified() }
            }
            5 -> {
                tempFilesList.sortBy { it.lastModified() }
            }
        }
    }

    private fun getFilesByTypes(tempFilesList: Array<File>,
                                fileTypesList: List<String>): MutableList<File> {

        val mutableFileList: MutableList<File> = mutableListOf()
        for (i in tempFilesList.indices) {
            for (j in fileTypesList.indices) {
                if (tempFilesList[i].extension == fileTypesList[j]) {
                    mutableFileList.add(tempFilesList[i])
                }
            }
        }

        return mutableFileList
    }


    fun getRecentFiles(needUpdate: Boolean = false): List<File> {
        if (recentFilesList.isEmpty() || needUpdate) {
            recentFilesList.clear()

            val tempTopFiles: List<File>? =
                Environment.getExternalStorageDirectory().listFiles()?.filter { file ->
                    file.name != "Android"
                }

            if (tempTopFiles != null) {
                for (i in tempTopFiles.indices) {
                    recentFilesList.addAll(listFiles(tempTopFiles[i]))
                }

                recentFilesList.sortByDescending {file ->
                    file.lastModified()
                }
            }
        }

        return recentFilesList.toList()
    }

    private fun listFiles(directory: File): MutableList<File> {
        val allFilesList: MutableList<File> = mutableListOf()
        directory.listFiles()?.forEach { file ->
            if (file.isFile && file.name[0] != '.') {
                allFilesList.add(file)
            } else if (file.isDirectory) {
                allFilesList.addAll(listFiles(file))
            }
        }
        return allFilesList
    }
}