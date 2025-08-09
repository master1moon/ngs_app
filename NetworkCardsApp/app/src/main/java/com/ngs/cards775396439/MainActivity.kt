package com.ngs.cards775396439

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ngs.cards775396439.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Simple test to make sure the app builds
        binding.textView.text = "نظام بيع الكروت والمصروفات\n\nتم إنشاء التطبيق بنجاح!"
    }
}