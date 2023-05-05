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

    fun setCurFile(newFile: File) {
        curFile = newFile
        curFileMutableFlow.value = curFile
    }

    fun getCurFile(): File {
        return curFile
    }
}