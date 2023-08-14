package com.example.filemanagerapp.view

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateInterpolator
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
    private var isFragmentOpening = true

    @SuppressLint("ObjectAnimatorBinding")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.openHomeFragment = true
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val rootFile = viewModel.getCurFile()
                if (rootFile != viewModel.defaultDirectory) {
                    val parentFile = rootFile.parentFile
                    if (parentFile != null) {
                        viewModel.setCurFile(parentFile)
                    }
                } else {
                    if (viewModel.openRecentFragment) {
                        viewModel.openHomeFragment = false
                        findNavController().navigate(R.id.action_homeFragment_to_recentChangesFragment)
                    } else {
                        requireActivity().finish()
                    }
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)



        myAdapter = FileRecyclerItem(requireContext(), this)
        binding.homeRecyclerView.adapter = myAdapter

        viewModel.getFilesInDirectory()

        val noFileTvAnim = ObjectAnimator.ofFloat(
            binding.noFileTvHome, "alpha",
            0.0f, 1.0f
        )
        noFileTvAnim.duration = 400
        noFileTvAnim.interpolator = AnticipateInterpolator()


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.filesInDirectoryStateFlow.collect { filesList ->

                    val newRoot = viewModel.getCurFile()

                    if (newRoot == viewModel.defaultDirectory) {
                        binding.pathTvHome.text = "/"
                        binding.topAppBarHome.transitionToStart()
                    } else {
                        val startPathIndex = viewModel.defaultDirectory.path.length
                        binding.pathTvHome.text = newRoot.path.substring(startPathIndex)

                        if (binding.backButtonHome.alpha == 0f){
                            binding.topAppBarHome.transitionToEnd()
                        }
                    }

                    val delayMillis: Long = if (isFragmentOpening) {
                        isFragmentOpening = false
                        300
                    } else {
                        myAdapter.removeAllFiles()
                        200
                    }

                    Handler(Looper.getMainLooper()).postDelayed(
                        {

                            if (filesList.isEmpty()) {
                                myAdapter.removeAllFiles()

                                noFileTvAnim.start()
                            } else {
                                noFileTvAnim.end()
                                binding.noFileTvHome.alpha = 0f
                                myAdapter.setNewList(filesList.toMutableList())
                            }
                        },
                        delayMillis
                    )

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

    override fun onItemClick(clickedFile: File) {
        if (clickedFile.isDirectory) {
            viewModel.setCurFile(clickedFile)
        } else {
            Toast.makeText(requireContext(), "Okay, its file", Toast.LENGTH_SHORT).show()
        }
    }


}