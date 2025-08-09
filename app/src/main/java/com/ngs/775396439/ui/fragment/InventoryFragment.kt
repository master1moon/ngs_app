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
import com.ngs.`775396439`.data.entity.Inventory
import com.ngs.`775396439`.data.entity.Package
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import com.ngs.`775396439`.databinding.FragmentInventoryBinding
import com.ngs.`775396439`.ui.adapter.InventoryAdapter
import com.ngs.`775396439`.ui.dialog.InventoryDialogFragment
import com.ngs.`775396439`.ui.viewmodel.InventoryViewModel
import com.ngs.`775396439`.utils.ExportUtils
import kotlinx.coroutines.launch

class InventoryFragment : Fragment() {

    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var inventoryAdapter: InventoryAdapter
    private lateinit var repository: NetworkCardsRepository

    private val viewModel: InventoryViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val database = AppDatabase.getDatabase(requireContext())
                val repository = NetworkCardsRepository(database)
                return InventoryViewModel(repository) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInventoryBinding.inflate(inflater, container, false)
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
        inventoryAdapter = InventoryAdapter(
            onEditClick = { inventory -> showEditDialog(inventory) },
            onDeleteClick = { inventory -> showDeleteDialog(inventory) }
        )

        binding.recyclerViewInventory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = inventoryAdapter
        }
    }

    private fun setupObservers() {
        // Observe inventory list
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.inventory.collect { inventoryList ->
                inventoryAdapter.submitList(inventoryList)
                updateEmptyState(inventoryList.isEmpty())
            }
        }

        // Observe packages list
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.packages.collect { packagesList ->
                inventoryAdapter.setPackages(packagesList)
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
        // Add inventory button
        binding.fabAddInventory.setOnClickListener {
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
        binding.recyclerViewInventory.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun showAddDialog() {
        val packages = viewModel.packages.value
        if (packages.isEmpty()) {
            showSnackbar(getString(com.ngs.`775396439`.R.string.no_packages_available))
            return
        }

        InventoryDialogFragment.newInstance(
            inventory = null,
            packages = packages,
            onSaveClick = { inventory ->
                viewModel.addInventory(
                    packageId = inventory.packageId,
                    quantity = inventory.quantity
                )
            }
        ).show(childFragmentManager, "add_inventory_dialog")
    }

    private fun showEditDialog(inventory: Inventory) {
        val packages = viewModel.packages.value
        if (packages.isEmpty()) {
            showSnackbar(getString(com.ngs.`775396439`.R.string.no_packages_available))
            return
        }

        InventoryDialogFragment.newInstance(
            inventory = inventory,
            packages = packages,
            onSaveClick = { updatedInventory ->
                viewModel.updateInventory(
                    id = updatedInventory.id,
                    packageId = updatedInventory.packageId,
                    quantity = updatedInventory.quantity
                )
            }
        ).show(childFragmentManager, "edit_inventory_dialog")
    }

    private fun showDeleteDialog(inventory: Inventory) {
        val package_ = viewModel.getPackageById(inventory.packageId)
        val packageName = package_?.name ?: "باقة غير معروفة"
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(com.ngs.`775396439`.R.string.delete_inventory_title))
            .setMessage(getString(com.ngs.`775396439`.R.string.delete_inventory_message, packageName))
            .setPositiveButton(getString(com.ngs.`775396439`.R.string.delete)) { _, _ ->
                viewModel.deleteInventory(inventory)
                showSnackbar(getString(com.ngs.`775396439`.R.string.inventory_deleted))
            }
            .setNegativeButton(getString(com.ngs.`775396439`.R.string.cancel), null)
            .show()
    }

    private fun exportToPdf() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val inventory = viewModel.inventory.value
                val packages = viewModel.packages.value
                if (inventory.isNotEmpty()) {
                    ExportUtils.exportInventoryToPdf(requireContext(), inventory, packages)
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
                val inventory = viewModel.inventory.value
                val packages = viewModel.packages.value
                if (inventory.isNotEmpty()) {
                    ExportUtils.exportInventoryToExcel(requireContext(), inventory, packages)
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
                val inventory = viewModel.inventory.value
                val packages = viewModel.packages.value
                if (inventory.isNotEmpty()) {
                    ExportUtils.exportInventoryToJson(requireContext(), inventory, packages)
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