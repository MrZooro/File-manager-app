package com.example.filemanagerapp.viewModel

import android.app.Application
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val tag = "MainViewModel"

    val defaultDirectory = File(Environment.getExternalStorageDirectory().path)

    private var curFile = defaultDirectory
    private val curFileMutableFlow: MutableStateFlow<File> = MutableStateFlow(curFile)
    val curFileStateFlow: StateFlow<File> = curFileMutableFlow

    private val sortByMutableStateFlow: MutableStateFlow<Int> = MutableStateFlow(1)
    val sortByStateFlow: StateFlow<Int> = sortByMutableStateFlow

    private val fileTypesList: MutableList<String> = mutableListOf()
    private val fileTypesMutableStateFlow: MutableStateFlow<List<String>> = MutableStateFlow(fileTypesList.toList())
    val fileTypesStateFlow: StateFlow<List<String>> = fileTypesMutableStateFlow

    private val recentFilesList: MutableList<File> = mutableListOf()
    private val recentFilesMutableStateFlow: MutableStateFlow<List<File>>
    = MutableStateFlow(recentFilesList.toList())

    fun setCurFile(newFile: File) {
        curFile = newFile
        curFileMutableFlow.value = curFile
    }

    fun getCurFile(): File {
        return curFile
    }

    fun setSortBy(sortBy: Int) {
        sortByMutableStateFlow.value = sortBy
    }

    fun getSortBy(): Int {
        return sortByMutableStateFlow.value
    }

    fun setFileTypes(newList: List<String>) {
        fileTypesList.clear()
        fileTypesList.addAll(newList)

        fileTypesMutableStateFlow.value = fileTypesList.toList()
    }

    fun getFileTypes(): List<String> {
        return fileTypesMutableStateFlow.value
    }

    fun getRecentFiles(): List<File> {
        if (recentFilesList.isEmpty()) {
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