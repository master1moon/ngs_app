package com.ngs.cards775396439.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ngs.cards775396439.data.AppDatabase
import com.ngs.cards775396439.data.repository.NetworkCardsRepository
import com.ngs.cards775396439.databinding.FragmentPackagesBinding
import com.ngs.cards775396439.ui.viewmodel.PackagesViewModel

class PackagesFragment : Fragment() {
    
    private var _binding: FragmentPackagesBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: PackagesViewModel
    
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
        
        setupRepository()
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupRepository() {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = NetworkCardsRepository(database)
        viewModel = ViewModelProvider(this)[PackagesViewModel::class.java]
    }
    
    private fun setupObservers() {
        viewModel.packages.observe(viewLifecycleOwner) { packages ->
            // TODO: Update RecyclerView with packages
            binding.textView.text = "عدد الباقات: ${packages.size}"
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                // TODO: Show error message
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.fabAdd.setOnClickListener {
            // TODO: Show add package dialog
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}