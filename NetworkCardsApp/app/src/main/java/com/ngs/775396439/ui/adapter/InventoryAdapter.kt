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
                // Set package name
                tvPackageName.text = package_?.name ?: "باقة غير معروفة"
                tvCreatedDate.text = inventory.createdAt

                // Format quantity with commas
                tvQuantity.text = NetworkCardsRepository(null).formatNumber(inventory.quantity.toDouble())

                // Calculate and format values
                val retailValue = (package_?.retailPrice ?: 0.0) * inventory.quantity
                val wholesaleValue = (package_?.wholesalePrice ?: 0.0) * inventory.quantity
                val distributorValue = (package_?.distributorPrice ?: 0.0) * inventory.quantity

                tvRetailValue.text = if (retailValue > 0) {
                    NetworkCardsRepository(null).formatNumber(retailValue)
                } else {
                    "-"
                }

                tvWholesaleValue.text = if (wholesaleValue > 0) {
                    NetworkCardsRepository(null).formatNumber(wholesaleValue)
                } else {
                    "-"
                }

                tvDistributorValue.text = if (distributorValue > 0) {
                    NetworkCardsRepository(null).formatNumber(distributorValue)
                } else {
                    "-"
                }

                // Set click listeners
                btnEditInventory.setOnClickListener {
                    onEditClick(inventory)
                }

                btnDeleteInventory.setOnClickListener {
                    onDeleteClick(inventory)
                }
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