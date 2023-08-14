package com.example.filemanagerapp.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.filemanagerapp.R
import com.example.filemanagerapp.databinding.FragmentRecentChangesBinding
import com.example.filemanagerapp.model.dataClasses.OnItemClickListener
import com.example.filemanagerapp.viewModel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class RecentChangesFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentRecentChangesBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        (activity as MainActivity).turnOnBottomNavigation()
        binding = FragmentRecentChangesBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        return binding.root
    }

    private lateinit var myAdapter: FileRecyclerItem

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.openRecentFragment = true
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewModel.openHomeFragment) {
                    viewModel.openRecentFragment = false
                    findNavController().navigate(R.id.action_recentChangesFragment_to_homeFragment)
                } else {
                    requireActivity().finish()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)

        myAdapter = FileRecyclerItem(requireContext(), this)
        binding.recentChangesRecyclerView.adapter = myAdapter

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val recentFiles = viewModel.getRecentFiles()

            Handler(Looper.getMainLooper()).postDelayed(
                {
                    updateRecyclerView(recentFiles)
                },
                300
            )
        }
    }

    private fun updateRecyclerView(tempFilesList: List<File>) {
        myAdapter.setNewList(tempFilesList.toMutableList())
    }

    override fun onItemClick(clickedFile: File) {
        Toast.makeText(requireContext(), "Okay, its file", Toast.LENGTH_SHORT).show()
    }
}