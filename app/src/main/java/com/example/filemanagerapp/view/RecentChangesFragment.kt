package com.example.filemanagerapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filemanagerapp.databinding.FragmentRecentChangesBinding
import com.example.filemanagerapp.model.room.FileEntity
import com.example.filemanagerapp.viewModel.MainViewModel
import kotlinx.coroutines.launch
import java.io.File

class RecentChangesFragment : Fragment(), FileRecyclerItem.OnItemClickListener {

    private lateinit var binding: FragmentRecentChangesBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentRecentChangesBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        return binding.root
    }

    private lateinit var adapterList: MutableList<File>
    private lateinit var myAdapter: FileRecyclerItem

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapterList = mutableListOf()
        myAdapter = FileRecyclerItem(adapterList, requireContext(), this)
        binding.recentChangesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recentChangesRecyclerView.adapter = myAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.getRecentChanged().collect{
                    val filesList = convertFileEntity(it)
                    updateRecyclerView(filesList)
                }
            }
        }
    }

    private fun convertFileEntity(fileEntities: List<FileEntity>): List<File> {
        val fileList: MutableList<File> = mutableListOf()
        for (i in fileEntities.indices) {
            val tempFile = File(fileEntities[i].path)
            fileList.add(tempFile)
        }
        return fileList
    }

    private fun updateRecyclerView(tempFilesList: List<File>?) {
        adapterList.clear()
        if (tempFilesList != null) {
            adapterList.addAll(tempFilesList)
        }
        myAdapter.notifyDataSetChanged()
    }

    override fun onItemClick(clickedFile: File) {
    }

    override fun fileDeleted(file: File) {
    }

    override fun fileRenamed() {
    }
}