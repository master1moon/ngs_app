package com.ngs.`775396439`.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ngs.`775396439`.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
    }

    private fun setupListeners() {
        // زر الاتصال بالهاتف
        binding.btnCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${binding.tvPhone.text}")
            }
            startActivity(intent)
        }

        // زر الواتساب
        binding.btnWhatsapp.setOnClickListener {
            val phoneNumber = binding.tvPhone.text.toString().replace(" ", "")
            val message = "مرحباً، أريد معلومات عن تطبيق نظام بيع الكروت والمصروفات"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://wa.me/$phoneNumber?text=${Uri.encode(message)}")
            }
            startActivity(intent)
        }

        // زر البريد الإلكتروني
        binding.btnEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:${binding.tvEmail.text}")
                putExtra(Intent.EXTRA_SUBJECT, "استفسار عن تطبيق نظام بيع الكروت")
                putExtra(Intent.EXTRA_TEXT, "مرحباً،\n\nأريد معلومات عن تطبيق نظام بيع الكروت والمصروفات.\n\nشكراً لكم")
            }
            startActivity(intent)
        }

        // زر الموقع الإلكتروني
        binding.btnWebsite.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://www.ngs-solutions.com")
            }
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}