package com.example.filemanagerapp.viewModel

import android.app.Application
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val defaultDirectory = File(Environment.getExternalStorageDirectory().path)

    private var curFile = defaultDirectory
    private val curFileMutableFlow: MutableStateFlow<File> = MutableStateFlow(curFile)
    val curFileStateFlow: StateFlow<File> = curFileMutableFlow

    private val sortByMutableStateFlow: MutableStateFlow<Int> = MutableStateFlow(1)
    val sortByStateFlow: StateFlow<Int> = sortByMutableStateFlow

    private val fileTypesList: MutableList<String> = mutableListOf()
    private val fileTypesMutableStateFlow: MutableStateFlow<List<String>> = MutableStateFlow(fileTypesList.toList())
    val fileTypesStateFlow: StateFlow<List<String>> = fileTypesMutableStateFlow

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
}