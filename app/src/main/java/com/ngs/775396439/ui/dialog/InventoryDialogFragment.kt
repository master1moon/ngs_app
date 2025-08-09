package com.ngs.`775396439`.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.ngs.`775396439`.data.entity.Inventory
import com.ngs.`775396439`.data.entity.Package
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import com.ngs.`775396439`.databinding.DialogInventoryBinding
import com.ngs.`775396439`.utils.EditTextUtils

class InventoryDialogFragment : DialogFragment() {

    private var _binding: DialogInventoryBinding? = null
    private val binding get() = _binding!!
    
    private var inventory: Inventory? = null
    private var packages: List<Package> = emptyList()
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
        setupNumberFormatting()
        setupPackageSpinner()
        fillData()
    }

    private fun setupViews() {
        // Set dialog title
        binding.tvDialogTitle.text = if (inventory == null) {
            getString(com.ngs.`775396439`.R.string.add_inventory)
        } else {
            getString(com.ngs.`775396439`.R.string.edit_inventory)
        }

        // Set current date
        binding.etInventoryDate.setText(NetworkCardsRepository(null).getCurrentDate())

        // Setup save button
        binding.btnSave.setOnClickListener {
            saveInventory()
        }

        // Setup cancel button
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun setupNumberFormatting() {
        // Apply number formatting to quantity field
        EditTextUtils.applyNumberFormatting(binding.etInventoryQuantity)
    }

    private fun setupPackageSpinner() {
        // Create package names list
        val packageNames = packages.map { it.name }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            packageNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPackage.adapter = adapter
    }

    private fun fillData() {
        inventory?.let { inv ->
            // Find the package index
            val packageIndex = packages.indexOfFirst { it.id == inv.packageId }
            if (packageIndex >= 0) {
                binding.spinnerPackage.setSelection(packageIndex)
            }

            // Fill quantity with formatted number
            EditTextUtils.setNumericValue(binding.etInventoryQuantity, inv.quantity.toLong())
            binding.etInventoryDate.setText(inv.createdAt)
        }
    }

    private fun saveInventory() {
        val selectedPosition = binding.spinnerPackage.selectedItemPosition
        if (selectedPosition < 0 || selectedPosition >= packages.size) {
            binding.spinnerPackage.error = getString(com.ngs.`775396439`.R.string.select_package_required)
            return
        }

        val quantityText = binding.etInventoryQuantity.text.toString()
        if (quantityText.isEmpty()) {
            binding.etInventoryQuantity.error = getString(com.ngs.`775396439`.R.string.quantity_required)
            return
        }

        val quantity = try {
            EditTextUtils.getNumericValue(binding.etInventoryQuantity).toInt()
        } catch (e: NumberFormatException) {
            binding.etInventoryQuantity.error = getString(com.ngs.`775396439`.R.string.invalid_quantity)
            return
        }

        if (quantity <= 0) {
            binding.etInventoryQuantity.error = getString(com.ngs.`775396439`.R.string.quantity_must_be_positive)
            return
        }

        val selectedPackage = packages[selectedPosition]
        val date = binding.etInventoryDate.text.toString()

        val newInventory = Inventory(
            id = inventory?.id ?: NetworkCardsRepository(null).generateId(),
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