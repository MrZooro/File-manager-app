package com.example.filemanagerapp.model.dataClasses

import java.io.File

interface OnItemClickListener{
    fun onItemClick(clickedFile: File)
}