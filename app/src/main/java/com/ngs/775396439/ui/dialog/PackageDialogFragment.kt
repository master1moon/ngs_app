package com.ngs.`775396439`.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ngs.`775396439`.data.entity.Package
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import com.ngs.`775396439`.databinding.DialogPackageBinding
import com.ngs.`775396439`.utils.EditTextUtils

class PackageDialogFragment : DialogFragment() {

    private var _binding: DialogPackageBinding? = null
    private val binding get() = _binding!!
    
    private var package_: Package? = null
    private var onSaveClick: ((Package) -> Unit)? = null

    companion object {
        fun newInstance(package_: Package? = null, onSaveClick: (Package) -> Unit): PackageDialogFragment {
            return PackageDialogFragment().apply {
                this.package_ = package_
                this.onSaveClick = onSaveClick
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogPackageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews()
        setupNumberFormatting()
        fillData()
    }

    private fun setupViews() {
        // Set dialog title
        binding.tvDialogTitle.text = if (package_ == null) {
            getString(com.ngs.`775396439`.R.string.add_package)
        } else {
            getString(com.ngs.`775396439`.R.string.edit_package)
        }

        // Set current date
        binding.etPackageDate.setText(NetworkCardsRepository(null).getCurrentDate())

        // Setup save button
        binding.btnSave.setOnClickListener {
            savePackage()
        }

        // Setup cancel button
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun setupNumberFormatting() {
        // Apply number formatting to price fields
        EditTextUtils.applyNumberFormatting(binding.etRetailPrice)
        EditTextUtils.applyNumberFormatting(binding.etWholesalePrice)
        EditTextUtils.applyNumberFormatting(binding.etDistributorPrice)
    }

    private fun fillData() {
        package_?.let { pkg ->
            binding.etPackageName.setText(pkg.name)
            binding.etPackageDate.setText(pkg.createdAt)
            
            // Fill price fields with formatted numbers
            if (pkg.retailPrice != null && pkg.retailPrice > 0) {
                EditTextUtils.setNumericValue(binding.etRetailPrice, pkg.retailPrice.toLong())
            }
            if (pkg.wholesalePrice != null && pkg.wholesalePrice > 0) {
                EditTextUtils.setNumericValue(binding.etWholesalePrice, pkg.wholesalePrice.toLong())
            }
            if (pkg.distributorPrice != null && pkg.distributorPrice > 0) {
                EditTextUtils.setNumericValue(binding.etDistributorPrice, pkg.distributorPrice.toLong())
            }
        }
    }

    private fun savePackage() {
        val name = binding.etPackageName.text.toString().trim()
        val date = binding.etPackageDate.text.toString()
        
        if (name.isEmpty()) {
            binding.etPackageName.error = getString(com.ngs.`775396439`.R.string.package_name_required)
            return
        }

        // Parse prices
        val retailPrice = parsePrice(binding.etRetailPrice.text.toString())
        val wholesalePrice = parsePrice(binding.etWholesalePrice.text.toString())
        val distributorPrice = parsePrice(binding.etDistributorPrice.text.toString())

        val newPackage = Package(
            id = package_?.id ?: NetworkCardsRepository(null).generateId(),
            name = name,
            retailPrice = retailPrice,
            wholesalePrice = wholesalePrice,
            distributorPrice = distributorPrice,
            createdAt = date,
            image = package_?.image ?: ""
        )

        onSaveClick?.invoke(newPackage)
        dismiss()
    }

    private fun parsePrice(priceText: String): Double? {
        return if (priceText.isNotEmpty()) {
            try {
                val numericValue = EditTextUtils.getNumericValue(binding.etRetailPrice)
                numericValue.toDouble()
            } catch (e: NumberFormatException) {
                null
            }
        } else {
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}