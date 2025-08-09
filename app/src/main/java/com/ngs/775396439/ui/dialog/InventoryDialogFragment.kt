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

class InventoryDialogFragment : DialogFragment() {

    private var _binding: DialogInventoryBinding? = null
    private val binding get() = _binding!!

    private var inventory: Inventory? = null
    private var packages: List<Package> = emptyList()
    private var onSaveCallback: ((Inventory) -> Unit)? = null

    // Note: This repository initialization is a placeholder.
    // In a real app, it should be injected via a DI framework or passed through constructor.
    private val repository by lazy {
        NetworkCardsRepository(null)
    }

    companion object {
        private const val ARG_INVENTORY = "inventory"
        private const val ARG_PACKAGES = "packages"

        fun newInstance(inventory: Inventory? = null, packages: List<Package> = emptyList()): InventoryDialogFragment {
            return InventoryDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_INVENTORY, inventory)
                    putParcelableArrayList(ARG_PACKAGES, ArrayList(packages))
                }
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

        inventory = arguments?.getParcelable(ARG_INVENTORY)
        packages = arguments?.getParcelableArrayList(ARG_PACKAGES) ?: emptyList()

        setupViews()
        setupListeners()
        setupPackagesSpinner()

        if (inventory != null) {
            fillData(inventory!!)
            binding.dialogTitle.text = getString(com.ngs.`775396439`.R.string.edit_inventory)
        } else {
            binding.dialogTitle.text = getString(com.ngs.`775396439`.R.string.add_inventory)
            binding.inventoryDate.setText(repository.getCurrentDate())
        }
    }

    private fun setupViews() {
        // إعداد حقول الإدخال
        binding.inventoryQuantity.hint = getString(com.ngs.`775396439`.R.string.quantity_hint)
        binding.inventoryDate.hint = getString(com.ngs.`775396439`.R.string.date_hint)
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            saveInventory()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun setupPackagesSpinner() {
        val packageNames = packages.map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, packageNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.inventoryPackage.adapter = adapter
    }

    private fun fillData(inventory: Inventory) {
        // تحديد الباقة المحددة
        val packageIndex = packages.indexOfFirst { it.id == inventory.packageId }
        if (packageIndex >= 0) {
            binding.inventoryPackage.setSelection(packageIndex)
        }

        binding.inventoryQuantity.setText(inventory.quantity.toString())
        binding.inventoryDate.setText(inventory.createdAt)
    }

    private fun saveInventory() {
        val selectedPackageIndex = binding.inventoryPackage.selectedItemPosition
        val quantityText = binding.inventoryQuantity.text.toString().trim()
        val date = binding.inventoryDate.text.toString().trim()

        if (selectedPackageIndex < 0 || selectedPackageIndex >= packages.size) {
            binding.inventoryPackage.error = "يرجى اختيار الباقة"
            return
        }

        if (quantityText.isEmpty()) {
            binding.inventoryQuantity.error = "يرجى إدخال الكمية"
            return
        }

        val quantity = try {
            quantityText.toInt()
        } catch (e: NumberFormatException) {
            binding.inventoryQuantity.error = "يرجى إدخال كمية صحيحة"
            return
        }

        if (quantity <= 0) {
            binding.inventoryQuantity.error = "يجب أن تكون الكمية أكبر من صفر"
            return
        }

        val selectedPackage = packages[selectedPackageIndex]
        val newInventory = if (inventory != null) {
            inventory!!.copy(
                packageId = selectedPackage.id,
                quantity = quantity,
                createdAt = date
            )
        } else {
            Inventory(
                id = repository.generateId(),
                packageId = selectedPackage.id,
                quantity = quantity,
                createdAt = date.ifBlank { repository.getCurrentDate() }
            )
        }

        onSaveCallback?.invoke(newInventory)
        dismiss()
    }

    fun setOnSaveCallback(callback: (Inventory) -> Unit) {
        onSaveCallback = callback
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}