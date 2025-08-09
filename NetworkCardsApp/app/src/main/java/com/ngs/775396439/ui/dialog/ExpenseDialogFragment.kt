package com.ngs.`775396439`.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ngs.`775396439`.data.entity.Expense
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import com.ngs.`775396439`.databinding.DialogExpenseBinding
import com.ngs.`775396439`.utils.NumberFormatTextWatcher

class ExpenseDialogFragment : DialogFragment() {

    private var _binding: DialogExpenseBinding? = null
    private val binding get() = _binding!!
    
    private var expense: Expense? = null
    private var onSaveClick: ((Expense) -> Unit)? = null

    companion object {
        fun newInstance(
            expense: Expense? = null,
            onSaveClick: (Expense) -> Unit
        ): ExpenseDialogFragment {
            return ExpenseDialogFragment().apply {
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
        setupNumberFormatting()
        fillData()
    }

    private fun setupViews() {
        // Set dialog title
        binding.tvDialogTitle.text = if (expense == null) {
            getString(com.ngs.`775396439`.R.string.add_expense)
        } else {
            getString(com.ngs.`775396439`.R.string.edit_expense)
        }

        // Set current date
        binding.etExpenseDate.setText(NetworkCardsRepository(null).getCurrentDate())

        // Setup save button
        binding.btnSave.setOnClickListener {
            saveExpense()
        }

        // Setup cancel button
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun setupNumberFormatting() {
        // Apply number formatting to amount field
        binding.etExpenseAmount.addTextChangedListener(NumberFormatTextWatcher())
    }

    private fun fillData() {
        expense?.let { exp ->
            binding.etExpenseType.setText(exp.type)
            binding.etExpenseAmount.setText(NumberFormatTextWatcher.formatNumber(exp.amount.toLong()))
            binding.etExpenseNotes.setText(exp.notes)
            binding.etExpenseDate.setText(exp.date)
            binding.switchAddLater.isChecked = exp.addLater
        }
    }

    private fun saveExpense() {
        val type = binding.etExpenseType.text.toString().trim()
        val amountText = binding.etExpenseAmount.text.toString()
        val notes = binding.etExpenseNotes.text.toString().trim()
        val date = binding.etExpenseDate.text.toString()
        val addLater = binding.switchAddLater.isChecked
        
        if (type.isEmpty()) {
            binding.etExpenseType.error = getString(com.ngs.`775396439`.R.string.expense_type_required)
            return
        }

        if (amountText.isEmpty()) {
            binding.etExpenseAmount.error = getString(com.ngs.`775396439`.R.string.expense_amount_required)
            return
        }

        if (notes.isEmpty()) {
            binding.etExpenseNotes.error = getString(com.ngs.`775396439`.R.string.expense_notes_required)
            return
        }

        val amount = try {
            NumberFormatTextWatcher.removeFormatting(amountText).toDouble()
        } catch (e: NumberFormatException) {
            binding.etExpenseAmount.error = getString(com.ngs.`775396439`.R.string.invalid_amount)
            return
        }

        if (amount <= 0) {
            binding.etExpenseAmount.error = getString(com.ngs.`775396439`.R.string.amount_must_be_positive)
            return
        }

        val newExpense = Expense(
            id = expense?.id ?: NetworkCardsRepository(null).generateId(),
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