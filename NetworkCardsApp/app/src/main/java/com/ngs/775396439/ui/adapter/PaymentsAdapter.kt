package com.ngs.`775396439`.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ngs.`775396439`.data.entity.Payment
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import com.ngs.`775396439`.databinding.ItemPaymentBinding

class PaymentsAdapter(
    private val onEditClick: (Payment) -> Unit,
    private val onDeleteClick: (Payment) -> Unit
) : ListAdapter<Payment, PaymentsAdapter.PaymentViewHolder>(PaymentDiffCallback()) {

    private lateinit var viewModel: com.ngs.`775396439`.ui.viewmodel.PaymentsViewModel

    fun setViewModel(viewModel: com.ngs.`775396439`.ui.viewmodel.PaymentsViewModel) {
        this.viewModel = viewModel
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
        holder.bind(getItem(position))
    }

    inner class PaymentViewHolder(
        private val binding: ItemPaymentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(payment: Payment) {
            binding.apply {
                // Get store name
                val store = viewModel.getStoreById(payment.storeId)
                val storeName = store?.name ?: "محل غير محدد"
                
                tvStoreName.text = storeName
                tvPaymentNotes.text = payment.notes
                tvPaymentAmount.text = formatCurrency(payment.amount)
                tvPaymentDate.text = payment.date

                // Set click listeners
                btnEditPayment.setOnClickListener {
                    onEditClick(payment)
                }

                btnDeletePayment.setOnClickListener {
                    onDeleteClick(payment)
                }
            }
        }

        private fun formatCurrency(amount: Double): String {
            return NetworkCardsRepository(null).formatNumber(amount.toLong()) + " د.ك"
        }
    }

    private class PaymentDiffCallback : DiffUtil.ItemCallback<Payment>() {
        override fun areItemsTheSame(oldItem: Payment, newItem: Payment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Payment, newItem: Payment): Boolean {
            return oldItem == newItem
        }
    }
}