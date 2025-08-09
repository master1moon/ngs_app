package com.ngs.cards775396439.utils

import android.content.Context
import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ExportUtils {
    
    companion object {
        
        /**
         * تصدير البيانات إلى ملف TXT (مبسط)
         */
        fun exportToTXT(
            context: Context,
            title: String,
            data: List<Map<String, String>>,
            columns: List<String>,
            fileName: String
        ): File? {
            return try {
                val file = createFile(context, fileName, "txt")
                val content = StringBuilder()
                
                // إضافة العنوان
                content.append("$title\n")
                content.append("=".repeat(title.length))
                content.append("\n\n")
                
                // إضافة التاريخ
                val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                content.append("تاريخ التصدير: $date\n\n")
                
                // إضافة رؤوس الأعمدة
                content.append(columns.joinToString("\t"))
                content.append("\n")
                content.append("-".repeat(columns.joinToString("\t").length))
                content.append("\n")
                
                // إضافة البيانات
                data.forEach { row ->
                    val rowData = columns.map { column -> row[column] ?: "" }
                    content.append(rowData.joinToString("\t"))
                    content.append("\n")
                }
                
                file.writeText(content.toString())
                
                file
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
        
        /**
         * تصدير البيانات إلى ملف JSON (مبسط)
         */
        fun exportToJSON(
            context: Context,
            data: List<Map<String, String>>,
            fileName: String
        ): File? {
            return try {
                val file = createFile(context, fileName, "json")
                
                // إنشاء JSON بسيط
                val jsonBuilder = StringBuilder()
                jsonBuilder.append("[\n")
                
                data.forEachIndexed { index, row ->
                    jsonBuilder.append("  {\n")
                    val entries = row.entries.toList()
                    entries.forEachIndexed { colIndex, (key, value) ->
                        jsonBuilder.append("    \"$key\": \"$value\"")
                        if (colIndex < entries.size - 1) jsonBuilder.append(",")
                        jsonBuilder.append("\n")
                    }
                    jsonBuilder.append("  }")
                    if (index < data.size - 1) jsonBuilder.append(",")
                    jsonBuilder.append("\n")
                }
                
                jsonBuilder.append("]\n")
                
                file.writeText(jsonBuilder.toString())
                
                file
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
        
        /**
         * إنشاء ملف في مجلد التحميلات
         */
        private fun createFile(context: Context, fileName: String, extension: String): File {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val finalFileName = "${fileName}_$timestamp.$extension"
            return File(downloadsDir, finalFileName)
        }
        
        /**
         * تحويل قائمة المصروفات إلى تنسيق مناسب للتصدير
         */
        fun formatExpensesForExport(expenses: List<com.ngs.cards775396439.data.entity.Expense>): List<Map<String, String>> {
            return expenses.map { expense ->
                mapOf(
                    "نوع المصروف" to expense.type,
                    "المبلغ" to "${EditTextUtils.formatNumber(expense.amount.toLong())} د.ك",
                    "الملاحظات" to expense.notes,
                    "التاريخ" to expense.date,
                    "إضافة لاحقاً" to if (expense.addLater) "نعم" else "لا"
                )
            }
        }
        
        /**
         * تحويل قائمة المبيعات إلى تنسيق مناسب للتصدير
         */
        fun formatSalesForExport(sales: List<com.ngs.cards775396439.data.entity.Sale>): List<Map<String, String>> {
            return sales.map { sale ->
                mapOf(
                    "المحل" to sale.storeId,
                    "الباقة" to sale.packageId,
                    "السبب" to sale.reason,
                    "الكمية" to sale.quantity.toString(),
                    "السعر" to "${EditTextUtils.formatNumber(sale.pricePerUnit.toLong())} د.ك",
                    "الإجمالي" to "${EditTextUtils.formatNumber(sale.total.toLong())} د.ك",
                    "التاريخ" to sale.date
                )
            }
        }
        
        /**
         * تحويل قائمة المدفوعات إلى تنسيق مناسب للتصدير
         */
        fun formatPaymentsForExport(payments: List<com.ngs.cards775396439.data.entity.Payment>): List<Map<String, String>> {
            return payments.map { payment ->
                mapOf(
                    "المحل" to payment.storeId,
                    "المبلغ" to "${EditTextUtils.formatNumber(payment.amount.toLong())} د.ك",
                    "الملاحظات" to payment.notes,
                    "التاريخ" to payment.date
                )
            }
        }
        
        /**
         * تحويل تقرير إلى تنسيق مناسب للتصدير
         */
        fun formatReportForExport(
            totalSales: Double,
            totalPayments: Double,
            totalExpenses: Double,
            netProfit: Double,
            fromDate: String,
            toDate: String
        ): List<Map<String, String>> {
            return listOf(
                mapOf(
                    "الفترة" to "$fromDate إلى $toDate",
                    "إجمالي المبيعات" to "${EditTextUtils.formatNumber(totalSales.toLong())} د.ك",
                    "إجمالي المدفوعات" to "${EditTextUtils.formatNumber(totalPayments.toLong())} د.ك",
                    "إجمالي المصروفات" to "${EditTextUtils.formatNumber(totalExpenses.toLong())} د.ك",
                    "صافي الربح" to "${EditTextUtils.formatNumber(netProfit.toLong())} د.ك"
                )
            )
        }
        
        /**
         * تصدير إلى PDF (مبسط - نص فقط)
         */
        fun exportToPDF(
            context: Context,
            title: String,
            data: List<Map<String, String>>,
            columns: List<String>,
            fileName: String
        ): File? {
            // للتبسيط، سنستخدم TXT مع امتداد PDF
            return exportToTXT(context, title, data, columns, fileName.replace(".pdf", "_pdf"))
        }
        
        /**
         * تصدير إلى Excel (مبسط - نص فقط)
         */
        fun exportToExcel(
            context: Context,
            title: String,
            data: List<Map<String, String>>,
            columns: List<String>,
            fileName: String
        ): File? {
            // للتبسيط، سنستخدم TXT مع امتداد xlsx
            return exportToTXT(context, title, data, columns, fileName.replace(".xlsx", "_xlsx"))
        }
    }
}