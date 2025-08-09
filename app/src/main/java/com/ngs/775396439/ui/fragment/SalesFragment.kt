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
import com.ngs.`775396439`.data.entity.Sale
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import com.ngs.`775396439`.databinding.FragmentSalesBinding
import com.ngs.`775396439`.ui.adapter.SalesAdapter
import com.ngs.`775396439`.ui.dialog.SaleDialogFragment
import com.ngs.`775396439`.ui.viewmodel.SalesViewModel
import com.ngs.`775396439`.utils.ExportUtils
import kotlinx.coroutines.launch

class SalesFragment : Fragment() {

    private var _binding: FragmentSalesBinding? = null
    private val binding get() = _binding!!

    private lateinit var salesAdapter: SalesAdapter
    private lateinit var repository: NetworkCardsRepository

    private val viewModel: SalesViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val database = AppDatabase.getDatabase(requireContext())
                val repository = NetworkCardsRepository(database)
                return SalesViewModel(repository) as T
            }
        }
    }

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
        salesAdapter = SalesAdapter(
            onEditClick = { sale -> showEditDialog(sale) },
            onDeleteClick = { sale -> showDeleteDialog(sale) }
        )

        salesAdapter.setViewModel(viewModel)

        binding.recyclerViewSales.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = salesAdapter
        }
    }

    private fun setupObservers() {
        // Observe sales list
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sales.collect { salesList ->
                salesAdapter.submitList(salesList)
                updateEmptyState(salesList.isEmpty())
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
        // Add sale button
        binding.fabAddSale.setOnClickListener {
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
        binding.recyclerViewSales.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun showAddDialog() {
        val stores = viewModel.stores.value
        val packages = viewModel.packages.value
        
        if (stores.isEmpty()) {
            showSnackbar("يرجى إضافة محلات أولاً")
            return
        }
        
        if (packages.isEmpty()) {
            showSnackbar("يرجى إضافة باقات أولاً")
            return
        }

        SaleDialogFragment.newInstance(
            sale = null,
            stores = stores,
            packages = packages,
            onSaveClick = { sale ->
                viewModel.addSale(
                    storeId = sale.storeId,
                    packageId = sale.packageId,
                    reason = sale.reason,
                    quantity = sale.quantity,
                    amount = sale.amount,
                    pricePerUnit = sale.pricePerUnit,
                    total = sale.total,
                    date = sale.date
                )
            }
        ).show(childFragmentManager, "add_sale_dialog")
    }

    private fun showEditDialog(sale: Sale) {
        val stores = viewModel.stores.value
        val packages = viewModel.packages.value

        SaleDialogFragment.newInstance(
            sale = sale,
            stores = stores,
            packages = packages,
            onSaveClick = { updatedSale ->
                viewModel.updateSale(
                    id = updatedSale.id,
                    storeId = updatedSale.storeId,
                    packageId = updatedSale.packageId,
                    reason = updatedSale.reason,
                    quantity = updatedSale.quantity,
                    amount = updatedSale.amount,
                    pricePerUnit = updatedSale.pricePerUnit,
                    total = updatedSale.total,
                    date = updatedSale.date
                )
            }
        ).show(childFragmentManager, "edit_sale_dialog")
    }

    private fun showDeleteDialog(sale: Sale) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(com.ngs.`775396439`.R.string.delete_sale_title))
            .setMessage(getString(com.ngs.`775396439`.R.string.delete_sale_message, sale.reason))
            .setPositiveButton(getString(com.ngs.`775396439`.R.string.delete)) { _, _ ->
                viewModel.deleteSale(sale)
                showSnackbar(getString(com.ngs.`775396439`.R.string.sale_deleted))
            }
            .setNegativeButton(getString(com.ngs.`775396439`.R.string.cancel), null)
            .show()
    }

    private fun exportToPdf() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val sales = viewModel.sales.value
                if (sales.isNotEmpty()) {
                    ExportUtils.exportSalesToPdf(requireContext(), sales)
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
                val sales = viewModel.sales.value
                if (sales.isNotEmpty()) {
                    ExportUtils.exportSalesToExcel(requireContext(), sales)
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
                val sales = viewModel.sales.value
                if (sales.isNotEmpty()) {
                    ExportUtils.exportSalesToJson(requireContext(), sales)
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