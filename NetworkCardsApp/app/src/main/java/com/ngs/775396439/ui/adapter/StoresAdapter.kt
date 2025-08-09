package com.ngs.`775396439`.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ngs.`775396439`.data.entity.Store
import com.ngs.`775396439`.ui.viewmodel.StoresViewModel
import com.ngs.`775396439`.databinding.ItemStoreBinding

class StoresAdapter(
    private val onViewDetailsClick: (Store) -> Unit,
    private val onEditClick: (Store) -> Unit,
    private val onDeleteClick: (Store) -> Unit
) : ListAdapter<Store, StoresAdapter.StoreViewHolder>(StoreDiffCallback()) {

    private lateinit var viewModel: StoresViewModel

    fun setViewModel(viewModel: StoresViewModel) {
        this.viewModel = viewModel
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
        holder.bind(getItem(position))
    }

    inner class StoreViewHolder(
        private val binding: ItemStoreBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(store: Store) {
            binding.apply {
                tvStoreName.text = store.name
                tvCreatedDate.text = store.createdAt

                // Set price type chip
                val priceTypeDisplay = viewModel.getPriceTypeDisplayName(store.priceType)
                chipPriceType.text = priceTypeDisplay

                // Set chip color based on price type
                when (store.priceType) {
                    "retail" -> {
                        chipPriceType.setChipBackgroundColorResource(com.ngs.`775396439`.R.color.primary_light)
                        chipPriceType.setChipStrokeColorResource(com.ngs.`775396439`.R.color.primary)
                    }
                    "wholesale" -> {
                        chipPriceType.setChipBackgroundColorResource(com.ngs.`775396439`.R.color.info)
                        chipPriceType.setChipStrokeColorResource(com.ngs.`775396439`.R.color.info)
                    }
                    "distributor" -> {
                        chipPriceType.setChipBackgroundColorResource(com.ngs.`775396439`.R.color.warning)
                        chipPriceType.setChipStrokeColorResource(com.ngs.`775396439`.R.color.warning)
                    }
                }

                // Set click listeners
                btnViewDetails.setOnClickListener {
                    onViewDetailsClick(store)
                }

                btnEditStore.setOnClickListener {
                    onEditClick(store)
                }

                btnDeleteStore.setOnClickListener {
                    onDeleteClick(store)
                }
            }
        }
    }

    private class StoreDiffCallback : DiffUtil.ItemCallback<Store>() {
        override fun areItemsTheSame(oldItem: Store, newItem: Store): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Store, newItem: Store): Boolean {
            return oldItem == newItem
        }
    }
}