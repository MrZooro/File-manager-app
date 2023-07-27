package com.example.filemanagerapp.view

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.animation.doOnEnd
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.filemanagerapp.BuildConfig
import com.example.filemanagerapp.R
import com.example.filemanagerapp.databinding.FragmentSplashBinding


class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSplashBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appIconAnim = ObjectAnimator.ofFloat(
            binding.wasteidFileManagerIconSplash, "alpha",
            0.0f, 1.0f
        )
        appIconAnim.duration = 200
        appIconAnim.interpolator = LinearInterpolator()

        appIconAnim.doOnEnd {
            val permission = if (isAdded) {
                isPermissionGranted()
            } else {
                return@doOnEnd
            }

            if (permission) {
                openHomeFragment()
            } else {
                val scaleXAnim = ObjectAnimator.ofFloat(
                    binding.startButtonSplash, "scaleX",
                    0.0f, 1.0f
                )
                val fadeOutAnim = ObjectAnimator.ofFloat(
                    binding.startButtonSplash, "alpha",
                    0.0f, 1.0f
                )

                val animatorSet = AnimatorSet()
                animatorSet.duration = 200
                animatorSet.interpolator = LinearInterpolator()
                animatorSet.playTogether(scaleXAnim, fadeOutAnim)

                animatorSet.doOnEnd {
                    binding.startButtonSplash.setOnClickListener {
                        requestPermissions()
                    }
                }

                animatorSet.start()
            }
        }
        appIconAnim.start()
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
            startActivity(
                Intent(
                    Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    uri
                )
            )
        } else {

            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(requireContext(), "Storage permission is requires," +
                        "please allow from settings", Toast.LENGTH_SHORT).show()
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private fun isPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result = ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

            result != PackageManager.PERMISSION_DENIED
        }
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

            } else {
                openHomeFragment()
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
        findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
    }
}