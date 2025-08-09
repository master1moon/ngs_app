package com.ngs.cards775396439.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ngs.cards775396439.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {
    
    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        // Note: In a real app, you would implement actual contact actions
        // For now, we'll show placeholder dialogs
        
        // Find contact buttons by their text content since we don't have specific IDs
        view?.findViewWithTag<View>("call_button")?.setOnClickListener {
            showContactDialog("اتصال", "سيتم الاتصال بالرقم: +965 12345678")
        }
        
        view?.findViewWithTag<View>("whatsapp_button")?.setOnClickListener {
            showContactDialog("واتساب", "سيتم فتح واتساب للرقم: +965 12345678")
        }
    }
    
    private fun showDeveloperInfo() {
        val developerInfo = """
            معلومات المطور:
            
            الاسم: أحمد محمد علي
            الهاتف: +965 12345678
            واتساب: +965 12345678
            البريد الإلكتروني: ahmed@example.com
            
            حقوق النشر © 2025
            جميع الحقوق محفوظة
            
            تم تطوير هذا التطبيق باستخدام:
            • Kotlin
            • Android Jetpack
            • Material Design 3
            • Room Database
            • MVVM Architecture
        """.trimIndent()
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("معلومات المطور")
            .setMessage(developerInfo)
            .setPositiveButton("حسناً", null)
            .show()
    }
    
    private fun showContactDialog(title: String, message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("حسناً", null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}