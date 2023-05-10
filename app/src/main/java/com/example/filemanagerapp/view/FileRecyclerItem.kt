package com.example.filemanagerapp.view

import android.annotation.SuppressLint
import androidx.appcompat.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.filemanagerapp.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import org.w3c.dom.Text
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FileRecyclerItem(private val mainList: MutableList<File>, private val context: Context,
onClickListener: OnItemClickListener)
    : RecyclerView.Adapter<FileRecyclerItem.MyViewHolder>() {

    private var mainListener: OnItemClickListener = onClickListener
    private val fileSizePostfix = arrayOf(" bytes", " kilobytes", " megabytes", " gigabytes")
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy, HH:mm:ss", Locale.getDefault())
    private val tag = "FileRecyclerItem"
    interface OnItemClickListener{
        fun onItemClick(clickedFile: File)
        fun fileDeleted(file: File)
        fun fileRenamed()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardTitle: TextView = itemView.findViewById(R.id.file_card_title)
        val cardImage: View = itemView.findViewById(R.id.file_image_card)
        val cardButton: Button = itemView.findViewById(R.id.file_card_button)
        val cardFileSize: TextView = itemView.findViewById(R.id.file_size_card)
        val cardFileExtension: TextView = itemView.findViewById(R.id.file_card_extension)
        val cardFileDate: TextView = itemView.findViewById(R.id.file_card_date)
        val moreButton: Button = itemView.findViewById(R.id.more_ib_home)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.file_card, parent, false)
        return MyViewHolder(itemView)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val curFile = mainList[position]
        holder.cardTitle.text = curFile.name

        if (curFile.isDirectory) {
            holder.cardImage.visibility = View.VISIBLE
            holder.cardFileExtension.visibility = View.GONE
            holder.cardImage.background = ContextCompat.getDrawable(context, R.drawable.ic_folder_32)
        } else {
            holder.cardFileExtension.visibility = View.VISIBLE
            holder.cardImage.visibility = View.GONE
            holder.cardFileExtension.text = curFile.extension

            //Добавить иконки для разных типов файлов
        }

        holder.cardButton.setOnClickListener{
            mainListener.onItemClick(mainList[position])
        }

        holder.moreButton.setOnClickListener{
            showMenu(it, R.menu.file_menu, curFile)
        }

        var fileSize = curFile.length().toDouble()
        var sizePostfixIndex = 0
        while (fileSize >= 1024.0) {
            fileSize /= 1024
            sizePostfixIndex++
        }
        holder.cardFileSize.text = String.format("%.1f", fileSize) + fileSizePostfix[sizePostfixIndex]

        val date = Date(curFile.lastModified())
        holder.cardFileDate.text = dateFormat.format(date)
    }

    private fun showMenu(view: View, @MenuRes menuRes: Int, curFile: File) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.menuInflater.inflate(menuRes, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when(menuItem.itemId) {
                R.id.rename -> {
                    val renameDialog = MaterialAlertDialogBuilder(context)
                        .setTitle("Enter a new file name")
                        .setView(R.layout.dialog_rename)
                        .create()
                    renameDialog.show()

                    val renameButton = renameDialog.findViewById<Button>(R.id.rename_tb_dialog_rename)
                    val cancelButton = renameDialog.findViewById<Button>(R.id.cancel_tb_dialog_rename)
                    val textLayout = renameDialog.findViewById<TextInputLayout>(R.id.rename_dialog_text_layout)
                    val newNameStr = textLayout?.editText?.text

                    renameButton?.setOnClickListener{
                        if (newNameStr != null) {
                            renameFile(newNameStr.toString(), curFile, textLayout, renameDialog)
                        }
                    }

                    cancelButton?.setOnClickListener{
                        renameDialog.dismiss()
                    }

                    true
                }
                R.id.delete -> {
                    val message: String = if (curFile.isDirectory) {
                        "Are you sure you want to permanently delete the file: " +
                                "\"${curFile.name}\"?" +
                                "\n\nWARNING: \"${curFile.name}\" is a directory, when you delete " +
                                "a directory, the files in it will also be deleted."
                    } else {
                        "Are you sure you want to permanently delete the file: " +
                                "\"${curFile.name}\"?"
                    }

                    MaterialAlertDialogBuilder(context)
                        .setTitle("Delete a file?")
                        .setMessage(message)
                        .setNeutralButton("Cancel") { _, _ ->
                        }
                        .setPositiveButton("Remove") { _, _ ->
                            deleteFile(curFile)
                        }
                        .show()
                    true
                }
                else -> {
                    false
                }
            }
        }

        popupMenu.show()
    }

    private fun deleteFile(file: File) {
        if (file.isDirectory) {
            deleteRecursive(file)
            mainListener.fileDeleted(file)
        } else {
            val isDeleted = file.delete()
            if (isDeleted) {
                mainListener.fileDeleted(file)
                Toast.makeText(context, "The file was successfully deleted.",
                    Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "The file could not be deleted.",
                    Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun deleteRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory) {
            val tempFilesList = fileOrDirectory.listFiles()
            if (tempFilesList != null) {
                for (element in tempFilesList) {
                    deleteRecursive(element)
                }
            }
        }
        fileOrDirectory.delete()
    }

    private fun renameFile(newNameStr: String, curFile: File, textLayout: TextInputLayout,
                           renameDialog: AlertDialog) {
        if (newNameStr.isNotEmpty()) {
            val newFile = if (curFile.extension.isNotEmpty()) {
                File(
                    curFile.parentFile?.path
                            + "/" + newNameStr + "." + curFile.extension
                )
            } else {
                File(
                    curFile.parentFile?.path
                            + "/" + newNameStr
                )
            }

            if (newFile.exists()) {
                textLayout.error = "The file already exists"
            } else {
                val isRenamed = curFile.renameTo(newFile)
                if (isRenamed) {
                    renameDialog.dismiss()
                    mainListener.fileRenamed()
                    Toast.makeText(context, "The file has been renamed", Toast.LENGTH_SHORT).show()
                } else {
                    textLayout.error = "Couldn't rename the file"
                }
            }

        } else {
            textLayout.error = "The file name cannot be empty"
        }
    }

    override fun getItemCount(): Int {
        return mainList.size
    }
}