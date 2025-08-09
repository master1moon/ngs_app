package com.ngs.cards775396439

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.ngs.cards775396439.databinding.ActivityMainBinding
import com.ngs.cards775396439.ui.fragment.*

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
            loadFragment(DashboardFragment())
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
                R.id.nav_dashboard -> {
                    loadFragment(DashboardFragment())
                    binding.drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_packages -> {
                    loadFragment(PackagesFragment())
                    binding.drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_inventory -> {
                    loadFragment(InventoryFragment())
                    binding.drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_stores -> {
                    loadFragment(StoresFragment())
                    binding.drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_expenses -> {
                    loadFragment(ExpensesFragment())
                    binding.drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_sales -> {
                    loadFragment(SalesFragment())
                    binding.drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_payments -> {
                    loadFragment(PaymentsFragment())
                    binding.drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_reports -> {
                    loadFragment(ReportsFragment())
                    binding.drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_import_export -> {
                    loadFragment(ImportExportFragment())
                    binding.drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_about -> {
                    loadFragment(AboutFragment())
                    binding.drawerLayout.closeDrawers()
                    true
                }
                else -> {
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