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
import com.ngs.cards775396439.data.entity.Payment
import com.ngs.cards775396439.databinding.FragmentPaymentsBinding
import com.ngs.cards775396439.ui.adapter.PaymentsAdapter
import com.ngs.cards775396439.ui.viewmodel.PaymentsViewModel
import kotlinx.coroutines.launch

class PaymentsFragment : Fragment() {
    
    private var _binding: FragmentPaymentsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: PaymentsViewModel
    private lateinit var adapter: PaymentsAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentsBinding.inflate(inflater, container, false)
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
        viewModel = ViewModelProvider(this)[PaymentsViewModel::class.java]
    }
    
    private fun setupRecyclerView() {
        adapter = PaymentsAdapter(
            onEditClick = { payment -> showEditDialog(payment) },
            onDeleteClick = { payment -> showDeleteDialog(payment) }
        )
        
        binding.rvPayments.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@PaymentsFragment.adapter
        }
    }
    
    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.payments.collect { payments ->
                viewModel.stores.collect { stores ->
                    adapter.updatePayments(payments, stores)
                    updateEmptyState(payments.isEmpty())
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
        binding.fabAddPayment.setOnClickListener {
            showAddDialog()
        }
    }
    
    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvPayments.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
    
    private fun showAddDialog() {
        // TODO: Implement payments dialog
        showErrorDialog("سيتم إضافة نافذة إضافة المدفوعات في المرحلة التالية")
    }
    
    private fun showEditDialog(payment: Payment) {
        // TODO: Implement payments edit dialog
        showErrorDialog("سيتم إضافة نافذة تعديل المدفوعات في المرحلة التالية")
    }
    
    private fun showDeleteDialog(payment: Payment) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("حذف المدفوعات")
            .setMessage("هل أنت متأكد من حذف هذه المدفوعات؟")
            .setPositiveButton("حذف") { _, _ ->
                viewModel.deletePayment(payment)
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