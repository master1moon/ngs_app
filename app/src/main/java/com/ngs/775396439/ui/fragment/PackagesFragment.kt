package com.ngs.`775396439`.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ngs.`775396439`.data.AppDatabase
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import com.ngs.`775396439`.databinding.FragmentPackagesBinding
import com.ngs.`775396439`.ui.viewmodel.PackageViewModel

class PackagesFragment : Fragment() {
    
    private var _binding: FragmentPackagesBinding? = null
    private val binding get() = _binding!!
    
    private val repository by lazy {
        NetworkCardsRepository(AppDatabase.getDatabase(requireContext()))
    }
    
    private val viewModel: PackageViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return PackageViewModel(repository) as T
            }
        }
    }
    
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
        
        // TODO: Implement packages UI
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}