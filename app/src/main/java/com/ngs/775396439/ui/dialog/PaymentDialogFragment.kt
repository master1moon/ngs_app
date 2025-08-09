package com.ngs.`775396439`.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.ngs.`775396439`.data.entity.Payment
import com.ngs.`775396439`.data.entity.Store
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import com.ngs.`775396439`.databinding.DialogPaymentBinding
import com.ngs.`775396439`.utils.NumberFormatTextWatcher

class PaymentDialogFragment : DialogFragment() {

    private var _binding: DialogPaymentBinding? = null
    private val binding get() = _binding!!
    
    private var payment: Payment? = null
    private var onSaveClick: ((Payment) -> Unit)? = null
    private var stores: List<Store> = emptyList()

    companion object {
        fun newInstance(
            payment: Payment? = null,
            stores: List<Store>,
            onSaveClick: (Payment) -> Unit
        ): PaymentDialogFragment {
            return PaymentDialogFragment().apply {
                this.payment = payment
                this.stores = stores
                this.onSaveClick = onSaveClick
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews()
        setupSpinner()
        setupNumberFormatting()
        fillData()
    }

    private fun setupViews() {
        // Set dialog title
        binding.tvDialogTitle.text = if (payment == null) {
            getString(com.ngs.`775396439`.R.string.add_payment)
        } else {
            getString(com.ngs.`775396439`.R.string.edit_payment)
        }

        // Set current date
        binding.etPaymentDate.setText(NetworkCardsRepository(null).getCurrentDate())

        // Setup save button
        binding.btnSave.setOnClickListener {
            savePayment()
        }

        // Setup cancel button
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun setupSpinner() {
        // Setup stores spinner
        val storeNames = stores.map { it.name }
        val storeAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            storeNames
        )
        storeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStore.adapter = storeAdapter
    }

    private fun setupNumberFormatting() {
        // Apply number formatting to amount field
        binding.etPaymentAmount.addTextChangedListener(NumberFormatTextWatcher())
    }

    private fun fillData() {
        payment?.let { p ->
            // Set store selection
            val storeIndex = stores.indexOfFirst { it.id == p.storeId }
            if (storeIndex >= 0) {
                binding.spinnerStore.setSelection(storeIndex)
            }

            binding.etPaymentAmount.setText(NumberFormatTextWatcher.formatNumber(p.amount.toLong()))
            binding.etPaymentNotes.setText(p.notes)
            binding.etPaymentDate.setText(p.date)
        }
    }

    private fun savePayment() {
        val storeId = getSelectedStoreId()
        val amountText = binding.etPaymentAmount.text.toString()
        val notes = binding.etPaymentNotes.text.toString().trim()
        val date = binding.etPaymentDate.text.toString()
        
        if (storeId.isEmpty()) {
            binding.spinnerStore.error = getString(com.ngs.`775396439`.R.string.store_required)
            return
        }

        if (amountText.isEmpty()) {
            binding.etPaymentAmount.error = getString(com.ngs.`775396439`.R.string.amount_required)
            return
        }

        if (notes.isEmpty()) {
            binding.etPaymentNotes.error = getString(com.ngs.`775396439`.R.string.payment_notes_required)
            return
        }

        val amount = try {
            NumberFormatTextWatcher.removeFormatting(amountText).toDouble()
        } catch (e: NumberFormatException) {
            binding.etPaymentAmount.error = getString(com.ngs.`775396439`.R.string.invalid_amount)
            return
        }

        if (amount <= 0) {
            binding.etPaymentAmount.error = getString(com.ngs.`775396439`.R.string.amount_must_be_positive)
            return
        }

        val newPayment = Payment(
            id = payment?.id ?: NetworkCardsRepository(null).generateId(),
            storeId = storeId,
            amount = amount,
            notes = notes,
            date = date
        )

        onSaveClick?.invoke(newPayment)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}