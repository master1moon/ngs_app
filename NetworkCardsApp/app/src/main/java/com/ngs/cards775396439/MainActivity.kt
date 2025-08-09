package com.ngs.cards775396439

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ngs.cards775396439.databinding.ActivityMainBinding
import com.ngs.cards775396439.ui.fragment.PackagesFragment

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupNavigation()
        
        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(PackagesFragment())
        }
    }
    
    private fun setupNavigation() {
        // TODO: Setup navigation drawer and menu items
    }
    
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}