package com.ngs.`775396439`.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ngs.`775396439`.data.entity.Inventory
import com.ngs.`775396439`.data.entity.Package
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import com.ngs.`775396439`.databinding.ItemInventoryBinding

class InventoryAdapter(
    private val onEditClick: (Inventory) -> Unit,
    private val onDeleteClick: (Inventory) -> Unit
) : ListAdapter<Inventory, InventoryAdapter.InventoryViewHolder>(InventoryDiffCallback()) {

    // Note: This repository initialization is a placeholder.
    // In a real app, it should be injected via a DI framework or passed through constructor.
    private val repository = NetworkCardsRepository(null)

    private var packages: List<Package> = emptyList()

    fun setPackages(packages: List<Package>) {
        this.packages = packages
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val binding = ItemInventoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return InventoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class InventoryViewHolder(
        private val binding: ItemInventoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(inventory: Inventory) {
            val package_ = packages.find { it.id == inventory.packageId }
            
            binding.apply {
                // اسم الباقة
                packageName.text = package_?.name ?: "باقة غير معروفة"
                
                // الكمية
                quantity.text = formatNumber(inventory.quantity.toDouble())
                
                // القيم المحسوبة
                retailValue.text = formatPrice(calculateValue(inventory, package_, "retail"))
                wholesaleValue.text = formatPrice(calculateValue(inventory, package_, "wholesale"))
                distributorValue.text = formatPrice(calculateValue(inventory, package_, "distributor"))
                
                // تاريخ الإضافة
                inventoryDate.text = inventory.createdAt
                
                // أزرار الإجراءات
                btnEdit.setOnClickListener { onEditClick(inventory) }
                btnDelete.setOnClickListener { onDeleteClick(inventory) }
            }
        }

        private fun calculateValue(inventory: Inventory, package_: Package?, priceType: String): Double {
            if (package_ == null) return 0.0
            
            val price = when (priceType) {
                "retail" -> package_.retailPrice
                "wholesale" -> package_.wholesalePrice
                "distributor" -> package_.distributorPrice
                else -> package_.retailPrice
            }
            
            return (price ?: 0.0) * inventory.quantity
        }

        private fun formatNumber(number: Double): String {
            return repository.formatNumber(number)
        }

        private fun formatPrice(price: Double): String {
            return if (price > 0) {
                repository.formatNumber(price)
            } else {
                "0"
            }
        }
    }

    private class InventoryDiffCallback : DiffUtil.ItemCallback<Inventory>() {
        override fun areItemsTheSame(oldItem: Inventory, newItem: Inventory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Inventory, newItem: Inventory): Boolean {
            return oldItem == newItem
        }
    }
}