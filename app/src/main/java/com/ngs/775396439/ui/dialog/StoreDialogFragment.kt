package com.ngs.`775396439`.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.ngs.`775396439`.data.entity.Store
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import com.ngs.`775396439`.databinding.DialogStoreBinding

class StoreDialogFragment : DialogFragment() {

    private var _binding: DialogStoreBinding? = null
    private val binding get() = _binding!!
    
    private var store: Store? = null
    private var onSaveClick: ((Store) -> Unit)? = null

    companion object {
        fun newInstance(
            store: Store? = null,
            onSaveClick: (Store) -> Unit
        ): StoreDialogFragment {
            return StoreDialogFragment().apply {
                this.store = store
                this.onSaveClick = onSaveClick
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogStoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews()
        setupPriceTypeSpinner()
        fillData()
    }

    private fun setupViews() {
        // Set dialog title
        binding.tvDialogTitle.text = if (store == null) {
            getString(com.ngs.`775396439`.R.string.add_store)
        } else {
            getString(com.ngs.`775396439`.R.string.edit_store)
        }

        // Set current date
        binding.etStoreDate.setText(NetworkCardsRepository(null).getCurrentDate())

        // Setup save button
        binding.btnSave.setOnClickListener {
            saveStore()
        }

        // Setup cancel button
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun setupPriceTypeSpinner() {
        // Create price type options
        val priceTypes = listOf(
            "retail" to getString(com.ngs.`775396439`.R.string.retail),
            "wholesale" to getString(com.ngs.`775396439`.R.string.wholesale),
            "distributor" to getString(com.ngs.`775396439`.R.string.distributor)
        )
        
        val displayNames = priceTypes.map { it.second }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            displayNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPriceType.adapter = adapter
    }

    private fun fillData() {
        store?.let { str ->
            binding.etStoreName.setText(str.name)
            binding.etStoreDate.setText(str.createdAt)
            
            // Set price type selection
            val priceTypeIndex = when (str.priceType) {
                "retail" -> 0
                "wholesale" -> 1
                "distributor" -> 2
                else -> 0
            }
            binding.spinnerPriceType.setSelection(priceTypeIndex)
        }
    }

    private fun saveStore() {
        val name = binding.etStoreName.text.toString().trim()
        val date = binding.etStoreDate.text.toString()
        
        if (name.isEmpty()) {
            binding.etStoreName.error = getString(com.ngs.`775396439`.R.string.store_name_required)
            return
        }

        // Get selected price type
        val selectedPosition = binding.spinnerPriceType.selectedItemPosition
        val priceType = when (selectedPosition) {
            0 -> "retail"
            1 -> "wholesale"
            2 -> "distributor"
            else -> "retail"
        }

        val newStore = Store(
            id = store?.id ?: NetworkCardsRepository(null).generateId(),
            name = name,
            priceType = priceType,
            createdAt = date
        )

        onSaveClick?.invoke(newStore)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}