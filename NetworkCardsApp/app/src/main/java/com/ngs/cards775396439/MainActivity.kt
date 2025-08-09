package com.ngs.cards775396439

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.ngs.cards775396439.databinding.ActivityMainBinding
import com.ngs.cards775396439.ui.fragment.PackagesFragment

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupNavigation()
        
        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(PackagesFragment())
        }
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }
    }
    
    private fun setupNavigation() {
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_packages -> {
                    loadFragment(PackagesFragment())
                    binding.drawerLayout.closeDrawers()
                    true
                }
                else -> {
                    // TODO: Load other fragments
                    binding.drawerLayout.closeDrawers()
                    true
                }
            }
        }
        
        // Setup toolbar navigation icon
        binding.toolbar.setNavigationOnClickListener {
            binding.drawerLayout.open()
        }
    }
    
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}