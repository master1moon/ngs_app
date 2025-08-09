package com.ngs.cards775396439.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ngs.cards775396439.data.AppDatabase
import com.ngs.cards775396439.data.entity.Package
import com.ngs.cards775396439.data.repository.NetworkCardsRepository
import com.ngs.cards775396439.databinding.FragmentPackagesBinding
import com.ngs.cards775396439.ui.adapter.PackagesAdapter
import com.ngs.cards775396439.ui.dialog.PackageDialogFragment
import com.ngs.cards775396439.ui.viewmodel.PackagesViewModel

class PackagesFragment : Fragment() {
    
    private var _binding: FragmentPackagesBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: PackagesViewModel
    private lateinit var adapter: PackagesAdapter
    
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
        setupClickListeners()
    }
    
    private fun setupRepository() {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = NetworkCardsRepository(database)
        
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return PackagesViewModel(repository) as T
            }
        })[PackagesViewModel::class.java]
    }
    
    private fun setupRecyclerView() {
        adapter = PackagesAdapter(
            onEditClick = { package_ -> showEditDialog(package_) },
            onDeleteClick = { package_ -> showDeleteDialog(package_) }
        )
        
        binding.rvPackages.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@PackagesFragment.adapter
        }
    }
    
    private fun setupObservers() {
        viewModel.packages.observe(viewLifecycleOwner) { packages ->
            adapter.updatePackages(packages)
            updateEmptyState(packages.isEmpty())
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                // Show error message
                showErrorDialog(it)
                viewModel.clearError()
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.fabAddPackage.setOnClickListener {
            showAddDialog()
        }
    }
    
    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvPackages.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
    
    private fun showAddDialog() {
        PackageDialogFragment.newInstance(
            package_ = null,
            onSaveClick = { package_ ->
                viewModel.addPackage(
                    name = package_.name,
                    retailPrice = package_.retailPrice,
                    wholesalePrice = package_.wholesalePrice,
                    distributorPrice = package_.distributorPrice
                )
            }
        ).show(childFragmentManager, "add_package_dialog")
    }
    
    private fun showEditDialog(package_: Package) {
        PackageDialogFragment.newInstance(
            package_ = package_,
            onSaveClick = { updatedPackage ->
                viewModel.updatePackage(updatedPackage)
            }
        ).show(childFragmentManager, "edit_package_dialog")
    }
    
    private fun showDeleteDialog(package_: Package) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("حذف الباقة")
            .setMessage("هل أنت متأكد من حذف الباقة \"${package_.name}\"؟")
            .setPositiveButton("حذف") { _, _ ->
                viewModel.deletePackage(package_)
            }
            .setNegativeButton("إلغاء", null)
            .show()
    }
    
    private fun showErrorDialog(message: String) {
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