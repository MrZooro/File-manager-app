package com.example.filemanagerapp.view

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.example.filemanagerapp.databinding.FileTypeCardBinding
import com.example.filemanagerapp.databinding.FragmentFileTypesBinding
import com.example.filemanagerapp.viewModel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.math.ceil

class FileTypesFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentFileTypesBinding
    private lateinit var viewModel: MainViewModel
    private val fileTypes = arrayOf("txt", "pdf", "png", "jpg")
    private val checkBoxList: MutableList<CheckBox> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFileTypesBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selectedTypes = viewModel.getFileTypes()

        val ds = resources.displayMetrics.density
        val defaultTopMargin = ceil(48 * ds).toInt()
        var topMargin = 0
        for (i in fileTypes.indices) {
            val fileTypeCardBinding = FileTypeCardBinding.inflate(layoutInflater,
                binding.placeForFileTypes, false)

            val layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.topMargin = topMargin
            if (i == fileTypes.size-1) {
                layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                layoutParams.bottomMargin = ceil(16 * ds).toInt()
            }

            fileTypeCardBinding.fileTypeCheckbox.text = fileTypes[i]
            for (j in selectedTypes.indices) {
                if (selectedTypes[j] == fileTypes[i]) {
                    fileTypeCardBinding.fileTypeCheckbox.isChecked = true
                    break
                }
            }

            fileTypeCardBinding.root.layoutParams = layoutParams
            checkBoxList.add(fileTypeCardBinding.fileTypeCheckbox)
            binding.placeForFileTypes.addView(fileTypeCardBinding.root)

            topMargin += defaultTopMargin
        }

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        val fileTypesList: MutableList<String> = mutableListOf()
        for (i in 0 until checkBoxList.size) {
            if (checkBoxList[i].isChecked) {
                fileTypesList.add(checkBoxList[i].text.toString())
            }
        }
        viewModel.setFileTypes(fileTypesList)
    }
}