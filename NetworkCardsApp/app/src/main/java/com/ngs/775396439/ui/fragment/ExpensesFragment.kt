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
import com.ngs.`775396439`.data.entity.Expense
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import com.ngs.`775396439`.databinding.FragmentExpensesBinding
import com.ngs.`775396439`.ui.adapter.ExpensesAdapter
import com.ngs.`775396439`.ui.dialog.ExpenseDialogFragment
import com.ngs.`775396439`.ui.viewmodel.ExpensesViewModel
import com.ngs.`775396439`.utils.ExportUtils
import kotlinx.coroutines.launch

class ExpensesFragment : Fragment() {

    private var _binding: FragmentExpensesBinding? = null
    private val binding get() = _binding!!

    private lateinit var expensesAdapter: ExpensesAdapter
    private lateinit var repository: NetworkCardsRepository

    private val viewModel: ExpensesViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val database = AppDatabase.getDatabase(requireContext())
                val repository = NetworkCardsRepository(database)
                return ExpensesViewModel(repository) as T
            }
        }
    }

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
        expensesAdapter = ExpensesAdapter(
            onEditClick = { expense -> showEditDialog(expense) },
            onDeleteClick = { expense -> showDeleteDialog(expense) }
        )

        binding.recyclerViewExpenses.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = expensesAdapter
        }
    }

    private fun setupObservers() {
        // Observe expenses list
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.expenses.collect { expensesList ->
                expensesAdapter.submitList(expensesList)
                updateEmptyState(expensesList.isEmpty())
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
        // Add expense button
        binding.fabAddExpense.setOnClickListener {
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
        binding.recyclerViewExpenses.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun showAddDialog() {
        ExpenseDialogFragment.newInstance(
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
        ExpenseDialogFragment.newInstance(
            expense = expense,
            onSaveClick = { updatedExpense ->
                viewModel.updateExpense(
                    id = updatedExpense.id,
                    type = updatedExpense.type,
                    amount = updatedExpense.amount,
                    notes = updatedExpense.notes,
                    date = updatedExpense.date,
                    addLater = updatedExpense.addLater
                )
            }
        ).show(childFragmentManager, "edit_expense_dialog")
    }

    private fun showDeleteDialog(expense: Expense) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(com.ngs.`775396439`.R.string.delete_expense_title))
            .setMessage(getString(com.ngs.`775396439`.R.string.delete_expense_message, expense.type))
            .setPositiveButton(getString(com.ngs.`775396439`.R.string.delete)) { _, _ ->
                viewModel.deleteExpense(expense)
                showSnackbar(getString(com.ngs.`775396439`.R.string.expense_deleted))
            }
            .setNegativeButton(getString(com.ngs.`775396439`.R.string.cancel), null)
            .show()
    }

    private fun exportToPdf() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val expenses = viewModel.expenses.value
                if (expenses.isNotEmpty()) {
                    ExportUtils.exportExpensesToPdf(requireContext(), expenses)
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
                val expenses = viewModel.expenses.value
                if (expenses.isNotEmpty()) {
                    ExportUtils.exportExpensesToExcel(requireContext(), expenses)
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
                val expenses = viewModel.expenses.value
                if (expenses.isNotEmpty()) {
                    ExportUtils.exportExpensesToJson(requireContext(), expenses)
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