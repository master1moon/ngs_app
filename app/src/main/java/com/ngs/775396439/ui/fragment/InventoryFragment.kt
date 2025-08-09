package com.ngs.`775396439`.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ngs.`775396439`.data.AppDatabase
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import com.ngs.`775396439`.databinding.FragmentInventoryBinding

class InventoryFragment : Fragment() {
    
    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!
    
    private val repository by lazy {
        NetworkCardsRepository(AppDatabase.getDatabase(requireContext()))
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // TODO: Implement inventory UI
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}