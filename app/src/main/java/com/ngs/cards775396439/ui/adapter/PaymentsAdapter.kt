package com.ngs.cards775396439.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ngs.cards775396439.data.entity.Payment
import com.ngs.cards775396439.data.entity.Store
import com.ngs.cards775396439.databinding.ItemPaymentBinding
import com.ngs.cards775396439.utils.EditTextUtils

class PaymentsAdapter(
    private val onEditClick: (Payment) -> Unit,
    private val onDeleteClick: (Payment) -> Unit
) : RecyclerView.Adapter<PaymentsAdapter.PaymentViewHolder>() {
    
    private var paymentsList = listOf<Payment>()
    private var storesList = listOf<Store>()
    
    fun updatePayments(newPayments: List<Payment>, stores: List<Store>) {
        paymentsList = newPayments
        storesList = stores
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val binding = ItemPaymentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PaymentViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.bind(paymentsList[position])
    }
    
    override fun getItemCount(): Int = paymentsList.size
    
    inner class PaymentViewHolder(
        private val binding: ItemPaymentBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(payment: Payment) {
            val store = storesList.find { it.id == payment.storeId }
            
            binding.apply {
                tvStoreName.text = store?.name ?: "محل غير معروف"
                tvAmount.text = "${EditTextUtils.formatNumber(payment.amount.toLong())} د.ك"
                tvDate.text = payment.date
                
                // Show notes if available
                if (payment.notes.isNotEmpty()) {
                    tvNotes.text = payment.notes
                    tvNotes.visibility = android.view.View.VISIBLE
                } else {
                    tvNotes.visibility = android.view.View.GONE
                }
                
                // Set click listeners
                btnEdit.setOnClickListener { onEditClick(payment) }
                btnDelete.setOnClickListener { onDeleteClick(payment) }
            }
        }
    }
}