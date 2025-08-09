package com.ngs.`775396439`.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.ngs.`775396439`.data.entity.Sale
import com.ngs.`775396439`.data.entity.Store
import com.ngs.`775396439`.data.entity.Package
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import com.ngs.`775396439`.databinding.DialogSaleBinding
import com.ngs.`775396439`.utils.NumberFormatTextWatcher

class SaleDialogFragment : DialogFragment() {

    private var _binding: DialogSaleBinding? = null
    private val binding get() = _binding!!
    
    private var sale: Sale? = null
    private var onSaveClick: ((Sale) -> Unit)? = null
    private var stores: List<Store> = emptyList()
    private var packages: List<Package> = emptyList()

    companion object {
        fun newInstance(
            sale: Sale? = null,
            stores: List<Store>,
            packages: List<Package>,
            onSaveClick: (Sale) -> Unit
        ): SaleDialogFragment {
            return SaleDialogFragment().apply {
                this.sale = sale
                this.stores = stores
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
        _binding = DialogSaleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews()
        setupSpinners()
        setupNumberFormatting()
        fillData()
    }

    private fun setupViews() {
        // Set dialog title
        binding.tvDialogTitle.text = if (sale == null) {
            getString(com.ngs.`775396439`.R.string.add_sale)
        } else {
            getString(com.ngs.`775396439`.R.string.edit_sale)
        }

        // Set current date
        binding.etSaleDate.setText(NetworkCardsRepository(null).getCurrentDate())

        // Setup save button
        binding.btnSave.setOnClickListener {
            saveSale()
        }

        // Setup cancel button
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun setupSpinners() {
        // Setup stores spinner
        val storeNames = stores.map { it.name }
        val storeAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            storeNames
        )
        storeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStore.adapter = storeAdapter

        // Setup packages spinner
        val packageNames = packages.map { it.name }
        val packageAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            packageNames
        )
        packageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPackage.adapter = packageAdapter
    }

    private fun setupNumberFormatting() {
        // Apply number formatting to amount fields
        binding.etSaleAmount.addTextChangedListener(NumberFormatTextWatcher())
        binding.etSalePricePerUnit.addTextChangedListener(NumberFormatTextWatcher())
        binding.etSaleQuantity.addTextChangedListener(NumberFormatTextWatcher())
    }

    private fun fillData() {
        sale?.let { s ->
            // Set store selection
            val storeIndex = stores.indexOfFirst { it.id == s.storeId }
            if (storeIndex >= 0) {
                binding.spinnerStore.setSelection(storeIndex)
            }

            // Set package selection
            val packageIndex = packages.indexOfFirst { it.id == s.packageId }
            if (packageIndex >= 0) {
                binding.spinnerPackage.setSelection(packageIndex)
            }

            binding.etSaleReason.setText(s.reason)
            binding.etSaleQuantity.setText(NumberFormatTextWatcher.formatNumber(s.quantity.toLong()))
            binding.etSaleAmount.setText(NumberFormatTextWatcher.formatNumber(s.amount.toLong()))
            binding.etSalePricePerUnit.setText(NumberFormatTextWatcher.formatNumber(s.pricePerUnit.toLong()))
            binding.etSaleDate.setText(s.date)
        }
    }

    private fun saveSale() {
        val storeId = getSelectedStoreId()
        val packageId = getSelectedPackageId()
        val reason = binding.etSaleReason.text.toString().trim()
        val quantityText = binding.etSaleQuantity.text.toString()
        val amountText = binding.etSaleAmount.text.toString()
        val pricePerUnitText = binding.etSalePricePerUnit.text.toString()
        val date = binding.etSaleDate.text.toString()
        
        if (storeId.isEmpty()) {
            binding.spinnerStore.error = getString(com.ngs.`775396439`.R.string.store_required)
            return
        }

        if (packageId.isEmpty()) {
            binding.spinnerPackage.error = getString(com.ngs.`775396439`.R.string.package_required)
            return
        }

        if (reason.isEmpty()) {
            binding.etSaleReason.error = getString(com.ngs.`775396439`.R.string.sale_reason_required)
            return
        }

        if (quantityText.isEmpty()) {
            binding.etSaleQuantity.error = getString(com.ngs.`775396439`.R.string.quantity_required)
            return
        }

        if (amountText.isEmpty()) {
            binding.etSaleAmount.error = getString(com.ngs.`775396439`.R.string.amount_required)
            return
        }

        if (pricePerUnitText.isEmpty()) {
            binding.etSalePricePerUnit.error = getString(com.ngs.`775396439`.R.string.price_per_unit_required)
            return
        }

        val quantity = try {
            NumberFormatTextWatcher.removeFormatting(quantityText).toInt()
        } catch (e: NumberFormatException) {
            binding.etSaleQuantity.error = getString(com.ngs.`775396439`.R.string.invalid_quantity)
            return
        }

        val amount = try {
            NumberFormatTextWatcher.removeFormatting(amountText).toDouble()
        } catch (e: NumberFormatException) {
            binding.etSaleAmount.error = getString(com.ngs.`775396439`.R.string.invalid_amount)
            return
        }

        val pricePerUnit = try {
            NumberFormatTextWatcher.removeFormatting(pricePerUnitText).toDouble()
        } catch (e: NumberFormatException) {
            binding.etSalePricePerUnit.error = getString(com.ngs.`775396439`.R.string.invalid_price)
            return
        }

        if (quantity <= 0) {
            binding.etSaleQuantity.error = getString(com.ngs.`775396439`.R.string.quantity_must_be_positive)
            return
        }

        if (amount <= 0) {
            binding.etSaleAmount.error = getString(com.ngs.`775396439`.R.string.amount_must_be_positive)
            return
        }

        if (pricePerUnit <= 0) {
            binding.etSalePricePerUnit.error = getString(com.ngs.`775396439`.R.string.price_must_be_positive)
            return
        }

        val total = quantity * pricePerUnit

        val newSale = Sale(
            id = sale?.id ?: NetworkCardsRepository(null).generateId(),
            storeId = storeId,
            packageId = packageId,
            reason = reason,
            quantity = quantity,
            amount = amount,
            pricePerUnit = pricePerUnit,
            total = total,
            date = date
        )

        onSaveClick?.invoke(newSale)
        dismiss()
    }

    private fun getSelectedStoreId(): String {
        val selectedPosition = binding.spinnerStore.selectedItemPosition
        return if (selectedPosition >= 0 && selectedPosition < stores.size) {
            stores[selectedPosition].id
        } else {
            ""
        }
    }

    private fun getSelectedPackageId(): String {
        val selectedPosition = binding.spinnerPackage.selectedItemPosition
        return if (selectedPosition >= 0 && selectedPosition < packages.size) {
            packages[selectedPosition].id
        } else {
            ""
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}