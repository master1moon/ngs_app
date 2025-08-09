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
import com.ngs.cards775396439.data.entity.Sale
import com.ngs.cards775396439.databinding.FragmentSalesBinding
import com.ngs.cards775396439.ui.adapter.SalesAdapter
import com.ngs.cards775396439.ui.viewmodel.SalesViewModel
import kotlinx.coroutines.launch

class SalesFragment : Fragment() {
    
    private var _binding: FragmentSalesBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: SalesViewModel
    private lateinit var adapter: SalesAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSalesBinding.inflate(inflater, container, false)
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
        viewModel = ViewModelProvider(this)[SalesViewModel::class.java]
    }
    
    private fun setupRecyclerView() {
        adapter = SalesAdapter(
            onEditClick = { sale -> showEditDialog(sale) },
            onDeleteClick = { sale -> showDeleteDialog(sale) }
        )
        
        binding.rvSales.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@SalesFragment.adapter
        }
    }
    
    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.sales.collect { sales ->
                viewModel.stores.collect { stores ->
                    viewModel.packages.collect { packages ->
                        adapter.updateSales(sales, stores, packages)
                        updateEmptyState(sales.isEmpty())
                    }
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
        binding.fabAddSale.setOnClickListener {
            showAddDialog()
        }
    }
    
    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvSales.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
    
    private fun showAddDialog() {
        // TODO: Implement sales dialog
        showErrorDialog("سيتم إضافة نافذة إضافة المبيعات في المرحلة التالية")
    }
    
    private fun showEditDialog(sale: Sale) {
        // TODO: Implement sales edit dialog
        showErrorDialog("سيتم إضافة نافذة تعديل المبيعات في المرحلة التالية")
    }
    
    private fun showDeleteDialog(sale: Sale) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("حذف المبيعات")
            .setMessage("هل أنت متأكد من حذف هذه المبيعات؟")
            .setPositiveButton("حذف") { _, _ ->
                viewModel.deleteSale(sale)
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