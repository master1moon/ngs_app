package com.ngs.`775396439`.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ngs.`775396439`.data.entity.Sale
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import com.ngs.`775396439`.databinding.ItemSaleBinding

class SalesAdapter(
    private val onEditClick: (Sale) -> Unit,
    private val onDeleteClick: (Sale) -> Unit
) : ListAdapter<Sale, SalesAdapter.SaleViewHolder>(SaleDiffCallback()) {

    private lateinit var viewModel: com.ngs.`775396439`.ui.viewmodel.SalesViewModel

    fun setViewModel(viewModel: com.ngs.`775396439`.ui.viewmodel.SalesViewModel) {
        this.viewModel = viewModel
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleViewHolder {
        val binding = ItemSaleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SaleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SaleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SaleViewHolder(
        private val binding: ItemSaleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(sale: Sale) {
            binding.apply {
                // Get store and package names
                val store = viewModel.getStoreById(sale.storeId)
                val package_ = viewModel.getPackageById(sale.packageId)
                
                val storeName = store?.name ?: "محل غير محدد"
                val packageName = package_?.name ?: "باقة غير محدد"
                
                tvStoreAndPackage.text = "$storeName - $packageName"
                tvSaleReason.text = sale.reason
                tvQuantity.text = "الكمية: ${sale.quantity}"
                tvPricePerUnit.text = "السعر: ${formatCurrency(sale.pricePerUnit)}"
                tvTotalAmount.text = formatCurrency(sale.total)
                tvSaleDate.text = sale.date

                // Set click listeners
                btnEditSale.setOnClickListener {
                    onEditClick(sale)
                }

                btnDeleteSale.setOnClickListener {
                    onDeleteClick(sale)
                }
            }
        }

        private fun formatCurrency(amount: Double): String {
            return NetworkCardsRepository(null).formatNumber(amount.toLong()) + " د.ك"
        }
    }

    private class SaleDiffCallback : DiffUtil.ItemCallback<Sale>() {
        override fun areItemsTheSame(oldItem: Sale, newItem: Sale): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Sale, newItem: Sale): Boolean {
            return oldItem == newItem
        }
    }
}