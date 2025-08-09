package com.ngs.`775396439`.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ngs.`775396439`.data.entity.Package
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import com.ngs.`775396439`.databinding.DialogPackageBinding

class PackageDialogFragment : DialogFragment() {
    
    private var _binding: DialogPackageBinding? = null
    private val binding get() = _binding!!
    
    private var package_: Package? = null
    private var onSaveCallback: ((Package) -> Unit)? = null
    
    private val repository by lazy {
        NetworkCardsRepository(null) // سيتم حقنه لاحقاً
    }
    
    companion object {
        private const val ARG_PACKAGE = "package"
        
        fun newInstance(package_: Package? = null): PackageDialogFragment {
            return PackageDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PACKAGE, package_)
                }
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
        
        package_ = arguments?.getParcelable(ARG_PACKAGE)
        
        setupViews()
        setupListeners()
        
        if (package_ != null) {
            fillData(package_!!)
            binding.dialogTitle.text = getString(com.ngs.`775396439`.R.string.edit_package)
        } else {
            binding.dialogTitle.text = getString(com.ngs.`775396439`.R.string.add_package)
            binding.packageDate.setText(repository.getCurrentDate())
        }
    }
    
    private fun setupViews() {
        // إعداد حقول الإدخال
        binding.packageName.hint = getString(com.ngs.`775396439`.R.string.package_name_hint)
        binding.retailPrice.hint = getString(com.ngs.`775396439`.R.string.price_hint)
        binding.wholesalePrice.hint = getString(com.ngs.`775396439`.R.string.price_hint)
        binding.distributorPrice.hint = getString(com.ngs.`775396439`.R.string.price_hint)
    }
    
    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            savePackage()
        }
        
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }
    
    private fun fillData(package_: Package) {
        binding.packageName.setText(package_.name)
        binding.retailPrice.setText(formatPriceForInput(package_.retailPrice))
        binding.wholesalePrice.setText(formatPriceForInput(package_.wholesalePrice))
        binding.distributorPrice.setText(formatPriceForInput(package_.distributorPrice))
        binding.packageDate.setText(package_.createdAt)
    }
    
    private fun savePackage() {
        val name = binding.packageName.text.toString().trim()
        val retailPrice = parsePrice(binding.retailPrice.text.toString())
        val wholesalePrice = parsePrice(binding.wholesalePrice.text.toString())
        val distributorPrice = parsePrice(binding.distributorPrice.text.toString())
        val date = binding.packageDate.text.toString()
        
        if (name.isEmpty()) {
            binding.packageName.error = "يرجى إدخال اسم الباقة"
            return
        }
        
        val newPackage = if (package_ != null) {
            package_!!.copy(
                name = name,
                retailPrice = retailPrice,
                wholesalePrice = wholesalePrice,
                distributorPrice = distributorPrice,
                createdAt = date
            )
        } else {
            Package(
                id = repository.generateId(),
                name = name,
                retailPrice = retailPrice,
                wholesalePrice = wholesalePrice,
                distributorPrice = distributorPrice,
                createdAt = date,
                image = ""
            )
        }
        
        onSaveCallback?.invoke(newPackage)
        dismiss()
    }
    
    private fun parsePrice(text: String): Double? {
        return try {
            val cleanText = text.replace(",", "").trim()
            if (cleanText.isEmpty()) null else cleanText.toDouble()
        } catch (e: NumberFormatException) {
            null
        }
    }
    
    private fun formatPriceForInput(price: Double?): String {
        return if (price != null && price > 0) {
            repository.formatNumber(price)
        } else {
            ""
        }
    }
    
    fun setOnSaveCallback(callback: (Package) -> Unit) {
        onSaveCallback = callback
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}