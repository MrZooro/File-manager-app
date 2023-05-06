package com.example.filemanagerapp.view

import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filemanagerapp.R
import com.example.filemanagerapp.databinding.FragmentHomeBinding
import com.example.filemanagerapp.viewModel.MainViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileFilter


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapterList: MutableList<File> = mutableListOf()
        val myAdapter = FileRecyclerItem(adapterList, requireContext(), this)
        binding.homeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.homeRecyclerView.adapter = myAdapter


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.curFileStateFlow.collect{ newRoot ->
                    adapterList.clear()

                    binding.pathTvHome.text = newRoot.path

                    if (newRoot == viewModel.defaultDirectory) {
                        binding.backButtonHome.visibility = View.GONE
                    } else if (binding.backButtonHome.isGone){
                        binding.backButtonHome.visibility = View.VISIBLE
                    }

                    val tempFilesList = newRoot.listFiles()
                    if (tempFilesList != null) {
                        when (viewModel.getSortBy()) {
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
                        adapterList.addAll(tempFilesList)
                    }
                    myAdapter.notifyDataSetChanged()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.sortByStateFlow.collect{sortBy ->
                    adapterList.clear()
                    Log.w(tag, sortBy.toString())

                    val tempFilesList = viewModel.getCurFile().listFiles()
                    if (tempFilesList != null) {
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
                        adapterList.addAll(tempFilesList)
                    }
                    myAdapter.notifyDataSetChanged()
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
    }

    override fun onItemClick(clickedFile: File) {
        if (clickedFile.isDirectory) {
            viewModel.setCurFile(clickedFile)
        } else {
            Toast.makeText(requireContext(), "Okay, its file", Toast.LENGTH_SHORT).show()
        }
    }


}