package com.ngs.cards775396439.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ngs.cards775396439.data.entity.Inventory
import com.ngs.cards775396439.data.entity.Package
import com.ngs.cards775396439.databinding.ItemInventoryBinding
import com.ngs.cards775396439.utils.EditTextUtils

class InventoryAdapter(
    private val onEditClick: (Inventory) -> Unit,
    private val onDeleteClick: (Inventory) -> Unit
) : RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>() {
    
    private var inventoryList = listOf<Inventory>()
    private var packagesList = listOf<Package>()
    
    fun updateInventory(newInventory: List<Inventory>, packages: List<Package>) {
        inventoryList = newInventory
        packagesList = packages
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
        holder.bind(inventoryList[position])
    }
    
    override fun getItemCount(): Int = inventoryList.size
    
    inner class InventoryViewHolder(
        private val binding: ItemInventoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(inventory: Inventory) {
            val package_ = packagesList.find { it.id == inventory.packageId }
            
            binding.apply {
                tvPackageName.text = package_?.name ?: "باقة غير معروفة"
                tvQuantity.text = "الكمية: ${EditTextUtils.formatNumber(inventory.quantity.toLong())}"
                tvCreatedAt.text = inventory.createdAt
                
                // Calculate values
                val retailValue = inventory.quantity * (package_?.retailPrice ?: 0.0)
                val wholesaleValue = inventory.quantity * (package_?.wholesalePrice ?: 0.0)
                val distributorValue = inventory.quantity * (package_?.distributorPrice ?: 0.0)
                
                tvRetailValue.text = "تجزئة: ${EditTextUtils.formatNumber(retailValue.toLong())} د.ك"
                tvWholesaleValue.text = "جملة: ${EditTextUtils.formatNumber(wholesaleValue.toLong())} د.ك"
                tvDistributorValue.text = "موزعين: ${EditTextUtils.formatNumber(distributorValue.toLong())} د.ك"
                
                btnEdit.setOnClickListener { onEditClick(inventory) }
                btnDelete.setOnClickListener { onDeleteClick(inventory) }
            }
        }
    }
}