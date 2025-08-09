package com.ngs.`775396439`.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.ngs.`775396439`.data.AppDatabase
import com.ngs.`775396439`.data.entity.Package
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import com.ngs.`775396439`.databinding.FragmentPackagesBinding
import com.ngs.`775396439`.ui.adapter.PackagesAdapter
import com.ngs.`775396439`.ui.dialog.PackageDialogFragment
import com.ngs.`775396439`.ui.viewmodel.PackagesViewModel
import com.ngs.`775396439`.utils.ExportUtils
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
        setupClickListeners()
    }

    private fun setupRepository() {
        val database = AppDatabase.getDatabase(requireContext())
        repository = NetworkCardsRepository(database)
    }

    private fun setupRecyclerView() {
        packagesAdapter = PackagesAdapter(
            onEditClick = { package_ -> showEditDialog(package_) },
            onDeleteClick = { package_ -> showDeleteDialog(package_) }
        )

        binding.recyclerViewPackages.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = packagesAdapter
        }
    }

    private fun setupObservers() {
        // Observe packages list
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.packages.collect { packages ->
                packagesAdapter.submitList(packages)
                updateEmptyState(packages.isEmpty())
            }
        }

        // Observe loading state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.loadingState.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        // Observe error messages
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.errorMessage.collect { errorMessage ->
                errorMessage?.let {
                    showSnackbar(it)
                    viewModel.clearError()
                }
            }
        }
    }

    private fun setupClickListeners() {
        // Add package button
        binding.fabAddPackage.setOnClickListener {
            showAddDialog()
        }

        // Export buttons
        binding.btnExportPdf.setOnClickListener {
            exportToPdf()
        }

        binding.btnExportExcel.setOnClickListener {
            exportToExcel()
        }

        binding.btnExportJson.setOnClickListener {
            exportToJson()
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.recyclerViewPackages.visibility = if (isEmpty) View.GONE else View.VISIBLE
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
                viewModel.updatePackage(
                    id = updatedPackage.id,
                    name = updatedPackage.name,
                    retailPrice = updatedPackage.retailPrice,
                    wholesalePrice = updatedPackage.wholesalePrice,
                    distributorPrice = updatedPackage.distributorPrice
                )
            }
        ).show(childFragmentManager, "edit_package_dialog")
    }

    private fun showDeleteDialog(package_: Package) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(com.ngs.`775396439`.R.string.delete_package_title))
            .setMessage(getString(com.ngs.`775396439`.R.string.delete_package_message, package_.name))
            .setPositiveButton(getString(com.ngs.`775396439`.R.string.delete)) { _, _ ->
                viewModel.deletePackage(package_)
                showSnackbar(getString(com.ngs.`775396439`.R.string.package_deleted))
            }
            .setNegativeButton(getString(com.ngs.`775396439`.R.string.cancel), null)
            .show()
    }

    private fun exportToPdf() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val packages = viewModel.packages.value
                if (packages.isNotEmpty()) {
                    ExportUtils.exportPackagesToPdf(requireContext(), packages)
                    showSnackbar(getString(com.ngs.`775396439`.R.string.export_pdf_success))
                } else {
                    showSnackbar(getString(com.ngs.`775396439`.R.string.no_data_to_export))
                }
            } catch (e: Exception) {
                showSnackbar(getString(com.ngs.`775396439`.R.string.export_error))
            }
        }
    }

    private fun exportToExcel() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val packages = viewModel.packages.value
                if (packages.isNotEmpty()) {
                    ExportUtils.exportPackagesToExcel(requireContext(), packages)
                    showSnackbar(getString(com.ngs.`775396439`.R.string.export_excel_success))
                } else {
                    showSnackbar(getString(com.ngs.`775396439`.R.string.no_data_to_export))
                }
            } catch (e: Exception) {
                showSnackbar(getString(com.ngs.`775396439`.R.string.export_error))
            }
        }
    }

    private fun exportToJson() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val packages = viewModel.packages.value
                if (packages.isNotEmpty()) {
                    ExportUtils.exportPackagesToJson(requireContext(), packages)
                    showSnackbar(getString(com.ngs.`775396439`.R.string.export_json_success))
                } else {
                    showSnackbar(getString(com.ngs.`775396439`.R.string.no_data_to_export))
                }
            } catch (e: Exception) {
                showSnackbar(getString(com.ngs.`775396439`.R.string.export_error))
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}