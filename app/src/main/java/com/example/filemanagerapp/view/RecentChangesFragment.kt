package com.example.filemanagerapp.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.filemanagerapp.databinding.FragmentRecentChangesBinding
import com.example.filemanagerapp.model.dataClasses.OnItemClickListener
import com.example.filemanagerapp.viewModel.MainViewModel
import java.io.File

class RecentChangesFragment : Fragment(), OnItemClickListener {

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

    private lateinit var myAdapter: FileRecyclerItem

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myAdapter = FileRecyclerItem(requireContext(), this)
        binding.recentChangesRecyclerView.adapter = myAdapter

        Handler(Looper.getMainLooper()).postDelayed(
            {
                updateRecyclerView(viewModel.getRecentFiles())
            },
            300
        )
    }

    private fun updateRecyclerView(tempFilesList: List<File>?) {
        val newList: MutableList<File> = mutableListOf()

        if (tempFilesList != null) {
            newList.addAll(tempFilesList)
        }
        myAdapter.setNewList(newList)
    }

    override fun onItemClick(clickedFile: File) {
        Toast.makeText(requireContext(), "Okay, its file", Toast.LENGTH_SHORT).show()
    }
}