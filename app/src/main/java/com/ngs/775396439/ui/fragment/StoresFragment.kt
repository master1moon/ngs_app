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
import com.ngs.`775396439`.data.entity.Store
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import com.ngs.`775396439`.databinding.FragmentStoresBinding
import com.ngs.`775396439`.ui.adapter.StoresAdapter
import com.ngs.`775396439`.ui.dialog.StoreDialogFragment
import com.ngs.`775396439`.ui.viewmodel.StoresViewModel
import com.ngs.`775396439`.utils.ExportUtils
import kotlinx.coroutines.launch

class StoresFragment : Fragment() {

    private var _binding: FragmentStoresBinding? = null
    private val binding get() = _binding!!

    private lateinit var storesAdapter: StoresAdapter
    private lateinit var repository: NetworkCardsRepository

    private val viewModel: StoresViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val database = AppDatabase.getDatabase(requireContext())
                val repository = NetworkCardsRepository(database)
                return StoresViewModel(repository) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoresBinding.inflate(inflater, container, false)
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
        storesAdapter = StoresAdapter(
            onViewDetailsClick = { store -> showStoreDetails(store) },
            onEditClick = { store -> showEditDialog(store) },
            onDeleteClick = { store -> showDeleteDialog(store) }
        )

        storesAdapter.setViewModel(viewModel)

        binding.recyclerViewStores.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = storesAdapter
        }
    }

    private fun setupObservers() {
        // Observe stores list
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.stores.collect { storesList ->
                storesAdapter.submitList(storesList)
                updateEmptyState(storesList.isEmpty())
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
        // Add store button
        binding.fabAddStore.setOnClickListener {
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
        binding.recyclerViewStores.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun showAddDialog() {
        StoreDialogFragment.newInstance(
            store = null,
            onSaveClick = { store ->
                viewModel.addStore(
                    name = store.name,
                    priceType = store.priceType
                )
            }
        ).show(childFragmentManager, "add_store_dialog")
    }

    private fun showEditDialog(store: Store) {
        StoreDialogFragment.newInstance(
            store = store,
            onSaveClick = { updatedStore ->
                viewModel.updateStore(
                    id = updatedStore.id,
                    name = updatedStore.name,
                    priceType = updatedStore.priceType
                )
            }
        ).show(childFragmentManager, "edit_store_dialog")
    }

    private fun showDeleteDialog(store: Store) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(com.ngs.`775396439`.R.string.delete_store_title))
            .setMessage(getString(com.ngs.`775396439`.R.string.delete_store_message, store.name))
            .setPositiveButton(getString(com.ngs.`775396439`.R.string.delete)) { _, _ ->
                viewModel.deleteStore(store)
                showSnackbar(getString(com.ngs.`775396439`.R.string.store_deleted))
            }
            .setNegativeButton(getString(com.ngs.`775396439`.R.string.cancel), null)
            .show()
    }

    private fun showStoreDetails(store: Store) {
        // TODO: Navigate to store details fragment
        showSnackbar("عرض تفاصيل المحل: ${store.name}")
    }

    private fun exportToPdf() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val stores = viewModel.stores.value
                if (stores.isNotEmpty()) {
                    ExportUtils.exportStoresToPdf(requireContext(), stores)
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
                val stores = viewModel.stores.value
                if (stores.isNotEmpty()) {
                    ExportUtils.exportStoresToExcel(requireContext(), stores)
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
                val stores = viewModel.stores.value
                if (stores.isNotEmpty()) {
                    ExportUtils.exportStoresToJson(requireContext(), stores)
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