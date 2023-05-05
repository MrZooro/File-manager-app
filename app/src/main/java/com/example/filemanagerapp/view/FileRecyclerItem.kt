package com.example.filemanagerapp.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.filemanagerapp.R
import java.io.File

class FileRecyclerItem(private val mainList: MutableList<File>, private val context: Context)
    : RecyclerView.Adapter<FileRecyclerItem.MyViewHolder>() {



    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardTitle: TextView = itemView.findViewById(R.id.file_card_title)
        val cardImage: View = itemView.findViewById(R.id.file_image_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.file_card, parent, false)
        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.cardTitle.text = mainList[position].name

        if (mainList[position].isDirectory) {
            holder.cardImage.background = ContextCompat.getDrawable(context, R.drawable.ic_folder_32)
        }
    }

    override fun getItemCount(): Int {
        return mainList.size
    }
}