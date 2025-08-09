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
        
        // Create ViewModel with repository
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return PackagesViewModel(repository) as T
            }
        })[PackagesViewModel::class.java]
    }
    
    private fun setupObservers() {
        viewModel.packages.observe(viewLifecycleOwner) { packages ->
            binding.textView.text = "عدد الباقات: ${packages.size}"
            
            if (packages.isEmpty()) {
                binding.textView.text = "لا توجد باقات حالياً\nاضغط زر الإضافة لإنشاء باقة جديدة"
            } else {
                val packagesText = packages.joinToString("\n") { 
                    "• ${it.name} - ${it.price} د.ك" 
                }
                binding.textView.text = "الباقات المتوفرة:\n$packagesText"
            }
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                binding.textView.text = "خطأ: $it"
            }
        }
    }
    
    private fun setupClickListeners() {
        // Add a simple button for testing
        binding.textView.setOnClickListener {
            // Add a test package
            viewModel.addPackage("باقة تجريبية", 10.0, "باقة للاختبار")
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}