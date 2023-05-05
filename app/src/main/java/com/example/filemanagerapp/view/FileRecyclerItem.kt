package com.example.filemanagerapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface.OnClickListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.filemanagerapp.R
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
    interface OnItemClickListener{
        fun onItemClick(clickedFile: File)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardTitle: TextView = itemView.findViewById(R.id.file_card_title)
        val cardImage: View = itemView.findViewById(R.id.file_image_card)
        val cardButton: Button = itemView.findViewById(R.id.file_card_button)
        val cardFileSize: TextView = itemView.findViewById(R.id.file_size_card)
        val cardFileExtension: TextView = itemView.findViewById(R.id.file_card_extension)
        val cardFileDate: TextView = itemView.findViewById(R.id.file_card_date)
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

    override fun getItemCount(): Int {
        return mainList.size
    }
}