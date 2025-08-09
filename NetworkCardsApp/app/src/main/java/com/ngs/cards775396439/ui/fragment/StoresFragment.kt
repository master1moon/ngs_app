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
import com.ngs.cards775396439.data.entity.Store
import com.ngs.cards775396439.databinding.FragmentStoresBinding
import com.ngs.cards775396439.ui.adapter.StoresAdapter
import com.ngs.cards775396439.ui.dialog.StoresDialogFragment
import com.ngs.cards775396439.ui.viewmodel.StoresViewModel
import kotlinx.coroutines.launch

class StoresFragment : Fragment() {
    
    private var _binding: FragmentStoresBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: StoresViewModel
    private lateinit var adapter: StoresAdapter
    
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
        
        setupViewModel()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[StoresViewModel::class.java]
    }
    
    private fun setupRecyclerView() {
        adapter = StoresAdapter(
            onEditClick = { store -> showEditDialog(store) },
            onDeleteClick = { store -> showDeleteDialog(store) },
            onStoreClick = { store -> showStoreDetails(store) }
        )
        
        binding.rvStores.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@StoresFragment.adapter
        }
    }
    
    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.stores.collect { stores ->
                adapter.updateStores(stores)
                updateEmptyState(stores.isEmpty())
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
        binding.fabAddStore.setOnClickListener {
            showAddDialog()
        }
    }
    
    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvStores.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
    
    private fun showAddDialog() {
        StoresDialogFragment.newInstance(
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
        StoresDialogFragment.newInstance(
            store = store,
            onSaveClick = { updatedStore ->
                viewModel.updateStore(updatedStore)
            }
        ).show(childFragmentManager, "edit_store_dialog")
    }
    
    private fun showDeleteDialog(store: Store) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("حذف المحل")
            .setMessage("هل أنت متأكد من حذف المحل \"${store.name}\"؟")
            .setPositiveButton("حذف") { _, _ ->
                viewModel.deleteStore(store)
            }
            .setNegativeButton("إلغاء", null)
            .show()
    }
    
    private fun showStoreDetails(store: Store) {
        // TODO: Implement store details view
        // This would show sales, payments, and balance for the store
        showErrorDialog("تفاصيل المحل: ${store.name}\nسيتم إضافة التفاصيل الكاملة في المرحلة التالية")
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