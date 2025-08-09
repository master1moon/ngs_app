package com.ngs.cards775396439.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ngs.cards775396439.databinding.FragmentPackagesBinding
import com.ngs.cards775396439.R

class PackagesFragment : Fragment() {
    
    private var _binding: FragmentPackagesBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPackagesBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupPackagesSection()
    }
    
    private fun setupPackagesSection() {
        binding.tvTitle.text = getString(R.string.packages_title)
        binding.tvDescription.text = getString(R.string.packages_description)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}