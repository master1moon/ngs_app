package com.ngs.`775396439`.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ngs.`775396439`.data.entity.Package
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import com.ngs.`775396439`.databinding.ItemPackageBinding

class PackagesAdapter(
    private val onEditClick: (Package) -> Unit,
    private val onDeleteClick: (Package) -> Unit
) : ListAdapter<Package, PackagesAdapter.PackageViewHolder>(PackageDiffCallback()) {

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
                tvPackageName.text = package_.name
                tvCreatedDate.text = package_.createdAt

                // Format prices with commas
                tvRetailPrice.text = if (package_.retailPrice != null && package_.retailPrice > 0) {
                    NetworkCardsRepository(null).formatNumber(package_.retailPrice)
                } else {
                    "-"
                }

                tvWholesalePrice.text = if (package_.wholesalePrice != null && package_.wholesalePrice > 0) {
                    NetworkCardsRepository(null).formatNumber(package_.wholesalePrice)
                } else {
                    "-"
                }

                tvDistributorPrice.text = if (package_.distributorPrice != null && package_.distributorPrice > 0) {
                    NetworkCardsRepository(null).formatNumber(package_.distributorPrice)
                } else {
                    "-"
                }

                // Set click listeners
                btnEditPackage.setOnClickListener {
                    onEditClick(package_)
                }

                btnDeletePackage.setOnClickListener {
                    onDeleteClick(package_)
                }
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