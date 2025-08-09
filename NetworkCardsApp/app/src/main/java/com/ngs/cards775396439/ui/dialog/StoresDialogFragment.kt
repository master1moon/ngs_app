package com.ngs.cards775396439.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.ngs.cards775396439.data.entity.Store
import com.ngs.cards775396439.databinding.DialogStoreBinding

class StoresDialogFragment : DialogFragment() {
    
    private var _binding: DialogStoreBinding? = null
    private val binding get() = _binding!!
    
    private var store: Store? = null
    private var onSaveClick: ((Store) -> Unit)? = null
    
    companion object {
        fun newInstance(
            store: Store? = null,
            onSaveClick: (Store) -> Unit
        ): StoresDialogFragment {
            return StoresDialogFragment().apply {
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
        setupClickListeners()
        loadStoreData()
    }
    
    private fun setupViews() {
        // Setup price type spinner
        val priceTypeNames = arrayOf("سعر التجزئة", "سعر الجملة", "سعر الموزعين")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, priceTypeNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPriceType.adapter = adapter
    }
    
    private fun setupClickListeners() {
        binding.btnSave.setOnClickListener {
            saveStore()
        }
        
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }
    
    private fun loadStoreData() {
        store?.let { s ->
            binding.tvTitle.text = "تعديل المحل"
            
            // Set store name
            binding.etStoreName.setText(s.name)
            
            // Set selected price type
            val priceTypeIndex = when (s.priceType) {
                "retail" -> 0
                "wholesale" -> 1
                "distributor" -> 2
                else -> 0
            }
            binding.spinnerPriceType.setSelection(priceTypeIndex)
            
            // Set date
            binding.etStoreDate.setText(s.createdAt)
        } ?: run {
            binding.tvTitle.text = "إضافة محل جديد"
            
            // Set today's date
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(java.util.Date())
            binding.etStoreDate.setText(today)
        }
    }
    
    private fun saveStore() {
        val name = binding.etStoreName.text.toString().trim()
        if (name.isEmpty()) {
            binding.etStoreName.error = "يرجى إدخال اسم المحل"
            return
        }
        
        val selectedPosition = binding.spinnerPriceType.selectedItemPosition
        val priceType = when (selectedPosition) {
            0 -> "retail"
            1 -> "wholesale"
            2 -> "distributor"
            else -> "retail"
        }
        
        val date = binding.etStoreDate.text.toString()
        
        val newStore = store?.copy(
            name = name,
            priceType = priceType,
            createdAt = date
        ) ?: Store(
            id = "store_${System.currentTimeMillis()}",
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