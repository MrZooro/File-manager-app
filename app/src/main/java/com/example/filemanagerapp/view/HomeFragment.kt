package com.example.filemanagerapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.filemanagerapp.R
import com.example.filemanagerapp.databinding.FragmentHomeBinding
import com.example.filemanagerapp.model.dataClasses.OnItemClickListener
import com.example.filemanagerapp.viewModel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class HomeFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: MainViewModel
    private val tag = "HomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        (activity as MainActivity).turnOnBottomNavigation()
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        return binding.root
    }

    private lateinit var myAdapter: FileRecyclerItem

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myAdapter = FileRecyclerItem(requireContext(), this)
        binding.homeRecyclerView.adapter = myAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.filesInDirectoryStateFlow.collect { filesList ->

                    val newRoot = viewModel.getCurFile()
                    binding.pathTvHome.text = newRoot.path
                    if (newRoot == viewModel.defaultDirectory) {
                        binding.backButtonHome.visibility = View.GONE
                    } else if (binding.backButtonHome.isGone){
                        binding.backButtonHome.visibility = View.VISIBLE
                    }

                    if (filesList.isEmpty()) {
                        binding.noFileTvHome.visibility = View.VISIBLE
                    } else {
                        binding.noFileTvHome.visibility = View.GONE
                    }
                    myAdapter.setNewList(filesList.toMutableList())

                }
            }
        }

        viewModel.getFilesInDirectory()

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

    override fun onItemClick(clickedFile: File) {
        if (clickedFile.isDirectory) {
            viewModel.setCurFile(clickedFile)
        } else {
            Toast.makeText(requireContext(), "Okay, its file", Toast.LENGTH_SHORT).show()
        }
    }


}