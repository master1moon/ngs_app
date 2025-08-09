package com.ngs.cards775396439.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ngs.cards775396439.data.entity.Sale
import com.ngs.cards775396439.data.entity.Store
import com.ngs.cards775396439.data.entity.Package
import com.ngs.cards775396439.databinding.ItemSaleBinding
import com.ngs.cards775396439.utils.EditTextUtils

class SalesAdapter(
    private val onEditClick: (Sale) -> Unit,
    private val onDeleteClick: (Sale) -> Unit
) : RecyclerView.Adapter<SalesAdapter.SaleViewHolder>() {
    
    private var salesList = listOf<Sale>()
    private var storesList = listOf<Store>()
    private var packagesList = listOf<Package>()
    
    fun updateSales(newSales: List<Sale>, stores: List<Store>, packages: List<Package>) {
        salesList = newSales
        storesList = stores
        packagesList = packages
        notifyDataSetChanged()
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
        holder.bind(salesList[position])
    }
    
    override fun getItemCount(): Int = salesList.size
    
    inner class SaleViewHolder(
        private val binding: ItemSaleBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(sale: Sale) {
            val store = storesList.find { it.id == sale.storeId }
            val package_ = packagesList.find { it.id == sale.packageId }
            
            binding.apply {
                tvStoreName.text = store?.name ?: "محل غير معروف"
                tvPackageName.text = package_?.name ?: "باقة غير معروفة"
                tvReason.text = sale.reason
                tvQuantity.text = "الكمية: ${EditTextUtils.formatNumber(sale.quantity.toLong())}"
                tvPricePerUnit.text = "السعر: ${EditTextUtils.formatNumber(sale.pricePerUnit.toLong())} د.ك"
                tvTotal.text = "الإجمالي: ${EditTextUtils.formatNumber(sale.total.toLong())} د.ك"
                tvDate.text = sale.date
                
                // Set click listeners
                btnEdit.setOnClickListener { onEditClick(sale) }
                btnDelete.setOnClickListener { onDeleteClick(sale) }
            }
        }
    }
}