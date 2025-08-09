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
import com.ngs.cards775396439.data.entity.Expense
import com.ngs.cards775396439.databinding.FragmentExpensesBinding
import com.ngs.cards775396439.ui.adapter.ExpensesAdapter
import com.ngs.cards775396439.ui.dialog.ExpensesDialogFragment
import com.ngs.cards775396439.ui.viewmodel.ExpensesViewModel
import kotlinx.coroutines.launch

class ExpensesFragment : Fragment() {
    
    private var _binding: FragmentExpensesBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: ExpensesViewModel
    private lateinit var adapter: ExpensesAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpensesBinding.inflate(inflater, container, false)
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
        viewModel = ViewModelProvider(this)[ExpensesViewModel::class.java]
    }
    
    private fun setupRecyclerView() {
        adapter = ExpensesAdapter(
            onEditClick = { expense -> showEditDialog(expense) },
            onDeleteClick = { expense -> showDeleteDialog(expense) }
        )
        
        binding.rvExpenses.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ExpensesFragment.adapter
        }
    }
    
    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.expenses.collect { expenses ->
                adapter.updateExpenses(expenses)
                updateEmptyState(expenses.isEmpty())
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
        binding.fabAddExpense.setOnClickListener {
            showAddDialog()
        }
    }
    
    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvExpenses.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
    
    private fun showAddDialog() {
        ExpensesDialogFragment.newInstance(
            expense = null,
            onSaveClick = { expense ->
                viewModel.addExpense(
                    type = expense.type,
                    amount = expense.amount,
                    notes = expense.notes,
                    date = expense.date,
                    addLater = expense.addLater
                )
            }
        ).show(childFragmentManager, "add_expense_dialog")
    }
    
    private fun showEditDialog(expense: Expense) {
        ExpensesDialogFragment.newInstance(
            expense = expense,
            onSaveClick = { updatedExpense ->
                viewModel.updateExpense(updatedExpense)
            }
        ).show(childFragmentManager, "edit_expense_dialog")
    }
    
    private fun showDeleteDialog(expense: Expense) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("حذف المصروف")
            .setMessage("هل أنت متأكد من حذف المصروف \"${expense.type}\"؟")
            .setPositiveButton("حذف") { _, _ ->
                viewModel.deleteExpense(expense)
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