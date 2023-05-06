package com.example.filemanagerapp.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RadioGroup.OnCheckedChangeListener
import androidx.lifecycle.ViewModelProvider
import com.example.filemanagerapp.R
import com.example.filemanagerapp.databinding.FragmentSortByBinding
import com.example.filemanagerapp.viewModel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class SortByFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentSortByBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSortByBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (viewModel.getSortBy()) {
            1 -> {
                binding.alphabeticallyRbSortCard.isChecked = true
            }
            2 -> {
                binding.biggestSizeRbSortCard.isChecked = true
            }
            3 -> {
                binding.smallestSizeRbSortCard.isChecked = true
            }
            4 -> {
                binding.newestDateRbSortCard.isChecked = true
            }
            5 -> {
                binding.oldestDateRbSortCard.isChecked = true
            }
        }

        binding.radioGroupSortCard.setOnCheckedChangeListener{_, checkedId ->

            when (checkedId) {
                R.id.alphabetically_rb_sort_card -> {
                    viewModel.setSortBy(1)
                }
                R.id.biggest_size_rb_sort_card -> {
                    viewModel.setSortBy(2)
                }
                R.id.smallest_size_rb_sort_card -> {
                    viewModel.setSortBy(3)
                }
                R.id.newest_date_rb_sort_card -> {
                    viewModel.setSortBy(4)
                }
                R.id.oldest_date_rb_sort_card -> {
                    viewModel.setSortBy(5)
                }
            }
        }
    }



}
