package com.example.filemanagerapp.view

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.filemanagerapp.BuildConfig
import com.example.filemanagerapp.R
import com.example.filemanagerapp.databinding.FragmentSplashBinding
import com.example.filemanagerapp.viewModel.MainViewModel
import com.example.filemanagerapp.viewModel.SplashViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File

class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding
    private lateinit var viewModel: SplashViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSplashBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[SplashViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.motionLayoutSplash.setTransitionListener(object : MotionLayout.TransitionListener {

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                viewLifecycleOwner.lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.CREATED) {
                        viewModel.databaseFilledStateFlow.collect{
                            if (it) {
                                Log.i(tag, "Database filled")
                                findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                            }
                        }
                    }
                }

                binding.startButtonSplash.setOnClickListener{
                    checkPermissions()
                }
            }

            override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int
            ) {
            }

            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int,
                                            progress: Float
            ) {
            }

            override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean,
                progress: Float
            ) {
            }

        })

    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                startActivity(
                    Intent(
                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                        uri
                    )
                )

            }
        } else {
            val result = ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if (result == PackageManager.PERMISSION_DENIED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(requireContext(), "Storage permission is requires," +
                            "please allow from settings", Toast.LENGTH_SHORT).show()
                } else {
                    requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            } else {
                openHomeFragment()
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(requireContext(), "Permission is granted", Toast.LENGTH_SHORT).show()
            openHomeFragment()
        } else {
            Toast.makeText(requireContext(), "Permission is not granted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openHomeFragment() {
        if (!viewModel.databaseIsFilling) {
            viewModel.databaseIsFilling = true
            viewModel.fillDatabase()
        }
    }
}