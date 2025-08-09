package com.ngs.cards775396439.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.ngs.cards775396439.data.entity.Inventory
import com.ngs.cards775396439.data.entity.Package
import com.ngs.cards775396439.databinding.DialogInventoryBinding
import com.ngs.cards775396439.utils.EditTextUtils

class InventoryDialogFragment : DialogFragment() {
    
    private var _binding: DialogInventoryBinding? = null
    private val binding get() = _binding!!
    
    private var inventory: Inventory? = null
    private var packages = listOf<Package>()
    private var onSaveClick: ((Inventory) -> Unit)? = null
    
    companion object {
        fun newInstance(
            inventory: Inventory? = null,
            packages: List<Package>,
            onSaveClick: (Inventory) -> Unit
        ): InventoryDialogFragment {
            return InventoryDialogFragment().apply {
                this.inventory = inventory
                this.packages = packages
                this.onSaveClick = onSaveClick
            }
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews()
        setupClickListeners()
        loadInventoryData()
    }
    
    private fun setupViews() {
        // Apply number formatting to quantity field
        EditTextUtils.applyNumberFormatting(binding.etQuantity)
        
        // Setup packages spinner
        val packageNames = packages.map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, packageNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPackage.adapter = adapter
    }
    
    private fun setupClickListeners() {
        binding.btnSave.setOnClickListener {
            saveInventory()
        }
        
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }
    
    private fun loadInventoryData() {
        inventory?.let { inv ->
            binding.tvTitle.text = "تعديل الكمية"
            
            // Set selected package
            val selectedPackageIndex = packages.indexOfFirst { it.id == inv.packageId }
            if (selectedPackageIndex >= 0) {
                binding.spinnerPackage.setSelection(selectedPackageIndex)
            }
            
            // Set quantity
            binding.etQuantity.setText(inv.quantity.toString())
            
            // Set date
            binding.etInventoryDate.setText(inv.createdAt)
        } ?: run {
            binding.tvTitle.text = "إضافة كمية جديدة"
            
            // Set today's date
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(java.util.Date())
            binding.etInventoryDate.setText(today)
        }
    }
    
    private fun saveInventory() {
        val selectedPosition = binding.spinnerPackage.selectedItemPosition
        if (selectedPosition < 0 || selectedPosition >= packages.size) {
            // Show error message
            return
        }
        
        val quantityText = EditTextUtils.getNumericValue(binding.etQuantity)
        if (quantityText.isEmpty()) {
            // Show error message
            return
        }
        
        val quantity = quantityText.toIntOrNull()
        if (quantity == null || quantity <= 0) {
            // Show error message
            return
        }
        
        val selectedPackage = packages[selectedPosition]
        val date = binding.etInventoryDate.text.toString()
        
        val newInventory = inventory?.copy(
            packageId = selectedPackage.id,
            quantity = quantity,
            createdAt = date
        ) ?: Inventory(
            id = "inv_${System.currentTimeMillis()}",
            packageId = selectedPackage.id,
            quantity = quantity,
            createdAt = date
        )
        
        onSaveClick?.invoke(newInventory)
        dismiss()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}