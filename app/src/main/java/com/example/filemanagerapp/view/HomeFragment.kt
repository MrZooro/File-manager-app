package com.example.filemanagerapp.view

import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filemanagerapp.databinding.FragmentHomeBinding
import java.io.File


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val path = Environment.getExternalStorageDirectory().path
        val root = File(path)

        val adapterList: MutableList<File> = mutableListOf()
        root.listFiles()?.let { adapterList.addAll(it.toList()) }
        val myAdapter = FileRecyclerItem(adapterList, requireContext())

        binding.homeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.homeRecyclerView.adapter = myAdapter

        binding.pathTvHome.text = path
    }



}