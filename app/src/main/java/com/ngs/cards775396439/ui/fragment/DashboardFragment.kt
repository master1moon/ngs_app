package com.ngs.cards775396439.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ngs.cards775396439.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {
    
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize dashboard with sample data
        setupDashboard()
    }
    
    private fun setupDashboard() {
        // Sample dashboard data
        binding.tvPackagesCount.text = "0"
        binding.tvTotalCards.text = "0"
        binding.tvStoresCount.text = "0"
        binding.tvTotalSales.text = "0 د.ك"
        binding.tvNetProfit.text = "0 د.ك"
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}