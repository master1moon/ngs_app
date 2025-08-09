package com.ngs.cards775396439.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ngs.cards775396439.data.entity.Expense
import com.ngs.cards775396439.databinding.ItemExpenseBinding
import com.ngs.cards775396439.utils.EditTextUtils

class ExpensesAdapter(
    private val onEditClick: (Expense) -> Unit,
    private val onDeleteClick: (Expense) -> Unit
) : RecyclerView.Adapter<ExpensesAdapter.ExpenseViewHolder>() {
    
    private var expensesList = listOf<Expense>()
    
    fun updateExpenses(newExpenses: List<Expense>) {
        expensesList = newExpenses
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExpenseViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(expensesList[position])
    }
    
    override fun getItemCount(): Int = expensesList.size
    
    inner class ExpenseViewHolder(
        private val binding: ItemExpenseBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(expense: Expense) {
            binding.apply {
                tvExpenseType.text = expense.type
                tvAmount.text = "${EditTextUtils.formatNumber(expense.amount.toLong())} د.ك"
                tvDate.text = expense.date
                
                // Show notes if available
                if (expense.notes.isNotEmpty()) {
                    tvNotes.text = expense.notes
                    tvNotes.visibility = android.view.View.VISIBLE
                } else {
                    tvNotes.visibility = android.view.View.GONE
                }
                
                // Show add later indicator
                if (expense.addLater) {
                    chipAddLater.visibility = android.view.View.VISIBLE
                } else {
                    chipAddLater.visibility = android.view.View.GONE
                }
                
                // Set click listeners
                btnEdit.setOnClickListener { onEditClick(expense) }
                btnDelete.setOnClickListener { onDeleteClick(expense) }
            }
        }
    }
}