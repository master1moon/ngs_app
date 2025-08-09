package com.ngs.`775396439`.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ngs.`775396439`.data.entity.Package
import com.ngs.`775396439`.databinding.ItemPackageBinding
import com.ngs.`775396439`.data.repository.NetworkCardsRepository

class PackagesAdapter(
    private val onEditClick: (Package) -> Unit,
    private val onDeleteClick: (Package) -> Unit
) : ListAdapter<Package, PackagesAdapter.PackageViewHolder>(PackageDiffCallback()) {

    private val repository = NetworkCardsRepository(null) // سيتم حقنه لاحقاً

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageViewHolder {
        val binding = ItemPackageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PackageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PackageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PackageViewHolder(
        private val binding: ItemPackageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(package_: Package) {
            binding.apply {
                packageName.text = package_.name
                packageDate.text = package_.createdAt
                
                // عرض الأسعار
                retailPrice.text = "${getString(com.ngs.`775396439`.R.string.retail_label)}: ${formatPrice(package_.retailPrice)}"
                wholesalePrice.text = "${getString(com.ngs.`775396439`.R.string.wholesale_label)}: ${formatPrice(package_.wholesalePrice)}"
                distributorPrice.text = "${getString(com.ngs.`775396439`.R.string.distributor_label)}: ${formatPrice(package_.distributorPrice)}"
                
                // أزرار الإجراءات
                btnEdit.setOnClickListener { onEditClick(package_) }
                btnDelete.setOnClickListener { onDeleteClick(package_) }
            }
        }

        private fun formatPrice(price: Double?): String {
            return if (price != null && price > 0) {
                repository.formatNumber(price)
            } else {
                "0"
            }
        }
    }

    private class PackageDiffCallback : DiffUtil.ItemCallback<Package>() {
        override fun areItemsTheSame(oldItem: Package, newItem: Package): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Package, newItem: Package): Boolean {
            return oldItem == newItem
        }
    }
}