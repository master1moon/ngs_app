package com.ngs.`775396439`.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ngs.`775396439`.data.AppDatabase
import com.ngs.`775396439`.data.entity.Package
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import com.ngs.`775396439`.databinding.FragmentPackagesBinding
import com.ngs.`775396439`.ui.adapter.PackagesAdapter
import com.ngs.`775396439`.ui.dialog.PackageDialogFragment
import com.ngs.`775396439`.ui.viewmodel.PackagesViewModel
import kotlinx.coroutines.launch

class PackagesFragment : Fragment() {
    
    private var _binding: FragmentPackagesBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var packagesAdapter: PackagesAdapter
    private lateinit var repository: NetworkCardsRepository
    
    private val viewModel: PackagesViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val database = AppDatabase.getDatabase(requireContext())
                val repository = NetworkCardsRepository(database)
                return PackagesViewModel(repository) as T
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
        
        setupRepository()
        setupRecyclerView()
        setupObservers()
        setupListeners()
    }
    
    private fun setupRepository() {
        val database = AppDatabase.getDatabase(requireContext())
        repository = NetworkCardsRepository(database)
    }
    
    private fun setupRecyclerView() {
        packagesAdapter = PackagesAdapter(
            onEditClick = { package_ ->
                showPackageDialog(package_)
            },
            onDeleteClick = { package_ ->
                showDeleteConfirmation(package_)
            }
        )
        
        binding.packagesRecycler.adapter = packagesAdapter
    }
    
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.packages.collect { packages ->
                packagesAdapter.submitList(packages)
                updateUI(packages)
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.errorMessage.collect { errorMessage ->
                errorMessage?.let { error ->
                    showError(error)
                    viewModel.clearError()
                }
            }
        }
    }
    
    private fun setupListeners() {
        binding.fabAddPackage.setOnClickListener {
            showPackageDialog()
        }
    }
    
    private fun updateUI(packages: List<Package>) {
        binding.packagesCount.text = packages.size.toString()
        
        if (packages.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
            binding.packagesRecycler.visibility = View.GONE
        } else {
            binding.emptyState.visibility = View.GONE
            binding.packagesRecycler.visibility = View.VISIBLE
        }
    }
    
    private fun showPackageDialog(package_: Package? = null) {
        val dialog = PackageDialogFragment.newInstance(package_)
        dialog.setOnSaveCallback { newPackage ->
            if (package_ != null) {
                viewModel.updatePackage(newPackage)
            } else {
                viewModel.addPackage(
                    name = newPackage.name,
                    retailPrice = newPackage.retailPrice,
                    wholesalePrice = newPackage.wholesalePrice,
                    distributorPrice = newPackage.distributorPrice,
                    image = newPackage.image
                )
            }
        }
        dialog.show(childFragmentManager, "package_dialog")
    }
    
    private fun showDeleteConfirmation(package_: Package) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("تأكيد الحذف")
            .setMessage("هل أنت متأكد من حذف الباقة \"${package_.name}\"؟")
            .setPositiveButton("حذف") { _, _ ->
                viewModel.deletePackage(package_)
            }
            .setNegativeButton("إلغاء", null)
            .show()
    }
    
    private fun showError(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("خطأ")
            .setMessage(message)
            .setPositiveButton("حسناً", null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}