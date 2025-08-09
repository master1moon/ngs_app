package com.ngs.`775396439`.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ngs.`775396439`.data.AppDatabase
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import com.ngs.`775396439`.databinding.FragmentDashboardBinding
import com.ngs.`775396439`.ui.viewmodel.DashboardViewModel
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {
    
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    
    private val repository by lazy {
        NetworkCardsRepository(AppDatabase.getDatabase(requireContext()))
    }
    
    private val viewModel: DashboardViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return DashboardViewModel(repository) as T
            }
        }
    }
    
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
        
        observeDashboardData()
    }
    
    private fun observeDashboardData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dashboardData.collect { data ->
                updateDashboardUI(data)
            }
        }
    }
    
    private fun updateDashboardUI(data: DashboardViewModel.DashboardData) {
        binding.packagesCount.text = data.packagesCount.toString()
        binding.totalCards.text = data.totalCards.toString()
        binding.storesCount.text = data.storesCount.toString()
        binding.totalSales.text = repository.formatNumber(data.totalSales)
        binding.netProfit.text = repository.formatNumber(data.netProfit)
        
        // TODO: Update RecyclerViews for recent packages and stores
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}