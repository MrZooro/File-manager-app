package com.example.filemanagerapp.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filemanagerapp.databinding.FragmentRecentChangesBinding
import com.example.filemanagerapp.viewModel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        binding.recentChangesRecyclerView.adapter = myAdapter

        Handler(Looper.getMainLooper()).postDelayed(
            {
                updateRecyclerView(viewModel.getRecentFiles())
            },
            300
        )
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