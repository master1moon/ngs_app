package com.ngs.cards775396439.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ngs.cards775396439.data.entity.Inventory
import com.ngs.cards775396439.databinding.FragmentInventoryBinding
import com.ngs.cards775396439.ui.adapter.InventoryAdapter
import com.ngs.cards775396439.ui.dialog.InventoryDialogFragment
import com.ngs.cards775396439.ui.viewmodel.InventoryViewModel
import kotlinx.coroutines.launch

class InventoryFragment : Fragment() {
    
    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: InventoryViewModel
    private lateinit var adapter: InventoryAdapter
    
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
        
        setupViewModel()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[InventoryViewModel::class.java]
    }
    
    private fun setupRecyclerView() {
        adapter = InventoryAdapter(
            onEditClick = { inventory -> showEditDialog(inventory) },
            onDeleteClick = { inventory -> showDeleteDialog(inventory) }
        )
        
        binding.rvInventory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@InventoryFragment.adapter
        }
    }
    
    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.inventory.collect { inventory ->
                viewModel.packages.collect { packages ->
                    adapter.updateInventory(inventory, packages)
                    updateEmptyState(inventory.isEmpty())
                }
            }
        }
        
        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
        
        lifecycleScope.launch {
            viewModel.errorMessage.collect { error ->
                error?.let {
                    showErrorDialog(it)
                    viewModel.clearError()
                }
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.fabAddInventory.setOnClickListener {
            showAddDialog()
        }
    }
    
    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvInventory.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
    
    private fun showAddDialog() {
        lifecycleScope.launch {
            viewModel.packages.collect { packages ->
                if (packages.isEmpty()) {
                    showErrorDialog("يرجى إضافة باقات أولاً قبل إضافة الكميات")
                    return@collect
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
        }
    }
    
    private fun showEditDialog(inventory: Inventory) {
        lifecycleScope.launch {
            viewModel.packages.collect { packages ->
                InventoryDialogFragment.newInstance(
                    inventory = inventory,
                    packages = packages,
                    onSaveClick = { updatedInventory ->
                        viewModel.updateInventory(updatedInventory)
                    }
                ).show(childFragmentManager, "edit_inventory_dialog")
            }
        }
    }
    
    private fun showDeleteDialog(inventory: Inventory) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("حذف الكمية")
            .setMessage("هل أنت متأكد من حذف هذه الكمية؟")
            .setPositiveButton("حذف") { _, _ ->
                viewModel.deleteInventory(inventory)
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