package com.ngs.`775396439`.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.ngs.`775396439`.data.AppDatabase
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import com.ngs.`775396439`.databinding.FragmentReportsBinding
import com.ngs.`775396439`.ui.viewmodel.ReportsViewModel
import com.ngs.`775396439`.utils.ExportUtils
import kotlinx.coroutines.launch

class ReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: NetworkCardsRepository

    private val viewModel: ReportsViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val database = AppDatabase.getDatabase(requireContext())
                val repository = NetworkCardsRepository(database)
                return ReportsViewModel(repository) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRepository()
        setupObservers()
        setupClickListeners()
    }

    private fun setupRepository() {
        val database = AppDatabase.getDatabase(requireContext())
        repository = NetworkCardsRepository(database)
    }

    private fun setupObservers() {
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

        // Observe dashboard stats
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.totalPackages.collect { total ->
                binding.tvTotalPackages.text = total.toString()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.totalStores.collect { total ->
                binding.tvTotalStores.text = total.toString()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.totalCards.collect { total ->
                binding.tvTotalCards.text = total.toString()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.netProfit.collect { profit ->
                binding.tvNetProfit.text = formatCurrency(profit)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.totalSales.collect { sales ->
                binding.tvTotalSales.text = formatCurrency(sales)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.totalExpenses.collect { expenses ->
                binding.tvTotalExpenses.text = formatCurrency(expenses)
            }
        }
    }

    private fun setupClickListeners() {
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

    private fun formatCurrency(amount: Double): String {
        return repository.formatNumber(amount.toLong()) + " د.ك"
    }

    private fun exportToPdf() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val sales = viewModel.salesReport.value
                val expenses = viewModel.expensesReport.value
                val payments = viewModel.paymentsReport.value
                
                if (sales.isNotEmpty() || expenses.isNotEmpty() || payments.isNotEmpty()) {
                    ExportUtils.exportReportsToPdf(requireContext(), sales, expenses, payments)
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
                val sales = viewModel.salesReport.value
                val expenses = viewModel.expensesReport.value
                val payments = viewModel.paymentsReport.value
                
                if (sales.isNotEmpty() || expenses.isNotEmpty() || payments.isNotEmpty()) {
                    ExportUtils.exportReportsToExcel(requireContext(), sales, expenses, payments)
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
                val sales = viewModel.salesReport.value
                val expenses = viewModel.expensesReport.value
                val payments = viewModel.paymentsReport.value
                
                if (sales.isNotEmpty() || expenses.isNotEmpty() || payments.isNotEmpty()) {
                    ExportUtils.exportReportsToJson(requireContext(), sales, expenses, payments)
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