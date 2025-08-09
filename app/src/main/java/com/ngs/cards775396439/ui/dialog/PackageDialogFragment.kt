package com.ngs.cards775396439.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ngs.cards775396439.data.entity.Package
import com.ngs.cards775396439.databinding.DialogPackageBinding
import com.ngs.cards775396439.utils.EditTextUtils

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
        setupClickListeners()
        loadPackageData()
    }
    
    private fun setupViews() {
        // Apply number formatting to price fields
        EditTextUtils.applyNumberFormatting(binding.etRetailPrice)
        EditTextUtils.applyNumberFormatting(binding.etWholesalePrice)
        EditTextUtils.applyNumberFormatting(binding.etDistributorPrice)
    }
    
    private fun setupClickListeners() {
        binding.btnSave.setOnClickListener {
            savePackage()
        }
        
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }
    
    private fun loadPackageData() {
        package_?.let { pkg ->
            binding.tvTitle.text = "تعديل الباقة"
            binding.etPackageName.setText(pkg.name)
            binding.etRetailPrice.setText(pkg.retailPrice?.toString() ?: "")
            binding.etWholesalePrice.setText(pkg.wholesalePrice?.toString() ?: "")
            binding.etDistributorPrice.setText(pkg.distributorPrice?.toString() ?: "")
            binding.etPackageDate.setText(pkg.createdAt)
        } ?: run {
            binding.tvTitle.text = "إضافة باقة جديدة"
            // Set today's date
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(java.util.Date())
            binding.etPackageDate.setText(today)
        }
    }
    
    private fun savePackage() {
        val name = binding.etPackageName.text.toString().trim()
        if (name.isEmpty()) {
            binding.etPackageName.error = "يرجى إدخال اسم الباقة"
            return
        }
        
        val retailPrice = EditTextUtils.getNumericValue(binding.etRetailPrice).toDoubleOrNull()
        val wholesalePrice = EditTextUtils.getNumericValue(binding.etWholesalePrice).toDoubleOrNull()
        val distributorPrice = EditTextUtils.getNumericValue(binding.etDistributorPrice).toDoubleOrNull()
        val date = binding.etPackageDate.text.toString()
        
        val newPackage = package_?.copy(
            name = name,
            retailPrice = retailPrice,
            wholesalePrice = wholesalePrice,
            distributorPrice = distributorPrice,
            createdAt = date
        ) ?: Package(
            id = "pkg_${System.currentTimeMillis()}",
            name = name,
            retailPrice = retailPrice,
            wholesalePrice = wholesalePrice,
            distributorPrice = distributorPrice,
            createdAt = date
        )
        
        onSaveClick?.invoke(newPackage)
        dismiss()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}