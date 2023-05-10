package com.example.filemanagerapp.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filemanagerapp.R
import com.example.filemanagerapp.databinding.FragmentHomeBinding
import com.example.filemanagerapp.viewModel.MainViewModel
import kotlinx.coroutines.launch
import java.io.File


class HomeFragment : Fragment(), FileRecyclerItem.OnItemClickListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: MainViewModel
    private val tag = "HomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        return binding.root
    }

    private lateinit var adapterList: MutableList<File>
    private lateinit var myAdapter: FileRecyclerItem

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapterList= mutableListOf()
        myAdapter = FileRecyclerItem(adapterList, requireContext(), this)
        binding.homeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.homeRecyclerView.adapter = myAdapter


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.curFileStateFlow.collect{ newRoot ->

                    binding.pathTvHome.text = newRoot.path

                    if (newRoot == viewModel.defaultDirectory) {
                        binding.backButtonHome.visibility = View.GONE
                    } else if (binding.backButtonHome.isGone){
                        binding.backButtonHome.visibility = View.VISIBLE
                    }

                    val tempFilesList = newRoot.listFiles()

                    updateRecyclerView(tempFilesList, viewModel.getFileTypes(), viewModel.getSortBy())
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.sortByStateFlow.collect{sortBy ->
                    updateRecyclerView(viewModel.getCurFile().listFiles(), viewModel.getFileTypes(),
                    sortBy)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.fileTypesStateFlow.collect{ fileTypesList ->
                    updateRecyclerView(viewModel.getCurFile().listFiles(), fileTypesList,
                        viewModel.getSortBy())
                }
            }
        }



        binding.backButtonHome.setOnClickListener{
            val tempFile = viewModel.getCurFile()
            if (tempFile != viewModel.defaultDirectory) {
                val parentFile = tempFile.parentFile
                if (parentFile != null) {
                    viewModel.setCurFile(parentFile)
                }
            }
        }

        binding.sortByButtonHome.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_sortByFragment)
        }

        binding.fileTypesButtonHome.setOnClickListener{
            val frag = FileTypesFragment()
            frag.show(requireActivity().supportFragmentManager, "Something")
        }

    }

    private fun sortList(tempFilesList: MutableList<File>, sortBy: Int) {
        when (sortBy) {
            2 -> {
                tempFilesList.sortByDescending { it.length() }
            }
            3 -> {
                tempFilesList.sortBy { it.length() }
            }
            4 -> {
                tempFilesList.sortByDescending { it.lastModified() }
            }
            5 -> {
                tempFilesList.sortBy { it.lastModified() }
            }
        }
    }

    private fun sortList(tempFilesList: Array<File>, sortBy: Int) {
        when (sortBy) {
            2 -> {
                tempFilesList.sortByDescending { it.length() }
            }
            3 -> {
                tempFilesList.sortBy { it.length() }
            }
            4 -> {
                tempFilesList.sortByDescending { it.lastModified() }
            }
            5 -> {
                tempFilesList.sortBy { it.lastModified() }
            }
        }
    }

    private fun getFilesByTypes(tempFilesList: Array<File>,
                                fileTypesList: List<String>): MutableList<File> {

        val mutableFileList: MutableList<File> = mutableListOf()
        for (i in tempFilesList.indices) {
            for (j in fileTypesList.indices) {
                if (tempFilesList[i].extension == fileTypesList[j]) {
                    mutableFileList.add(tempFilesList[i])
                }
            }
        }

        return mutableFileList
    }

    override fun onItemClick(clickedFile: File) {
        if (clickedFile.isDirectory) {
            viewModel.setCurFile(clickedFile)
        } else {
            Toast.makeText(requireContext(), "Okay, its file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateRecyclerView(tempFilesList: Array<File>?, fileTypes: List<String>, sortBy: Int) {
        adapterList.clear()

        if (tempFilesList != null) {
            if (fileTypes.isNotEmpty()) {
                val newFileList = getFilesByTypes(tempFilesList, fileTypes)
                sortList(newFileList, sortBy)
                adapterList.addAll(newFileList)
            } else {
                sortList(tempFilesList, sortBy)
                adapterList.addAll(tempFilesList)
            }
        }
        if (adapterList.isEmpty()) {
            binding.noFileTvHome.visibility = View.VISIBLE
        } else {
            binding.noFileTvHome.visibility = View.GONE
        }
        myAdapter.notifyDataSetChanged()
    }

    override fun fileDeleted(file: File) {
        adapterList.remove(file)
        myAdapter.notifyDataSetChanged()
    }

    override fun fileRenamed() {
        updateRecyclerView(viewModel.getCurFile().listFiles(), viewModel.getFileTypes(),
        viewModel.getSortBy())
    }


}