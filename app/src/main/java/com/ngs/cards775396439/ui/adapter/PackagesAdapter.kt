package com.ngs.cards775396439.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ngs.cards775396439.data.entity.Package
import com.ngs.cards775396439.databinding.ItemPackageBinding

class PackagesAdapter(
    private val onEditClick: (Package) -> Unit,
    private val onDeleteClick: (Package) -> Unit
) : RecyclerView.Adapter<PackagesAdapter.PackageViewHolder>() {
    
    private var packages = listOf<Package>()
    
    fun updatePackages(newPackages: List<Package>) {
        packages = newPackages
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageViewHolder {
        val binding = ItemPackageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PackageViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: PackageViewHolder, position: Int) {
        holder.bind(packages[position])
    }
    
    override fun getItemCount(): Int = packages.size
    
    inner class PackageViewHolder(
        private val binding: ItemPackageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(package_: Package) {
            binding.apply {
                tvPackageName.text = package_.name
                tvRetailPrice.text = package_.retailPrice?.let { "تجزئة: $it د.ك" } ?: "تجزئة: -"
                tvWholesalePrice.text = package_.wholesalePrice?.let { "جملة: $it د.ك" } ?: "جملة: -"
                tvDistributorPrice.text = package_.distributorPrice?.let { "موزعين: $it د.ك" } ?: "موزعين: -"
                tvCreatedAt.text = package_.createdAt
                
                btnEdit.setOnClickListener { onEditClick(package_) }
                btnDelete.setOnClickListener { onDeleteClick(package_) }
            }
        }
    }
}