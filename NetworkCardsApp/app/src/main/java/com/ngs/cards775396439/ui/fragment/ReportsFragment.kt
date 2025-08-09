package com.ngs.cards775396439.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ngs.cards775396439.databinding.FragmentReportsBinding
import com.ngs.cards775396439.ui.viewmodel.ReportsViewModel
import com.ngs.cards775396439.utils.EditTextUtils
import com.ngs.cards775396439.utils.ExportUtils
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ReportsFragment : Fragment() {
    
    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: ReportsViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        setupObservers()
        setupClickListeners()
        setupDateRange()
    }
    
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[ReportsViewModel::class.java]
    }
    
    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.sales.collect { sales ->
                viewModel.payments.collect { payments ->
                    viewModel.expenses.collect { expenses ->
                        updateSummaryCards()
                    }
                }
            }
        }
        
        lifecycleScope.launch {
            viewModel.errorMessage.collect { error ->
                error?.let {
                    showErrorDialog(it)
                    viewModel.clearError()
                }
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.btnExportPDF.setOnClickListener {
            showExportDialog("PDF")
        }
        
        binding.btnExportExcel.setOnClickListener {
            showExportDialog("Excel")
        }
        
        binding.btnGenerateReport.setOnClickListener {
            generateReport()
        }
    }
    
    private fun setupDateRange() {
        // Set default date range (current month)
        val calendar = Calendar.getInstance()
        val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendar.time)
        val fromDate = "${currentMonth}-01"
        val toDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        
        binding.etFromDate.setText(fromDate)
        binding.etToDate.setText(toDate)
    }
    
    private fun updateSummaryCards() {
        val totalSales = viewModel.getTotalSales()
        val totalPayments = viewModel.getTotalPayments()
        val totalExpenses = viewModel.getTotalExpenses()
        val netProfit = viewModel.getNetProfit()
        
        binding.tvTotalSales.text = "${EditTextUtils.formatNumber(totalSales.toLong())} د.ك"
        binding.tvTotalPayments.text = "${EditTextUtils.formatNumber(totalPayments.toLong())} د.ك"
        binding.tvTotalExpenses.text = "${EditTextUtils.formatNumber(totalExpenses.toLong())} د.ك"
        binding.tvNetProfit.text = "${EditTextUtils.formatNumber(netProfit.toLong())} د.ك"
    }
    
    private fun generateReport() {
        val fromDate = binding.etFromDate.text.toString()
        val toDate = binding.etToDate.text.toString()
        
        if (fromDate.isEmpty() || toDate.isEmpty()) {
            showErrorDialog("يرجى إدخال الفترة الزمنية")
            return
        }
        
        if (fromDate > toDate) {
            showErrorDialog("تاريخ البداية يجب أن يكون قبل تاريخ النهاية")
            return
        }
        
        // Generate report for the selected date range
        val salesInRange = viewModel.getSalesByDateRange(fromDate, toDate)
        val paymentsInRange = viewModel.getPaymentsByDateRange(fromDate, toDate)
        val expensesInRange = viewModel.getExpensesByDateRange(fromDate, toDate)
        
        val totalSalesInRange = salesInRange.sumOf { it.total }
        val totalPaymentsInRange = paymentsInRange.sumOf { it.amount }
        val totalExpensesInRange = expensesInRange.sumOf { it.amount }
        val netProfitInRange = totalSalesInRange + totalPaymentsInRange - totalExpensesInRange
        
        val reportMessage = """
            تقرير الفترة من $fromDate إلى $toDate
            
            إجمالي المبيعات: ${EditTextUtils.formatNumber(totalSalesInRange.toLong())} د.ك
            إجمالي المدفوعات: ${EditTextUtils.formatNumber(totalPaymentsInRange.toLong())} د.ك
            إجمالي المصروفات: ${EditTextUtils.formatNumber(totalExpensesInRange.toLong())} د.ك
            صافي الربح: ${EditTextUtils.formatNumber(netProfitInRange.toLong())} د.ك
            
            عدد المبيعات: ${salesInRange.size}
            عدد المدفوعات: ${paymentsInRange.size}
            عدد المصروفات: ${expensesInRange.size}
        """.trimIndent()
        
        showReportDialog(reportMessage)
    }
    
    private fun showExportDialog(format: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("تصدير التقرير")
            .setMessage("سيتم تصدير التقرير بصيغة $format")
            .setPositiveButton("تصدير") { _, _ ->
                when (format) {
                    "PDF" -> exportReportToPDF()
                    "Excel" -> exportReportToExcel()
                    else -> showErrorDialog("سيتم إضافة وظيفة التصدير في المرحلة التالية")
                }
            }
            .setNegativeButton("إلغاء", null)
            .show()
    }
    
    private fun exportReportToPDF() {
        val fromDate = binding.etFromDate.text.toString()
        val toDate = binding.etToDate.text.toString()
        
        if (fromDate.isEmpty() || toDate.isEmpty()) {
            showErrorDialog("يرجى إدخال الفترة الزمنية")
            return
        }
        
        val totalSales = viewModel.getTotalSales()
        val totalPayments = viewModel.getTotalPayments()
        val totalExpenses = viewModel.getTotalExpenses()
        val netProfit = viewModel.getNetProfit()
        
        val data = ExportUtils.formatReportForExport(
            totalSales, totalPayments, totalExpenses, netProfit, fromDate, toDate
        )
        val columns = listOf("الفترة", "إجمالي المبيعات", "إجمالي المدفوعات", "إجمالي المصروفات", "صافي الربح")
        
        val file = ExportUtils.exportToPDF(
            requireContext(),
            "تقرير مالي شامل",
            data,
            columns,
            "financial_report"
        )
        
        if (file != null) {
            showSuccessDialog("تم تصدير التقرير إلى PDF بنجاح\nالملف: ${file.name}")
        } else {
            showErrorDialog("فشل في تصدير الملف")
        }
    }
    
    private fun exportReportToExcel() {
        val fromDate = binding.etFromDate.text.toString()
        val toDate = binding.etToDate.text.toString()
        
        if (fromDate.isEmpty() || toDate.isEmpty()) {
            showErrorDialog("يرجى إدخال الفترة الزمنية")
            return
        }
        
        val totalSales = viewModel.getTotalSales()
        val totalPayments = viewModel.getTotalPayments()
        val totalExpenses = viewModel.getTotalExpenses()
        val netProfit = viewModel.getNetProfit()
        
        val data = ExportUtils.formatReportForExport(
            totalSales, totalPayments, totalExpenses, netProfit, fromDate, toDate
        )
        val columns = listOf("الفترة", "إجمالي المبيعات", "إجمالي المدفوعات", "إجمالي المصروفات", "صافي الربح")
        
        val file = ExportUtils.exportToExcel(
            requireContext(),
            "تقرير مالي شامل",
            data,
            columns,
            "financial_report"
        )
        
        if (file != null) {
            showSuccessDialog("تم تصدير التقرير إلى Excel بنجاح\nالملف: ${file.name}")
        } else {
            showErrorDialog("فشل في تصدير الملف")
        }
    }
    
    private fun showSuccessDialog(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("نجح التصدير")
            .setMessage(message)
            .setPositiveButton("حسناً", null)
            .show()
    }
    
    private fun showReportDialog(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("التقرير")
            .setMessage(message)
            .setPositiveButton("حسناً", null)
            .show()
    }
    
    private fun showErrorDialog(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("خطأ")
            .setMessage(message)
            .setPositiveButton("حسناً", null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}