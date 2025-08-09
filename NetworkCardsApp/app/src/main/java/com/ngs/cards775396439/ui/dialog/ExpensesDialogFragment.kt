package com.ngs.cards775396439.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.ngs.cards775396439.data.entity.Expense
import com.ngs.cards775396439.databinding.DialogExpenseBinding
import com.ngs.cards775396439.utils.EditTextUtils

class ExpensesDialogFragment : DialogFragment() {
    
    private var _binding: DialogExpenseBinding? = null
    private val binding get() = _binding!!
    
    private var expense: Expense? = null
    private var onSaveClick: ((Expense) -> Unit)? = null
    
    companion object {
        fun newInstance(
            expense: Expense? = null,
            onSaveClick: (Expense) -> Unit
        ): ExpensesDialogFragment {
            return ExpensesDialogFragment().apply {
                this.expense = expense
                this.onSaveClick = onSaveClick
            }
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews()
        setupClickListeners()
        loadExpenseData()
    }
    
    private fun setupViews() {
        // Apply number formatting to amount field
        EditTextUtils.applyNumberFormatting(binding.etAmount)
        
        // Setup expense types
        val expenseTypes = arrayOf(
            "كهرباء",
            "انترنت ADSL",
            "انترنت فايبر",
            "انترنت ستار لينك",
            "صيانة",
            "سويتش",
            "كيبل كهرباء",
            "كيبل انترنت رئيسي",
            "كيبل انترنت منزلي",
            "مواصلات",
            "عامل",
            "ايجار سطوح",
            "أخرى"
        )
        
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, expenseTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerExpenseType.adapter = adapter
    }
    
    private fun setupClickListeners() {
        binding.btnSave.setOnClickListener {
            saveExpense()
        }
        
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }
    
    private fun loadExpenseData() {
        expense?.let { exp ->
            binding.tvTitle.text = "تعديل المصروف"
            
            // Set expense type
            val expenseTypeIndex = getExpenseTypeIndex(exp.type)
            if (expenseTypeIndex >= 0) {
                binding.spinnerExpenseType.setSelection(expenseTypeIndex)
            }
            
            // Set amount
            binding.etAmount.setText(exp.amount.toString())
            
            // Set notes
            binding.etNotes.setText(exp.notes)
            
            // Set date
            binding.etExpenseDate.setText(exp.date)
            
            // Set add later checkbox
            binding.cbAddLater.isChecked = exp.addLater
        } ?: run {
            binding.tvTitle.text = "إضافة مصروف جديد"
            
            // Set today's date
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(java.util.Date())
            binding.etExpenseDate.setText(today)
        }
    }
    
    private fun getExpenseTypeIndex(type: String): Int {
        val types = arrayOf(
            "كهرباء",
            "انترنت ADSL",
            "انترنت فايبر",
            "انترنت ستار لينك",
            "صيانة",
            "سويتش",
            "كيبل كهرباء",
            "كيبل انترنت رئيسي",
            "كيبل انترنت منزلي",
            "مواصلات",
            "عامل",
            "ايجار سطوح",
            "أخرى"
        )
        return types.indexOf(type)
    }
    
    private fun saveExpense() {
        val selectedPosition = binding.spinnerExpenseType.selectedItemPosition
        if (selectedPosition < 0) {
            // Show error message
            return
        }
        
        val amountText = EditTextUtils.getNumericValue(binding.etAmount)
        if (amountText.isEmpty()) {
            binding.etAmount.error = "يرجى إدخال المبلغ"
            return
        }
        
        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            binding.etAmount.error = "يرجى إدخال مبلغ صحيح"
            return
        }
        
        val expenseTypes = arrayOf(
            "كهرباء",
            "انترنت ADSL",
            "انترنت فايبر",
            "انترنت ستار لينك",
            "صيانة",
            "سويتش",
            "كيبل كهرباء",
            "كيبل انترنت رئيسي",
            "كيبل انترنت منزلي",
            "مواصلات",
            "عامل",
            "ايجار سطوح",
            "أخرى"
        )
        
        val type = expenseTypes[selectedPosition]
        val notes = binding.etNotes.text.toString().trim()
        val date = binding.etExpenseDate.text.toString()
        val addLater = binding.cbAddLater.isChecked
        
        val newExpense = expense?.copy(
            type = type,
            amount = amount,
            notes = notes,
            date = date,
            addLater = addLater
        ) ?: Expense(
            id = "exp_${System.currentTimeMillis()}",
            type = type,
            amount = amount,
            notes = notes,
            date = date,
            addLater = addLater
        )
        
        onSaveClick?.invoke(newExpense)
        dismiss()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}