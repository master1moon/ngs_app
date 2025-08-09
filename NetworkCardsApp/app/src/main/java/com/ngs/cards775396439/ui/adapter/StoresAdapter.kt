package com.ngs.cards775396439.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ngs.cards775396439.data.entity.Store
import com.ngs.cards775396439.databinding.ItemStoreBinding

class StoresAdapter(
    private val onEditClick: (Store) -> Unit,
    private val onDeleteClick: (Store) -> Unit,
    private val onStoreClick: (Store) -> Unit
) : RecyclerView.Adapter<StoresAdapter.StoreViewHolder>() {
    
    private var storesList = listOf<Store>()
    
    fun updateStores(newStores: List<Store>) {
        storesList = newStores
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val binding = ItemStoreBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StoreViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        holder.bind(storesList[position])
    }
    
    override fun getItemCount(): Int = storesList.size
    
    inner class StoreViewHolder(
        private val binding: ItemStoreBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(store: Store) {
            binding.apply {
                tvStoreName.text = store.name
                tvPriceType.text = getPriceTypeName(store.priceType)
                tvCreatedAt.text = store.createdAt
                
                // Set click listeners
                root.setOnClickListener { onStoreClick(store) }
                btnEdit.setOnClickListener { onEditClick(store) }
                btnDelete.setOnClickListener { onDeleteClick(store) }
            }
        }
        
        private fun getPriceTypeName(priceType: String): String {
            return when (priceType) {
                "retail" -> "سعر التجزئة"
                "wholesale" -> "سعر الجملة"
                "distributor" -> "سعر الموزعين"
                else -> "غير محدد"
            }
        }
    }
}