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
import com.ngs.`775396439`.data.entity.Payment
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import com.ngs.`775396439`.databinding.FragmentPaymentsBinding
import com.ngs.`775396439`.ui.adapter.PaymentsAdapter
import com.ngs.`775396439`.ui.dialog.PaymentDialogFragment
import com.ngs.`775396439`.ui.viewmodel.PaymentsViewModel
import com.ngs.`775396439`.utils.ExportUtils
import kotlinx.coroutines.launch

class PaymentsFragment : Fragment() {

    private var _binding: FragmentPaymentsBinding? = null
    private val binding get() = _binding!!

    private lateinit var paymentsAdapter: PaymentsAdapter
    private lateinit var repository: NetworkCardsRepository

    private val viewModel: PaymentsViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val database = AppDatabase.getDatabase(requireContext())
                val repository = NetworkCardsRepository(database)
                return PaymentsViewModel(repository) as T
            }
        }
    }

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
        paymentsAdapter = PaymentsAdapter(
            onEditClick = { payment -> showEditDialog(payment) },
            onDeleteClick = { payment -> showDeleteDialog(payment) }
        )

        paymentsAdapter.setViewModel(viewModel)

        binding.recyclerViewPayments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = paymentsAdapter
        }
    }

    private fun setupObservers() {
        // Observe payments list
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.payments.collect { paymentsList ->
                paymentsAdapter.submitList(paymentsList)
                updateEmptyState(paymentsList.isEmpty())
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
        // Add payment button
        binding.fabAddPayment.setOnClickListener {
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
        binding.recyclerViewPayments.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun showAddDialog() {
        val stores = viewModel.stores.value
        
        if (stores.isEmpty()) {
            showSnackbar("يرجى إضافة محلات أولاً")
            return
        }

        PaymentDialogFragment.newInstance(
            payment = null,
            stores = stores,
            onSaveClick = { payment ->
                viewModel.addPayment(
                    storeId = payment.storeId,
                    amount = payment.amount,
                    notes = payment.notes,
                    date = payment.date
                )
            }
        ).show(childFragmentManager, "add_payment_dialog")
    }

    private fun showEditDialog(payment: Payment) {
        val stores = viewModel.stores.value

        PaymentDialogFragment.newInstance(
            payment = payment,
            stores = stores,
            onSaveClick = { updatedPayment ->
                viewModel.updatePayment(
                    id = updatedPayment.id,
                    storeId = updatedPayment.storeId,
                    amount = updatedPayment.amount,
                    notes = updatedPayment.notes,
                    date = updatedPayment.date
                )
            }
        ).show(childFragmentManager, "edit_payment_dialog")
    }

    private fun showDeleteDialog(payment: Payment) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(com.ngs.`775396439`.R.string.delete_payment_title))
            .setMessage(getString(com.ngs.`775396439`.R.string.delete_payment_message, payment.notes))
            .setPositiveButton(getString(com.ngs.`775396439`.R.string.delete)) { _, _ ->
                viewModel.deletePayment(payment)
                showSnackbar(getString(com.ngs.`775396439`.R.string.payment_deleted))
            }
            .setNegativeButton(getString(com.ngs.`775396439`.R.string.cancel), null)
            .show()
    }

    private fun exportToPdf() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val payments = viewModel.payments.value
                if (payments.isNotEmpty()) {
                    ExportUtils.exportPaymentsToPdf(requireContext(), payments)
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
                val payments = viewModel.payments.value
                if (payments.isNotEmpty()) {
                    ExportUtils.exportPaymentsToExcel(requireContext(), payments)
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
                val payments = viewModel.payments.value
                if (payments.isNotEmpty()) {
                    ExportUtils.exportPaymentsToJson(requireContext(), payments)
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