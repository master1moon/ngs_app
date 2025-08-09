package com.ngs.`775396439`.ui.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.ngs.`775396439`.data.AppDatabase
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import com.ngs.`775396439`.databinding.FragmentImportExportBinding
import com.ngs.`775396439`.ui.viewmodel.ImportExportViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class ImportExportFragment : Fragment() {

    private var _binding: FragmentImportExportBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: NetworkCardsRepository

    private val viewModel: ImportExportViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val database = AppDatabase.getDatabase(requireContext())
                val repository = NetworkCardsRepository(database)
                return ImportExportViewModel(repository) as T
            }
        }
    }

    private val exportFileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                exportDataToFile(uri)
            }
        }
    }

    private val importFileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                importDataFromFile(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImportExportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRepository()
        setupObservers()
        setupClickListeners()
    }

    private fun setupRepository() {
        val database = AppDatabase.getDatabase(requireContext())
        repository = NetworkCardsRepository(database)
    }

    private fun setupObservers() {
        // Observe loading state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.loadingState.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        // Observe error messages
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.errorMessage.collect { errorMessage ->
                errorMessage?.let {
                    showSnackbar(it)
                    viewModel.clearError()
                }
            }
        }

        // Observe success messages
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.successMessage.collect { successMessage ->
                successMessage?.let {
                    showSnackbar(it)
                    viewModel.clearSuccess()
                }
            }
        }
    }

    private fun setupClickListeners() {
        // Export all data
        binding.btnExportAll.setOnClickListener {
            showExportDialog()
        }

        // Import all data
        binding.btnImportAll.setOnClickListener {
            showImportDialog()
        }

        // Backup database
        binding.btnBackupDatabase.setOnClickListener {
            backupDatabase()
        }
    }

    private fun showExportDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(com.ngs.`775396439`.R.string.export_confirmation_title))
            .setMessage(getString(com.ngs.`775396439`.R.string.export_confirmation_message))
            .setPositiveButton(getString(com.ngs.`775396439`.R.string.export)) { _, _ ->
                exportAllData()
            }
            .setNegativeButton(getString(com.ngs.`775396439`.R.string.cancel), null)
            .show()
    }

    private fun showImportDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(com.ngs.`775396439`.R.string.import_confirmation_title))
            .setMessage(getString(com.ngs.`775396439`.R.string.import_confirmation_message))
            .setPositiveButton(getString(com.ngs.`775396439`.R.string.import)) { _, _ ->
                selectImportFile()
            }
            .setNegativeButton(getString(com.ngs.`775396439`.R.string.cancel), null)
            .show()
    }

    private fun exportAllData() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val jsonData = viewModel.exportAllData()
                selectExportFile(jsonData)
            } catch (e: Exception) {
                showSnackbar("خطأ في تصدير البيانات: ${e.message}")
            }
        }
    }

    private fun selectExportFile(jsonData: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra(Intent.EXTRA_TITLE, "network_cards_backup_${repository.getCurrentDate()}.json")
        }
        exportFileLauncher.launch(intent)
    }

    private fun exportDataToFile(uri: Uri) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val jsonData = viewModel.exportAllData()
                requireContext().contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(jsonData.toByteArray())
                }
                showSnackbar(getString(com.ngs.`775396439`.R.string.export_success))
            } catch (e: Exception) {
                showSnackbar("خطأ في حفظ الملف: ${e.message}")
            }
        }
    }

    private fun selectImportFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
        }
        importFileLauncher.launch(intent)
    }

    private fun importDataFromFile(uri: Uri) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val jsonData = requireContext().contentResolver.openInputStream(uri)?.bufferedReader()?.readText()
                if (jsonData != null) {
                    viewModel.importAllData(jsonData)
                } else {
                    showSnackbar("خطأ في قراءة الملف")
                }
            } catch (e: Exception) {
                showSnackbar("خطأ في استيراد البيانات: ${e.message}")
            }
        }
    }

    private fun backupDatabase() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val database = AppDatabase.getDatabase(requireContext())
                val dbFile = requireContext().getDatabasePath("network_cards_database")
                
                if (dbFile.exists()) {
                    val backupFile = File(requireContext().getExternalFilesDir(null), "backup_${repository.getCurrentDate()}.db")
                    dbFile.copyTo(backupFile, overwrite = true)
                    showSnackbar(getString(com.ngs.`775396439`.R.string.backup_success))
                } else {
                    showSnackbar("لا توجد قاعدة بيانات للنسخ الاحتياطي")
                }
            } catch (e: Exception) {
                showSnackbar("خطأ في النسخ الاحتياطي: ${e.message}")
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}