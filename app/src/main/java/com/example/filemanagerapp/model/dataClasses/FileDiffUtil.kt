package com.example.filemanagerapp.model.dataClasses

import androidx.recyclerview.widget.DiffUtil
import java.io.File

class FileDiffUtil(
    private val oldList: List<File>,
    private val newList: List<File>
): DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.count()
    }

    override fun getNewListSize(): Int {
        return newList.count()
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldFile = oldList[oldItemPosition]
        val newFile = newList[newItemPosition]

         return oldFile::class == newFile::class
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldFile = oldList[oldItemPosition]
        val newFile = newList[newItemPosition]

        return oldFile == newFile
    }
}