package com.example.filemanagerapp.model.dataClasses

import java.io.File

data class RecyclerViewFile(
    val file: File,
    var fileSize: Double,
    var sizePostfixIndex: Int = -1
)
