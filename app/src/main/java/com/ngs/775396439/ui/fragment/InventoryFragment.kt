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
import com.ngs.`775396439`.data.entity.Inventory
import com.ngs.`775396439`.data.entity.Package
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import com.ngs.`775396439`.databinding.FragmentInventoryBinding
import com.ngs.`775396439`.ui.adapter.InventoryAdapter
import com.ngs.`775396439`.ui.dialog.InventoryDialogFragment
import com.ngs.`775396439`.ui.viewmodel.InventoryViewModel
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
        inflater: LayoutInflater, container: ViewGroup?,
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
        setupListeners()
    }

    private fun setupRepository() {
        val database = AppDatabase.getDatabase(requireContext())
        repository = NetworkCardsRepository(database)
    }

    private fun setupRecyclerView() {
        inventoryAdapter = InventoryAdapter(
            onEditClick = { inventory ->
                showInventoryDialog(inventory)
            },
            onDeleteClick = { inventory ->
                showDeleteConfirmation(inventory)
            }
        )

        binding.inventoryRecycler.adapter = inventoryAdapter
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.inventory.collect { inventory ->
                inventoryAdapter.submitList(inventory)
                updateUI(inventory)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.packages.collect { packages ->
                inventoryAdapter.setPackages(packages)
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
        binding.fabAddInventory.setOnClickListener {
            showInventoryDialog()
        }
    }

    private fun updateUI(inventory: List<Inventory>) {
        binding.inventoryCount.text = inventory.size.toString()

        if (inventory.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
            binding.inventoryRecycler.visibility = View.GONE
        } else {
            binding.emptyState.visibility = View.GONE
            binding.inventoryRecycler.visibility = View.VISIBLE
        }
    }

    private fun showInventoryDialog(inventory: Inventory? = null) {
        // الحصول على قائمة الباقات الحالية
        val currentPackages = viewModel.packages.value
        
        val dialog = InventoryDialogFragment.newInstance(inventory, currentPackages)
        dialog.setOnSaveCallback { newInventory ->
            if (inventory != null) {
                viewModel.updateInventory(newInventory)
            } else {
                viewModel.addInventory(
                    packageId = newInventory.packageId,
                    quantity = newInventory.quantity,
                    date = newInventory.createdAt
                )
            }
        }
        dialog.show(childFragmentManager, "inventory_dialog")
    }

    private fun showDeleteConfirmation(inventory: Inventory) {
        val package_ = viewModel.getPackageById(inventory.packageId)
        val packageName = package_?.name ?: "باقة غير معروفة"
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("تأكيد الحذف")
            .setMessage("هل أنت متأكد من حذف الكمية \"${packageName}\"؟")
            .setPositiveButton("حذف") { _, _ ->
                viewModel.deleteInventory(inventory)
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